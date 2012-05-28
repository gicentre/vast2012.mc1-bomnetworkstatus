package org.gicentre.vast2012.bomnetworkstatus.ui.bugview;

import org.gicentre.vast2012.bomnetworkstatus.Businessunit;
import org.gicentre.vast2012.bomnetworkstatus.CompactTimestamp;
import org.gicentre.vast2012.bomnetworkstatus.Facility;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroup;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroupStatus;
import org.gicentre.vast2012.bomnetworkstatus.ui.BusinessunitGrid;

import processing.core.PGraphics;

public class OverallView extends CommonBUGView {

	public OverallView(BusinessunitGrid grid) {
		super(grid);
	}

	public void drawBusinessunit(PGraphics canvas, String businessunitName, Thread thread) {
		float offsetX = bug.getColX(bug.getCol(businessunitName));
		float offsetY = bug.getColY(bug.getRow(businessunitName));

		Businessunit bu = bug.getBusinessunits().get(businessunitName);

		canvas.noStroke();

		// The value that will be drawn
		if (thread.isInterrupted())
			return;

		for (short t = 191; t >= 0; t--) {

			// The value that will be drawn
			if (thread.isInterrupted())
				return;

			if (CompactTimestamp.isWithin48HrsWindow(t)) {

				int values[] = new int[6];
				int total = 0;
				int requiredHeight = bug.getBusinessunitHeight(bu) - 1;

				for (Facility f : bu.sortedFacilities) {
					MachineGroup mg = f.machinegroups[currentMachineGroup];
					MachineGroupStatus mgs = mg.statuses[t];

					total += mg.machinecount;
					for (int i = 0; i < 6; i++) {
						if (currentParameter == P_ACTIVITYFLAG)
							values[i] += mgs.countByActivityFlag[i];
						else if (currentParameter == P_POLICYSTATUS)
							values[i] += mgs.countByPolicyStatus[i];
					}

				}

				if (total > 0) {
					int[] heightInPx = new int[6];

					int iOfMaxHeight = 0;
					int maxHeight = 0;
					int sumHeight = 0;
					int currentValue = 0;

					for (int i = 0; i <= 5; i++) {
						currentValue = currentParameter == P_ACTIVITYFLAG ? values[i] : values[i];
						heightInPx[i] = (int) Math.round(requiredHeight * 1f * currentValue / total);
						// Making sure there is at least 1 px if there is at least 1 machine having such value
						if (heightInPx[i] == 0 && currentValue > 0)
							heightInPx[i] = 1;

						sumHeight += heightInPx[i];
						if (heightInPx[i] > maxHeight) {
							maxHeight = heightInPx[i];
							iOfMaxHeight = i;
						}
					}

					// Making the biggest bar a bit smaller/larger if needed to make sure the sum of width is equal requiredHeight
					if (sumHeight > requiredHeight || sumHeight < requiredHeight)
						heightInPx[iOfMaxHeight] -= sumHeight - requiredHeight;

					// Drawing the bars
					int offsetY2 = 0;
					int offsetX2 = t;
					for (int i = 0; i <= 5; i++) {
						canvas.fill(getColour(currentParameter, 5-i));
						canvas.rect(offsetX + offsetX2, offsetY + offsetY2, 1, heightInPx[5-i]);
						offsetY2 += heightInPx[5-i];
					}
				}
			}
		}
	}

	public void selectAt(int x, int y) {
	}

	public void highlightSelectedElement(PGraphics canvas, Thread thread) {
	}

	public void drawLegend(PGraphics canvas, float x, float y, float width, float height) {
	}

}
