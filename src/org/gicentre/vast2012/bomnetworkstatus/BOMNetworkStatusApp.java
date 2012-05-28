package org.gicentre.vast2012.bomnetworkstatus;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.gicentre.utils.gui.HelpScreen;
import org.gicentre.utils.gui.ThreadedDraw;
import org.gicentre.utils.gui.ThreadedGraphicBuffer;
import org.gicentre.utils.move.ZoomPan;
import org.gicentre.utils.move.ZoomPanState;
import org.gicentre.vast2012.bomnetworkstatus.ui.BusinessunitGrid;
import org.gicentre.vast2012.bomnetworkstatus.ui.FlyingText;
import org.gicentre.vast2012.bomnetworkstatus.ui.bugview.AbstractBUGView;
import org.gicentre.vast2012.bomnetworkstatus.ui.bugview.OverallView;
import org.gicentre.vast2012.bomnetworkstatus.ui.bugview.SnapshotBUGView;
import org.gicentre.vast2012.bomnetworkstatus.ui.bugview.TimeBUGView;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;

public class BOMNetworkStatusApp extends PApplet {

	private static final long serialVersionUID = 4320695214813440866L;

	// public static String dbURL = "jdbc:postgresql://localhost/vast_mc1";
	// public static String dbUser = "postgres";
	// public static String dbPassword = "hh";

	public HashMap<String, Businessunit> businessunits;
	public BusinessunitGrid businessunitGrid;
	ZoomPan gridZoomPan;
	ThreadedGraphicBuffer gridGraphicBuffer;
	Rectangle gridGraphicBufferBounds; // screen bounds of the GraphicBuffer

	FlyingText flyingText;
	HelpScreen helpScreen;
	final int HS_HEIGHT = 500;

	TimeBUGView timeView;
	SnapshotBUGView snapshotView;
	OverallView overallView;
	AbstractBUGView currentView;

	PFont titleFont;
	PFont subtitleFont;
	PFont selectedInfoFont;
	PFont bottomRowFont;
	boolean[] keys = new boolean[526];
	String sortText = null;

	public void setup() {

		size(1280, 1000);
		titleFont = createFont("Helvetica", 20);
		subtitleFont = createFont("Helvetica", 16);
		selectedInfoFont = createFont("Arial", 14);
		bottomRowFont = createFont("Arial", 12);
		// smooth();
	}

	int loadStage = 0;

	public void draw() {
		background(255);
		switch (loadStage++) {
		case 0:
			helpScreen = new HelpScreen(this, createFont("Helvetica", 12));
			helpScreen.setBackgroundColour(0xf0fffb9d);
			helpScreen.setIsActive(true);
			helpScreen.setHeader("Bank of Money Network Status", 30, 20);
			helpScreen.putEntry("H", "Show / hide this help");
			helpScreen.addSpacer();
			helpScreen.putEntry("A", "Show activity flag in the grid. Press together with 0-5 in temporal mode to display counts for a particular activity flag value.");
			helpScreen.putEntry("P", "Show policy status in the grid. Press together with 0-5 in temporal mode to display counts for a particular policy status value.");
			helpScreen.putEntry("M + 0-3", "Choose between statistics for: 0 - all machines, 1 - ATMs, 2 - servers, 3 - workstations in the grid.");
			helpScreen.addSpacer();
			helpScreen.putEntry("T", "Toggle between gloabal / local time in the grid (applicable for temporal view only)");
			helpScreen.putEntry("L", "Toggle labels on the grid");
			helpScreen.putEntry("R", "Reset pan and zoom of the grid");
			helpScreen.putEntry("V + 1-3", "Toggle snapshot / temporal / overview grid modes");
			helpScreen.putEntry("[ ] + ±", "Increase / decrease lower / upper boundary value colouring in temporal view and for connections (helps to better distunguish between values)");
			helpScreen.putEntry("[ ] + SPACE", "Set lower / upper boundary value colouring to default");
			helpScreen.addSpacer();
			helpScreen.putEntry("S + 1-4", "Sort facilities within the business units by: 1 - name, 2 - time zone (W↘E); 3 - latitude (N↘S), 4 - longitude (W↘E).");
			helpScreen.putEntry("S + A + 0-5", "Sort facilities within the business units by counts of machines having activity flag equal to pressed digit at selected time");
			helpScreen.putEntry("S + P + 0-5", "Sort facilities within the business units by counts of machines having policy status equal to pressed digit at selected time");
			helpScreen.putEntry("S + SPACE", "Include / exclude headquarters when sorting");
			helpScreen.putEntry("G + 1-2", "Sort business units in the grid by: 1 - name, 2 - geographically");
			helpScreen.addSpacer();
			helpScreen.putEntry("Mouse actions", "Roll over a facility to see its detailed statistics at a particular time. Click to select the facility permanently.");
			helpScreen.putEntry("ESC", "Deselect currently selected facility");
			helpScreen.putEntry("← →", "± 15 minutes (press with SHIFT for ± 1 hour; with CONTROL for ± 1 day)");
			helpScreen.putEntry("↑ ↓", "View details of the next / previous facility in the business unit, when one is selected (press with SHIFT to jump over 5 facilities; with CONTROL to go to first / last facility)");

			return;
		case 1:
			fill(0, 95);
			textAlign(CENTER, CENTER);
			text("Loading facility status data...", width / 2, height / 2 + HS_HEIGHT / 2);
			helpScreen.draw();
			stroke(10);
			return;
		case 2:
			// Loading the data
			businessunits = loadFaclityStatusData();
			fill(0, 95);
			textAlign(CENTER, CENTER);
			text("Preparing the grid...", width / 2, height / 2 + HS_HEIGHT / 2);
			helpScreen.draw();
			return;

		case 3:
			// Preparing the grid and the graphic buffer
			businessunitGrid = new BusinessunitGrid(businessunits, BusinessunitGrid.LAYOUT_SEQ);

			// Snapshot view
			snapshotView = new SnapshotBUGView(businessunitGrid);
			snapshotView.currentParameter = AbstractBUGView.P_POLICYSTATUS;
			snapshotView.currentCompactTimestamp = CompactTimestamp.fullTimestampToCompact("2012-02-02 14:00:00");
			snapshotView.selectedCompactTimestamp = snapshotView.currentCompactTimestamp;

			// Time view
			timeView = new TimeBUGView(businessunitGrid);
			timeView.currentParameter = AbstractBUGView.P_POLICYSTATUS;
			timeView.currentValue = 1;
			timeView.currentCompactTimestamp = CompactTimestamp.fullTimestampToCompact("2012-02-04 08:00:00");

			// Overall view
			overallView = new OverallView(businessunitGrid);
			overallView.currentParameter = AbstractBUGView.P_POLICYSTATUS;
			overallView.currentValue = 1;
			overallView.currentCompactTimestamp = CompactTimestamp.fullTimestampToCompact("2012-02-04 08:00:00");

			currentView = snapshotView;

			gridZoomPan = new ZoomPan(this);
			gridZoomPan.setMinZoomScale(1);
			gridZoomPan.setMaxZoomScale(20);
			gridZoomPan.setZoomMouseButton(RIGHT);
			gridGraphicBufferBounds = new Rectangle(0, 30, (int) businessunitGrid.getWidth(), (int) businessunitGrid.getHeight());
			gridGraphicBuffer = new ThreadedGraphicBuffer(this, gridZoomPan, new ThreadedDrawClass(), gridGraphicBufferBounds);
			gridGraphicBuffer.setUpdateDuringZoomPan(false);
			gridGraphicBuffer.setUseFadeEffect(true, 20);

			flyingText = new FlyingText(this, (float) (gridGraphicBufferBounds.getX() + businessunitGrid.getWidth() / 2),
					(float) (gridGraphicBufferBounds.getY() + businessunitGrid.getHeight() / 2));

			fill(0, 95);
			textAlign(CENTER, CENTER);
			text("Almost done...", width / 2, height / 2 + HS_HEIGHT / 2);
			helpScreen.draw();
			return;
		}

		loadStage = 42;
		background(255);

		noStroke();
		fill(220);
		rect(gridGraphicBufferBounds.x, gridGraphicBufferBounds.y, gridGraphicBufferBounds.width, gridGraphicBufferBounds.height);

		gridGraphicBuffer.draw();
		if (currentView.selectionIsLocked) {
			pushMatrix();
			gridZoomPan.transform(this.g);
			translate(gridGraphicBufferBounds.x, gridGraphicBufferBounds.y);
			currentView.highlightSelectedElement(this.g, null);
			popMatrix();
		}
		drawTitle();
		drawBottomRow();
		drawSelectedFacilityInfo();
		flyingText.draw();

		if (helpScreen.getIsActive())
			helpScreen.draw();
	}

	/**
	 * Draws the text above the grid
	 */
	protected void drawTitle() {
		textFont(titleFont);
		textAlign(LEFT, BASELINE);
		fill(60);

		String title = "";

		title += BOMDictionary.machineGroupToHR(currentView.currentMachineGroup) + ": ";

		switch (currentView.currentParameter) {
		case AbstractBUGView.P_ACTIVITYFLAG:
			title += "activityflag";
			if (currentView == timeView)
				title += " = " + BOMDictionary.activityFlagToHR(currentView.currentValue);
			break;
		case AbstractBUGView.P_POLICYSTATUS:
			title += "policystatus";
			if (currentView == timeView)
				title += " = " + BOMDictionary.policyStatusToHR(currentView.currentValue);
			break;
		case AbstractBUGView.P_CONNECTIONS:
			title += "connections - " + BOMDictionary.connectionsToHR(currentView.currentValue);
		}
		text(title, 5, 22);
	}

	/**
	 * Draws the legend, current time and sort mode below the grid
	 */
	protected void drawBottomRow() {
		pushMatrix();
		translate((float) gridGraphicBufferBounds.getMinX() + BusinessunitGrid.PADDING, (float) gridGraphicBufferBounds.getMaxY() + 14);
		textFont(bottomRowFont);
		textAlign(LEFT, TOP);
		fill(120);

		// Current time (time range)
		if (currentView == snapshotView) {
			text(CompactTimestamp.toHRString(currentView.currentCompactTimestamp), 0, 0);
		} else {
			if (currentView.timeIsRelative)
				text(CompactTimestamp.toHRString((short) (currentView.currentCompactTimestamp - 191), -3).substring(0, 20) + " ...  "
						+ CompactTimestamp.toHRString(currentView.currentCompactTimestamp, -3).substring(0, 20), 0, 0);
			else
				text(CompactTimestamp.toHRString((short) (currentView.currentCompactTimestamp - 191)) + " ...  " + CompactTimestamp.toHRString(currentView.currentCompactTimestamp),
						0, 0);
		}

		// Sort order
		// translate(2*(BusinessunitGrid.COL_WIDTH + BusinessunitGrid.CELLPADDING_H), 0);
		if (sortText != null) {
			translate(0, -11);
			text("   Facilities are sorted by " + sortText + (FacilityComparator.getInstance().sortHeadquarters || textWidth(sortText) > 230 ? "" : " (excluding headquarters)")
					+ ".");
			translate(0, 11);
		}

		// Legend
		translate(4 * (BusinessunitGrid.COL_WIDTH + BusinessunitGrid.CELLPADDING_H), 0);
		currentView.drawLegend(this.g, 0, 0, BusinessunitGrid.COL_WIDTH, 18);

		popMatrix();
	}

	protected void drawSelectedFacilityInfo() {
		
		if (currentView == overallView)
			return;
		
		Facility f = currentView.selectedFacility;
		if (f == null)
			return;

		MachineGroup mg = f.machinegroups[currentView.currentMachineGroup];
		MachineGroupStatus mgs = CompactTimestamp.isWithin48HrsWindow(currentView.selectedCompactTimestamp) ? mg.statuses[currentView.selectedCompactTimestamp] : null;
		pushMatrix();
		translate(gridGraphicBufferBounds.width + 30, 0);

		textAlign(LEFT, BASELINE);

		// Title
		fill(0);
		textFont(titleFont);
		text(currentView.selectedFacility.businessunitName + " → " + currentView.selectedFacility.facilityName, 0, 22);

		// Location
		textFont(selectedInfoFont);
		fill(120);
		text("Location: ", 0, 45);
		textAlign(RIGHT);
		text(String.format("%.4f", Math.abs(f.lat)) + "", 120, 45);
		text(String.format("%.4f", Math.abs(f.lon)) + "", 200, 45);
		textAlign(LEFT);
		text(f.lat < 0 ? " S" : " N,", 120, 45);
		text(" W", 200, 45);

		// IP range
		text("IP range: ", 0, 60);
		// Machine count
		text(BOMDictionary.machineGroupToHR(currentView.currentMachineGroup) + " count: " + mg.machinecount
				+ (currentView.currentMachineGroup != 0 ? " (out of " + f.machinegroups[0].machinecount + ")" : ""), 0, 75);

		// Time
		String tsHRAbs = CompactTimestamp.toHRString(currentView.selectedCompactTimestamp);
		String tsHRLoc = CompactTimestamp.toHRString(currentView.selectedCompactTimestamp, f.timezoneOffset);
		float tsHRLocX = 0;
		if (tsHRAbs.substring(0, 10).equals(tsHRLoc.substring(0, 10))) {
			tsHRLocX = textWidth(tsHRLoc.substring(0, 10));
			tsHRLoc = tsHRLoc.substring(10);
		}
		fill(0);
		text(tsHRAbs, 0, 105);
		text(tsHRLoc, tsHRLocX, 120);
		fill(120);

		translate(0, 150);
		ellipseMode(CORNER);

		if (mgs == null) {
			text("No data available", 0, 0);
		} else {

			// PolicyStatus
			pushMatrix();
			noStroke();
			text("Policy Status", 0, 0);
			textFont(selectedInfoFont);
			translate(0, 16);
			for (int i = 0; i < 6; i++) {
				// circle
				if (currentView.currentParameter == AbstractBUGView.P_POLICYSTATUS && (currentView == snapshotView || i == currentView.currentValue)) {
					fill(currentView.getColour(AbstractBUGView.P_POLICYSTATUS, i));
					ellipse(10, -9 + 15 * i, 8, 8);
				}

				fill(0, 120);
				textAlign(LEFT);
				// value
				text(String.valueOf(i), 0, 15 * i);
				// machine count
				textAlign(RIGHT);
				text(mgs.countByPolicyStatus[i], 60, 15 * i);
				// %
				if (mg.machinecount > 0) {
					fill(0, 60);
					text(mgs.countByPolicyStatus[i] * 100 / mg.machinecount, 90, 15 * i);
					if (i == 0) {
						textAlign(LEFT);
						text("%", 90, 15 * i);
					}
				}

			}
			popMatrix();

			// Activity flag
			pushMatrix();
			translate(140, 0);
			fill(120);
			noStroke();
			textAlign(LEFT);
			text("Activity Flag", 0, 0);
			textFont(selectedInfoFont);
			translate(0, 16);
			for (int i = 0; i < 6; i++) {
				// circle
				if (currentView.currentParameter == AbstractBUGView.P_ACTIVITYFLAG && (currentView == snapshotView || i == currentView.currentValue)) {
					fill(currentView.getColour(AbstractBUGView.P_ACTIVITYFLAG, i));
					ellipse(10, -9 + 15 * i, 8, 8);
				}

				// value
				fill(0, 120);
				textAlign(LEFT);
				text(String.valueOf(i), 0, 15 * i);
				// machine count
				textAlign(RIGHT);
				text(mgs.countByActivityFlag[i], 60, 15 * i);

				// %
				if (mg.machinecount > 0) {
					fill(0, 60);
					text(mgs.countByActivityFlag[i] * 100 / mg.machinecount, 90, 15 * i);
					if (i == 0) {
						textAlign(LEFT);
						text("%", 90, 15 * i);
					}
				}

			}
			popMatrix();

			// Connections
			pushMatrix();
			translate(0, 120);
			fill(120);
			noStroke();
			textAlign(LEFT);
			text("Connections", 0, 0);
			textFont(selectedInfoFont);
			translate(0, 16);
			for (int i = 1; i < 5; i++) {
				// circle
				if (currentView.currentParameter == AbstractBUGView.P_CONNECTIONS && (currentView == snapshotView || i == currentView.currentValue)) {
					fill(currentView.getColour(AbstractBUGView.P_CONNECTIONS, i));
					ellipse(10, -9 + 15 * (i - 1), 8, 8);
				}

				if (i == 3)
					translate(140, -30);

				// value
				fill(0, 120);
				textAlign(LEFT);
				text(BOMDictionary.connectionsToHR(i), 0, 15 * (i - 1));
				// connections
				textAlign(RIGHT);
				text((int) mgs.connections[i], 90, 15 * (i - 1));
			}
			popMatrix();

		}

		popMatrix();

	}

	/* multi-key-press support */
	boolean checkKey(String k) {
		for (int i = 0; i < keys.length; i++)
			if (KeyEvent.getKeyText(i).toLowerCase().equals(k.toLowerCase()))
				return keys[i];
		return false;
	}

	boolean checkKey(int k) {
		return keys[k];
	}

	public void keyPressed() {
		if (loadStage < 42)
			return;

		boolean keyMore = key == '+' || key == '=';
		boolean keyLess = key == '-' || key == '_';
		boolean keySpace = key == ' ';

		keys[keyCode] = true;

		// Businessunits in the grid order: 1 - name, 2 - geographically
		if (checkKey("g") && keyCode >= '1' && keyCode <= '4') {
			if (keyCode == '1') {
				businessunitGrid.setLayout(BusinessunitGrid.LAYOUT_SEQ);
				flyingText.startFly("Arrange business units by name");
			} else {
				businessunitGrid.setLayout(BusinessunitGrid.LAYOUT_GEO);
				flyingText.startFly("Arrange business units geographically");
			}
			gridGraphicBuffer.setUpdateFlag();
			return;
		}

			// Facility sort order: 1 - name, 2 - timezone and name, 3 - lat, 4 - lon ' ' or a parameter
		if (checkKey("s") && currentView != overallView) {
			
			FacilityComparator fc = FacilityComparator.getInstance();

			// Sorting by a parameter
			if (checkKey("a") || checkKey("p") || checkKey("c")) {
				if (keyCode >= '0' && keyCode <= '5') {
					if (checkKey("a")) {
						fc.sortMode = FacilityComparator.SM_ACTIVITY_FLAG;
						sortText = "count of activity flag = " + (keyCode - 0x30);
					} else if (checkKey("p")) {
						fc.sortMode = FacilityComparator.SM_POLICY_STATUS;
						sortText = "count of policy status = " + (keyCode - 0x30);
					} else if (checkKey("c")) {
						if (keyCode == 0 || keyCode == 5)
							return;
						fc.sortMode = FacilityComparator.SM_CONNECTIONS;
						sortText = BOMDictionary.connectionsToHR(keyCode - 0x30) + " connections";
					}

					sortText += " at " + CompactTimestamp.toHRString(currentView.selectedCompactTimestamp);

					if (currentView.currentMachineGroup != 0) {
						sortText = BOMDictionary.machineGroupToHR(currentView.currentMachineGroup).toLowerCase() + "’ " + sortText;
					}

					flyingText.startFly("Sort facilities by + " + sortText);
					fc.sortSubmode = keyCode - 0x30;
					fc.sortMachineGroup = currentView.currentMachineGroup;
					fc.sortComactTimestamp = currentView.selectedCompactTimestamp >= 0 ? currentView.selectedCompactTimestamp : currentView.currentCompactTimestamp;

					businessunitGrid.sortFacilities();
				}

			} else {

				if (keyCode >= '1' && keyCode <= '4') {
					fc.sortMode = keyCode - 0x31;
					switch (fc.sortMode) {
					case FacilityComparator.SM_NAME:
						sortText = null;
						break;
					case FacilityComparator.SM_TIMEZONE:
						sortText = "time zone";
						break;
					case FacilityComparator.SM_LAT:
						sortText = "latitude";
						break;
					case FacilityComparator.SM_LON:
						sortText = "longitude (W↘E)";
						break;
					}
					flyingText.startFly("Sort facilities by " + (sortText == null ? "name" : sortText));
					businessunitGrid.sortFacilities();
				} else if (key == ' ') {
					fc.sortHeadquarters = !fc.sortHeadquarters;

					if (fc.sortHeadquarters)
						flyingText.startFly("Include headquarters when sorting");
					else
						flyingText.startFly("Exclude headquarters when sorting");
					businessunitGrid.sortFacilities();
				}
			}
			gridGraphicBuffer.setUpdateFlag();
			mouseMoved();
			return;
		}

		// Toggle timezone alignment
		if (key == 't' && currentView == timeView) {
			timeView.timeIsRelative = !timeView.timeIsRelative;
			flyingText.startFly("Toggle BMT / local time");
			gridGraphicBuffer.setUpdateFlag();
			return;
		}

		// Show policy status
		if (checkKey("p")) {
			if (currentView != timeView) {
				timeView.currentParameter = AbstractBUGView.P_POLICYSTATUS;
				snapshotView.currentParameter = AbstractBUGView.P_POLICYSTATUS;
				overallView.currentParameter = AbstractBUGView.P_POLICYSTATUS;
				flyingText.startFly("Policy status");
				gridGraphicBuffer.setUpdateFlag();
			} else if (keyCode >= '0' && keyCode <= '5') {
				snapshotView.currentParameter = AbstractBUGView.P_POLICYSTATUS;
				timeView.currentParameter = AbstractBUGView.P_POLICYSTATUS;
				overallView.currentParameter = AbstractBUGView.P_POLICYSTATUS;
				timeView.currentValue = keyCode - 0x30;
				flyingText.startFly("Policy status = " + BOMDictionary.policyStatusToHR(currentView.currentValue));
				gridGraphicBuffer.setUpdateFlag();
			}
			return;
		}

		// Show activityflag
		if (checkKey("a")) {
			if (currentView != timeView) {
				snapshotView.currentParameter = AbstractBUGView.P_ACTIVITYFLAG;
				overallView.currentParameter = AbstractBUGView.P_ACTIVITYFLAG;
				flyingText.startFly("Activity flag");
				gridGraphicBuffer.setUpdateFlag();
			} else if (keyCode >= '0' && keyCode <= '5') {
				snapshotView.currentParameter = AbstractBUGView.P_ACTIVITYFLAG;
				timeView.currentParameter = AbstractBUGView.P_ACTIVITYFLAG;
				timeView.currentValue = keyCode - 0x30;
				flyingText.startFly("Activity flag = " + BOMDictionary.activityFlagToHR(currentView.currentValue));
				gridGraphicBuffer.setUpdateFlag();
			}
			return;
		}

		// Show connections (total, min, max, avg, sd)
		if (checkKey("c") && keyCode >= '0' && keyCode <= '4') {
			currentView.currentParameter = AbstractBUGView.P_CONNECTIONS;
			currentView.currentValue = keyCode - 0x30;
			flyingText.startFly("Connections - " + BOMDictionary.connectionsToHR(currentView.currentValue));
			gridGraphicBuffer.setUpdateFlag();
			return;
		}

		// Choose machinegroup (all, atms, servers, workstations)
		if (checkKey("m") && keyCode >= '0' && keyCode <= '3') {
			currentView.currentMachineGroup = keyCode - 0x30;
			flyingText.startFly("Machine group: " + BOMDictionary.machineGroupToHR(currentView.currentMachineGroup));
			gridGraphicBuffer.setUpdateFlag();
			return;
		}

		// Show/hide labels on the grid
		if (key == 'l') {
			currentView.showLabels = !currentView.showLabels;
			if (currentView.showLabels)
				flyingText.startFly("Show labels on grid");
			else
				flyingText.startFly("Hide labels on grid");
			gridGraphicBuffer.setUpdateFlag();
			return;
		}

		// Reset View
		if (key == 'r') {
			gridZoomPan.reset();
			gridZoomPan.setPanOffset(0, 0);
			flyingText.startFly("Reset pan and zoom");
			gridGraphicBuffer.setUpdateFlag();
			return;
		}

		// Toggle view
		if (checkKey("v") && keyCode > '0' && keyCode < '4') {
			AbstractBUGView oldView = currentView;

			switch (keyCode) {
			case '1':
				currentView = snapshotView;
				break;
			case '2':
				currentView = timeView;
				break;
			case '3':
				currentView = overallView;
				break;
			}

			if (currentView == oldView)
				return;
			
			if (currentView == snapshotView)
				flyingText.startFly("Snapshot view");
			else if (currentView == timeView)
				flyingText.startFly("Timeline view");
			else
				flyingText.startFly("Overall view");

			if (oldView.selectionIsLocked) {
				currentView.selectedFacility = oldView.selectedFacility;
				currentView.selectedCompactTimestamp = oldView.selectedCompactTimestamp;
				currentView.selectionIsLocked = true;
				currentView.showLabels = oldView.showLabels;
				if (currentView == snapshotView)
					currentView.currentCompactTimestamp = oldView.selectedCompactTimestamp;
			} else {
				currentView.selectionIsLocked = false;
				currentView.selectedFacility = null;
			}

			currentView.currentMachineGroup = oldView.currentMachineGroup;
			gridGraphicBuffer.setUpdateFlag();
			return;
		}

		// Facility ±
		if ((keyCode == UP || keyCode == DOWN) && currentView != overallView) {
			int df = keyCode == DOWN ? 1 : -1;
			String label = df > 0 ? "Next facility" : "Previous facility";
			if (checkKey(SHIFT)) {
				df *= 5;
				label = df > 0 ? "5th next facility" : "5th previous facility";
			} else if (checkKey(CONTROL)) {
				df *= 100500;
				label = df > 0 ? "Bottom-most facility" : "Top-most facility";
			}
			if (currentView.selectedFacility == null)
				return;
			if (currentView.selectNeighbourFacility(df)) {
				gridGraphicBuffer.setUpdateFlag();
				flyingText.startFly(label);
				mouseMoved();
			}
			return;
		}

		// Time ±
		if ((keyCode == RIGHT || keyCode == LEFT) && currentView != overallView) {
			int dt = keyCode == RIGHT ? 1 : -1;
			String label = "15 minunites";
			if (checkKey(SHIFT)) {
				dt *= 4;
				label = "1 hour";
			} else if (checkKey(CONTROL)) {
				dt *= 4 * 24;
				label = "1 day";
			}
			if (currentView.selectNeighbourTimestamp(dt)) {
				flyingText.startFly((dt < 0 ? "- " : "+ ") + label);
				gridGraphicBuffer.setUpdateFlag();
				mouseMoved();
			}
			return;
		}

		// Range (lower/upper boundary) ±
		if (currentView == timeView) {
			TimeBUGView currentViewC = (TimeBUGView) currentView;
			if (checkKey("[")) {
				float newRangeMin = currentViewC.rangeMin;
				float dr = (currentViewC.rangeMaxLimit - currentViewC.rangeMinLimit) / 10f;

				if (keySpace)
					newRangeMin = currentViewC.rangeMinLimit;
				else if (keyLess)
					newRangeMin -= dr;
				else if (keyMore)
					newRangeMin += dr;

				if (newRangeMin < currentViewC.rangeMinLimit)
					newRangeMin = currentViewC.rangeMinLimit;
				if (newRangeMin > currentViewC.rangeMax - dr / 2)
					newRangeMin = currentViewC.rangeMax - dr / 2;

				if (newRangeMin != currentViewC.rangeMin) {
					currentViewC.rangeMin = newRangeMin;
					flyingText.startFly("Change range lower limit");
					gridGraphicBuffer.setUpdateFlag();
				}
				return;
			}
			if (checkKey("]")) {
				float newRangeMax = currentViewC.rangeMax;
				float dr = (currentViewC.rangeMaxLimit - currentViewC.rangeMinLimit) / 10f;

				if (keySpace)
					newRangeMax = currentViewC.rangeMaxLimit;
				else if (keyLess)
					newRangeMax -= dr;
				else if (keyMore)
					newRangeMax += dr;

				if (newRangeMax > currentViewC.rangeMaxLimit)
					newRangeMax = currentViewC.rangeMaxLimit;
				if (newRangeMax < currentViewC.rangeMin + dr / 2)
					newRangeMax = currentViewC.rangeMin + dr / 2;

				if (newRangeMax != currentViewC.rangeMax) {
					currentViewC.rangeMax = newRangeMax;
					flyingText.startFly("Change range upper limit");
					gridGraphicBuffer.setUpdateFlag();
				}
				return;
			}
		}

		// Unlock focus (or hid the help)
		if (key == ESC) {
			if (helpScreen.getIsActive()) {
				helpScreen.setIsActive(false);
				return;
			}
			timeView.selectionIsLocked = false;
			snapshotView.selectionIsLocked = false;
			mouseMoved();
			key = 0; // Prevent applet from closing
			return;
		}

		// Toggle help
		if (key == 'h') {
			helpScreen.setIsActive(!helpScreen.getIsActive());
			return;
		}
	}

	public void keyReleased() {
		if (loadStage < 42)
			return;
		keys[keyCode] = false;
	}

	public void mouseMoved() {
		if (loadStage < 42)
			return;

		// Getting object details by mouse coordinates
		if (!gridGraphicBufferBounds.contains(mouseX, mouseY)) {
			if (!currentView.selectionIsLocked)
				currentView.selectedFacility = null;
		} else {
			if (!currentView.selectionIsLocked) {
				PVector mouseCoordWithOffset = new PVector(mouseX, mouseY);
				PVector convertedMouseCoord = gridZoomPan.getZoomPanState().getDispToCoord(mouseCoordWithOffset);
				currentView.selectAt((int) convertedMouseCoord.x - gridGraphicBufferBounds.x, (int) convertedMouseCoord.y - gridGraphicBufferBounds.y);
			}
		}
	}

	public void mouseClicked() {
		if (loadStage < 42)
			return;
		if (gridGraphicBufferBounds.contains(mouseX, mouseY)) {
			PVector mouseCoordWithOffset = new PVector(mouseX, mouseY);
			PVector convertedMouseCoord = gridZoomPan.getZoomPanState().getDispToCoord(mouseCoordWithOffset);
			currentView.selectAt((int) convertedMouseCoord.x - gridGraphicBufferBounds.x, (int) convertedMouseCoord.y - gridGraphicBufferBounds.y);
			currentView.selectionIsLocked = currentView.selectedFacility != null;
		}
	}

	public HashMap<String, Businessunit> loadFaclityStatusData() {

		HashMap<String, Businessunit> businessunits = new HashMap<String, Businessunit>();

		try {

			BufferedReader reader;
			String line;

			// Reading facilities info
			reader = createReader("facility.tab");
			reader.readLine(); // Skipping the first line with headers

			while (true) {
				line = reader.readLine();
				if (line == null) {
					break;
				} else {
					String[] tokens = split(line, TAB);
					String currentBuName = tokens[0];
					String currentBuRealName = null;
					String currentFName = tokens[1];

					// Hack for business unit name to have data centres in different cells. See Facility.businessunitRealName
					if (currentBuName.equals("headquarters")) {
						currentBuRealName = currentBuName;
						currentBuName = currentFName;
					}

					Businessunit currentBu = businessunits.get(currentBuName);
					if (currentBu == null) {
						currentBu = new Businessunit(currentBuName);
						businessunits.put(currentBuName, currentBu);
					}

					Facility currentF = new Facility();
					currentBu.facilities.put(currentFName, currentF);
					currentBu.sortedFacilities.add(currentF);
					currentF.businessunitName = currentBuName;
					currentF.businessunitRealName = currentBuRealName;
					currentF.facilityName = currentFName;
					currentF.lat = Float.valueOf(tokens[2]);
					currentF.lon = Float.valueOf(tokens[3]);
					currentF.timezoneOffset = Short.valueOf(tokens[4]);
					currentF.machinegroups[0] = new MachineGroup(Integer.valueOf(tokens[5]));
					currentF.machinegroups[1] = new MachineGroup(Integer.valueOf(tokens[8]));
					currentF.machinegroups[2] = new MachineGroup(Integer.valueOf(tokens[6]));
					currentF.machinegroups[3] = new MachineGroup(Integer.valueOf(tokens[7]));
					currentBu.sortFacilities();
				}
			}
			reader.close();

			// Reading statuses for all facilities
			reader = createReader("facilitystatus.tab");
			// reader = createReader("facilitystatus_short.tab");
			// Skipping the first line with headers
			reader.readLine();

			while (true) {
				line = reader.readLine();
				if (line == null) {
					break;
				} else {
					String[] tokens = split(line, TAB);

					String currentBuName = tokens[0];
					String currentFName = tokens[1];
					short compactTimestamp = CompactTimestamp.fullTimestampToCompact(tokens[2]);

					// Hack for business unit name to have data centres in different cells. See Facility.businessunitRealName
					if (currentBuName.equals("headquarters"))
						currentBuName = currentFName;

					if (businessunits.get(currentBuName) == null)
						continue;
					Facility currentF = businessunits.get(currentBuName).facilities.get(currentFName);

					for (short machineGroupId = 0; machineGroupId < 4; machineGroupId++) {
						MachineGroupStatus currentMGStatus = new MachineGroupStatus();

						for (int i = 0; i < 6; i++)
							currentMGStatus.countByActivityFlag[i] = Integer.parseInt(tokens[3 + 24 + machineGroupId * 6 + i]);
						for (int i = 0; i < 6; i++)
							currentMGStatus.countByPolicyStatus[i] = Integer.parseInt(tokens[3 + machineGroupId * 6 + i]);

						currentMGStatus.connections[0] = Integer.parseInt(tokens[3 + 24 + 24 + machineGroupId]); // count
						currentMGStatus.connections[1] = Integer.parseInt(tokens[3 + 24 + 24 + 12 + machineGroupId]); // min
						currentMGStatus.connections[2] = Integer.parseInt(tokens[3 + 24 + 24 + 8 + machineGroupId]); // max
						currentMGStatus.connections[3] = Float.parseFloat(tokens[3 + 24 + 24 + 4 + machineGroupId]); // avg
						currentMGStatus.connections[4] = Float.parseFloat(tokens[3 + 24 + 24 + 16 + machineGroupId]); // sd

						currentF.machinegroups[machineGroupId].statuses[compactTimestamp] = currentMGStatus;

					}
				}
			}
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return businessunits;
	}

	// Class that implements ThreadedDraw for drawing in a different thread
	class ThreadedDrawClass implements ThreadedDraw {

		// Code to draw onto the buffer
		public void threadedDraw(PGraphics canvas, ZoomPanState zoomPanState, Object extraInfo) {
			canvas.pushMatrix();
			zoomPanState.transform(canvas);
			canvas.translate(gridGraphicBufferBounds.x, gridGraphicBufferBounds.y);
			currentView.draw(canvas, Thread.currentThread());
			canvas.popMatrix();
		}
	}

}
