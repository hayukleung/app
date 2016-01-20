package com.hayukleung.app.widget.paintpad.drawings;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.hayukleung.app.widget.paintpad.tools.Brush;

public class Oval extends Drawing {
	private RectF mRectF = null;

	public Oval() {
		mRectF = new RectF();
	}

	@Override
	public void draw(Canvas canvas) {
		mRectF.left = this.startX;
		mRectF.right = this.stopX;
		mRectF.top = this.startY;
		mRectF.bottom = this.stopY;

		canvas.drawOval(mRectF, Brush.getPen());
	}
}
