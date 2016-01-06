/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hayukleung.app.widget.qrcode;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;

import java.util.Collection;
import java.util.HashSet;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

    // private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192, 128, 64 };
    private static final long ANIMATION_DELAY = 40L;
    private static final int OPAQUE = 0xFF;

    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int frameColor;
    // private final int laserColor;
    // private final int resultPointColor;
    // private int scannerAlpha;
    private Collection<ResultPoint> possibleResultPoints;
    // private Collection<ResultPoint> lastPossibleResultPoints;

    /**
     * 四个绿色边角对应的长度
     */
    private int cornerLength;
    /**
     * 扫描框中的中间线的宽度
     */
    // private static final int MIDDLE_LINE_WIDTH = 6;

    /**
     * 扫描框中的中间线的与扫描框左右的间隙
     */
    // private static final int MIDDLE_LINE_PADDING = 5;
    /**
     * 四个绿色边角对应的宽度
     */
    private final int CORNER_WIDTH;
    /**
     * 中间那条线每次刷新移动的距离
     */
    private final int SPEED_DISTANCE;
    /**
     * 中间滑动线的最顶端位置
     */
    private int slideTop;

    /**
     * 中间滑动线的最底端位置
     */
    // private int slideBottom;

    boolean isFirst;

    private Rect mLineRect;

    /** 跟微信二维码扫描一样的镭射线 */
    private Bitmap mBitmapLaser = ((BitmapDrawable) (getResources().getDrawable(R.drawable.img_frame_corner_laser_horz))).getBitmap();

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

//        Screen screen = Screen.getInstance(context);

        CORNER_WIDTH = getResources().getDimensionPixelSize(R.dimen.x4);
        SPEED_DISTANCE = getResources().getDimensionPixelSize(R.dimen.x6);

        // 将像素转换成dp
        cornerLength = getResources().getDimensionPixelSize(R.dimen.x40);

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        frameColor = resources.getColor(R.color.viewfinder_frame);
        // laserColor = resources.getColor(R.color.viewfinder_laser);
        // resultPointColor = resources.getColor(R.color.possible_result_points);
        // scannerAlpha = 0;
        possibleResultPoints = new HashSet<>(5);

        mLineRect = new Rect();
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }

        // 初始化中间线滑动的最上边和最下边
        if (!isFirst) {
            isFirst = true;
            slideTop = frame.top;
            // slideBottom = frame.bottom;
        }

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // 画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
        // 扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {

            // Draw a two pixel solid black border inside the framing rect
            paint.setColor(frameColor);
            /*
            // top
            canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
            // left
            canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
            // right
            canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
            // bottom
            canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);
            */

            // 仿照微信扫描二维码风格
            // top - left
            canvas.drawRect(frame.left, frame.top, frame.left + cornerLength, frame.top + CORNER_WIDTH, paint);
            canvas.drawRect(frame.left, frame.top, frame.left + CORNER_WIDTH, frame.top + cornerLength, paint);
            // top - right
            canvas.drawRect(frame.right - cornerLength, frame.top, frame.right, frame.top + CORNER_WIDTH, paint);
            canvas.drawRect(frame.right - CORNER_WIDTH, frame.top, frame.right, frame.top + cornerLength, paint);
            // bottom - left
            canvas.drawRect(frame.left, frame.bottom - CORNER_WIDTH, frame.left + cornerLength, frame.bottom, paint);
            canvas.drawRect(frame.left, frame.bottom - cornerLength, frame.left + CORNER_WIDTH, frame.bottom, paint);
            // bottom - right
            canvas.drawRect(frame.right - cornerLength, frame.bottom - CORNER_WIDTH, frame.right, frame.bottom, paint);
            canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom - cornerLength, frame.right, frame.bottom, paint);

            // 绘制中间的线，每次刷新界面，中间的线往下移动SPEEN_DISTANCE
            slideTop += SPEED_DISTANCE;
            if (slideTop >= frame.bottom) {
                slideTop = frame.top;
            }
            // 用画笔绘制中间线
            // canvas.drawRect(frame.left + MIDDLE_LINE_PADDING, slideTop - MIDDLE_LINE_WIDTH/2, frame.right - MIDDLE_LINE_PADDING, slideTop + MIDDLE_LINE_WIDTH/2, paint);
            // 用图片绘制中间线

            mLineRect.left = frame.left;
            mLineRect.right = frame.right;
            mLineRect.top = slideTop;
            mLineRect.bottom = slideTop + CORNER_WIDTH;
            canvas.drawBitmap(mBitmapLaser, null, mLineRect, paint);

            // // Draw a red "laser scanner" line through the middle to show decoding is active
            // paint.setColor(laserColor);
            // paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            // scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
            // int middle = frame.height() / 2 + frame.top;
            // canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);

            // 注释掉黄色闪点
            // Collection<ResultPoint> currentPossible = possibleResultPoints;
            // Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            // if (currentPossible.isEmpty()) {
            // lastPossibleResultPoints = null;
            // } else {
            // possibleResultPoints = new HashSet<ResultPoint>(5);
            // lastPossibleResultPoints = currentPossible;
            // paint.setAlpha(OPAQUE);
            // paint.setColor(resultPointColor);
            // for (ResultPoint point : currentPossible) {
            // canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);
            // }
            // }
            // if (currentLast != null) {
            // paint.setAlpha(OPAQUE / 2);
            // paint.setColor(resultPointColor);
            // for (ResultPoint point : currentLast) {
            // canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
            // }
            // }

            // 只刷新扫描框的内容，其他地方不刷新
            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode
     *            An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }

}
