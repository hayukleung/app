package com.hayukleung.app.widget.paintpad.drawings;

import android.graphics.Canvas;

import com.hayukleung.app.widget.paintpad.tools.Brush;

/**
 * A straight line.
 */
public class StraightLine extends Drawing {
	@Override
	public void draw(Canvas canvas) {
		canvas.drawLine(this.startX, this.startY, this.stopX, this.stopY,
				Brush.getPen());
	}
}
