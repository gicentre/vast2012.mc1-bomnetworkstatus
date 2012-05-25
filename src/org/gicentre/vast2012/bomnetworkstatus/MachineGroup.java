package org.gicentre.vast2012.bomnetworkstatus;

/**
 * Instances of this class aggregate statistics for a machine group (e.g. servers, atms, workstations or all machines in a facility)
 * over time and also store some meta data (e.g. number of machines in the group)
 */
public class MachineGroup {

	public int machinecount;

	public int   maxConnectionsCount;
	public float maxConnectionsAvg;
	public float maxConnectionsSD;
	public short maxConnectionsMax;
	public short maxConnectionsMin;
	
	public MachineGroupStatus[] statuses;

	public MachineGroup(int machinecount) {
		this.machinecount = machinecount;
		statuses = new MachineGroupStatus[192];
	}
}