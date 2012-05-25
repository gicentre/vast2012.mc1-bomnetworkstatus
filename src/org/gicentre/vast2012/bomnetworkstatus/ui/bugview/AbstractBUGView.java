package org.gicentre.vast2012.bomnetworkstatus.ui.bugview;

import processing.core.PGraphics;
import org.gicentre.vast2012.bomnetworkstatus.Facility;

public abstract class AbstractBUGView {

	public static final int P_ACTIVITYFLAG = 0x10;
	public static final int P_POLICYSTATUS = 0x11;
	public static final int P_CONNECTIONS = 0x12;

	public static final int V_CONN_COUNT = 0;
	public static final int V_CONN_MIN = 1;
	public static final int V_CONN_MAX = 2;
	public static final int V_CONN_AVG = 3;
	public static final int V_CONN_SD = 4;
	
	public int currentMachineGroup; // 0, 1, 2, 3
	public int currentParameter; // 0, 1, 2 (P_XXX)
	public int currentValue; // 0-5 or V_XXX
	public short currentCompactTimestamp;
	public boolean timeIsRelative;

	public Facility selectedFacility;
	public short selectedCompactTimestamp;
	public boolean selectionIsLocked;
	
	public boolean showLabels = true;

	/**
	 * Draws the view
	 * @param canvas
	 * @param thread The thread the drawing is done in, can be null
	 *        If the thread becomes interrupted, it is possible to break the drawing too.
	 */
	public abstract void draw(PGraphics canvas, Thread thread);

	/**
	 * Sets facility and time stamp that are located at x and y focused
	 * (see focusedFacility, focusedCompactTimestamp)
	 */
	public abstract void selectAt(int x, int y);

	public abstract void highlightSelectedElement(PGraphics canvas, Thread thread);

	public abstract int getColour(int parameter, int value);
	
	/**
	 * If selectedFacility is not null, changes the selection so that another facility located at diff from the original is selected
	 */
	public abstract boolean selectNeighbourFacility(int diff);

	/**
	 * Changes selected timestamp (increments/decrements it by diff)
	 */
	public abstract boolean selectNeighbourTimestamp(int diff);

}
