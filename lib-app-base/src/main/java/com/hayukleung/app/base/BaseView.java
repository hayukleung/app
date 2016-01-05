package com.hayukleung.app.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义View的基础
 *
 * BaseView.java
 * <p/>
 * Created by hayukleung on 1/1/16.
 */
public abstract class BaseView extends View {

    /**
     * 绘制线程
     */
    private DrawingThread mDrawingThread;
    /**
     * 绘制刷新时间间隔，单位毫秒
     */
    protected int mAnimationGapMS = 30;
    /**
     * View是否在屏幕中
     */
    private boolean mRunning = true;

    public BaseView(Context context) {
        super(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected final void onDraw(Canvas canvas) {

        if (null == mDrawingThread) {
            mRunning = true;
            mDrawingThread = new DrawingThread();
            mDrawingThread.start();
        } else {
            doDraw(canvas);
        }
    }

    @Override
    protected final void onDetachedFromWindow() {
        mRunning = false;
        super.onDetachedFromWindow();
    }

    /**
     * 绘制
     *
     * @param canvas
     */
    protected abstract void doDraw(Canvas canvas);

    /**
     * 逻辑
     */
    protected abstract void doLogic();

    /**
     * 绘制线程
     */
    private class DrawingThread extends Thread {

        @Override
        public void run() {

            while (mRunning) {
                postInvalidate();
                doLogic();
                try {
                    Thread.sleep(mAnimationGapMS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
