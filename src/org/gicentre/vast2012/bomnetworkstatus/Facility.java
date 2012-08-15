package org.gicentre.vast2012.bomnetworkstatus;

/**
 * The instance of this class stores some information about the facility
 * and statistics of 1+3+8 machine groups within it (all machines, ATMs, servers, workstations, each machine function).
 *
 * A facility can be identified by its name (facilityName) and the name of the business unit (businessunitName).
 *
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
