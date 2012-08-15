package org.gicentre.vast2012.bomnetworkstatus;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Business unit - part of BoM Hierarchy
 *
 * @author Alexander Kachkaev <alexander.kachkaev.1@city.ac.uk>
 */

/* 
 * This file is part of BoM Network Status Application, VAST 2012 Mini Challenge 1 entry
 * awarded for "Efficient Use of Visualization". It is free software: you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License 
 * by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * BoM Network Status is distributed WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this
 * source code (see COPYING.LESSER included with this source code). If not, see 
 * http://www.gnu.org/licenses/.
 * 
 * For report on challenge, video and summary paper see http://gicentre.org/vast2012/
 */

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
	 * Examples: "region-42" → 42
	 *           "headquarters" → 0
	 */
	public static int extractIdFromName(String businessunitName) {
		if (businessunitName.charAt(0) == 'r')
			return Integer.valueOf(businessunitName.substring(7));
		return 0;
	}
}