package org.gicentre.vast2012.statusgrid;

import java.sql.Timestamp;
import java.util.HashMap;

public class MachineGroup {

	int machinecount;

	int   maxConnectionsCount;
	float maxConnectionsAvg;
	float maxConnectionsSD;
	short maxConnectionsMax;
	short maxConnectionsMin;
	
	public HashMap<Short, MachineGroupStatus> statuses;

	public MachineGroup(int machinecount) {
		this.machinecount = machinecount;
		statuses = new HashMap<Short, MachineGroupStatus>();
	}
}