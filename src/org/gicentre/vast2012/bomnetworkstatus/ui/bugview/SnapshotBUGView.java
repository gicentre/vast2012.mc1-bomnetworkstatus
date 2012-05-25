package org.gicentre.vast2012.bomnetworkstatus.ui.bugview;

import org.gicentre.utils.colour.ColourTable;
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

		// Picking colours
		ColourTable currentCT;
		switch (currentParameter) {
		case P_ACTIVITYFLAG:
			currentCT = activityFlagCT;
			break;
		case P_POLICYSTATUS:
			currentCT = policyStatusCT;
			break;
		default:
			currentCT = connectionCT;
			break;
		}

		for (Facility f : bu.sortedFacilities) {
			MachineGroup mg = f.machinegroups[currentMachineGroup];

			currentY += getGapBetweenFacilities(prevFacility, f);
			currentFacilityHeight = getFacilityHeight(f);

			short ts = currentCompactTimestamp;

			// Offsetting time by timezone if time is relative
			if (timeIsRelative)
				ts = (short) (ts - f.timezoneOffset * 4 - 4 * 3);

			if (thread.isInterrupted())
				return;

			if (CompactTimestamp.isWithin48HrsWindow(ts)) {
				MachineGroupStatus mgs = mg.statuses[ts];

				if (currentParameter == P_ACTIVITYFLAG || currentParameter == P_POLICYSTATUS) {
					canvas.noStroke();
					int offsetX2 = 0;
					canvas.fill((mgs == null || mg.machinecount == 0) ? FILL_NODATA : 255);
					canvas.rect(offsetX, offsetY + currentY, BusinessunitGrid.COL_WIDTH, currentFacilityHeight);

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
							canvas.fill(currentCT.findColour(i) & 0x88ffffff); // add ≈ 50 alpha
							canvas.rect(offsetX + offsetX2, offsetY + currentY, widthInPx[i], currentFacilityHeight);
							offsetX2 += widthInPx[i];
						}
					}
				}
			}
			currentY += currentFacilityHeight;
			prevFacility = f;
		}
	}

	public void highlightSelectedElement(PGraphics canvas, Thread thread) {
	}
}
