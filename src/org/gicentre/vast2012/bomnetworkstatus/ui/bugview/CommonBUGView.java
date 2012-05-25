package org.gicentre.vast2012.bomnetworkstatus.ui.bugview;

import java.awt.Color;
import java.awt.Font;

import org.gicentre.utils.colour.ColourTable;
import org.gicentre.vast2012.bomnetworkstatus.Businessunit;
import org.gicentre.vast2012.bomnetworkstatus.Facility;
import org.gicentre.vast2012.bomnetworkstatus.ui.BusinessunitGrid;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

public abstract class CommonBUGView extends AbstractBUGView {

	protected static PFont captionFont = new PFont(new Font("Helvetica", 0, 20), true);
	protected BusinessunitGrid bug;

	ColourTable activityFlagCT;
	ColourTable policyStatusCT;
	ColourTable connectionCT;
	
	protected static final int FILL_NODATA = 0; // Colour used when no data presents

	public CommonBUGView(BusinessunitGrid grid) {
		bug = grid;

		// Initializing colour tables
		if (activityFlagCT == null) {
			activityFlagCT = ColourTable.getPresetColourTable(ColourTable.SET1_6, 0, 5);
			policyStatusCT = new ColourTable();
			policyStatusCT.addContinuousColourRule(0, Color.HSBtoRGB(0, 0, 0));
			policyStatusCT.addContinuousColourRule(1, Color.HSBtoRGB(.33f, .8f, .6f));
			policyStatusCT.addContinuousColourRule(2, Color.HSBtoRGB(.2f, .8f, .6f));
			policyStatusCT.addContinuousColourRule(3, Color.HSBtoRGB(.15f, .8f, .6f));
			policyStatusCT.addContinuousColourRule(4, Color.HSBtoRGB(.1f, .8f, .6f));
			policyStatusCT.addContinuousColourRule(5, Color.HSBtoRGB(.0f, .8f, .6f));
			connectionCT = ColourTable.getPresetColourTable(ColourTable.SET3_5, 0, 4);

		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void draw(PGraphics canvas, Thread thread) {
		drawLayout(canvas);
		for (int i = 0; i < bug.getColCount(); i++)
			for (int j = 0; j < bug.getRowCount(); j++)
				if (bug.getBusinessunitNameAt(i, j) != null)
					drawBusinessunit(canvas, bug.getBusinessunitNameAt(i, j), thread);
		drawCaptions(canvas);
	}

	/**
	 * Draws the layout of the grid
	 */
	protected void drawLayout(PGraphics canvas) {
		canvas.background(240);
		// canvas.rect(bug.x, bug.y, bug.getWidth(), bug.getHeight());
		// canvas.noStroke();
		// for (int i = 0; i < bug.getColCount(); i++)
		// for (int j = 0; j < bug.getRowCount(); j++)
		// canvas.rect(bug.getBuX(i), bug.getBuY(j), bug.getColWidth(), bug.getRowHeight());
	}

	/**
	 * Draws the captions of the business units
	 */
	private void drawCaptions(PGraphics canvas) {
		canvas.textAlign(PApplet.CENTER, PApplet.CENTER);
		canvas.textFont(captionFont);

		for (int i = 0; i < bug.getColCount(); i++)
			for (int j = 0; j < bug.getRowCount(); j++) {
				Businessunit bu = bug.getBusinessunitAt(i, j);
				if (bu == null)
					continue;
				canvas.fill(0, 50);
				canvas.text(bu.name, bug.getColX(i) + BusinessunitGrid.COL_WIDTH / 2, bug.getColY(j) + BusinessunitGrid.ROW_HEIGHT / 2);
				canvas.fill(255, 50);
				canvas.text(bu.name, bug.getColX(i) + BusinessunitGrid.COL_WIDTH / 2, bug.getColY(j) + BusinessunitGrid.ROW_HEIGHT / 2);
			}
	}

	protected void drawFocusRect(PGraphics p, int x, int y, int w, int h) {
		p.noFill();
		p.stroke(0, 128);
		p.rect(x - 1, y - 1, w + 2, h + 2);
	}

	/**
	 * Returns businessunit height in pixels
	 * - branch (≈100 machines): 1
	 * - headqaurters
	 *     small regions (≈500 machines): 3
	 *     large regions (≈21K machines): 11
	 *     headquarters (≈ 15K machines): 8
	 * - datacenter (≈50K machines): 15
	 * 
	 */
	protected int getBusinessunitHeight(Businessunit businessunit) {
		if (businessunit.name == "headquarters") {
			return 42;
		} else {
			if (Businessunit.extractIdFromName(businessunit.name) <= 10)
				return 200 + 2 + 11;
			else
				return 50 + 2 + 3;
		}
	}
	
	/**
	 * Returns facility height in pixels
	 * - branch (≈100 machines): 1
	 * - headqaurters
	 *     small regions (≈500 machines): 3
	 *     large regions (≈21K machines): 11
	 *     headquarters (≈ 15K machines): 8
	 * - datacenter (≈50K machines): 15
	 * 
	 */
	protected int getFacilityHeight(Facility f) {
		switch (f.facilityName.charAt(0)) {
		case 'b':
			return 1;
		case 'h':
			int buId = Businessunit.extractIdFromName(f.businessunitName);
			if (buId == 0) {
				return 8;
			} else if (buId <= 10) {
				return 11;
			} else {
				return 3;
			}
		case 'd':
			return 15;
		}

		return -1;
	}

	/**
	 * Returns the size of the gap in pixels between two facilities
	 * Only headquarters are surrounded by a gap of 1 pixel.
	 */
	protected int getGapBetweenFacilities(Facility f1, Facility f2) {
		if (f1 == null || f2 == null)
			return 0;

		char l1 = f1.facilityName.charAt(0);
		char l2 = f2.facilityName.charAt(0);

		if (l1 == 'h' || l2 == 'h')
			return 1;
		else
			return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public void selectAt(int x, int y) {
		// Resetting selected facility
		selectedFacility = null;

		// Looking for a businessunit
		Businessunit selectedBU = null;
		for (int i = 0; i < bug.getColCount(); i++)
			for (int j = 0; j < bug.getRowCount(); j++)
				if (bug.getBusinessunitAt(i, j) != null && x >= bug.getColX(i) && x < bug.getColX(i) + BusinessunitGrid.COL_WIDTH && y >= bug.getColY(j)
						&& y < bug.getColY(j) + getBusinessunitHeight(bug.getBusinessunitAt(i, j))) {
					selectedBU = bug.getBusinessunitAt(i, j);
					break;
				}
		if (selectedBU == null)
			return;

		// Looking for a facility
		int localY = y - bug.getBusinessunitY(selectedBU.name);
		int currentY = 0;
		Facility prevFacility = null;
		for (Facility f: selectedBU.sortedFacilities) {
			currentY += getGapBetweenFacilities(prevFacility, f);
			
			// y is in the gap
			if (currentY > localY)
				return;
			currentY += getFacilityHeight(f);
			
			// y is above the current facility
			if (currentY > localY) {
				selectedFacility = f;
				return;
			}
			prevFacility = f;
		}
	}

	protected abstract void drawBusinessunit(PGraphics canvas, String businessunitName, Thread thread);

	public int getColour(int parameter, int value) {
		ColourTable ct;
		switch (currentParameter) {
		case P_ACTIVITYFLAG:
			ct = activityFlagCT;
			break;
		case P_POLICYSTATUS:
			ct = policyStatusCT;
			break;
		default:
			ct = connectionCT;
			break;
		}
		
		return PApplet.lerpColor(ct.findColour(value), 0x00ffffff, 0.5f, PApplet.BLEND);
	}

}
