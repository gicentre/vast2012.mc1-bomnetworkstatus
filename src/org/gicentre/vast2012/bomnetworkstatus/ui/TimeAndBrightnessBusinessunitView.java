package org.gicentre.vast2012.bomnetworkstatus.ui;

import org.gicentre.utils.colour.ColourTable;
import org.gicentre.vast2012.bomnetworkstatus.Businessunit;
import org.gicentre.vast2012.bomnetworkstatus.CompactTimestamp;
import org.gicentre.vast2012.bomnetworkstatus.Facility;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroup;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroupStatus;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.NoFixedFacet;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class TimeAndBrightnessBusinessunitView extends AbstractBusinessunitView {

	public float baseRangeMin = 0;
	public float baseRangeMax = 1;

	public TimeAndBrightnessBusinessunitView(BusinessunitGrid grid) {
		super(grid);
	}

	public void drawBusinessunit(PGraphics canvas, String businessunitName,
			Thread thread) {
		float offsetX = bug.getBuX(bug.getCol(businessunitName));
		float offsetY = bug.getBuY(bug.getRow(businessunitName));

		Businessunit bu = bug.getBusinessunits().get(businessunitName);

		int row = 0;

		int colourMax = 0;
		int colourMin = canvas.color(255, 255, 255);
		
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

		// System.out.println(colourMax);

		for (Facility f : bu.sortedFacilities) {
			MachineGroup mg = f.machinegroups[currentMachineGroup];

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
					canvas.stroke(240);
				// Data presents and the base is determined
				else
					canvas.stroke(canvas.lerpColor(colourMin, colourMax, Math
							.min(1, Math.max(0,
									((value / valueBase) - baseRangeMin)
											/ (baseRangeMax - baseRangeMin)))));
				// canvas.point(offsetX + t, offsetY + row);
				canvas.rect(offsetX + t, offsetY + row, 1, 1);
				
				cts--; // Subtracting 15 minutes
			}
			row++;
		}
	}

	public void focusOn(int mouseX, int mouseY) {
		mouseX = mouseX;
		mouseY = mouseY;
		// Dropping focused facility
		focusedCompactTimestamp = -1;
		focusedFacility = null;

		// Looking for a businessunit
		Businessunit focusedBU = null;
		for (int i = 0; i < bug.getColCount(); i++)
			for (int j = 0; j < bug.getRowCount(); j++)
				if (bug.getBu(i, j) != null
						&& mouseX >= bug.getBuX(i)
						&& mouseX < bug.getBuX(i) + bug.getColWidth()
						&& mouseY >= bug.getBuY(j)
						&& mouseY < bug.getBuY(j)
								+ bug.getBu(i, j).facilities.size()) {
					focusedBU = bug.getBu(i, j);
					break;
				}
		if (focusedBU == null)
			return;

		// Looking for a facility and timestamp
		focusedFacility = focusedBU.sortedFacilities.get((int) (mouseY - bug
				.getBuY(focusedBU.name)));
		focusedCompactTimestamp = (short) (mouseX - bug.getBuX(focusedBU.name)
				+ this.currentCompactTimestamp - 191);
		if (this.timeIsRelative)
			focusedCompactTimestamp -= focusedFacility.timezoneOffset * 4 + 4 * 3;
	}
	
	public void highlightFocusedElement(PGraphics canvas) {
		if (focusedFacility == null)
			return;
		
		int elementX = 0;
		int elementY = 0;
		
		Businessunit currentBU = bug.getBusinessunits().get(focusedFacility.businessunit);
		
		elementX += focusedCompactTimestamp + 191 - currentCompactTimestamp;
		if (this.timeIsRelative)
			elementX += focusedFacility.timezoneOffset * 4 + 4 * 3;

		elementY += currentBU.sortedFacilities.indexOf(focusedFacility);
		
		if (elementX < 0 || elementX > 191)
			return;
		
		elementX += (int)bug.getBuX(currentBU.name);
		elementY += (int)bug.getBuY(currentBU.name);
		drawFocusRect(canvas, elementX, elementY, 1, 1);
	}
}
