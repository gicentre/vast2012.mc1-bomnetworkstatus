package org.gicentre.vast2012.bomnetworkstatus;

import java.sql.Timestamp;
import java.util.HashMap;

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