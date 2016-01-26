package com.hayukleung.app.widget.paintpad.tools;

import android.graphics.Paint;

/**
 * 画笔
 */
public class Brush extends Paint {
    /**
     * Generate the instance when the class is loaded
     */
    private static final Brush brush = new Brush();

    /**
     * Make the constructor private, to stop others to create instance by the
     * default constructor
     */
    private Brush() {
    }

    /**
     * Provide a static method that can be access by others.
     *
     * @return the single instance
     */
    public static Brush getPen() {
        return brush;
    }

    /**
     * Reset the brush
     *
     * @param color
     * @param textWidth
     */
    public void reset(int color, float textWidth) {
        brush.setAntiAlias(true);
        brush.setDither(true);
        brush.setColor(color);
        brush.setStyle(Style.STROKE);
        brush.setStrokeJoin(Join.ROUND);
        brush.setStrokeCap(Cap.ROUND);
        brush.setStrokeWidth(textWidth);
    }

    public void setPaintColor(int color) {
        brush.setColor(color);
    }

    public void setPaintWidth(float textWidth) {
        brush.setStrokeWidth(textWidth);
    }

    public float getPaintWidth() {
        return brush.getStrokeWidth();
    }
}