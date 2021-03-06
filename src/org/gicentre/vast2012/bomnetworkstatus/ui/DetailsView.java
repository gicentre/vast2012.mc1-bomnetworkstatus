package org.gicentre.vast2012.bomnetworkstatus.ui;

import org.gicentre.vast2012.bomnetworkstatus.BOMNetworkStatusApp;
import org.gicentre.vast2012.bomnetworkstatus.Facility;
import org.gicentre.vast2012.bomnetworkstatus.MachineDetails;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroupDetails;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroupDetailsCache;
import org.gicentre.vast2012.bomnetworkstatus.ui.bugview.AbstractBUGView;

import java.awt.Font;
import processing.core.PFont;
import processing.core.PGraphics;

/**
 * Displays details-on-demand
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

public class DetailsView {

	private static final int UNIT_SIZE = 5;
	private static final int COL_WIDTH = 28;
	public static final int MARGIN_TOP = BOMNetworkStatusApp.DETAILS_CAPTIONS_HEIGHT + 6;

	public Facility currentFacility;
	public short currentCompactTimestamp;
	public int currentMachineGroup;

	protected MachineGroupDetails currentMachineGroupDetails;

	public MachineDetails selectedMachineDetails;
	public int selectedColumnMachineCount;
	public int selectedColumnMachineSeq;
	public int selectedColumn;

	PFont mainFont = new PFont(new Font("Arial", 0, 14), true);
	
	public DetailsView() {
	}

	/**
	 * Draws the detailed grid (process involves loading the data)
	 * @param canvas
	 * @param thread
	 * @return true if another draw is needed immediately after this one is done
	 */
	public void draw(PGraphics canvas, MachineGroupDetailsCache cache, AbstractBUGView gridView, Thread thread) {
		if (cache.isNotWorking() && currentFacility != null) {
			canvas.fill(0xffb35959);
			canvas.textFont(mainFont);
			canvas.textAlign(PGraphics.LEFT, PGraphics.TOP);
			canvas.text("Unable to get details for individual", 0, MARGIN_TOP);
			canvas.text("machines − a problem occured when", 0, MARGIN_TOP + 15);
			canvas.text("connecting to the database.", 0, MARGIN_TOP + 30);
			return;
		}
		
		currentMachineGroupDetails = null;
		if (currentFacility == null) {
			return;
		}

		canvas.noStroke();
		canvas.translate(0, MARGIN_TOP);

		currentMachineGroupDetails = cache.get(currentFacility.getBusinessunitRealName(), currentFacility.facilityName, currentCompactTimestamp);
		
		if (currentMachineGroupDetails == null) {
			cache.startLoad(currentFacility.getBusinessunitRealName(), currentFacility.facilityName, currentCompactTimestamp);
			return;
		}

		
		int offsetX = 0;
		int offsetY = 0;
		int size = currentMachineGroupDetails.details.size();
		
		boolean showThisColumn = false;
		int colourToAdd = 0;
		for (int i = 0; i < size; ++i) {
			if (thread.isInterrupted())
				break;

			MachineDetails md = currentMachineGroupDetails.details.get(i);

			if (i == 0 || md.machineFunction != currentMachineGroupDetails.details.get(i - 1).machineFunction) {
				offsetX = COL_WIDTH * md.machineFunction;
				offsetY = 0;

				if (currentMachineGroup == 0)
					showThisColumn = true;
				else if (currentMachineGroup < 4)
					showThisColumn =  md.machineClass == currentMachineGroup;
				else
					showThisColumn = currentMachineGroup == 3 + md.machineFunction;
				
				colourToAdd = showThisColumn ? 0xFFFFFFFF : 0x44FFFFFF;
			}
			
			canvas.fill(gridView.getColour(AbstractBUGView.P_POLICYSTATUS, md.policyStatus) & colourToAdd);
			canvas.rect(offsetX, offsetY, UNIT_SIZE, UNIT_SIZE);
			canvas.fill(gridView.getColour(AbstractBUGView.P_ACTIVITYFLAG, md.activityFlag) & colourToAdd);
			canvas.rect(offsetX + 1 * UNIT_SIZE, offsetY, UNIT_SIZE, UNIT_SIZE);
			canvas.fill(gridView.getConnectionsColour(canvas, md.numConnections) & colourToAdd);
			canvas.rect(offsetX + 2 * UNIT_SIZE, offsetY, UNIT_SIZE, UNIT_SIZE);
			offsetY += UNIT_SIZE;
		}
	}

	public void selectAt(int x, int y) {
		y -= MARGIN_TOP;
		selectedMachineDetails = null;
		selectedColumnMachineCount = -1;
		selectedColumnMachineSeq = -1;
		selectedColumn = -1;

		if (x < 0)
			return;

		if (currentMachineGroupDetails == null || currentMachineGroupDetails.details.size() == 0)
			return;

		try {
			for (int i = 0; i < 9; i++) {
				if (x <= 3 * UNIT_SIZE) {
					selectedColumnMachineCount = currentMachineGroupDetails.firstElements[i+1] - currentMachineGroupDetails.firstElements[i];
					selectedColumn = i;
					if (y < 0)
						return;
					int candidateMachineSeq = 0;
					for (int m = currentMachineGroupDetails.firstElements[i];; m++) {
						MachineDetails md = currentMachineGroupDetails.details.get(m);
						if (md.machineFunction != i)
							return;
						y -= UNIT_SIZE;
						++candidateMachineSeq;
						if (y < UNIT_SIZE) {
							selectedMachineDetails = md;
							selectedColumnMachineSeq = candidateMachineSeq;
							return;
						}

					}
				}
				if (x < COL_WIDTH)
					return;

				x -= COL_WIDTH;
			}
		} catch (IndexOutOfBoundsException e) {
		}
	}

	public void resetSelected() {
		selectedMachineDetails = null;
		selectedColumnMachineCount = -1;
		selectedColumnMachineSeq = -1;
		selectedColumn = -1;
	}
}
