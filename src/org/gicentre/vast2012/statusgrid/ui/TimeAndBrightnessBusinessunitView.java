package org.gicentre.vast2012.statusgrid.ui;

import org.gicentre.vast2012.statusgrid.Businessunit;
import org.gicentre.vast2012.statusgrid.BusinessunitGrid;
import org.gicentre.vast2012.statusgrid.Facility;
import org.gicentre.vast2012.statusgrid.MachineGroup;
import org.gicentre.vast2012.statusgrid.MachineGroupStatus;

import processing.core.PApplet;

public class TimeAndBrightnessBusinessunitView extends AbstractBusinessunitView{
	
	public static final int P_ACTIVITYFLAG = 0x10;
	public static final int P_POLICYSTATUS = 0x11;
	public static final int P_CONNECTIONS = 0x12;

	public static final int V_COUNT = 0;
	public static final int V_MIN = 1;
	public static final int V_MAX = 2;
	public static final int V_AVG = 3;
	public static final int V_SD = 4;
	
	public int currentMachineGroup; // 0, 1, 2, 3
	public int currentParameter; // 0, 1, 2 (P_XXX)
	public int currentValue; // 0-5 or V_XXX
	public short currentCompactTimestamp;
	public boolean timeIsRelative;
	
	public TimeAndBrightnessBusinessunitView(PApplet applet, BusinessunitGrid grid) {
		super(applet, grid);
	}
	
	public void drawBusinessunit(String businessunitName) {
		float offsetX = bug.getBuX(bug.getCol(businessunitName));
		float offsetY = bug.getBuY(bug.getRow(businessunitName));
		
		Businessunit bu = bug.getBusinessunits().get(businessunitName);
		
		int row = 0;
		for (Facility f : bu.facilities.values()) {
			MachineGroup mg = f.machinegroups[currentMachineGroup];
			
			short ts = currentCompactTimestamp;
			
			// Offsetting time by timezone if time is relative
			if (timeIsRelative)
				ts = (short)(ts - f.timezoneOffset*4);
			
			float norm = 100;
			
			for (int t = 191; t >=0; t--) {
				MachineGroupStatus mgs = mg.statuses[ts];

				// The value that will be drawn
				float value = -1;

				if (mgs != null) {
					switch (currentParameter) {
					case P_ACTIVITYFLAG:
						value = mgs.countByActivityFlag[currentValue];
						break;
					case P_POLICYSTATUS:
						value = mgs.countByPolicyStatus[currentValue];
						break;
					case P_CONNECTIONS:
						value = mgs.connections[currentValue];
						break;
					}
				}

				// Drawing a point
				a.stroke(a.lerpColor(255, 0, value/norm));
				//a.println(value);
				//a.stroke(a.lerpColor(255, 0, (float)Math.random()));
				a.point(offsetX + t, offsetY + row);

				ts--; // Subtracting 15 minutes
			}
			row++;
		}
	}
	
	public String getMouseInfo(int mouseX, int mouseY) {
		return "";
	}
}
