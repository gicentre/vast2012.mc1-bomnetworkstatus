package org.gicentre.vast2012.bomnetworkstatus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class MachineGroupDetailsCache extends Thread {

	private static final String dbURL = "jdbc:postgresql://localhost/vast2012_mc1";
	private static final String dbUser = "vast";
	private static final String dbPassword = "vast";

	private static final int CACHE_MAX_SIZE = 200000;
	private static final int DETAILS_PER_REQUEST = 10000;
	private static final int DETAILS_PER_FIRST_REQUEST = 2000;
	
	

	LinkedHashMap<String, MachineGroupDetails> cache;
	public boolean dataChanged;

	Connection conn;

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
		try {
		for (MachineGroupDetails mgd : cache.values()) {
			if (!mgd.loadingIsFinished) {
				try {
					int initialMGDSize = mgd.details.size();
					String stmtStr = "SELECT ipaddr, policystatus, activityflag, numconnections, machineclass, machinefunction "
							+ "FROM metastatus_ext_sorted "
							+ "WHERE businessunit = '" + mgd.businessunitName + "' " + "AND facility = '" + mgd.facilityName + "' " + "AND healthtime = '"
							+ CompactTimestamp.compactTimestimpToFull(mgd.compactTimestamp) + "' "
							//+ "ORDER BY machinefunction, ipaddr "
							+ "OFFSET " + initialMGDSize + " "
							+ "LIMIT " + (initialMGDSize == 0 ? DETAILS_PER_FIRST_REQUEST : DETAILS_PER_REQUEST);
					ResultSet rs = conn.createStatement().executeQuery(stmtStr);

					int detailsCollected = 0;
					while (rs.next()) {
						MachineDetails md = new MachineDetails();
						md.ipaddr = IPConverter.stringToInt(rs.getString(1));
						md.policyStatus = rs.getByte(2);
						md.activityFlag = rs.getByte(3);
						md.numConnections = rs.getByte(4);
						md.machineClass = BOMDictionary.machineClassFromHR(rs.getString(5));
						md.machineFunction = BOMDictionary.machineFunctionFromHR(rs.getString(6));
						mgd.details.add(md);
						++detailsCollected;
					}

					if (detailsCollected < (initialMGDSize == 0 ? DETAILS_PER_FIRST_REQUEST : DETAILS_PER_REQUEST)) {
						mgd.loadingIsFinished = true;
					}

					mgd.sortIfNeeded();
					mgd.calculateFirstElements();

					if (detailsCollected > 0) 
						dataChanged = true;

				} catch (SQLException e) {
					e.printStackTrace();
				}

				return;
			}
		}
		} catch (ConcurrentModificationException e) {
			System.err.println("Concurrent cache modification in processLoadQueue(). Exception catched.");
		}
	}

	public MachineGroupDetails get(String businessunitName, String facilityName, short compactTimestamp) {
		MachineGroupDetails mgd = cache.get(generateHash(businessunitName, facilityName, compactTimestamp));

		if (mgd != null) {
			mgd.sortIfNeeded();
			if (!mgd.loadingIsFinished)
				return mgd.clone();
		}

		return mgd;
	}

	protected String generateHash(String businessunitName, String facilityName, short compactTimestamp) {
		return businessunitName + facilityName + compactTimestamp;
	}

	public boolean isNotWorking() {
		return conn == null;
	}
}
