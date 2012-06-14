package org.gicentre.vast2012.bomnetworkstatus.ui.bugview;

import org.gicentre.utils.move.ZoomPanState;
import org.gicentre.vast2012.bomnetworkstatus.Businessunit;
import org.gicentre.vast2012.bomnetworkstatus.CompactTimestamp;
import org.gicentre.vast2012.bomnetworkstatus.Facility;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroup;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroupStatus;
import org.gicentre.vast2012.bomnetworkstatus.ui.BusinessunitGrid;

import processing.core.PGraphics;

public class OverallBUGView extends CommonBUGView {

	public OverallBUGView(BusinessunitGrid grid) {
		super(grid);
	}

	public void drawBusinessunit(PGraphics canvas, String businessunitName, ZoomPanState zps, Thread thread) {
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
				double requiredHeight = bug.getBusinessunitHeight(bu) - 1;

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
					double[] heightInPx = new double[6];

					int iOfMaxHeight = 0;
					double maxHeight = 0;
					double sumHeight = 0;
					double currentValue = 0;

					for (int i = 0; i <= 5; i++) {
						currentValue = currentParameter == P_ACTIVITYFLAG ? values[i] : values[i];
						heightInPx[i] = requiredHeight *  currentValue / total;
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
					double offsetY2 = 0;
					double offsetX2 = t;
					for (int i = 0; i <= 5; i++) {
						canvas.fill(getColour(currentParameter, 5-i));
						canvas.rect((float)(offsetX + offsetX2), (float)(offsetY + offsetY2), 1, (float)heightInPx[5-i]);
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
		drawSequentialLegend(canvas, x, y, width, height);
	}

}
