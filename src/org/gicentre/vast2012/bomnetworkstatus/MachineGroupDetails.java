package org.gicentre.vast2012.bomnetworkstatus;

import java.util.ArrayList;


public class MachineGroupDetails {
	public ArrayList<MachineDetails> details = new ArrayList<MachineDetails>();
	public int[] firstElements = new int[9];
	public boolean loadingIsFinished;
	
	public final short compactTimestamp;
	public final String facilityName;
	public final String businessunitName;

	public MachineGroupDetails(String businessunitName, String facilityName, short compactTimestamp) {
		this.businessunitName = businessunitName;
		this.facilityName = facilityName;
		this.compactTimestamp = compactTimestamp;
	}
	
	public void calculateFirstElements() {
		int size = details.size();
		for (int i = 1; i < size; i++) {
			if (details.get(i).machineFunction != details.get(i - 1).machineFunction) {
				firstElements[details.get(i).machineFunction] = i;
			}
			if (details.get(i).machineFunction == 8)
				return;
		}
	}

	@SuppressWarnings("unchecked")
	public MachineGroupDetails clone() {
		MachineGroupDetails mgd = new MachineGroupDetails(businessunitName, facilityName, compactTimestamp);
		
		mgd.details = (ArrayList<MachineDetails>)details.clone();
		mgd.firstElements = firstElements;
		mgd.loadingIsFinished = loadingIsFinished;
		
		return mgd;
	}

}