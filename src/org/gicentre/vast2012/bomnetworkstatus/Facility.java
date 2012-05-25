package org.gicentre.vast2012.bomnetworkstatus;

/**
 * Facility can be identified by its name (facilityName) and the name of the business unit (businessunitName).
 * 
 * The instance of this class stores some information about the facility
 * and statistics of 4 machine groups within it (all machines, ATMs, servers, workstations).
 */
public class Facility {

	public String businessunitName;
	public String facilityName;

	public float lat;
	public float lon;

	public short timezoneOffset;

	// 0 = all, 1 = atm, 2 = server, 3 = workstation
	public MachineGroup[] machinegroups = new MachineGroup[4];

	/**
	 * Returns the value of the number in the facility name (for branches only).
	 * Examples: "branch42" → 42
	 *           "headquarters" → 0
	 */
	public static int extractIdFromName(String facilityName) {
		if (facilityName.charAt(0) == 'b')
			return Integer.valueOf(facilityName.substring(6));
		return 0;
	}
}
