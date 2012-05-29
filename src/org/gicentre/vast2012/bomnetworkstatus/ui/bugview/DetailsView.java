package org.gicentre.vast2012.bomnetworkstatus.ui.bugview;

import org.gicentre.vast2012.bomnetworkstatus.Facility;
import org.gicentre.vast2012.bomnetworkstatus.MachineDetails;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroupDetails;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroupDetailsCache;

import java.awt.Font;
import processing.core.PFont;
import processing.core.PGraphics;

public class DetailsView {

	private static final int UNIT_SIZE = 5;
	private static final int COL_WIDTH = 28;
	private static final int MARGIN_TOP = 6;

	public Facility currentFacility;
	public short currentCompactTimestamp;
	public int currentMachineGroup;

	protected MachineGroupDetails currentMachineGroupDetails;

	public MachineDetails selectedMachineDetails;

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
		for (int i = 0; i < size; ++i) {
			if (thread.isInterrupted())
				break;

			MachineDetails md = currentMachineGroupDetails.details.get(i);

			if (i == 0 || md.machineFunction != currentMachineGroupDetails.details.get(i - 1).machineFunction) {
				offsetX = COL_WIDTH * md.machineFunction;
				offsetY = 0;
			}

			if (currentMachineGroup == 0 || currentMachineGroup == md.machineGroup) {
				canvas.fill(gridView.getColour(AbstractBUGView.P_POLICYSTATUS, md.policyStatus));
				canvas.rect(offsetX, offsetY, UNIT_SIZE, UNIT_SIZE);
				canvas.fill(gridView.getColour(AbstractBUGView.P_ACTIVITYFLAG, md.activityFlag));
				canvas.rect(offsetX + 1 * UNIT_SIZE, offsetY, UNIT_SIZE, UNIT_SIZE);
				canvas.fill(gridView.getConnectionsColour(canvas, md.numConnections));
				canvas.rect(offsetX + 2 * UNIT_SIZE, offsetY, UNIT_SIZE, UNIT_SIZE);
			}
			offsetY += UNIT_SIZE;
		}
	}

	public void selectAt(int x, int y) {
		y -= MARGIN_TOP;
		selectedMachineDetails = null;

		if (x < 0 || y < 0)
			return;

		if (currentMachineGroupDetails == null || currentMachineGroupDetails.details.size() == 0)
			return;

		try {
			for (int i = 0; i < 9; i++) {
				if (x <= 3 * UNIT_SIZE) {
					for (int m = currentMachineGroupDetails.firstElements[i];; m++) {
						MachineDetails md = currentMachineGroupDetails.details.get(m);
						if (md.machineFunction != i)
							return;
						if (currentMachineGroup != 0 && currentMachineGroup != md.machineGroup)
							continue;
						y -= UNIT_SIZE;
						if (y < UNIT_SIZE) {
							selectedMachineDetails = md;
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
}
