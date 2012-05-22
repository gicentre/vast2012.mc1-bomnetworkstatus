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
			Statement stmt;
			ResultSet rs;

			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * from facility");

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

			// Loading statuses for all facilities

			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * from facilitystatus-- limit 100000");
			System.out.println("!");

			String[] machineGroupSuffixes = { "", "_atms", "_servers",
					"_workstations" };

			while (rs.next()) {
				String currentBuName = rs.getString("businessunit");
				String currentFName = rs.getString("facility");
				short compactTimestamp = CompactTimestamp.FullTimestampToCompact(rs.getTimestamp("timestamp"));
				Facility currentF = businessunits.get(currentBuName).facilities
						.get(currentFName);

				for (int machineGroupId = 0; machineGroupId < 4; machineGroupId++) {
					MachineGroupStatus currentMGStatus = new MachineGroupStatus();

					for (int i = 0; i < 6; i++)
						currentMGStatus.countByActivityFlag[i] = rs
								.getInt("_count_activityflag_" + i
										+ machineGroupSuffixes[machineGroupId]);
					for (int i = 0; i < 6; i++)
						currentMGStatus.countByPolicyStatus[i] = rs
								.getInt("_count_policystatus_" + i
										+ machineGroupSuffixes[machineGroupId]);

					currentMGStatus.connections[0] = rs
							.getInt("_count_connections"
									+ machineGroupSuffixes[machineGroupId]);
					currentMGStatus.connections[1] = rs
							.getInt("_min_connections"
									+ machineGroupSuffixes[machineGroupId]);
					currentMGStatus.connections[2] = rs
							.getInt("_max_connections"
									+ machineGroupSuffixes[machineGroupId]);
					currentMGStatus.connections[3] = rs
							.getInt("_avg_connections"
									+ machineGroupSuffixes[machineGroupId]);
					currentMGStatus.connections[4] = rs
							.getInt("_sd_connections"
									+ machineGroupSuffixes[machineGroupId]);
					
					currentF.machinegroups[machineGroupId].statuses.put(compactTimestamp, currentMGStatus);
				}
			}
			rs.close();
			stmt.close();
			System.out.println("!!");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return businessunits;
	}
}
