package org.gicentre.vast2012.bomnetworkstatus.ui.bugview;

import processing.core.PGraphics;

import org.gicentre.utils.move.ZoomPanState;
import org.gicentre.vast2012.bomnetworkstatus.Facility;

/**
 * Abstract Business unit grid view, parent class for all three views
 * 
 * @author Alexander Kachkaev <alexander.kachkaev.1@city.ac.uk>
 */

/* 
 * This file is part of BoM Network Status Application, VAST 2012 Mini Challenge 1 entry
 * awarded for "Efficient Use of Visualization". It is free software: you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License 
 * by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * BoM Network Status is distributed WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this
 * source code (see COPYING.LESSER included with this source code). If not, see 
 * http://www.gnu.org/licenses/.
 * 
 * For report on challenge, video and summary paper see http://gicentre.org/vast2012/
 */

public abstract class AbstractBUGView {

	public static final int P_ACTIVITYFLAG = 0x10;
	public static final int P_POLICYSTATUS = 0x11;
	public static final int P_CONNECTIONS = 0x12;

	public static final int V_CONN_COUNT = 0;
	public static final int V_CONN_MIN = 1;
	public static final int V_CONN_MAX = 2;
	public static final int V_CONN_AVG = 3;
	public static final int V_CONN_SD = 4;
	
	public boolean rangeIsLocked = false;
	public double rangeMin = 0;
	public double rangeMax = 1;
	public double rangeMinLimit = 0;
	public double rangeMaxLimit = 1;
	
	public boolean rangeIsAbsolute = false;
	
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
	public abstract void draw(PGraphics canvas, ZoomPanState zps, Thread thread);

	public abstract void drawBusinessunit(PGraphics canvas, String businessunitName, ZoomPanState zps, Thread thread);

	/**
	 * Sets facility and time stamp that are located at x and y focused
	 * (see focusedFacility, focusedCompactTimestamp)
	 */
	public abstract void selectAt(int x, int y);

	public abstract void highlightSelectedElement(PGraphics canvas, Thread thread);

	public abstract void drawLegend(PGraphics canvas, float x, float y, float width, float height);

	public abstract int getColour(int parameter, int value);
	
	/**
	 * If selectedFacility is not null, changes the selection so that another facility located at diff from the original is selected
	 */
	public abstract boolean selectNeighbourFacility(int diff);

	/**
	 * Changes selected timestamp (increments/decrements it by diff)
	 */
	public abstract boolean selectNeighbourTimestamp(int diff);
	
	public abstract void resetRange();

	public abstract int getConnectionsColour(PGraphics canvas, int numConnections);
}
