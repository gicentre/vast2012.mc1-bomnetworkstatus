package org.gicentre.vast2012.bomnetworkstatus;

/**
 * Instances of this class aggregate statistics for a machine group (e.g. servers, atms, workstations or all machines in a facility)
 * over time and also store some meta data (e.g. number of machines in the group)
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

public class MachineGroup {

	public int machinecount;

	public int   maxConnectionsCount;
	public float maxConnectionsAvg;
	public float maxConnectionsSD;
	public short maxConnectionsMax;
	public short maxConnectionsMin;
	
	public MachineGroupStatus[] statuses;
	
	public int ipMin;
	public int ipMax;

	public MachineGroup() {
		statuses = new MachineGroupStatus[192];
	}
}