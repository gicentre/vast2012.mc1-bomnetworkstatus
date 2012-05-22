package org.gicentre.vast2012.statusgrid;

import java.util.ArrayList;
import java.util.HashMap;


public class Businessunit {
	public String name;
	
	public HashMap<String, Facility> facilities;
	
	public Businessunit (String name) {
		this.name = name;
		facilities = new HashMap<String, Facility>();
	}
}