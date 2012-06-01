package org.gicentre.vast2012.bomnetworkstatus.ui.bugview;

import org.gicentre.vast2012.bomnetworkstatus.Businessunit;
import org.gicentre.vast2012.bomnetworkstatus.CompactTimestamp;
import org.gicentre.vast2012.bomnetworkstatus.Facility;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroup;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroupStatus;
import org.gicentre.vast2012.bomnetworkstatus.ui.BusinessunitGrid;

import processing.core.PGraphics;

public class SnapshotBUGView extends CommonBUGView {

	public SnapshotBUGView(BusinessunitGrid grid) {
		super(grid);
	}

	public void drawBusinessunit(PGraphics canvas, String businessunitName, Thread thread) {
		float offsetX = bug.getColX(bug.getCol(businessunitName));
		float offsetY = bug.getColY(bug.getRow(businessunitName));

		Businessunit bu = bug.getBusinessunits().get(businessunitName);

		int currentY = 0;
		int currentFacilityHeight;
		Facility prevFacility = null;

		for (Facility f : bu.sortedFacilities) {
			MachineGroup mg = f.machinegroups[currentMachineGroup];

			currentY += bug.getGapBetweenFacilities(prevFacility, f);
			currentFacilityHeight = bug.getFacilityHeight(f);

			short ts = currentCompactTimestamp;

			// Offsetting time by timezone if time is relative
			if (timeIsRelative)
				ts = (short) (ts - f.timezoneOffset * 4 - 4 * 3);

			if (thread.isInterrupted())
				return;

			if (CompactTimestamp.isWithin48HrsWindow(ts)) {
				MachineGroupStatus mgs = mg.statuses[ts];

				canvas.fill((mgs == null || mg.machinecount == 0) ? FILL_NODATA : 255);
				canvas.rect(offsetX, offsetY + currentY, BusinessunitGrid.COL_WIDTH, currentFacilityHeight);

				if (mgs != null && mg.machinecount > 0) {
					canvas.noStroke();
					if (currentParameter == P_ACTIVITYFLAG || currentParameter == P_POLICYSTATUS) {
						// Activity flag / policy status
						int offsetX2 = 0;

						if (mgs != null && mg.machinecount > 0) {
							// Calculating widths of bars
							int[] widthInPx = new int[6];
							int iOfMaxWidth = 0;
							int maxWidth = 0;
							int sumWidth = 0;
							int currentValue = 0;
							for (int i = 0; i <= 5; i++) {
								currentValue = currentParameter == P_ACTIVITYFLAG ? mgs.countByActivityFlag[i] : mgs.countByPolicyStatus[i];
								widthInPx[i] = (int) Math.round(BusinessunitGrid.COL_WIDTH * 1f * currentValue / mg.machinecount);
								// Making sure there is at least 1 px if there is at least 1 machine having such value
								if (widthInPx[i] == 0 && currentValue > 0)
									widthInPx[i] = 1;

								sumWidth += widthInPx[i];
								if (widthInPx[i] > maxWidth) {
									maxWidth = widthInPx[i];
									iOfMaxWidth = i;
								}
							}

							// Making the biggest bar a bit smaller/larger if needed to make sure the sum of width is equal COL_WIDTH
							if (sumWidth > BusinessunitGrid.COL_WIDTH || sumWidth < BusinessunitGrid.COL_WIDTH)
								widthInPx[iOfMaxWidth] -= sumWidth - BusinessunitGrid.COL_WIDTH;

							// Drawing the bars
							for (int i = 0; i <= 5; i++) {
								canvas.fill(getColour(currentParameter, i));
								canvas.rect(offsetX + offsetX2, offsetY + currentY, widthInPx[i], currentFacilityHeight);
								offsetX2 += widthInPx[i];
							}
						}
					} else {
						// Connections
						int minX = (int) ((mgs.connections[2] - rangeMin) / (rangeMax - rangeMin) * 192);
						int maxX = (int) ((mgs.connections[3] - rangeMin) / (rangeMax - rangeMin) * 192);
						int avgX = (int) ((mgs.connections[0] - rangeMin) / (rangeMax - rangeMin) * 192);
						int sdX = (int) ((mgs.connections[1]) / (rangeMax - rangeMin) * 192);

						// Min - Max
						float minXCorrected = offsetX + Math.max(Math.min(minX, 191), 0);
						float maxXCorrected = offsetX + Math.max(Math.min(maxX, 191), 0);
						canvas.fill(230);
						canvas.rect(minXCorrected, offsetY + currentY, maxXCorrected - minXCorrected, currentFacilityHeight);
						
						// Sd
						canvas.fill(200);
						int sdLeft = Math.max(Math.min(avgX - sdX, 191), 0);
						int sdRight = Math.max(Math.min(avgX + sdX, 191), 0);
						canvas.rect(offsetX + sdLeft, offsetY + currentY, sdRight - sdLeft, currentFacilityHeight);

						// Avg
						canvas.fill(0);
						canvas.rect(offsetX + Math.max(Math.min(avgX, 191), 0), offsetY + currentY, 1, currentFacilityHeight);
					}
				}
			}
			currentY += currentFacilityHeight;
			prevFacility = f;
		}
	}

	public void highlightSelectedElement(PGraphics canvas, Thread thread) {
		if (selectedFacility == null)
			return;

		int elementX = 0;
		int elementY = 0;

		Businessunit currentBU = bug.getBusinessunits().get(selectedFacility.businessunitName);

		Facility prevFacility = null;
		for (Facility f : currentBU.sortedFacilities) {
			elementY += bug.getGapBetweenFacilities(prevFacility, f);
			if (f == selectedFacility)
				break;
			elementY += bug.getFacilityHeight(f);
			prevFacility = f;
		}

		elementX += bug.getBusinessunitX(currentBU.name);
		elementY += bug.getBusinessunitY(currentBU.name);
		drawSelectionHighlighter(canvas, elementX, elementY, 192, bug.getFacilityHeight(selectedFacility));
	}

	public void drawLegend(PGraphics canvas, float x, float y, float width, float height) {
		if (currentParameter == P_ACTIVITYFLAG || currentParameter == P_POLICYSTATUS) {
			drawSequentialLegend(canvas, x, y, width, height);
		} else {
			drawStatsLegend(canvas, x, y, width, height);
		}
	}
}
