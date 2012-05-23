package org.gicentre.vast2012.bomnetworkstatus;

public class Facility implements Comparable<Facility> {

	public static final int SM_NAME = 0;
	public static final int SM_TIMEZONE_AND_NAME = 1;
	public static int sortMode = SM_NAME;

	public String businessunit;
	public String facility;

	public float lat;
	public float lon;

	public short timezoneOffset;

	// 0 = all, 1 = atm, 2 = server, 3 = workstation
	public MachineGroup[] machinegroups = new MachineGroup[4];

	// sorts users based on sortMode. Users id as secondary sorting.
	public int compareTo(Facility faclilityToCompare) {

		// sort by name
		if (sortMode == SM_NAME) {
			return compareFacilityNames(this.facility,
					faclilityToCompare.facility);
			// sort by timezone and name
		} else {
			int dt = Integer.signum(this.timezoneOffset
					- faclilityToCompare.timezoneOffset);
			if (dt == 0)
				return compareFacilityNames(this.facility,
						faclilityToCompare.facility);
			return dt;
		}
	}

	/**
	 * Helps to comare strings like branch123 and branch8 correctly (looks at
	 * real values of numbers)
	 * 
	 * @param name1
	 * @param name2
	 * @return
	 */
	private int compareFacilityNames(String name1, String name2) {
		if (name1.charAt(0) == 'b' && name2.charAt(0) == 'b') {
			int id1 = Integer.valueOf(name1.substring(6));
			int id2 = Integer.valueOf(name2.substring(6));
			if (id1 == id2)
				return 0;
			else if (id1 < id2)
				return -1;
			else
				return 1;
		} else
			return name1.compareTo(name2);
	}
}
