package org.gicentre.vast2012.bomnetworkstatus.ui;

import java.awt.Font;
import java.util.Arrays;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PGraphics2D;
import processing.core.PImage;

public class FlyingText {

	private PApplet aContext;
	private float x;
	private float y;

	private int frame = 0;
	private String text = null;

	private final int flyingTextMaxOpacity = 180;

	public FlyingText(PApplet aContext, float x, float y) {
		this.aContext = aContext;
		this.x = x;
		this.y = y;
	}

	public void startFly(String text) {
		this.text = text;
		this.frame = 0;
	}

	public void stopFly() {
		this.text = null;
	}

	public void draw() {
		if (frame > 60)
			text = null;

		if (text == null)
			return;

		PFont flyingTextFont = new PFont(new Font("Helvetica", 0,
				(int) (25 + 0.3f * frame)), true);
		float opacity = flyingTextMaxOpacity;
		if (frame < 20)
			opacity = PApplet.lerp(0, flyingTextMaxOpacity, 0.05f * frame);
		else if (frame > 30)
			opacity = PApplet.lerp(flyingTextMaxOpacity, 0,
					1 - (60f - frame) / 30f);

		aContext.textFont(flyingTextFont);
		aContext.textAlign(PGraphics.CENTER, PGraphics.BASELINE);
		aContext.fill(255, opacity * 1.2f);
		aContext.text(text, x, y);
		aContext.fill(0, opacity);
		aContext.text(text, x, y);

		// Restoring transparent background
		frame++;

	}
}
