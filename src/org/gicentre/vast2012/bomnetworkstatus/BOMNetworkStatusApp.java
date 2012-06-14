package org.gicentre.vast2012.bomnetworkstatus;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import org.gicentre.utils.gui.Clipper;
import org.gicentre.utils.gui.HelpScreen;
import org.gicentre.utils.gui.ThreadedDraw;
import org.gicentre.utils.gui.ThreadedGraphicBuffer;
import org.gicentre.utils.gui.Tooltip;
import org.gicentre.utils.move.ZoomPan;
import org.gicentre.utils.move.ZoomPanState;
import org.gicentre.utils.move.ZoomPan.ZoomPanBehaviour;
import org.gicentre.utils.spatial.Direction;
import org.gicentre.vast2012.bomnetworkstatus.ui.BusinessunitGrid;
import org.gicentre.vast2012.bomnetworkstatus.ui.DetailsView;
import org.gicentre.vast2012.bomnetworkstatus.ui.FlyingText;
import org.gicentre.vast2012.bomnetworkstatus.ui.bugview.AbstractBUGView;
import org.gicentre.vast2012.bomnetworkstatus.ui.bugview.OverallBUGView;
import org.gicentre.vast2012.bomnetworkstatus.ui.bugview.SnapshotBUGView;
import org.gicentre.vast2012.bomnetworkstatus.ui.bugview.TimeBUGView;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PMatrix;
import processing.core.PVector;

public class BOMNetworkStatusApp extends PApplet {

	private static final long serialVersionUID = 4320695214813440866L;

	public static int DETAILS_CAPTIONS_HEIGHT = 0;
	public static int DETAILS_Y = 435;

	HashMap<String, Businessunit> businessunits;
	BusinessunitGrid businessunitGrid;
	Clipper gridClipper;
	ZoomPan gridZoomPan;
	ThreadedGraphicBuffer gridGraphicBuffer;

	DetailsView details;
	Clipper detailsClipper;
	ZoomPan detailsZoomPan;
	ThreadedGraphicBuffer detailsGraphicBuffer;
	MachineGroupDetailsCache detailsCache;
	int framesToUpdateDetailsGraphicBuffer = -1;

	FlyingText flyingText;
	HelpScreen helpScreen;
	final static int HS_HEIGHT = 600;

	TimeBUGView timeView;
	SnapshotBUGView snapshotView;
	OverallBUGView overallView;
	AbstractBUGView currentView;

	PFont titleFont;
	PFont subtitleFont;
	PFont selectedInfoFont;
	PFont bottomRowFont;
	boolean[] keys = new boolean[526];
	String sortText = null;
	DataLoader dataLoader;

	public static void main(String[] args) {
		PApplet.main(new String[] { "org.gicentre.vast2012.bomnetworkstatus.BOMNetworkStatusApp" });
	}

	public void setup() {
		size(1280, 1000);
		frame.setTitle("Bank of Money Network Status — giCentre, City University London");
		titleFont = createFont("Helvetica", 20);
		subtitleFont = createFont("Helvetica", 16);
		selectedInfoFont = createFont("Arial", 14);
		bottomRowFont = createFont("Arial", 12);
	}

	int loadStage = 0;
	boolean oldFocused;

	private String tooltipText;

	private int parameterUnderMouse;
	private int parameterValueUnderMouse;

	@SuppressWarnings("deprecation")
	public void draw() {
		
		if (!focused) {
			mouseX = -1;
			mouseY = -1;
		}
		if (oldFocused != focused)
			mouseMoved();

		oldFocused = focused;
		
		background(255);
		switch (loadStage++) {
		case 0:
			helpScreen = new HelpScreen(this, createFont("Helvetica", 12));
			helpScreen.setBackgroundColour(0xf0fffb9d);
			helpScreen.setIsActive(true);
			helpScreen.setHeader("Bank of Money Network Status", 30, 20);
			helpScreen.putEntry("H", "Show / hide this help");
			helpScreen.addSpacer();
			helpScreen.putEntry("V + 1-3", "Toggle snapshot / temporal / overview grid modes (views)");
			helpScreen.putEntry("L", "Toggle labels on the grid");
			helpScreen.putEntry("G + SPACE", "Reset pan and zoom of the grid");
			helpScreen.putEntry("D + SPACE", "Reset pan of the box with machine details for the selected facility");
			helpScreen.putEntry("T", "Toggle between alignment by global / local time in the grid (applicable for temporal view only)");
			helpScreen.putEntry("[ ] + ±", "Increase / decrease lower / upper boundary value colouring in temporal view (helps to better distunguish between values)");
			helpScreen.putEntry("[ ] + SPACE", "Set lower / upper boundary value colouring to default");
			helpScreen.addSpacer();
			helpScreen.putEntry("P", "Show policy status in the grid. Press together with 0-5 in temporal mode to display counts for a particular policy status value.");
			helpScreen.putEntry("A", "Show activity flag in the grid. Press together with 0-5 in temporal mode to display counts for a particular activity flag value.");
			helpScreen.putEntry("C",
					"Show connections in the grid (snapshot view and temporal view only). Press together with 1-4 in temporal mode to display: 1 - avg, 2 - sd, 3 - min, 4 - max.");
			helpScreen.putEntry("M + SPACE, 1-9, 0, -", "Choose between statistics for:\n" + "        SPACE - all machines,\n"
					+ "        1 - ATMs, 2 - servers, 3 - workstations,\n"
					+ "        4 - servers / compute, 5 - servers / email, 6 - servers / file servers, 7 - servers / multiple, 8 - servers / web,\n"
					+ "        9 - workstations / loan, 0 - workstations / office, - - workstations / teller");
			helpScreen.addSpacer();
			helpScreen.putEntry("G + 1-3", "Sort business units in the grid by: 1 - name, 2 - geographically, 3 - geographically with data centres in the bottom");
			helpScreen.putEntry("D + 1-4", "Sort machines within a facility in columns by: 1 - ip address, 2 - activity flag, 3 - policy status, 4 - number of connections");
			helpScreen
					.putEntry("F + 1-6",
							"Sort facilities within the business units by: 1 - name, 2 - time zone (W↘E), 3 - latitude (N↘S), 4 - longitude (W↘E), 5, 6 - lowest and highest ip address there");
			helpScreen.putEntry("F + A + 0-5", "Sort facilities within the business units by percentage of machines having activity flag equal to pressed digit at selected time");
			helpScreen.putEntry("F + P + 0-5", "Sort facilities within the business units by percentage of machines having policy status equal to pressed digit at selected time");
			helpScreen
					.putEntry("F + C + 1-4",
							"Sort facilities within the business units by: 1 - average connections, 2 - standard deviation of connections, 3 - minimum connections, 4 - maximum connections");
			helpScreen.putEntry("F + SPACE", "Include / exclude headquarters when sorting");
			helpScreen.addSpacer();
			helpScreen.putEntry("Mouse actions",
					"Roll over a facility to see its detailed statistics at a particular time. Click to select a facility permanently. Scroll and drag to pan and zoom the grid.");
			helpScreen.putEntry("ESC", "Deselect currently selected facility");
			helpScreen.putEntry("← →", "± 15 minutes (press with SHIFT for ± 1 hour; with CONTROL for ± 1 day)");
			helpScreen
					.putEntry("↑ ↓",
							"View details of the next / previous facility in the business unit, when one is selected (press with SHIFT to jump over 5 facilities; with CONTROL to go to first / last facility)");

			return;
		case 1:
			// Starting loading the data
			dataLoader = new DataLoader();
			return;

		case 2:
			// Not going to the next loadStage until the loader reports that everything's loaded
			helpScreen.draw();

			if (dataLoader.ready == true) {
				businessunits = dataLoader.businessunits;
				dataLoader.interrupt();
				dataLoader = null;
				return;
			}

			fill(0, 95);
			textAlign(CENTER, CENTER);
			text("Loading facility status data...", width / 2, height / 2 + HS_HEIGHT / 2);
			text((int) (dataLoader.progress * 100) + "%", width / 2, height / 2 + HS_HEIGHT / 2 + 20);
			--loadStage;
			return;

		case 3:
			// Preparing the grid and the graphic buffer
			businessunitGrid = new BusinessunitGrid(businessunits, BusinessunitGrid.LAYOUT_SEQ);

			// Snapshot view
			snapshotView = new SnapshotBUGView(businessunitGrid);
			snapshotView.currentParameter = AbstractBUGView.P_POLICYSTATUS;
			snapshotView.currentCompactTimestamp = CompactTimestamp.fullTimestampToCompact("2012-02-02 14:00:00");
			snapshotView.selectedCompactTimestamp = snapshotView.currentCompactTimestamp;
			snapshotView.resetRange();

			// Time view
			timeView = new TimeBUGView(businessunitGrid);
			timeView.currentParameter = AbstractBUGView.P_POLICYSTATUS;
			timeView.currentValue = 1;
			timeView.currentCompactTimestamp = CompactTimestamp.fullTimestampToCompact("2012-02-04 08:00:00");
			timeView.resetRange();

			// Overall view
			overallView = new OverallBUGView(businessunitGrid);
			overallView.currentParameter = AbstractBUGView.P_POLICYSTATUS;
			overallView.currentValue = 1;
			overallView.currentCompactTimestamp = CompactTimestamp.fullTimestampToCompact("2012-02-04 08:00:00");
			overallView.resetRange();

			currentView = snapshotView;

			// Grid
			gridClipper = new Clipper(this, new Rectangle(0, 35, (int) businessunitGrid.getWidth(), (int) businessunitGrid.getHeight()));
			gridZoomPan = new ZoomPan(this);
			gridZoomPan.setMinZoomScale(1);
			gridZoomPan.setMaxZoomScale(20);
			gridZoomPan.setZoomMouseButton(RIGHT);
			gridZoomPan.setMouseBoundsMask(gridClipper.getClippingRect().getBounds());
			gridGraphicBuffer = new ThreadedGraphicBuffer(this, gridZoomPan, new GridThreadedDraw(), gridClipper.getClippingRect().getBounds());
			gridGraphicBuffer.setUpdateDuringZoomPan(false);
			gridGraphicBuffer.setUseFadeEffect(true, 20);

			// Details
			details = new DetailsView();
			detailsClipper = new Clipper(this, new Rectangle((int) gridClipper.getClippingRect().getMaxX() + 30, DETAILS_Y, (int)(width  
					- (int) gridClipper.getClippingRect().getMaxX() - 55), (int) gridClipper.getClippingRect().getMaxY() - DETAILS_Y));
			detailsZoomPan = new ZoomPan(this);
			detailsZoomPan.setMinZoomScale(0.0025);
			detailsZoomPan.setMaxZoomScale(5);
			detailsZoomPan.setZoomMouseButton(RIGHT);
			detailsZoomPan.setZoomPanBehaviour(ZoomPanBehaviour.VERTICAL_ONLY);
			detailsZoomPan.setMouseBoundsMask(detailsClipper.getClippingRect().getBounds());
			detailsGraphicBuffer = new ThreadedGraphicBuffer(this, detailsZoomPan, new DetailsThreadedDraw(), detailsClipper.getClippingRect().getBounds());
			detailsGraphicBuffer.setUpdateDuringZoomPan(false);
			detailsCache = new MachineGroupDetailsCache();

			flyingText = new FlyingText(this, (float) (gridClipper.getClippingRect().getBounds().getX() + businessunitGrid.getWidth() / 2), (float) (gridClipper.getClippingRect()
					.getBounds().getY() + businessunitGrid.getHeight() / 2));

			fill(0, 95);
			textAlign(CENTER, CENTER);
			text("Almost done...", width / 2, height / 2 + HS_HEIGHT / 2);
			helpScreen.draw();
			return;
		}

		// Loading complete
		loadStage = 42;
		tooltipText = null;
		parameterUnderMouse = 0;
		parameterValueUnderMouse = 0;
		background(255);

		noStroke();
		fill(220);
		rect(gridClipper.getClippingRect().getBounds().x, gridClipper.getClippingRect().getBounds().y, gridClipper.getClippingRect().getBounds().width, gridClipper
				.getClippingRect().getBounds().height);

		gridGraphicBuffer.draw();
		if (currentView.selectionIsLocked) {
			gridClipper.startClipping();
			pushMatrix();
			gridZoomPan.transform(this.g);
			translate(gridClipper.getClippingRect().getBounds().x, gridClipper.getClippingRect().getBounds().y);
			currentView.highlightSelectedElement(this.g, null);
			popMatrix();
			gridClipper.stopClipping();
		}

		drawTitle();
		drawBottomRow();
		drawSelectedFacilityInfo();
		flyingText.draw();

		// Tooltip
		if (tooltipText != null) {
			textFont(bottomRowFont);
			Tooltip machineDetailsTT = new Tooltip(this, bottomRowFont, 12, textWidth(tooltipText) + 8);
			machineDetailsTT.setAnchor(Direction.NORTH_WEST);

			machineDetailsTT.setText(tooltipText);
			machineDetailsTT.draw(Math.min(mouseX + 16, width - machineDetailsTT.getWidth() - 3), Math.min(mouseY + 20, height - machineDetailsTT.getHeight() - 3));
			machineDetailsTT = null;
		}

		stroke(0);

		if (helpScreen.getIsActive())
			helpScreen.draw();
	}

	/**
	 * Draws the text above the grid
	 */
	protected void drawTitle() {
		textFont(titleFont);
		textAlign(LEFT, BASELINE);
		fill(0);

		String title = "";

		if (currentView == snapshotView) {
			title += "Snapshot view — ";
		} else if (currentView == timeView) {
			title += "Temporal view — ";
		} else if (currentView == overallView) {
			title += "Overall view — ";
		}

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
			title += "connections";
			if (currentView == timeView)
				title += " — " + BOMDictionary.connectionsToHR(currentView.currentValue);
		}
		text(title, 5, 25);
	}

	/**
	 * Draws the legend, current time and sort mode below the grid
	 */
	protected void drawBottomRow() {
		pushMatrix();
		translate((float) gridClipper.getClippingRect().getMinX() + BusinessunitGrid.PADDING, (float) gridClipper.getClippingRect().getMaxY() + 15);
		textFont(bottomRowFont);
		textAlign(LEFT, TOP);
		fill(120);

		// Current time (time range)
		if (currentView == snapshotView) {
			text(CompactTimestamp.toHRString(currentView.currentCompactTimestamp), 5, 0);
		} else {
			if (currentView.timeIsRelative)
				text(CompactTimestamp.toHRString((short) (currentView.currentCompactTimestamp - 191), -3).substring(0, 20) + " ...  "
						+ CompactTimestamp.toHRString(currentView.currentCompactTimestamp, -3).substring(0, 20), 5, 0);
			else
				text(CompactTimestamp.toHRString((short) (currentView.currentCompactTimestamp - 191)) + " ...  " + CompactTimestamp.toHRString(currentView.currentCompactTimestamp),
						5, 0);
		}

		// Sort order
		// translate(2*(BusinessunitGrid.COL_WIDTH + BusinessunitGrid.CELLPADDING_H), 0);
		if (sortText != null) {
			translate(0, -11);
			text("   Facilities are sorted by " + sortText + (FacilityComparator.getInstance().sortHeadquarters || textWidth(sortText) > 240 ? "" : " (excluding headquarters)")
					+ ".");
			translate(0, 11);
		}

		// Legend
		translate(4 * (BusinessunitGrid.COL_WIDTH + BusinessunitGrid.CELLPADDING_H), 0);
		currentView.drawLegend(this.g, 0, 0, BusinessunitGrid.COL_WIDTH, 18);

		popMatrix();
	}

	/**
	 * Draws everyting in the right column
	 */
	protected void drawSelectedFacilityInfo() {
		if (currentView == overallView)
			return;

		Facility f = currentView.selectedFacility;
		if (f == null)
			return;

		MachineGroup mg = f.machinegroups[currentView.currentMachineGroup];
		MachineGroupStatus mgs = CompactTimestamp.isWithin48HrsWindow(currentView.selectedCompactTimestamp) ? mg.statuses[currentView.selectedCompactTimestamp] : null;
		pushMatrix(); // Right shift - begin
		translate((float) detailsClipper.getClippingRect().getMinX(), 0);
		pushMatrix(); // Top shift - begin

		textAlign(LEFT, BASELINE);

		// Title
		fill(0);
		textFont(titleFont);
		text(currentView.selectedFacility.businessunitName + " → " + currentView.selectedFacility.facilityName, 0, 25);

		// Location
		textFont(selectedInfoFont);
		translate(0, 3);
		fill(120);
		text("location: ", 0, 45);
		textAlign(RIGHT);
		text(String.format("%.4f", Math.abs(f.lat)) + "", 120, 45);
		text(String.format("%.4f", Math.abs(f.lon)) + "", 200, 45);
		textAlign(LEFT);
		text(f.lat < 0 ? " S" : " N,", 120, 45);
		text(" W", 200, 45);

		// Machine count
		String sCount = BOMDictionary.machineGroupToHR(currentView.currentMachineGroup) + " count: " + mg.machinecount;
		String sCount2 = sCount + (currentView.currentMachineGroup != 0 ? " (out of " + f.machinegroups[0].machinecount + ")" : "");
		if (textWidth(sCount2) <= detailsClipper.getClippingRect().getWidth())
			sCount = sCount2;
		text(sCount, 0, 75);

		// IP range
		String ipr = IPConverter.intToStr(mg.ipMin) + " − " + IPConverter.intToStr(mg.ipMax);
		if (ipr.length() > 3)
			text("IP range: " + ipr, 0, 90);

		// Time
		String tsHRAbs = CompactTimestamp.toHRString(currentView.selectedCompactTimestamp);
		String tsHRLoc = CompactTimestamp.toHRString(currentView.selectedCompactTimestamp, f.timezoneOffset);
		float tsHRLocX = 0;
		if (tsHRAbs.substring(0, 10).equals(tsHRLoc.substring(0, 10))) {
			tsHRLocX = textWidth(tsHRLoc.substring(0, 10));
			tsHRLoc = tsHRLoc.substring(10);
		}
		fill(0);
		text(tsHRAbs, 0, 120);
		text(tsHRLoc, tsHRLocX, 135);
		fill(120);

		translate(0, 165);

		if (mgs == null) {
			text("No data available", 0, 0);
			popMatrix();
		} else {

			// PolicyStatus
			pushMatrix();
			noStroke();
			text("Policy Status", 0, 0);
			textFont(selectedInfoFont);
			translate(0, 16);
			
			// Mouse coordinate in local coordinates
			PMatrix m = getMatrix();
			m.invert();
			PVector localMouse = m.mult(new PVector(mouseX, mouseY), null);

			int b = 0;
			for (int i = 0; i < 6; i++) {
				// square
				if ((currentView.selectionIsLocked && !detailsCache.isNotWorking() && mgs.countByPolicyStatus[i] > 0)
						|| (currentView.currentParameter == AbstractBUGView.P_POLICYSTATUS && (currentView == snapshotView || i == currentView.currentValue))) {
					fill(currentView.getColour(AbstractBUGView.P_POLICYSTATUS, i));
					b = (currentView.currentParameter == AbstractBUGView.P_POLICYSTATUS && (currentView == snapshotView || i == currentView.currentValue)) ? 0 : 1;
					rect(11 + b, -8 + 15 * i + b, 5 - 2 * b, 5 - 2 * b);
				}

				fill(0, 120);
				textAlign(LEFT);
				// value
				text(String.valueOf(i), 0, 15 * i);
				// machine count
				textAlign(RIGHT);
				text((int) mgs.countByPolicyStatus[i], 60, 15 * i);
				// %
				if (mg.machinecount > 0) {
					fill(0, 60);
					text(mgs.countByPolicyStatus[i] * 100 / mg.machinecount, 90, 15 * i);
					if (i == 0) {
						textAlign(LEFT);
						text("%", 90, 15 * i);
					}
				}
				// tooltip
				if (localMouse.x > 3 && localMouse.y > -12  + 15*i && localMouse.x < 90 && localMouse.y < 15*i + 4) {
					tooltipText = BOMDictionary.policyStatusToHR(i, false, false);
					parameterUnderMouse = AbstractBUGView.P_POLICYSTATUS;
					parameterValueUnderMouse = i;
				}
			}
			popMatrix();

			// Activity flag
			pushMatrix();
			translate(145, 0);
			localMouse.x -= 145;
			fill(120);
			noStroke();
			textAlign(LEFT);
			text("Activity Flag", 0, 0);
			textFont(selectedInfoFont);
			translate(0, 16);
			for (int i = 0; i < 6; i++) {
				// square
				if ((currentView.selectionIsLocked && !detailsCache.isNotWorking() && mgs.countByActivityFlag[i] > 0)
						|| (currentView.currentParameter == AbstractBUGView.P_ACTIVITYFLAG && (currentView == snapshotView || i == currentView.currentValue))) {
					fill(currentView.getColour(AbstractBUGView.P_ACTIVITYFLAG, i));
					b = (currentView.currentParameter == AbstractBUGView.P_ACTIVITYFLAG && (currentView == snapshotView || i == currentView.currentValue)) ? 0 : 1;
					rect(11 + b, -8 + 15 * i + b, 5 - 2 * b, 5 - 2 * b);
				}
				// value
				fill(0, 120);
				textAlign(LEFT);
				text(String.valueOf(i), 0, 15 * i);
				// machine count
				textAlign(RIGHT);
				text((int) mgs.countByActivityFlag[i], 60, 15 * i);
				// %
				if (mg.machinecount > 0) {
					fill(0, 60);
					text(mgs.countByActivityFlag[i] * 100 / mg.machinecount, 90, 15 * i);
					if (i == 0) {
						textAlign(LEFT);
						text("%", 90, 15 * i);
					}
				}
				// tooltip
				if (localMouse.x > 3 && localMouse.y > -12  + 15*i && localMouse.x < 90 && localMouse.y < 15*i + 4) {
					parameterUnderMouse = AbstractBUGView.P_ACTIVITYFLAG;
					tooltipText = BOMDictionary.activityFlagToHR(i, false, false);
					parameterValueUnderMouse = i;
				}
			}
			popMatrix();

			// Connections
			pushMatrix();
			translate(0, 120);
			localMouse.x += 145;
			localMouse.y -= 120;
			fill(120);
			noStroke();
			textAlign(LEFT);
			text("Connections", 0, 0);
			textFont(selectedInfoFont);
			translate(0, 16);
			for (int i = 0; i < 4; i++) {
				if (i == 2) {
					translate(145, -30);
					localMouse.x -= 145;
					localMouse.y += 30;
				}

				// value
				fill(0, 120);
				textAlign(LEFT);
				text(BOMDictionary.connectionsToHR(i), 0, 15 * (i));
				// connections
				textAlign(RIGHT);
				text((int) mgs.connections[i], 90, 15 * (i));
				
				// tooltip
				if (localMouse.x > 3 && localMouse.y > -12  + 15*i && localMouse.x < 90 && localMouse.y < 15*i + 4) {
					parameterUnderMouse = AbstractBUGView.P_CONNECTIONS;
					parameterValueUnderMouse = i;
				}

			}
			popMatrix();

			// Details
			if (currentView.selectionIsLocked
					&& (details.currentCompactTimestamp != currentView.selectedCompactTimestamp || details.currentFacility != currentView.selectedFacility || details.currentMachineGroup != currentView.currentMachineGroup)) {
				if (details.currentFacility == null)
					detailsZoomPan.reset();

				details.currentCompactTimestamp = currentView.selectedCompactTimestamp;
				details.currentFacility = currentView.selectedFacility;
				details.currentMachineGroup = currentView.currentMachineGroup;

				// Adding a delay to redraw to avoid flickering
				detailsCache.startLoad(details.currentFacility.getBusinessunitRealName(), details.currentFacility.facilityName, details.currentCompactTimestamp);
				if (details.currentFacility == null)
					detailsGraphicBuffer.setUpdateFlag();
				else
					framesToUpdateDetailsGraphicBuffer = 4;
			} else if (!currentView.selectionIsLocked && details.currentFacility != null) {
				details.currentFacility = null;
				detailsZoomPan.reset();
				detailsGraphicBuffer.setUpdateFlag();
			}
			if (detailsCache.dataChanged) {
				detailsCache.dataChanged = false;
				detailsGraphicBuffer.setUpdateFlag();
			}

			// Delay to redraw to avoid flickering in branches
			if (framesToUpdateDetailsGraphicBuffer == 0) {
				framesToUpdateDetailsGraphicBuffer = -1;
				detailsGraphicBuffer.setUpdateFlag();
			} else if (framesToUpdateDetailsGraphicBuffer > 0)
				--framesToUpdateDetailsGraphicBuffer;

			popMatrix(); // Top shift - end

			if (details.currentFacility != null) {

				float w = (float) detailsClipper.getClippingRect().getWidth();
				float y = (float) detailsClipper.getClippingRect().getMinY();

				pushMatrix(); // Stuff around graphic buffer - begin
				translate(0, y);

				PMatrix pm = getMatrix();
				resetMatrix();
				
				if (detailsCache.isNotWorking()) {
					detailsZoomPan.allowPanButton(false);
					detailsZoomPan.allowZoomButton(false);
					detailsZoomPan.reset();
				}
				
				detailsGraphicBuffer.draw();
				setMatrix(pm);

				fill(255);
				rect(0, 0, w, DETAILS_CAPTIONS_HEIGHT);

				rotate((float) Math.PI / 2);
				translate(DETAILS_CAPTIONS_HEIGHT - 5, 0);
				fill(120);
				for (int i = 0; i < 9; i++) {
					if (i == details.selectedColumn)
						fill(0);
					text(BOMDictionary.machineFunctionToHR2(i), 0, -28f * i - 2);
					if (i == details.selectedColumn)
						fill(120);
				}
				translate(-DETAILS_CAPTIONS_HEIGHT + 5, 0);
				rotate(-(float) Math.PI / 2);

				// Details shade
				translate(0, DETAILS_CAPTIONS_HEIGHT - 1);

				for (int i = 7; i > 0; --i) {
					fill(255, 255 - i * 32);
					rect(0, i, w, 1);
				}

				popMatrix();

				pushMatrix();
				translate(0, (float) detailsClipper.getClippingRect().getMaxY() + 1);
				for (int i = 7; i > 0; --i) {
					fill(255, 255 - i * 32);
					rect(0, -i, w, 1);
				}

				// Selected machine details
				MachineDetails md = details.selectedMachineDetails;
				if (detailsClipper.getClippingRect().contains(mouseX, mouseY)) {
					if (md != null) {
						tooltipText = IPConverter.intToStr(md.ipaddr);
						tooltipText += "\n" + details.selectedColumnMachineSeq + " / " + details.selectedColumnMachineCount;
						tooltipText += "\n" + "PS: " + BOMDictionary.policyStatusToHR(md.policyStatus, true, true);
						tooltipText += "\n" + "AF: " + BOMDictionary.activityFlagToHR(md.activityFlag);
						tooltipText += "\n" + "Connections: " + md.numConnections;
	
					} else {
						if (details.selectedColumnMachineCount >= 0 && mouseY > detailsClipper.getClippingRect().getMinY() + DetailsView.MARGIN_TOP) {
							tooltipText = "Machines in column: " + details.selectedColumnMachineCount;
						}
					}
				}
				popMatrix();

				if (MachineDetailsComparator.getInstance().sortMode != MachineDetailsComparator.SM_IP) {
					pushMatrix();
					translate(0, (float) gridClipper.getClippingRect().getMaxY() + 15);
					fill(120);
					textAlign(LEFT, TOP);
					textFont(bottomRowFont);
					text("Machines are sorted by " + BOMDictionary.DetailsSortOrderToHR(MachineDetailsComparator.getInstance().sortMode) + ".", 0, 0);
					popMatrix();
				}
			}
		}
		popMatrix(); // Right shift - end
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

		// Toggle help
		if (key == 'h' && !helpScreen.getIsActive()) {
			helpScreen.setIsActive(true);
			return;
		}

		helpScreen.setIsActive(false);

		boolean keyMore = key == '+' || key == '=';
		boolean keyLess = key == '-' || key == '_';
		boolean keySpace = key == ' ';

		keys[keyCode] = true;

		// Businessunits in the grid order
		if (checkKey("g") && keyCode >= '1' && keyCode <= '3') {
			String text = null;
			int l = 0;
			if (keyCode == '1') {
				l = BusinessunitGrid.LAYOUT_SEQ;
				text = "Arrange business units by name";
			} else if (keyCode == '2') {
				l = BusinessunitGrid.LAYOUT_GEO;
				text = "Arrange business units geographically";
			} else {
				l = BusinessunitGrid.LAYOUT_GEO_EXCL_DC;
				text = "Arrange business units geographically (with data centres in the bottom)";
			}
			
			if (businessunitGrid.getLayout() == l)
				return;
			
			businessunitGrid.setLayout(l);
			flyingText.startFly(text);
			gridGraphicBuffer.setUpdateFlag();
			return;
		}

		// Reset Grid View
		if (checkKey("g") && keySpace) {
			gridZoomPan.reset();
			gridZoomPan.setPanOffset(0, 0);
			flyingText.startFly("Reset grid’s pan and zoom");
			gridGraphicBuffer.setUpdateFlag();
			return;
		}

		// Facility sort order: 1 - name, 2 - timezone and name, 3 - lat, 4 - lon ' ' or a parameter
		if (checkKey("f") && currentView != overallView) {

			FacilityComparator fc = FacilityComparator.getInstance();

			// Sorting by a parameter
			if (checkKey("a") || checkKey("p") || checkKey("c")) {
				if (keyCode >= '0' && keyCode <= '5') {
					if (checkKey("a")) {
						fc.sortMode = FacilityComparator.SM_ACTIVITY_FLAG;
						sortText = "percentage of activity flag = " + (keyCode - 0x30);
					} else if (checkKey("p")) {
						fc.sortMode = FacilityComparator.SM_POLICY_STATUS;
						sortText = "percentage of policy status = " + (keyCode - 0x30);
					} else if (checkKey("c")) {
						if (!(keyCode > '0' && keyCode < '5'))
							return;
						fc.sortMode = FacilityComparator.SM_CONNECTIONS;
						sortText = BOMDictionary.connectionsToHR(keyCode - 0x31) + " connections";
					}

					sortText += " at " + CompactTimestamp.toHRString(currentView.selectedCompactTimestamp);

					if (currentView.currentMachineGroup != 0) {
						sortText = sortText + " of " + BOMDictionary.machineGroupToHR(currentView.currentMachineGroup);
					}

					flyingText.startFly("Sort facilities by " + sortText);
					fc.sortSubmode = keyCode - 0x30;

					if (fc.sortMode == FacilityComparator.SM_CONNECTIONS)
						--fc.sortSubmode;

					fc.sortMachineGroup = currentView.currentMachineGroup;
					fc.sortComactTimestamp = currentView.selectedCompactTimestamp >= 0 ? currentView.selectedCompactTimestamp : currentView.currentCompactTimestamp;

					businessunitGrid.sortFacilities();
				}

			} else {

				if (keyCode >= '1' && keyCode <= '6') {
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
					case FacilityComparator.SM_IP_MIN:
						sortText = "lowest ip address";
						break;
					case FacilityComparator.SM_IP_MAX:
						sortText = "highest ip address";
						break;
					}
					flyingText.startFly("Sort facilities by " + (sortText == null ? "name" : sortText));
					businessunitGrid.sortFacilities();
				} else if (keySpace) {
					fc.sortHeadquarters = !fc.sortHeadquarters;

					if (fc.sortHeadquarters)
						flyingText.startFly("Include headquarters when sorting facilities");
					else
						flyingText.startFly("Exclude headquarters when sorting facilities");
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

		// Show activity flag / connections / policy status
		if (checkKey("a") || checkKey("c") || checkKey("p")) {
			int parameter = -1;
			int value = -1;

			if (keyCode >= '0' && keyCode <= '5')
				value = keyCode - 0x30;
			if (checkKey("c"))
				--value;

			// extracting parameter
			if (checkKey("a")) {
				parameter = AbstractBUGView.P_ACTIVITYFLAG;
			} else if (checkKey("c")) {
				if (currentView == overallView)
					return;
				
				if (currentView == timeView && (value < 0 || value > 3))
					return;
				
				parameter = AbstractBUGView.P_CONNECTIONS;
			} else if (checkKey("p")) {
				parameter = AbstractBUGView.P_POLICYSTATUS;
			}
			
			// extracting value
			if (parameter == -1)
				return;

			if (currentView == timeView) {
				if (value == -1)
					return;
				switchViewParameter(parameter, value);
			} else {
				switchViewParameter(parameter);
			}

			
			return;
		}

		// Choose machinegroup (all, atms, servers, workstations)
		if (checkKey("m") && (keyCode >= '0' && keyCode <= '9' || keyLess || keySpace)) {
			int oldMG = currentView.currentMachineGroup;
			if (keySpace)
				currentView.currentMachineGroup = 0;
			else if (keyCode == '0')
				currentView.currentMachineGroup = 3 + 7;
			else if (keyLess)
				currentView.currentMachineGroup = 3 + 8;
			else
				currentView.currentMachineGroup = keyCode - 0x30;
			
			if (currentView.currentMachineGroup == oldMG)
				return;

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

		// Details - sorting
		if (checkKey("d") && keyCode >= '1' && keyCode <= '4') {
			MachineDetailsComparator mdc = MachineDetailsComparator.getInstance();
			mdc.sortMode = keyCode - 0x31;
			flyingText.startFly("Sort machines within a facility by " + BOMDictionary.DetailsSortOrderToHR(mdc.sortMode));
			detailsGraphicBuffer.setUpdateFlag();
		}

		// Details - reset view
		if (checkKey("d") && keySpace) {
			if (details.currentFacility == null)
				return;

			detailsZoomPan.reset();
			detailsZoomPan.setPanOffset(0, 0);
			flyingText.startFly("Reset detail’s pan and zoom");
			detailsGraphicBuffer.setUpdateFlag();
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
				flyingText.startFly("Temporal view");
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

			currentView.currentParameter = oldView.currentParameter;
			if (currentView == overallView && currentView.currentParameter == AbstractBUGView.P_CONNECTIONS) {
				currentView.currentParameter = AbstractBUGView.P_POLICYSTATUS;
			}
			if (currentView.currentParameter == AbstractBUGView.P_CONNECTIONS && oldView.currentParameter != AbstractBUGView.P_CONNECTIONS) {
				currentView.currentValue = 1;
			}

			currentView.currentMachineGroup = oldView.currentMachineGroup;
			currentView.resetRange();

			gridGraphicBuffer.setUpdateFlag();
			return;
		}

		// Facility ±
		if ((keyCode == UP || keyCode == DOWN) && currentView != overallView && currentView.selectionIsLocked) {
			int df = keyCode == DOWN ? 1 : -1;
			String label = df > 0 ? "Next facility" : "Previous facility";
			if (checkKey(SHIFT)) {
				df *= 5;
				label = df > 0 ? "5ᵗʰ next facility" : "5ᵗʰ previous facility";
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
				double newRangeMin = currentViewC.rangeMin;
				double dr = (currentViewC.rangeMaxLimit - currentViewC.rangeMinLimit) / 10;

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

				detailsGraphicBuffer.setUpdateFlag();
				return;
			}
			if (checkKey("]")) {
				double newRangeMax = currentViewC.rangeMax;
				double dr = (currentViewC.rangeMaxLimit - currentViewC.rangeMinLimit) / 10;

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
				detailsGraphicBuffer.setUpdateFlag();
				return;
			}
		}

		// Unlock focus (or hid the help)
		if (key == ESC) {
			if (helpScreen.getIsActive()) {
				helpScreen.setIsActive(false);
				key = 0; // Prevent applet from closing
				return;
			}
			timeView.selectionIsLocked = false;
			snapshotView.selectionIsLocked = false;
			mouseMoved();
			key = 0; // Prevent applet from closing
			return;
		}

		// Back compatibility
		if (key == 'r') {
			flyingText.startFly("To reset grid press G + SPACE. See help.");
			return;
		}
		if (key == 's') {
			flyingText.startFly("To sort facilities press F + sort mode button. See help.");
			return;
		}
	}

	protected void switchViewParameter(int parameter) {
		switchViewParameter(parameter, -1);
	}
	
	protected void switchViewParameter(int parameter, int value) {
		String text = "";
		boolean needToResetRange = false;

		if (parameter == AbstractBUGView.P_ACTIVITYFLAG) {
			text = "Activity flag";
			if (currentView == timeView)
				text += " = " + BOMDictionary.activityFlagToHR(value);
			if (currentView.currentParameter == AbstractBUGView.P_CONNECTIONS)
				needToResetRange = true;
		} else if (parameter == AbstractBUGView.P_CONNECTIONS) {
			if (currentView == overallView)
				return;

			if (currentView == timeView && (value < 0 || value > 3))
				return;

			text = "Connections";
			if (currentView == timeView)
				text += " - " + BOMDictionary.connectionsToHR(value);
			if (currentView.currentParameter != AbstractBUGView.P_CONNECTIONS)
				needToResetRange = true;
		} else if (parameter == AbstractBUGView.P_POLICYSTATUS) {
			text = "Policy status";
			if (currentView == timeView)
				text += " = " + BOMDictionary.policyStatusToHR(value);
			if (currentView.currentParameter == AbstractBUGView.P_CONNECTIONS)
				needToResetRange = true;
		}

		if (currentView.currentParameter == parameter && (value == -1 || value == currentView.currentValue))
			return;
		
		currentView.currentParameter = parameter;
		flyingText.startFly(text);

		if (value != -1)
			currentView.currentValue = value;
		if (needToResetRange)
			currentView.resetRange();
		gridGraphicBuffer.setUpdateFlag();
	}

	public void keyReleased() {
		if (loadStage < 42)
			return;
		keys[keyCode] = false;
	}

	public void mouseMoved() {
		if (loadStage < 42)
			return;

		PVector mouseCoordWithOffset = new PVector(mouseX, mouseY);

		// Getting object details by mouse coordinates
		if (!gridClipper.getClippingRect().contains(mouseX, mouseY)) {
			if (!currentView.selectionIsLocked)
				currentView.selectedFacility = null;
		} else {
			if (!currentView.selectionIsLocked) {
				PVector convertedMouseCoordToGrid = gridZoomPan.getZoomPanState().getDispToCoord(mouseCoordWithOffset);
				currentView.selectAt((int) convertedMouseCoordToGrid.x - (int) gridClipper.getClippingRect().getMinX(), (int) convertedMouseCoordToGrid.y
						- (int) gridClipper.getClippingRect().getMinY());
			}
		}

		if (detailsClipper.getClippingRect().contains(mouseX, mouseY)) {
			PVector convertedMouseCoordToDetails = detailsZoomPan.getZoomPanState().getDispToCoord(mouseCoordWithOffset);
			details.selectAt((int) convertedMouseCoordToDetails.x - (int) detailsClipper.getClippingRect().getMinX(), (int) convertedMouseCoordToDetails.y
					- (int) detailsClipper.getClippingRect().getMinY());
		} else {
			details.resetSelected();
		}
	}

	public void mouseClicked() {
		if (loadStage < 42)
			return;

		helpScreen.setIsActive(false);
		
		// Emulating change of parameter (call of key press) when user clicked on ps / af / c in facility info 
		if (parameterUnderMouse != 0) {
			if (currentView != timeView)
				switchViewParameter(parameterUnderMouse);
			else 
				switchViewParameter(parameterUnderMouse, parameterValueUnderMouse);
		}

		if (gridClipper.getClippingRect().contains(mouseX, mouseY)) {
			PVector mouseCoordWithOffset = new PVector(mouseX, mouseY);
			PVector convertedMouseCoord = gridZoomPan.getZoomPanState().getDispToCoord(mouseCoordWithOffset);
			currentView.selectAt((int) convertedMouseCoord.x - (int) gridClipper.getClippingRect().getMinX(), (int) convertedMouseCoord.y
					- (int) gridClipper.getClippingRect().getMinY());
			currentView.selectionIsLocked = currentView.selectedFacility != null;
		}
		
		if (detailsClipper.getClippingRect().contains(mouseX, mouseY) && currentView.selectedFacility != null) {
			if (details.selectedColumn != -1) {
				int mg = details.selectedColumn + 3;
				if (mg == 3)
					mg = 1;
				
				if (currentView.currentMachineGroup != mg) {
					currentView.currentMachineGroup = mg;
				} else {
					switch (mg) {
					case 1:
						currentView.currentMachineGroup = 0;
						break;
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
						currentView.currentMachineGroup = 2;
						break;
					case 9:
					case 10:
					case 11:
						currentView.currentMachineGroup = 3;
						break;
					default:
						return;
					}
				}
						
				flyingText.startFly("Machine group: " + BOMDictionary.machineGroupToHR(currentView.currentMachineGroup));
				gridGraphicBuffer.setUpdateFlag();

			}
		}
		
	}

	// Class that implements ThreadedDraw for drawing in a different thread for the grid
	class GridThreadedDraw implements ThreadedDraw {

		// Code to draw onto the buffer
		public void threadedDraw(PGraphics canvas, ZoomPanState zoomPanState, Object extraInfo) {
			canvas.pushMatrix();
			zoomPanState.transform(canvas);
			canvas.translate((float) gridClipper.getClippingRect().getMinX(), (float) gridClipper.getClippingRect().getMinY());
			currentView.draw(canvas, Thread.currentThread());
			canvas.popMatrix();
		}
	}

	// Class that implements ThreadedDraw for drawing in a different thread for details
	class DetailsThreadedDraw implements ThreadedDraw {

		// Code to draw onto the buffer
		public void threadedDraw(PGraphics canvas, ZoomPanState zoomPanState, Object extraInfo) {
			canvas.pushMatrix();
			zoomPanState.transform(canvas);
			canvas.translate((float) detailsClipper.getClippingRect().getMinX(), (float) detailsClipper.getClippingRect().getMinY());
			details.draw(canvas, detailsCache, currentView, Thread.currentThread());
			canvas.popMatrix();
			mouseMoved();
		}
	}
}
