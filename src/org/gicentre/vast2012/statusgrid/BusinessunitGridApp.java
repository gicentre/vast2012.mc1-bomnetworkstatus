package org.gicentre.vast2012.statusgrid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.gicentre.vast2012.statusgrid.ui.TimeAndBrightnessBusinessunitView;

import com.sun.jmx.snmp.Timestamp;

import processing.core.PApplet;

public class BusinessunitGridApp extends PApplet {

	public static int padding = 1;

	public static String dbURL = "jdbc:postgresql://localhost/vast_mc1";
	public static String dbUser = "postgres";
	public static String dbPassword = "hh";

	public HashMap<String, Businessunit> businessunits;
	public BusinessunitGrid businessunitGrid;

	public void setup() {

		BusinessunitGrid.padding = padding;

		size(5 * (192 + padding) + padding, (51 + padding) * 16 + padding);

		// Getting the data
		businessunits = loadFaclityStatusData();

		// Drawing the grid
		businessunitGrid = new BusinessunitGrid(businessunits,
				BusinessunitGrid.LAYOUT_SEQ);

		background(180);
		
		TimeAndBrightnessBusinessunitView view = new TimeAndBrightnessBusinessunitView(this, businessunitGrid);
		view.currentParameter = TimeAndBrightnessBusinessunitView.P_ACTIVITYFLAG;
		view.currentValue = 0;//TimeAndBrightnessBusinessunitView.V_MAX;
		view.currentCompactTimestamp = CompactTimestamp.FullTimestampToCompact(new java.sql.Timestamp(1328342400000L)) ; //Sat, 04 Feb 2012 08:00:00 GMT
		view.draw();
		noLoop();

	}

	public void draw() {

	}

	public HashMap<String, Businessunit> loadFaclityStatusData() {

		HashMap<String, Businessunit> businessunits = new HashMap<String, Businessunit>();

		try {
			Class.forName("org.postgresql.Driver").newInstance();

			Connection conn = DriverManager.getConnection(dbURL, dbUser,
					dbPassword);

			// Loading all facilities
//			String[] lines=loadStrings("facility.tab");

			Statement stmt;
			ResultSet rs;

			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * from facility");
			System.out.println("Obtained facilities from db");

			while (rs.next()) {
				String currentBuName = rs.getString("businessunit");
				String currentFName = rs.getString("facility");

				Businessunit currentBu = businessunits.get(currentBuName);
				if (currentBu == null) {
					currentBu = new Businessunit(currentBuName);
					businessunits.put(currentBuName, currentBu);
				}

				Facility currentF = new Facility();
				currentBu.facilities.put(currentFName, currentF);
				currentF.businessunit = currentBuName;
				currentF.facility = currentFName;
				currentF.machinegroups[0] = new MachineGroup(
						rs.getInt("_machinecount"));
				currentF.machinegroups[1] = new MachineGroup(
						rs.getInt("_atmcount"));
				currentF.machinegroups[2] = new MachineGroup(
						rs.getInt("_servercount"));
				currentF.machinegroups[3] = new MachineGroup(
						rs.getInt("_workstationcount"));
			}
			rs.close();
			stmt.close();
			System.out.println("Loaded facilities data");

			// Loading statuses for all facilities

			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * from facilitystatus-- limit 100000");
			System.out.println("Obtained facilitystatuses from db");

			//String[] machineGroupSuffixes = { "", "_atms", "_servers",
			//		"_workstations" };

			while (rs.next()) {
				String currentBuName = rs.getString(1);
				String currentFName = rs.getString(2);
				short compactTimestamp = CompactTimestamp.FullTimestampToCompact(rs.getTimestamp(3));
				Facility currentF = businessunits.get(currentBuName).facilities
						.get(currentFName);

				for (int machineGroupId = 0; machineGroupId < 4; machineGroupId++) {
					MachineGroupStatus currentMGStatus = new MachineGroupStatus();

					for (int i = 0; i < 6; i++)
						currentMGStatus.countByActivityFlag[i] = rs
								.getInt(4 + 24 + machineGroupId + i);
					for (int i = 0; i < 6; i++)
						currentMGStatus.countByPolicyStatus[i] = rs
								.getInt(4 + machineGroupId + i);

					currentMGStatus.connections[0] = rs
							.getInt(4 + 24 + 24 + machineGroupId); // count
					currentMGStatus.connections[1] = rs
							.getInt(4 + 24 + 24 + 12 + machineGroupId); // min
					currentMGStatus.connections[2] = rs
							.getInt(4 + 24 + 24 + 8 + machineGroupId); // max
					currentMGStatus.connections[3] = rs
							.getInt(4 + 24 + 24 + 4 + machineGroupId); // avg
					currentMGStatus.connections[4] = rs
							.getInt(4 + 24 + 24 + 16 + machineGroupId); // sd
					
					currentF.machinegroups[machineGroupId].statuses[compactTimestamp] = currentMGStatus;
				}
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Loaded facilitystatuses data");

		return businessunits;
	}
}
