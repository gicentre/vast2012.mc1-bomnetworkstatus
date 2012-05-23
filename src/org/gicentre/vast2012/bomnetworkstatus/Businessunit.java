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
		Collections.sort(sortedFacilities);
	}
}