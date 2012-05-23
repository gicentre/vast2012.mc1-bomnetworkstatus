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
import java.util.HashSet;

import org.gicentre.utils.gui.ThreadedDraw;
import org.gicentre.utils.gui.ThreadedGraphicBuffer;
import org.gicentre.utils.move.ZoomPan;
import org.gicentre.utils.move.ZoomPanState;
import org.gicentre.utils.move.ZoomPan.ZoomPanBehaviour;
import org.gicentre.utils.slippymap.SlippyMap.SlippyMapType;
import org.gicentre.vast2012.bomnetworkstatus.ui.AbstractBusinessunitView;
import org.gicentre.vast2012.bomnetworkstatus.ui.BusinessunitGrid;
import org.gicentre.vast2012.bomnetworkstatus.ui.FlyingText;
import org.gicentre.vast2012.bomnetworkstatus.ui.TimeAndBrightnessBusinessunitView;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

public class BOMNetworkStatusApp extends PApplet {

	private static final long serialVersionUID = 4320695214813440866L;

	// public static String dbURL = "jdbc:postgresql://localhost/vast_mc1";
	// public static String dbUser = "postgres";
	// public static String dbPassword = "hh";

	public HashMap<String, Businessunit> businessunits;
	public BusinessunitGrid businessunitGrid;
	ZoomPan zoomPan;
	ThreadedGraphicBuffer gridGraphicBuffer;
	Rectangle graphicBufferBounds; // screen bounds of the GraphicBuffer
	
	FlyingText flyingText;

	TimeAndBrightnessBusinessunitView view;

	PFont titleFont;
	boolean[] keys = new boolean[526];
	
	public void setup() {

		size(300, 300);
		titleFont = createFont("Helvetica", 20);
		//smooth();
	}

	int loadStage = 0;

	public void draw() {
		background(255);
		switch (loadStage++) {
		case 0:
			return;
		case 1:
			fill(0, 95);
			textAlign(CENTER, CENTER);
			text("Loading facility status data...", width / 2, height / 2);
			stroke(10);
			return;
		case 2:
			// Loading the data
			businessunits = loadFaclityStatusData();
			text("Preparing the grid...", width / 2, height / 2);
			return;

		case 3:
			// Preparing the grid and the graphic buffer
			businessunitGrid = new BusinessunitGrid(businessunits,
					BusinessunitGrid.LAYOUT_SEQ);

			businessunitGrid.setY(40);

			size((int)(businessunitGrid.getX() + businessunitGrid.getWidth()) + 500, (int)(businessunitGrid.getY() + businessunitGrid.getHeight()));

			view = new TimeAndBrightnessBusinessunitView(businessunitGrid);
			view.currentParameter = TimeAndBrightnessBusinessunitView.P_ACTIVITYFLAG;
			view.currentValue = 0;
			view.currentCompactTimestamp = CompactTimestamp
					.FullTimestampToCompact("2012-02-04 08:00:00");

			zoomPan = new ZoomPan(this);
			//zoomPan.setZoomPanBehaviour(ZoomPanBehaviour.VERTICAL_ONLY);
			zoomPan.setMinZoomScale(1);
			zoomPan.setMaxZoomScale(10);
			//zoomPan.set
			zoomPan.setZoomMouseButton(RIGHT);
			graphicBufferBounds = new Rectangle((int) businessunitGrid.getX(),
					(int) businessunitGrid.getY(),
					(int) businessunitGrid.getWidth(),
					(int) businessunitGrid.getHeight());
			gridGraphicBuffer = new ThreadedGraphicBuffer(this, zoomPan,
					new ThreadedDrawClass(), graphicBufferBounds);
			gridGraphicBuffer.setUpdateDuringZoomPan(false);
			gridGraphicBuffer.setUseFadeEffect(true, 20);
			
			flyingText = new FlyingText(this, businessunitGrid.getX() + businessunitGrid.getWidth()/2, businessunitGrid.getY() + businessunitGrid.getHeight()/2);

			text("Almost done...", width / 2, height / 2);
			return;
		}

		loadStage = 42;
		background(255);

		noStroke();
		fill(220);
		rect(graphicBufferBounds.x, graphicBufferBounds.y, graphicBufferBounds.width, graphicBufferBounds.height);

		gridGraphicBuffer.draw();
		if (view.focusIsLocked)
			view.highlightFocusedElement(this.g);
		drawTitle();
		drawLegend();
		drawTimeline();
		flyingText.draw();
	}

	/**
	 * Draws the text above the grid
	 */
	protected void drawTitle() {
		textFont(titleFont);
		textAlign(LEFT, BASELINE);
		fill(60);

		String title = "";

		title += BOMDictionary.machineGroupToHR(view.currentMachineGroup) + ": ";
		
		switch (view.currentParameter) {
		case AbstractBusinessunitView.P_ACTIVITYFLAG:
			title += "activityflag = " + BOMDictionary.activityFlagToHR(view.currentValue);
			break;
		case AbstractBusinessunitView.P_POLICYSTATUS:
			title += "policystatus = " + BOMDictionary.policyStatusToHR(view.currentValue);
			break;
		case AbstractBusinessunitView.P_CONNECTIONS:
			title += "connections - " + BOMDictionary.connectionsToHR(view.currentValue);
		}
		text(title, 5, 25);
	}

	/**
	 * Draws the legend on the right from the title
	 */
	protected void drawLegend() {
	}

	private void drawTimeline() {
	}

	/* multi-key-press support */
	boolean checkKey(String k) {
		for (int i = 0; i < keys.length; i++)
			if (KeyEvent.getKeyText(i).toLowerCase().equals(k.toLowerCase()))
				return keys[i];
		return false;
	}

	public void keyPressed() {
		if (loadStage < 42)
			return;
		
		keys[keyCode] = true;

		// Toggle timezone alignment
		if (key == 't') {
			view.timeIsRelative = !view.timeIsRelative;
			flyingText.startFly("Toggle BMT / local time");
			gridGraphicBuffer.setUpdateFlag();
			return;
		}

		// Show policy status
		if (checkKey("p")) {
			view.currentParameter = AbstractBusinessunitView.P_POLICYSTATUS;
			if (keyCode >= '0' && keyCode <= '5') {
				view.currentValue = keyCode - 0x30;
			}
			flyingText.startFly("Policy status = "+BOMDictionary.policyStatusToHR(view.currentValue));
			gridGraphicBuffer.setUpdateFlag();
			return;
		}

		// Show activityflag
		if (checkKey("a")) {
			view.currentParameter = AbstractBusinessunitView.P_ACTIVITYFLAG;
			if (keyCode >= '0' && keyCode <= '5') {
				view.currentValue = keyCode - 0x30;
			}
			flyingText.startFly("Activity flag = "+BOMDictionary.activityFlagToHR(view.currentValue));
			gridGraphicBuffer.setUpdateFlag();
			return;
		}

		// Show connections (total, min, max, avg, sd)
		if (checkKey("c") && keyCode >= '0' && keyCode <= '4') {
			view.currentParameter = AbstractBusinessunitView.P_CONNECTIONS;
			view.currentValue = keyCode - 0x30;
			flyingText.startFly("Connections - "+BOMDictionary.connectionsToHR(view.currentValue));
			gridGraphicBuffer.setUpdateFlag();
			return;
		}

		// Choose machinegroup (all, atms, servers, workstations)
		if (checkKey("m") && keyCode >= '0' && keyCode <= '3') {
			view.currentMachineGroup = keyCode - 0x30;
			flyingText.startFly("Machine group: "+BOMDictionary.machineGroupToHR(view.currentMachineGroup));
			gridGraphicBuffer.setUpdateFlag();
			return;
		}
		
		// Facility sort order: 1 - name, 2 - timezone and name
		if (checkKey("s") && keyCode >= '1' && keyCode <= '2') {
			Facility.sortMode = keyCode - 0x31;
			if (Facility.sortMode == Facility.SM_NAME)
				flyingText.startFly("Sort facilities by name");
			else
				flyingText.startFly("Sort facilities by timezone");
			for (Businessunit bu : businessunitGrid.getBusinessunits().values()) {
				bu.sortFacilities();
			}
			gridGraphicBuffer.setUpdateFlag();
			return;
		}
		
		// Reset View
		if (key == 'r') {
			zoomPan.reset();
			zoomPan.setPanOffset(0, 0);
			flyingText.startFly("Reset view");
			gridGraphicBuffer.setUpdateFlag();
			return;
		}
		
		// Unlock focus
		if (key == ESC) {
			view.focusIsLocked = false;
			mouseMoved();
			key = 0; // Prevent applet from closing
			return;
		}

	}

	public void keyReleased() {
		keys[keyCode] = false;
	}
	
	public void mouseMoved() {
		if (loadStage < 42)
			return;
		// Getting object details by mouse coordinates
		if (!businessunitGrid.getRectangle().contains(mouseX, mouseY)) {
			if (!view.focusIsLocked)
				view.focusedFacility = null;
		} else {
			if (!view.focusIsLocked) {
				view.focusOn(mouseX, mouseY);
			}
		}
	}
	
	public void mouseClicked() {
		if (businessunitGrid.getRectangle().contains(mouseX, mouseY)) {
			view.focusOn(mouseX, mouseY);
			view.focusIsLocked = true;
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
					String currentFName = tokens[1];

					Businessunit currentBu = businessunits.get(currentBuName);
					if (currentBu == null) {
						currentBu = new Businessunit(currentBuName);
						businessunits.put(currentBuName, currentBu);
					}

					Facility currentF = new Facility();
					currentBu.facilities.put(currentFName, currentF);
					currentBu.sortedFacilities.add(currentF);
					currentF.businessunit = currentBuName;
					currentF.facility = currentFName;
					currentF.timezoneOffset = Short.valueOf(tokens[4]);
					currentF.machinegroups[0] = new MachineGroup(
							Integer.valueOf(tokens[5]));
					currentF.machinegroups[1] = new MachineGroup(
							Integer.valueOf(tokens[6]));
					currentF.machinegroups[2] = new MachineGroup(
							Integer.valueOf(tokens[7]));
					currentF.machinegroups[3] = new MachineGroup(
							Integer.valueOf(tokens[8]));
					currentBu.sortFacilities();
				}
			}
			reader.close();
			System.out.println("Loaded facilities data");

			// Reading statuses for all facilities
			reader = createReader("facilitystatus.tab");
			reader.readLine(); // Skipping the first line with headers

			while (true) {
				line = reader.readLine();
				if (line == null) {
					break;
				} else {
					String[] tokens = split(line, TAB);

					String currentBuName = tokens[0];
					String currentFName = tokens[1];
					short compactTimestamp = CompactTimestamp
							.FullTimestampToCompact(tokens[2]);
					if (businessunits.get(currentBuName) == null)
						continue;
					Facility currentF = businessunits.get(currentBuName).facilities
							.get(currentFName);

					for (short machineGroupId = 0; machineGroupId < 4; machineGroupId++) {
						MachineGroupStatus currentMGStatus = new MachineGroupStatus();

						for (int i = 0; i < 6; i++)
							currentMGStatus.countByActivityFlag[i] = Integer
									.parseInt(tokens[3 + 24 + machineGroupId
											* 6 + i]);
						for (int i = 0; i < 6; i++)
							currentMGStatus.countByPolicyStatus[i] = Integer
									.parseInt(tokens[3 + machineGroupId * 6 + i]);

						currentMGStatus.connections[0] = Integer
								.parseInt(tokens[3 + 24 + 24 + machineGroupId]); // count
						currentMGStatus.connections[1] = Integer
								.parseInt(tokens[3 + 24 + 24 + 12
										+ machineGroupId]); // min
						currentMGStatus.connections[2] = Integer
								.parseInt(tokens[3 + 24 + 24 + 8
										+ machineGroupId]); // max
						currentMGStatus.connections[3] = Float
								.parseFloat(tokens[3 + 24 + 24 + 4
										+ machineGroupId]); // avg
						currentMGStatus.connections[4] = Float
								.parseFloat(tokens[3 + 24 + 24 + 16
										+ machineGroupId]); // sd

						currentF.machinegroups[machineGroupId].statuses[compactTimestamp] = currentMGStatus;

					}
				}
			}
			reader.close();
			System.out.println("Loaded facilitystatuses data");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return businessunits;
	}

	// Class that implements ThreadedDraw for drawing in a different thread
	class ThreadedDrawClass implements ThreadedDraw {

		// Code to draw onto the buffer
		public void threadedDraw(PGraphics canvas, ZoomPanState zoomPanState,
				Object extraInfo) {

			Thread currentThread = Thread.currentThread();
			zoomPanState.transform(canvas);
			print("Start draw...");
			view.draw(canvas, currentThread);
			//canvas.popMatrix();
			println(" done.");
			// if (currentThread.isInterrupted())
			// return;
		}
	}

}
