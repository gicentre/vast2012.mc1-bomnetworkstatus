package org.gicentre.vast2012.bomnetworkstatus;

import java.util.Comparator;

/**
 * Compares 2 machines based on given options
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

public class MachineDetailsComparator implements Comparator<MachineDetails> {

	public static final int SM_IP = 0;

	public static final int SM_POLICY_STATUS = 1;
	public static final int SM_ACTIVITY_FLAG = 2;
	public static final int SM_CONNECTIONS = 3;
	
	public int sortMode = SM_IP;
	
	private static final MachineDetailsComparator instance = new MachineDetailsComparator();
	 
     // Private constructor prevents instantiation from other classes
     private MachineDetailsComparator() { }

     public static MachineDetailsComparator getInstance() {
             return instance;
     }

	/**
	 * Sorts machine details based on sortMode. Users machine function for primary sorting;
	 */
	public int compare(MachineDetails md1, MachineDetails md2) {
		
		int dp = Integer.signum(md1.machineFunction - md2.machineFunction);
		
		if (dp != 0)
			return dp;

		switch (sortMode) {
		case SM_POLICY_STATUS:
			dp = Integer.signum(md1.policyStatus - md2.policyStatus);
			break;
		case SM_ACTIVITY_FLAG:
			dp = Integer.signum(md1.activityFlag - md2.activityFlag);
			break;
		case SM_CONNECTIONS:
			dp = Integer.signum(md1.numConnections - md2.numConnections);
			break;
		}
		
		if (dp == 0)
			dp = Integer.signum(md1.ipaddr - md2.ipaddr);
		
		return dp;
	}
}
