package org.gicentre.vast2012.bomnetworkstatus.ui;

import java.awt.Font;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

/**
 * Shows text that "flies" towards a user for a few seconds and then fades out.
 * 
 * Usage:
 * 
 * FlyingText ft;
 * 
 * setup () {
 *     ft = new FlyingText(this, width/2, height/2);
 * }
 * 
 * draw () {
 *     ft.draw();
 * }
 * 
 * someEventHappened() {
 *     ft.startFly("Hello world!");
 * }
 * 
 * somethingCompletelyChangedInTheSketch() {
 *     ft.stopFly();
 * }
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

// TODO Pass font as a constructor parameter
// TODO Make duration an option

public class FlyingText {

	public static final int FRAMES = 60;

	private class FlyingTextInstance {
		String text = null;
		int frame = 0;
	};

	protected PApplet aContext;
	protected float x;
	protected float y;

	ArrayList<FlyingTextInstance> instances;

	private final int flyingTextMaxOpacity = 180;

	public FlyingText(PApplet aContext, float x, float y) {
		this.aContext = aContext;
		this.x = x;
		this.y = y;
		instances = new ArrayList<FlyingTextInstance>();
	}

	public void startFly(String text) {
		FlyingTextInstance newInstance = new FlyingTextInstance();
		newInstance.text = text;
		newInstance.frame = 0;
		instances.add(0, newInstance);
	}

	public void stopFly() {
		instances.clear();
	}

	public void draw() {
		for (FlyingTextInstance instance : new CopyOnWriteArrayList<FlyingTextInstance>(instances)) {
			if (instance.frame > FRAMES)
				instances.remove(instance);

			// tg(x*2.2+pi/2+0.65)/5 + 0.25 - zoom function x[0, 1] y ≈ (0, 1) for easing
			// tg(x*2.2+pi/2+0.5)/4 + 0.4
			PFont flyingTextFont = new PFont(new Font("Helvetica", 0, (int) (20 + (Math.tan(2.2f * instance.frame / FRAMES + Math.PI/2 + 0.5) / 4 + 0.4) * 40)), true);
			float opacity = flyingTextMaxOpacity;
			if (instance.frame < FRAMES / 3)
				opacity = PApplet.lerp(0, flyingTextMaxOpacity, 0.05f * instance.frame);
			else if (instance.frame > FRAMES / 2)
				opacity = PApplet.lerp(flyingTextMaxOpacity, 0, 1 - 2f * (FRAMES - instance.frame) / FRAMES);

			aContext.textFont(flyingTextFont);
			aContext.textAlign(PGraphics.CENTER, PGraphics.BASELINE);
			aContext.fill(255, opacity * 1.2f);
			aContext.text(instance.text, x, y);
			aContext.fill(0, opacity);
			aContext.text(instance.text, x, y);

			instance.frame++;
		}
	}
}
