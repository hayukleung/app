package com.hayukleung.app.widget.paintpad.drawings;

import android.graphics.Canvas;

import com.hayukleung.app.widget.paintpad.tools.Brush;

/**
 * A rectangle.
 */
public class Rect extends Drawing {
	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(this.startX, this.startY, this.stopX, this.stopY,
				Brush.getPen());
	}
}
