package org.gicentre.vast2012.bomnetworkstatus;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;


public class Businessunit {
	public String name;
	
	public HashMap<String, Facility> facilities;
	public List<Facility> sortedFacilities=new ArrayList<Facility>();
	
	public Businessunit (String name) {
		this.name = name;
		facilities = new HashMap<String, Facility>();
		sortedFacilities = new ArrayList<Facility>();
	}

	public void sortFacilities() {
		Collections.sort(sortedFacilities, FacilityComparator.getInstance());
	}

	/**
	 * Returns the value of the number in the business unit name (for regions only).
	 * Examples: "region-1" → 42
	 *           "headquarters" → 0
	 */
	public static int extractIdFromName(String businessunitName) {
		if (businessunitName.charAt(0) == 'r')
			return Integer.valueOf(businessunitName.substring(7));
		return 0;
	}
}