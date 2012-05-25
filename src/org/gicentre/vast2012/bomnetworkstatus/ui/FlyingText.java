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
 */

// TODO Pass font as a constructor parameter
// TODO Make duration an option

public class FlyingText {

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
			if (instance.frame > 60)
				instances.remove(instance);

			PFont flyingTextFont = new PFont(new Font("Helvetica", 0, (int) (25 + 0.3f * instance.frame)), true);
			float opacity = flyingTextMaxOpacity;
			if (instance.frame < 20)
				opacity = PApplet.lerp(0, flyingTextMaxOpacity, 0.05f * instance.frame);
			else if (instance.frame > 30)
				opacity = PApplet.lerp(flyingTextMaxOpacity, 0, 1 - (60f - instance.frame) / 30f);

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
