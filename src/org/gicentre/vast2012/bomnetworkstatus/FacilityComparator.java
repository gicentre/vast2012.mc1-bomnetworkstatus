package org.gicentre.vast2012.bomnetworkstatus;

import java.util.Comparator;

public class FacilityComparator implements Comparator<Facility> {

	public static final int SM_NAME = 0;
	public static final int SM_TIMEZONE = 1;
	public static final int SM_LAT = 2;
	public static final int SM_LON = 3;
	public static final int SM_IP_MIN = 4;
	public static final int SM_IP_MAX = 5;

	public static final int SM_ACTIVITY_FLAG = 0x10;
	public static final int SM_POLICY_STATUS = 0x11;
	public static final int SM_CONNECTIONS = 0x12;
	
	public int sortMode = SM_NAME;
	public int sortSubmode = 0;
	public boolean sortHeadquarters = true;
	public short sortComactTimestamp = 0;
	public int sortMachineGroup = 0;
	
	 private static final FacilityComparator instance = new FacilityComparator();
	 
     // Private constructor prevents instantiation from other classes
     private FacilityComparator() { }

     public static FacilityComparator getInstance() {
             return instance;
     }

	/**
	 * Sorts facilities based on sortMode. Users id as secondary sorting.
	 */
	public int compare(Facility facility1, Facility facility2) {
		
		if (!sortHeadquarters) {
			if (facility1.facilityName.charAt(0) == 'h')
				return 1;
			if (facility2.facilityName.charAt(0) == 'h')
				return -1;
		}

		// First sort by a parameter
		int dp = 0;

		switch (sortMode) {
		case SM_TIMEZONE:
			dp = Integer.signum(facility1.timezoneOffset - facility2.timezoneOffset);
			break;
		case SM_LAT:
			dp = -Integer.signum((int) (100000 * (facility1.lat - facility2.lat)));
			break;
		case SM_LON:
			dp = Integer.signum((int) (100000 * (facility1.lon - facility2.lon)));
			break;
		// WARNING: ip addres bigger than 127.255.255.255 are encoded as negative integers!
		case SM_IP_MIN:
			dp = Integer.signum(facility1.machinegroups[0].ipMin - facility2.machinegroups[0].ipMin); 
			break;
		case SM_IP_MAX:
			dp = Integer.signum(facility1.machinegroups[0].ipMax - facility2.machinegroups[0].ipMax);
			break;
		case SM_ACTIVITY_FLAG:
			dp = Integer.signum(facility1.machinegroups[sortMachineGroup].statuses[sortComactTimestamp].countByActivityFlag[sortSubmode] - facility2.machinegroups[sortMachineGroup].statuses[sortComactTimestamp].countByActivityFlag[sortSubmode]);
			break;
		case SM_POLICY_STATUS:
			dp = Integer.signum(facility1.machinegroups[sortMachineGroup].statuses[sortComactTimestamp].countByPolicyStatus[sortSubmode] - facility2.machinegroups[sortMachineGroup].statuses[sortComactTimestamp].countByPolicyStatus[sortSubmode]);
			break;
		case SM_CONNECTIONS:
			dp = Integer.signum((int)(facility1.machinegroups[sortMachineGroup].statuses[sortComactTimestamp].connections[sortSubmode] - facility2.machinegroups[sortMachineGroup].statuses[sortComactTimestamp].connections[sortSubmode]));
			break;
		}

		// If there is no difference, sort by name
		if (dp == 0)
			return compareFacilityNames(facility1.facilityName, facility2.facilityName);

		return dp;
	}

	/**
	 * Helps to compare strings like branch123 and branch8 correctly (looks at
	 * real values of numbers)
	 * 
	 * @param name1
	 * @param name2
	 * @return
	 */
	private int compareFacilityNames(String name1, String name2) {
		if (name1.charAt(0) == 'b' && name2.charAt(0) == 'b') {
			int id1 = Facility.extractIdFromName(name1);
			int id2 = Facility.extractIdFromName(name2);
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
