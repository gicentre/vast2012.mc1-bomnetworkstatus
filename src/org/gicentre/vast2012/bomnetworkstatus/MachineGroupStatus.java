package org.gicentre.vast2012.bomnetworkstatus;

/**
 * Instances of this class aggregate statistics for a machine group at a given time
 */

public class MachineGroupStatus {
	
	public int[] countByPolicyStatus;
	public int[] countByActivityFlag;
	public byte[] connections;

	public MachineGroupStatus() {
		countByPolicyStatus = new int[6];
		countByActivityFlag = new int[6];
		connections = new byte[4]; //1 - avg, 2 - sd, 3 - min, 4 - max, 
	}
}