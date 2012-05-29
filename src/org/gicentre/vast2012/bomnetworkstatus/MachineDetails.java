package org.gicentre.vast2012.bomnetworkstatus;


public class MachineDetails implements Comparable<MachineDetails> {
	public int ipaddr;
	public byte machineGroup;
	public byte machineFunction;
	public byte activityFlag;
	public byte policyStatus;
	public byte numConnections;

	public int compareTo(MachineDetails md) {
		int result = Integer.signum(machineFunction - md.machineFunction);
		if (result == 0) {
			result = Integer.signum(ipaddr - md.ipaddr);
		}
		return result;
	}
}