package org.gicentre.vast2012.statusgrid.ui;

import org.gicentre.vast2012.statusgrid.Businessunit;
import org.gicentre.vast2012.statusgrid.BusinessunitGrid;

import processing.core.PApplet;

public abstract class AbstractBusinessunitView {
	
	protected BusinessunitGrid bug;
	protected PApplet a;
	
	public AbstractBusinessunitView(PApplet applet, BusinessunitGrid grid) {
		bug = grid;
		a = applet;
	}
	
	public void draw() {
		drawLayout();
		for (int i = 0; i < bug.getColCount(); i++)
			for (int j = 0; j < bug.getRowCount(); j++)
				if (bug.getBuName(i, j) != null)
					drawBusinessunit(bug.getBuName(i, j));
	}
	
	protected void drawLayout() {
		a.fill(240);
		a.rect(bug.x, bug.y, bug.getWidth(), bug.getHeight());
		a.noStroke();
		for (int i = 0; i < bug.getColCount(); i++)
			for (int j = 0; j < bug.getRowCount(); j++)
				a.rect(bug.getBuX(i), bug.getBuY(j), bug.getColWidth(), bug.getRowHeight());
	}

	protected abstract void drawBusinessunit(String businessunitName);
	
	public abstract String getMouseInfo(int mouseX, int mouseY);
}
