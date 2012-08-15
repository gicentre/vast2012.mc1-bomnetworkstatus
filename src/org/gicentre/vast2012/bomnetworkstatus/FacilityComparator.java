package org.gicentre.vast2012.bomnetworkstatus;

import java.util.Comparator;

/**
 * Compares facilities based on given settings. Used in facility sorting
 * @author Alexander Kachkaev <alexander.kachkaev.1@city.ac.uk>
 */

/* 
 * This file is part of BoM Network Status Application, VAST 2012 Mini Challenge 1 entry
 * awarded for "Efficient Use of Visualization". It is free software: you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License 
 * by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * BoM Network Status is distributed WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this
 * source code (see COPYING.LESSER included with this source code). If not, see 
 * http://www.gnu.org/licenses/.
 * 
 * For report on challenge, video and summary paper see http://gicentre.org/vast2012/
 */

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
			dp = (int) Math.signum((double)facility1.machinegroups[sortMachineGroup].statuses[sortComactTimestamp].countByActivityFlag[sortSubmode] / facility1.machinegroups[sortMachineGroup].machinecount
					      		 - (double)facility2.machinegroups[sortMachineGroup].statuses[sortComactTimestamp].countByActivityFlag[sortSubmode] / facility2.machinegroups[sortMachineGroup].machinecount);
			break;
		case SM_POLICY_STATUS:
			dp = (int) Math.signum((double)facility1.machinegroups[sortMachineGroup].statuses[sortComactTimestamp].countByPolicyStatus[sortSubmode] / facility1.machinegroups[sortMachineGroup].machinecount
							 	 - (double)facility2.machinegroups[sortMachineGroup].statuses[sortComactTimestamp].countByPolicyStatus[sortSubmode] / facility2.machinegroups[sortMachineGroup].machinecount);
			break;
		case SM_CONNECTIONS:
			dp = Integer.signum(facility1.machinegroups[sortMachineGroup].statuses[sortComactTimestamp].connections[sortSubmode] - facility2.machinegroups[sortMachineGroup].statuses[sortComactTimestamp].connections[sortSubmode]);
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
