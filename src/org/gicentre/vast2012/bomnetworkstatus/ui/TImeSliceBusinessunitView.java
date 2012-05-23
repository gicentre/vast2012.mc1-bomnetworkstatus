package org.gicentre.vast2012.bomnetworkstatus.ui;

import org.gicentre.utils.colour.ColourTable;
import org.gicentre.vast2012.bomnetworkstatus.Businessunit;
import org.gicentre.vast2012.bomnetworkstatus.CompactTimestamp;
import org.gicentre.vast2012.bomnetworkstatus.Facility;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroup;
import org.gicentre.vast2012.bomnetworkstatus.MachineGroupStatus;

import processing.core.PApplet;
import processing.core.PGraphics;

public class TImeSliceBusinessunitView extends AbstractBusinessunitView{
	
	public TImeSliceBusinessunitView(BusinessunitGrid grid) {
		super(grid);
	}
	
	public void drawBusinessunit(PGraphics canvas, String businessunitName, Thread thread) {
		float offsetX = bug.getBuX(bug.getCol(businessunitName));
		float offsetY = bug.getBuY(bug.getRow(businessunitName));
		
		Businessunit bu = bug.getBusinessunits().get(businessunitName);
		
		int row = 0;
		
		int colourMax = 0;
		int colourMin = canvas.color(255, 255, 255);
		
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
		
		//System.out.println(colourMax);

		for (Facility f : bu.sortedFacilities) {
			MachineGroup mg = f.machinegroups[currentMachineGroup];
			
			short ts = currentCompactTimestamp;

			// Offsetting time by timezone if time is relative
			if (timeIsRelative)
				ts = (short)(ts - f.timezoneOffset*4 - 4*3);
			
			// The value that will be drawn
			float norm = -1;
			float value = -1;
			
			if (thread.isInterrupted())
				return;
			
			for (int t = 191; t >=0; t--) {
				if (CompactTimestamp.isWithin48HrsWindow(ts)) {
					MachineGroupStatus mgs = mg.statuses[ts];
	
					if (mgs != null) {
						switch (currentParameter) {
						case P_ACTIVITYFLAG:
							value = mgs.countByActivityFlag[currentValue];
							norm = mg.machinecount;
							break;
						case P_POLICYSTATUS:
							value = mgs.countByPolicyStatus[currentValue];
							norm = mg.machinecount;
							break;
						case P_CONNECTIONS:
							value = mgs.connections[currentValue];
							break;
						}
					}
				}
				
				// Drawing a point
				// No data
				if (value == -1 || norm == -1)
					canvas.stroke(220);
				// Data
				else
					canvas.stroke(canvas.lerpColor(colourMin, colourMax, Math.min(1, Math.max(0, value/norm))));
				canvas.point(offsetX + t, offsetY + row);

				ts--; // Subtracting 15 minutes
			}
			row++;
		}
	}
	
	public void focusOn(int mouseX, int mouseY) {
	}
	
	public void highlightFocusedElement(PGraphics canvas) {
	}
}
