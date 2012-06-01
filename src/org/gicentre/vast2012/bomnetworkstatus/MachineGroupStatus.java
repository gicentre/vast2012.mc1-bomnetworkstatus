package org.gicentre.vast2012.bomnetworkstatus;

/**
 * Instances of this class aggregate statistics for a machine group at a given time
 */

public class MachineGroupStatus {
	
	public char[] countByPolicyStatus; // char is used as unsigned short (16 bit) to save space
	public char[] countByActivityFlag; // char is used as unsigned short (16 bit) to save space
	public byte[] connections;

	public MachineGroupStatus() {
		countByPolicyStatus = new char[6];
		countByActivityFlag = new char[6];
		connections = new byte[4]; //1 - avg, 2 - sd, 3 - min, 4 - max, 
	}
}