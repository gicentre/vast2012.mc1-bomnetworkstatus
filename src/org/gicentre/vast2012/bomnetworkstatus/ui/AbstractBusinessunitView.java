package org.gicentre.vast2012.bomnetworkstatus.ui;

import java.awt.Font;

import org.gicentre.utils.colour.ColourTable;
import org.gicentre.vast2012.bomnetworkstatus.Businessunit;
import org.gicentre.vast2012.bomnetworkstatus.Facility;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

public abstract class AbstractBusinessunitView {

	public static final int P_ACTIVITYFLAG = 0x10;
	public static final int P_POLICYSTATUS = 0x11;
	public static final int P_CONNECTIONS = 0x12;

	public static final int V_COUNT = 0;
	public static final int V_MIN = 1;
	public static final int V_MAX = 2;
	public static final int V_AVG = 3;
	public static final int V_SD = 4;
	
	protected static PFont captionFont = new PFont(new Font("Helvetica", 0, 20), true);
	
	public int currentMachineGroup; // 0, 1, 2, 3
	public int currentParameter; // 0, 1, 2 (P_XXX)
	public int currentValue; // 0-5 or V_XXX
	public short currentCompactTimestamp;
	public boolean timeIsRelative;

	public Facility focusedFacility;
	public short focusedCompactTimestamp;
	public boolean focusIsLocked;

	protected BusinessunitGrid bug;
	ColourTable activityFlagCT = ColourTable.getPresetColourTable(ColourTable.SET1_6, 0, 5);
	ColourTable policyStatusCT = ColourTable.getPresetColourTable(ColourTable.SET2_6, 0, 5);
	ColourTable connectionCT = ColourTable.getPresetColourTable(ColourTable.SET3_5, 0, 4);
	
	public AbstractBusinessunitView(BusinessunitGrid grid) {
		bug = grid;
	}
	
	public void draw(PGraphics canvas, Thread thread) {
		drawLayout(canvas);
		for (int i = 0; i < bug.getColCount(); i++)
			for (int j = 0; j < bug.getRowCount(); j++)
				if (bug.getBuName(i, j) != null)
					drawBusinessunit(canvas, bug.getBuName(i, j), thread);
		drawCaptions(canvas);
	}
	
	protected void drawLayout(PGraphics canvas) {
		canvas.background(240);
		//canvas.rect(bug.x, bug.y, bug.getWidth(), bug.getHeight());
		//canvas.noStroke();
		//for (int i = 0; i < bug.getColCount(); i++)
		//	for (int j = 0; j < bug.getRowCount(); j++)
		//		canvas.rect(bug.getBuX(i), bug.getBuY(j), bug.getColWidth(), bug.getRowHeight());
	}

	private void drawCaptions(PGraphics canvas) {
		canvas.textAlign(canvas.CENTER, canvas.CENTER);
		canvas.textFont(captionFont);
		
		for (int i = 0; i < bug.getColCount(); i++)
			for (int j = 0; j < bug.getRowCount(); j++) {
				Businessunit bu = bug.getBu(i, j);
				if (bu == null)
					continue;
				canvas.fill(0, 50);
				canvas.text(bu.name, bug.getBuX(i) + bug.getColWidth()/2, bug.getBuY(j) + bug.getRowHeight()/2);
				canvas.fill(255, 50);
				canvas.text(bu.name, bug.getBuX(i) + bug.getColWidth()/2, bug.getBuY(j) + bug.getRowHeight()/2);
			}
	}
	
	protected void drawFocusRect(PGraphics p, int x, int y, int w, int h) {
		p.noFill();
		p.stroke(0, 128);
		p.rect(x-1, y-1, w+2, h+2);
	}
	
	protected abstract void drawBusinessunit(PGraphics canvas, String businessunitName, Thread thread);
	
	public abstract void focusOn (int mouseX, int mouseY);
	public abstract void highlightFocusedElement(PGraphics canvas);
}
