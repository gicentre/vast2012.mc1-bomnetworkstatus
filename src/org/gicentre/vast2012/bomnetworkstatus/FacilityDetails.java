package org.gicentre.vast2012.bomnetworkstatus;

/**
 * Stores detailed information for every machine in a facility (displayed on the right hand side)
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
