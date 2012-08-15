package org.gicentre.vast2012.bomnetworkstatus;

/**
 * Converts IP address from String to int and vice versa
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

public class IPConverter {
	public static int stringToInt(String ip) {
		
		if (ip.isEmpty())
			return 0;
		
		String[] ipArray = ip.split("\\.");

		int num = 0;

		for (int i = 0; i < ipArray.length; i++) {
			num += (Integer.parseInt(ipArray[i]) & 0xFF) << ((3-i)*8);
		}

		return num;
	}

	public static String intToStr(int ip) {
		if (ip == 0) return "";
		return ((ip >> 24) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + (ip & 0xFF);
	}
}
