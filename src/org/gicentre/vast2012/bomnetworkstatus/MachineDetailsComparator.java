package org.gicentre.vast2012.bomnetworkstatus;

import java.util.Comparator;

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
