package org.gicentre.vast2012.bomnetworkstatus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.EventObject;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.event.EventListenerList;

@SuppressWarnings("serial")
public class MachineGroupDetailsCache extends Thread {

	public static String dbURL = "jdbc:postgresql://localhost/vast2012_mc1";
	public static String dbUser = "vast";
	public static String dbPassword = "vast";

	private static final int CACHE_MAX_SIZE = 100000;
	private static final int DETAILS_PER_REQUEST = 5000;

	LinkedHashMap<String, MachineGroupDetails> cache;
	public boolean dataChanged;

	Connection conn;

	EventListenerList ChangedListeners = new EventListenerList();
	public class ChangedEvent extends EventObject {
		public ChangedEvent(MachineGroupDetailsCache source) {
			super((Object) source);
		}
	}
	
	public MachineGroupDetailsCache() {
		super();

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

		try {
			conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
		} catch (SQLException e) {
			e.printStackTrace();
			conn = null;
		}

		this.start();
	}

	public void startLoad(String businessunitName, String facilityName, short compactTimestamp) {

		String hash = generateHash(businessunitName, facilityName, compactTimestamp);

		MachineGroupDetails mgd = cache.get(hash);

		// Deleting all uncomplete data from cache
		for (MachineGroupDetails mgdToDelete : cache.values()) {
			if (!mgdToDelete.loadingIsFinished && mgd != mgdToDelete) {
				cache.remove(generateHash(mgdToDelete.businessunitName, mgdToDelete.facilityName, mgdToDelete.compactTimestamp));
			}
		}

		// Adding new element to cache if needed
		if (mgd == null) {
			mgd = new MachineGroupDetails(businessunitName, facilityName, compactTimestamp);
			cache.put(hash, mgd);
		}
	}

	public void run() {
		try {
			while (true) {
				processLoadQueue();
				sleep(10);
			}
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Makes one request to database
	 */
	public void processLoadQueue() {
		for (MachineGroupDetails mgd : cache.values()) {
			if (!mgd.loadingIsFinished) {
				try {
					String stmtStr = "SELECT ipaddr, policystatus, activityflag, numconnections, machineclass, machinefunction "
							+ "FROM metastatus_ext "
							+ "WHERE businessunit = '" + mgd.businessunitName + "' " + "AND facility = '" + mgd.facilityName + "' " + "AND healthtime = '"
							+ CompactTimestamp.compactTimestimpToFull(mgd.compactTimestamp) + "' "
							//+ "ORDER BY machinefunction, ipaddr "
							+ "OFFSET " + mgd.details.size() + " "
							+ "LIMIT " + DETAILS_PER_REQUEST;
					ResultSet rs = conn.createStatement().executeQuery(stmtStr);

					int detailsCollected = 0;
					while (rs.next()) {
						MachineDetails md = new MachineDetails();
						md.ipaddr = IPConverter.stringToInt(rs.getString(1));
						md.policyStatus = rs.getByte(2);
						md.activityFlag = rs.getByte(3);
						md.numConnections = rs.getByte(4);
						md.machineGroup = BOMDictionary.machineGroupFromHR(rs.getString(5));
						md.machineFunction = BOMDictionary.machineFunctionFromHR(rs.getString(6));
						mgd.details.add(md);
						++detailsCollected;
					}

					if (detailsCollected < DETAILS_PER_REQUEST) {
						mgd.loadingIsFinished = true;
					}

					Collections.sort(mgd.details);
					mgd.calculateFirstElements();

					if (detailsCollected > 0) 
						dataChanged = true;

				} catch (SQLException e) {
					e.printStackTrace();
				}

				return;
			}
		}
	}

	public MachineGroupDetails get(String businessunitName, String facilityName, short compactTimestamp) {
		MachineGroupDetails mgd = cache.get(generateHash(businessunitName, facilityName, compactTimestamp));

		if (mgd != null && !mgd.loadingIsFinished)
			return mgd.clone();

		return mgd;
	}

	protected String generateHash(String businessunitName, String facilityName, short compactTimestamp) {
		return businessunitName + facilityName + compactTimestamp;
	}

	public boolean isNotWorking() {
		return conn == null;
	}
}
