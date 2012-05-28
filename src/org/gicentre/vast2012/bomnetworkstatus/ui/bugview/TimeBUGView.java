package org.gicentre.vast2012.bomnetworkstatus.ui.bugview;

import org.gicentre.vast2012.bomnetworkstatus.Businessunit;
import org.gicentre.vast2012.bomnetworkstatus.CompactTimestamp;
import org.gicentre.vast2012.bomnetworkstatus.Facility;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroup;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroupStatus;
import org.gicentre.vast2012.bomnetworkstatus.ui.BusinessunitGrid;

import processing.core.PGraphics;

public class TimeBUGView extends CommonBUGView {

	public boolean rangeIsLocked = false;
	public float rangeMin = 0;
	public float rangeMax = 1;
	public float rangeMinLimit = 0;
	public float rangeMaxLimit = 1;
	
	public boolean rangeIsAbsolute = false;
	
	public TimeBUGView(BusinessunitGrid grid) {
		super(grid);
	}

	public void drawBusinessunit(PGraphics canvas, String businessunitName, Thread thread) {
		float offsetX = bug.getColX(bug.getCol(businessunitName));
		float offsetY = bug.getColY(bug.getRow(businessunitName));

		Businessunit bu = bug.getBusinessunits().get(businessunitName);

		int currentY = 0;
		int currentFacilityHeight;
		Facility prevFacility = null;

		int colourMax = getColour(currentParameter, currentValue, false);
		int colourMin = canvas.color(255, 255, 255);
		int tsWidth = 1;

		canvas.noStroke();

		for (Facility f : bu.sortedFacilities) {
			MachineGroup mg = f.machinegroups[currentMachineGroup];

			currentY += bug.getGapBetweenFacilities(prevFacility, f);
			currentFacilityHeight = bug.getFacilityHeight(f);

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
					canvas.fill(canvas.lerpColor(colourMin, colourMax, Math.min(1, Math.max(0, ((value / valueBase) - rangeMin) / (rangeMax - rangeMin)))));
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
		selectedCompactTimestamp = (short) (x - bug.getBusinessunitX(selectedFacility.businessunitName) + this.currentCompactTimestamp - 191);
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

		Facility prevFacility = null;
		for (Facility f : currentBU.sortedFacilities) {
			elementY += bug.getGapBetweenFacilities(prevFacility, f);
			if (f == selectedFacility)
				break;
			elementY += bug.getFacilityHeight(f);
			prevFacility = f;
		}

		if (elementX < 0 || elementX >= 192)
			return;
		
		elementX += (int) bug.getBusinessunitX(currentBU.name);
		elementY += (int) bug.getBusinessunitY(currentBU.name);
		drawSelectionHighlighter(canvas, elementX, elementY, 1, bug.getFacilityHeight(selectedFacility));
	}

	public void drawLegend(PGraphics canvas, float x, float y, float width, float height) {
		canvas.pushMatrix();
		canvas.translate(x, y);
		
		String t1, t2;
		
		if (rangeIsAbsolute) {
			t1 = String.valueOf((int)rangeMin);
			t2 = String.valueOf((int)rangeMax);
		} else {
			t1 = String.valueOf((int)(rangeMin*100)) + "%";
			t2 = String.valueOf((int)(rangeMax*100)) + "%";
		}
		
		canvas.fill(120);
		canvas.textAlign(PGraphics.LEFT, PGraphics.TOP);
		canvas.text(t1, 0, 0);
		canvas.textAlign(PGraphics.RIGHT, PGraphics.TOP);
		canvas.text(t2, width, 0);
		
		canvas.noStroke();
		int colourMax = getColour(currentParameter, currentValue, false);
		int colourMin = canvas.color(255, 255, 255);
		
		for (float i = 0; i< width; i++) {
			canvas.fill(canvas.lerpColor(colourMin, colourMax,i/width));
			canvas.rect(i, 16, 1, height-16);
		}
		
		canvas.popMatrix();
	}


}
