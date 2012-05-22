package org.gicentre.vast2012.statusgrid;

public class Facility {
	public String businessunit;
	public String facility;
	
	public short timezoneOffset;
	
	public MachineGroup[] machinegroups = new MachineGroup[4]; // 0 = all, 1 = atm, 2 = server, 3 = workstation
}
