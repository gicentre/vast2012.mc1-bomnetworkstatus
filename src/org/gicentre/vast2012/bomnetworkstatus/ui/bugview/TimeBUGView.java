package org.gicentre.vast2012.bomnetworkstatus.ui.bugview;

import org.gicentre.vast2012.bomnetworkstatus.Businessunit;
import org.gicentre.vast2012.bomnetworkstatus.CompactTimestamp;
import org.gicentre.vast2012.bomnetworkstatus.Facility;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroup;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroupStatus;
import org.gicentre.vast2012.bomnetworkstatus.ui.BusinessunitGrid;

import processing.core.PGraphics;
import processing.core.PVector;

public class TimeBUGView extends CommonBUGView {

	public float baseRangeMin = 0;
	public float baseRangeMax = 1;

	public TimeBUGView(BusinessunitGrid grid) {
		super(grid);
	}

	public void drawBusinessunit(PGraphics canvas, String businessunitName,
			Thread thread) {
		float offsetX = bug.getColX(bug.getCol(businessunitName));
		float offsetY = bug.getColY(bug.getRow(businessunitName));

		Businessunit bu = bug.getBusinessunits().get(businessunitName);

		int currentY = 0;
		int currentFacilityHeight;
		Facility prevFacility = null;

		int colourMax = 0;
		int colourMin = canvas.color(255, 255, 255);
		int tsWidth = 1;
		
		PVector focusedPixel = null;

		// Finding colours
		switch (currentParameter) {
		case P_ACTIVITYFLAG:
			colourMax = activityFlagCT.findColour(currentValue);
			break;
		case P_POLICYSTATUS:
			colourMax = policyStatusCT.findColour(currentValue);
			break;
		case P_CONNECTIONS:
			colourMax = connectionCT.findColour(currentValue);
			break;
		}

		canvas.noStroke();
		
		for (Facility f : bu.sortedFacilities) {
			MachineGroup mg = f.machinegroups[currentMachineGroup];

			currentY += getGapBetweenFacilities(prevFacility, f);
			currentFacilityHeight = getFacilityHeight(f);

			short cts = currentCompactTimestamp;

			// Offsetting time by timezone if time is relative
			if (timeIsRelative)
				cts = (short) (cts - f.timezoneOffset * 4 - 4 * 3);

			// The value that will be drawn
			if (thread.isInterrupted())
				return;

			for (int t = 191; t >= 0; t--) {
				float valueBase = -1;
				float value = -1;

				if (CompactTimestamp.isWithin48HrsWindow(cts)) {
					MachineGroupStatus mgs = mg.statuses[cts];

					if (mgs != null) {
						switch (currentParameter) {
						case P_ACTIVITYFLAG:
							value = mgs.countByActivityFlag[currentValue];
							valueBase = mg.machinecount;
							break;
						case P_POLICYSTATUS:
							value = mgs.countByPolicyStatus[currentValue];
							valueBase = mg.machinecount;
							break;
						case P_CONNECTIONS:
							value = mgs.connections[currentValue];
							break;
						}
					}
				}

				// Drawing a point
				// No data or base is not determined
				if (value == -1 || valueBase == -1)
					canvas.fill(FILL_NODATA);
				// Data presents and the base is determined
				else
					canvas.fill(canvas.lerpColor(colourMin, colourMax, Math
							.min(1, Math.max(0,
									((value / valueBase) - baseRangeMin)
											/ (baseRangeMax - baseRangeMin)))));
				// canvas.point(offsetX + t, offsetY + row);
				canvas.rect(offsetX + t, offsetY + currentY, tsWidth, currentFacilityHeight);
				
				cts--; // Subtracting 15 minutes
			}
			currentY += currentFacilityHeight;
			prevFacility = f;
		}
	}

	public void selectAt(int x, int y) {
		// Selecting facility
		super.selectAt(x, y);
		
		selectedCompactTimestamp = 0;
		if (selectedFacility == null)
			return;
		
		// Selecting timestamp
		selectedCompactTimestamp = (short) (x - bug.getBusinessunitX(selectedFacility.businessunitName)
				+ this.currentCompactTimestamp - 191);
		if (this.timeIsRelative)
			selectedCompactTimestamp -= selectedFacility.timezoneOffset * 4 + 4 * 3;
	}
	
	public void highlightSelectedElement(PGraphics canvas, Thread thread) {
		if (selectedFacility == null)
			return;
		
		int elementX = 0;
		int elementY = 0;
		
		Businessunit currentBU = bug.getBusinessunits().get(selectedFacility.businessunitName);
		
		elementX += selectedCompactTimestamp + 191 - currentCompactTimestamp;
		if (this.timeIsRelative)
			elementX += selectedFacility.timezoneOffset * 4 + 4 * 3;

		elementY += currentBU.sortedFacilities.indexOf(selectedFacility);
		
		if (elementX < 0 || elementX > 191)
			return;
		
		elementX += (int)bug.getBusinessunitX(currentBU.name);
		elementY += (int)bug.getBusinessunitY(currentBU.name);
		drawFocusRect(canvas, elementX, elementY, 1, 1);
	}
}
