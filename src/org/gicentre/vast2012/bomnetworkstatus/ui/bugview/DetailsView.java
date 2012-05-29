package org.gicentre.vast2012.bomnetworkstatus.ui.bugview;

import org.gicentre.vast2012.bomnetworkstatus.BOMDictionary;
import org.gicentre.vast2012.bomnetworkstatus.CompactTimestamp;
import org.gicentre.vast2012.bomnetworkstatus.Facility;
import org.gicentre.vast2012.bomnetworkstatus.IPConverter;

import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import processing.core.PFont;
import processing.core.PGraphics;

public class DetailsView {

	public static String dbURL = "jdbc:postgresql://localhost/vast2012_mc1";
	public static String dbUser = "vast";
	public static String dbPassword = "vast";

	private static final int CACHE_MAX_SIZE = 100000;
	private static final int DETAILS_PER_REQUEST = 10000;
	private static final int UNIT_SIZE = 5;
	private static final int COL_WIDTH = 28;

	public Facility currentFacility;
	public short currentCompactTimestamp;
	public int currentMachineGroup;

	protected class MachineDetails implements Comparable<MachineDetails> {
		public int ipaddr;
		public byte machineGroup;
		public byte machineFunction;
		public byte activityFlag;
		public byte policyStatus;
		public byte numConnections;
		
		public int compareTo(MachineDetails md) {
			int result = Integer.signum(machineFunction - md.machineFunction);
			if (result == 0) {
				result = Integer.signum(ipaddr - md.ipaddr);
			}
			return result;
		}
	}

	protected class MachineGroupDetails {
		ArrayList<MachineDetails> details = new ArrayList<MachineDetails>();
		boolean loadingIsFinished;
	}

	LinkedHashMap<String, MachineGroupDetails> cache;

	PFont mainFont = new PFont(new Font("Arial", 0, 14), true);

	@SuppressWarnings("serial")
	public DetailsView() {
		this.cache = new LinkedHashMap<String, MachineGroupDetails>() {
			protected boolean removeEldestEntry(Map.Entry<String, MachineGroupDetails> eldest) {
				// Remove the eldest entry if the size of the cache exceeds the maximum size
				int size = 0;
				for (MachineGroupDetails mgd : this.values()) {
					size += mgd.details.size();
				}
				return size > CACHE_MAX_SIZE;
			}
		};
	}

	/**
	 * Draws the detailed grid (process involves loading the data)
	 * @param canvas
	 * @param thread
	 * @return true if another draw is needed immediately after this one is done
	 */
	public boolean draw(PGraphics canvas, AbstractBUGView gridView, Thread thread) {
		if (currentFacility == null) {
			return false;
		}
		
		canvas.noStroke();
		canvas.translate(0, 6);
		
		String currentHash = generateCurrentHash();
		
		MachineGroupDetails mgd = cache.get(currentHash);
		if (mgd == null) {
			mgd = new MachineGroupDetails();
			cache.put(currentHash, mgd);
		}
		
		if (!mgd.loadingIsFinished) {
			Connection conn = null;
			PreparedStatement psGetDetails = null;

			try {
				conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
				psGetDetails = conn.prepareStatement("SELECT ipaddr, policystatus, activityflag, numconnections, machineclass, machinefunction FROM metastatus_ext WHERE businessunit = ?::e_meta_businessunit and facility = ?::e_meta_facility and healthtime = ? OFFSET ? LIMIT "
								+ DETAILS_PER_REQUEST);
				psGetDetails.setString(1, currentFacility.getBusinessunitRealName());
				psGetDetails.setString(2, currentFacility.facilityName);
				psGetDetails.setTimestamp(3, CompactTimestamp.compactTimestimpToFull(currentCompactTimestamp));
				psGetDetails.setInt(4, mgd.details.size());
				ResultSet rs = psGetDetails.executeQuery();
				
				int i = 0;
				while (rs.next()) {
					if (thread.isInterrupted())
						break;
					
					MachineDetails md = new MachineDetails();
					md.ipaddr = IPConverter.stringToInt(rs.getString(1));
					md.policyStatus = rs.getByte(2);
					md.activityFlag = rs.getByte(3);
					md.numConnections = rs.getByte(4);
					md.machineGroup = BOMDictionary.machineGroupFromHR(rs.getString(5));
					md.machineFunction = BOMDictionary.machineFunctionFromHR(rs.getString(6));
					mgd.details.add(md);
					++i;
				}
				
				//if (!thread.isInterrupted() && i < DETAILS_PER_REQUEST)
					mgd.loadingIsFinished = true;
				
				rs.close();
				psGetDetails.close();
				conn.close();
					
			} catch (SQLException e) {
				e.printStackTrace();
				canvas.fill(0xffb35959);
				canvas.textFont(mainFont);
				canvas.textAlign(PGraphics.LEFT, PGraphics.TOP);
				canvas.text("Unable to get details for individual", 0, 0);
				canvas.text("machines − a problem occured when", 0, 15);
				canvas.text("connecting to the database.", 0, 30);
				cache.remove(currentHash);
				return false;
			}
		}
		
		
		
		//canvas.fill(220);
		//canvas.rect(0, 0, 1000, 1000);
		//canvas.fill(100);
		//canvas.rect(10, 10, 10, new Random().nextInt(100));

		if (thread.isInterrupted())
			return !mgd.loadingIsFinished;
		
		Collections.sort(mgd.details);

		/*canvas.fill(120);
		canvas.textFont(mainFont);
		canvas.textAlign(PGraphics.LEFT, PGraphics.TOP);
		canvas.text("Details for " + BOMDictionary.machineGroupToHR(currentMachineGroup).toLowerCase(), 0, 0);
		canvas.text("at " + currentFacility.facilityName +" in " + currentFacility.getBusinessunitRealName(), 0, 15); 
		canvas.text("at " + CompactTimestamp.toHRString(currentCompactTimestamp), 0, 30);
		canvas.text("will be shown here.", 0, 45);*/
		
		int offsetX = 0;
		int offsetY = 0;
		int size = mgd.details.size();
		for (int i = 0; i < size; ++i) {
			if (thread.isInterrupted())
				break;
			
			MachineDetails md = mgd.details.get(i);
			
			if (i != 0 && md.machineFunction != mgd.details.get(i-1).machineFunction) {
				offsetX += COL_WIDTH;
				offsetY = 0;
			}
			
			if (currentMachineGroup == 0 || currentMachineGroup == md.machineGroup) {
				canvas.fill(gridView.getColour(AbstractBUGView.P_POLICYSTATUS, md.policyStatus));
				canvas.rect(offsetX, offsetY, UNIT_SIZE, UNIT_SIZE);
				canvas.fill(gridView.getColour(AbstractBUGView.P_ACTIVITYFLAG, md.activityFlag));
				canvas.rect(offsetX + 1*UNIT_SIZE, offsetY, UNIT_SIZE, UNIT_SIZE);
				canvas.fill(canvas.lerpColor(0xffffffff, gridView.getColour(AbstractBUGView.P_CONNECTIONS, 0), md.numConnections / 120f));
				canvas.rect(offsetX + 2*UNIT_SIZE, offsetY, UNIT_SIZE, UNIT_SIZE);
			}
			
			offsetY += UNIT_SIZE;
		}
		
		//canvas.text(mgd.details.size(), 0, 60);
		
		
		return !mgd.loadingIsFinished;
	}

	protected String generateCurrentHash() {
		return currentMachineGroup + currentFacility.businessunitName + currentFacility.facilityName + currentCompactTimestamp;
	}
}
