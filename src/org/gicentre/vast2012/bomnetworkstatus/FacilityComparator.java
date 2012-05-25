package org.gicentre.vast2012.bomnetworkstatus;

import java.util.Comparator;

public class FacilityComparator implements Comparator<Facility> {

	public static final int SM_NAME = 0;
	public static final int SM_TIMEZONE_AND_NAME = 1;
	public static final int SM_LAT_AND_NAME = 2;
	public static final int SM_LON_AND_NAME = 3;

	public int sortMode = SM_NAME;
	public boolean sortHeadquarters = true;
	
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
		case SM_TIMEZONE_AND_NAME:
			dp = Integer.signum(facility1.timezoneOffset - facility2.timezoneOffset);
			break;
		case SM_LAT_AND_NAME:
			dp = -Integer.signum((int) (100000 * (facility1.lat - facility2.lat)));
			break;
		case SM_LON_AND_NAME:
			dp = Integer.signum((int) (100000 * (facility1.lon - facility2.lon)));
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
