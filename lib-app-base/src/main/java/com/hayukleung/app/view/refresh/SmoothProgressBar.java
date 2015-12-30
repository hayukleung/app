package com.hayukleung.app.view.refresh;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.hayukleung.app.view.util.ViewUtils;

public class SmoothProgressBar extends View {
    private static final float PROGRESS_BAR_HEIGHT = 4;//dp

    private int mProgressBarHeight;
    private SwipeProgressBar mProgressBar;

    public SmoothProgressBar(Context context) {
        this(context, null);
    }

    public SmoothProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmoothProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                if (specSize < size) {
                    result = specSize | MEASURED_STATE_TOO_SMALL;
                } else {
                    result = size;
                }
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result | (childMeasuredState & MEASURED_STATE_MASK);
    }

    private void init() {
        mProgressBar = new SwipeProgressBar(this);
        mProgressBarHeight = ViewUtils.dp2px(getContext(), PROGRESS_BAR_HEIGHT);
        mProgressBar.setColorScheme(0xff00ddff, 0xff99cc00, 0xffffbb33, 0xffff4444);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int dw = 0;
        int dh = mProgressBarHeight;

        setMeasuredDimension(resolveSizeAndState(dw, widthMeasureSpec, 0), resolveSizeAndState(dh, heightMeasureSpec, 0));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mProgressBar.setBounds(0, 0, right - left, bottom - top);
    }

    public void setTriggerPercentage(float percent) {
        mProgressBar.setTriggerPercentage(percent);
    }

    public void setPercentage(float percent) {
        mProgressBar.setPercentage(percent);
    }

    /**
     * Set the four colors used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     */
    public void setColorSchemeResources(int colorRes1, int colorRes2, int colorRes3, int colorRes4) {
        final Resources res = getResources();
        setColorSchemeColors(res.getColor(colorRes1), res.getColor(colorRes2), res.getColor(colorRes3), res.getColor(colorRes4));
    }

    /**
     * Set the four colors used in the progress animation. The first color will
     * also be the color of the bar that grows in response to a user swipe
     * gesture.
     */
    public void setColorSchemeColors(int color1, int color2, int color3, int color4) {
        mProgressBar.setColorScheme(color1, color2, color3, color4);
    }

    public void start() {
        mProgressBar.start();
    }

    public void stop() {
        mProgressBar.stop();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        mProgressBar.draw(canvas);
    }
}
