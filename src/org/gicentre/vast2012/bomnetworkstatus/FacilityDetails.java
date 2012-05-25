package org.gicentre.vast2012.bomnetworkstatus;

/**
 * Stores detailed information for every machine in a facility (displayed on the right hand side)
 */
public class FacilityDetails {

	/** ip address */
	int[] ipaddr;
	/** machine group */
	byte[] mg;
	/** machine function */
	byte[] mf;
	/** activity flag */
	byte[] af;
	/** policy status */
	byte[] ps;
	/** connections count */
	byte[] cc;

	public FacilityDetails(int machinecount) {
		ipaddr = new int[machinecount];
		mg = new byte[machinecount];
		mf = new byte[machinecount];
		af = new byte[machinecount];
		ps = new byte[machinecount];
		cc = new byte[machinecount];
	}
}
