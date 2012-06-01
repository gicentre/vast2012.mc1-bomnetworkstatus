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
	
	// Datacentres live in "artificial" businessunits, called as facilities (datacentre-1 ... datacentre-5)
	// in order to be placed in separate grid cells. businessunitRealName field in this case
	// is to be equal to "headquarters" and is used in titles if not equal to null
	public String businessunitRealName;

	public float lat;
	public float lon;

	public short timezoneOffset;

	// 0 = all, 1 = atm, 2 = server, 3 = workstation
	public MachineGroup[] machinegroups = new MachineGroup[1+3+8];
	
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

	public String getBusinessunitRealName() {
		return (businessunitRealName == null ? businessunitName : businessunitRealName);
	}
}
