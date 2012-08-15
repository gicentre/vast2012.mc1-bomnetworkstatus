package org.gicentre.vast2012.bomnetworkstatus;

import java.io.BufferedReader;
import java.util.HashMap;

import processing.core.PApplet;

/**
 * Loads data about facilities and facility statuses from csv files
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

public class DataLoader extends Thread {
	
	HashMap<String, Businessunit> businessunits;

	public boolean ready;
	public double progress; // [0 - 0.1] - facility data; (0.1 - 1] facility status data
	
	public DataLoader() {
		super();
		start();
	}

	public void run() {

		businessunits = new HashMap<String, Businessunit>();

		try {
			
			PApplet helperApplet = new PApplet();

			BufferedReader reader;
			String line;

			// Reading facilities info
			reader = helperApplet.createReader("facility.tab");
			if (reader == null) {
				System.exit(1);
			}
			reader.readLine(); // Skipping the first line with headers

			int l = 0;
			while (true) {
				line = reader.readLine();
				if (line == null) {
					break;
				} else {
					if (++l % 1000 == 0) {
						sleep(1);
						progress = 0.1f * l / 4056;
					}
					String[] tokens = line.split("	");
					String currentBuName = tokens[0];
					String currentBuRealName = null;
					String currentFName = tokens[1];

					// Hack for business unit name to have data centres in different cells. See Facility.businessunitRealName
					if (currentBuName.equals("headquarters")) {
						currentBuRealName = currentBuName;
						currentBuName = currentFName;
					}

					Businessunit currentBu = businessunits.get(currentBuName);
					if (currentBu == null) {
						currentBu = new Businessunit(currentBuName);
						businessunits.put(currentBuName, currentBu);
					}

					Facility currentF = new Facility();
					currentBu.facilities.put(currentFName, currentF);
					currentBu.sortedFacilities.add(currentF);
					currentF.businessunitName = currentBuName;
					currentF.businessunitRealName = currentBuRealName;
					currentF.facilityName = currentFName;
					currentF.lat = Float.valueOf(tokens[2]);
					currentF.lon = Float.valueOf(tokens[3]);
					currentF.timezoneOffset = Short.valueOf(tokens[4]);
					
					for (int i = 0; i < 1 + 3 + 8; i++) {
						currentF.machinegroups[i] = new MachineGroup();
					}

					for (int i = 0; i < 4; i++) {
						currentF.machinegroups[i].ipMin = IPConverter.stringToInt(tokens[9 + 2 * i]);
						currentF.machinegroups[i].ipMax = IPConverter.stringToInt(tokens[10 + 2 * i]);
					}
					currentBu.sortFacilities();
				}
			}
			reader.close();

			// Reading statuses for all facilities
			reader = helperApplet.createReader("facilitystatus.tab");
			if (reader == null) {
				System.exit(1);
			}
			reader.readLine(); // Skipping the first line with headers

			char currentValue;

			l = 0;
			while (true) {
				line = reader.readLine();

				if (++l % 10000 == 0)
					sleep(1);
					progress = 0.1f + 0.9f * l / 778752;

				if (line == null) {
					break;
				} else {

					String[] tokens = line.split("	");
					
					String currentBuName = tokens[0];
					String currentFName = tokens[1];
					short compactTimestamp = Short.parseShort(tokens[2]);

					// Hack for business unit name to have data centres in different cells. See Facility.businessunitRealName
					if (currentBuName.equals("headquarters"))
						currentBuName = currentFName;

					if (businessunits.get(currentBuName) == null)
						continue;

					Facility currentF = businessunits.get(currentBuName).facilities.get(currentFName);

					// Reading connections
					for (short currentMachineGroup = 0; currentMachineGroup < 3 + 1 + 8; currentMachineGroup++) {
						MachineGroupStatus currentMGStatus = new MachineGroupStatus();
						currentF.machinegroups[currentMachineGroup].statuses[compactTimestamp] = currentMGStatus;
						
						currentMGStatus.connections[0] = Byte.parseByte(tokens[3 + 54 + 54 + 12 * 0 + currentMachineGroup]); // avg
						currentMGStatus.connections[1] = Byte.parseByte(tokens[3 + 54 + 54 + 12 * 1 + currentMachineGroup]); // sd
						currentMGStatus.connections[2] = Byte.parseByte(tokens[3 + 54 + 54 + 12 * 2 + currentMachineGroup]); // min
						currentMGStatus.connections[3] = Byte.parseByte(tokens[3 + 54 + 54 + 12 * 3 + currentMachineGroup]); // max
					}
					

					// Reading policy statuses and activity flags (only for machine function groups, first 4 groups get aggregate data)
					MachineGroupStatus currentMCStatus = null;
					MachineGroupStatus currentMFStatus = null;
					MachineGroupStatus currentAllstatus = currentF.machinegroups[0].statuses[compactTimestamp];
					
					for (short currentMachineFunction = 0; currentMachineFunction < 9; currentMachineFunction++) {
						
						if (currentMachineFunction != 0)
							currentMFStatus = currentF.machinegroups[3 + currentMachineFunction].statuses[compactTimestamp];

						switch (currentMachineFunction) {
						case 0:
							currentMCStatus = currentF.machinegroups[1].statuses[compactTimestamp];
							break;
						case 6:
						case 7:
						case 8:
							currentMCStatus = currentF.machinegroups[3].statuses[compactTimestamp];
							break;
						default:
							currentMCStatus = currentF.machinegroups[2].statuses[compactTimestamp];
						}

						
						for (int i = 0; i < 6; i++) {
							currentValue = (char)Integer.parseInt(tokens[3 + currentMachineFunction * 6 + i]);
							if (currentMachineFunction != 0) // ATMs are not forming a separate group because they have only one machine function
								currentMFStatus.countByPolicyStatus[i] = currentValue;
							currentMCStatus.countByPolicyStatus[i] += currentValue;
							currentAllstatus.countByPolicyStatus[i] += currentValue;
							
							// Getting count of machines from the sum of policystatuses 
							if (compactTimestamp == 0) {
								if (currentMachineFunction != 0) // ATMs are not forming a separate group because they have only one machine function
									currentF.machinegroups[3 + currentMachineFunction].machinecount += currentValue;
									
								switch (currentMachineFunction) {
								case 0:
									currentF.machinegroups[1].machinecount += currentValue;
									break;
								case 6:
								case 7:
								case 8:
									currentF.machinegroups[3].machinecount += currentValue;
									break;
								default:
									currentF.machinegroups[2].machinecount += currentValue;
								}
								currentF.machinegroups[0].machinecount += currentValue;
							}
						}
						

						for (int i = 0; i < 6; i++) {
							currentValue = (char)Integer.parseInt(tokens[3 + 54 + currentMachineFunction * 6 + i]);
							if (currentMachineFunction != 0) // ATMs are not forming a separate group because they have only one machine function
								currentMFStatus.countByActivityFlag[i] = currentValue;
							currentMCStatus.countByActivityFlag[i] += currentValue;
							currentAllstatus.countByActivityFlag[i] += currentValue;
						}
					}
				}
			}
			reader.close();

		} catch (InterruptedException e) {
			businessunits = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ready = true;
	}
}
