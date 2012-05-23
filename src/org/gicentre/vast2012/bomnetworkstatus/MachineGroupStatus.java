package org.gicentre.vast2012.bomnetworkstatus;

public class MachineGroupStatus {
	
	public int[] countByPolicyStatus;
	public int[] countByActivityFlag;
	
	public float[] connections;

	public MachineGroupStatus() {
		countByPolicyStatus = new int[6];
		countByActivityFlag = new int[6];
		connections = new float[5]; // 0 - count, 1 - min, 2 - max, 3 - avg, 4 - sd
	}
}