package org.gicentre.vast2012.bomnetworkstatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;


public class MachineGroupDetails {
	public ArrayList<MachineDetails> details = new ArrayList<MachineDetails>();
	public int[] firstElements = new int[10];
	public boolean loadingIsFinished;
	
	public final short compactTimestamp;
	public final String facilityName;
	public final String businessunitName;
	
	public int sortModeWhenLastSorted = -1;
	public int sizeWhenLastSorted = -1;

	public MachineGroupDetails(String businessunitName, String facilityName, short compactTimestamp) {
		this.businessunitName = businessunitName;
		this.facilityName = facilityName;
		this.compactTimestamp = compactTimestamp;
	}
	
	public void calculateFirstElements() {
		int size = details.size();
		for (int i = 0; i < 9; i++)
			firstElements[i] = 0;

		for (int i = 1; i < size; i++) {
			if (details.get(i).machineFunction != details.get(i - 1).machineFunction) {
				firstElements[details.get(i).machineFunction] = i;
			}
			if (details.get(i).machineFunction == 8)
				break;
		}

		firstElements[9] = size;
		for (int i = 1; i < 9; i++)
			if (firstElements[i] == 0 && firstElements[i-1] != 0) {
				for (int j = i+1; j < 10; j++) {
					if (firstElements[j] != 0) {
						firstElements[i] = firstElements[j];
						break;
					}
				}
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

	public void sortIfNeeded() {
		int sm = MachineDetailsComparator.getInstance().sortMode;
		int ds = details.size();
		if (sizeWhenLastSorted != ds || sortModeWhenLastSorted != MachineDetailsComparator.getInstance().sortMode) {
			while (true) {
				try {
					Collections.sort(details, MachineDetailsComparator.getInstance());
					break;
				} catch (ConcurrentModificationException e) {
					System.err.println("Warning. Exception occured when sorting machine details: " + e.getMessage());
				}
			}
			sizeWhenLastSorted = ds;
			sortModeWhenLastSorted = sm;
		}
	}
}