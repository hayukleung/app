package com.hayukleung.app.widget.paintpad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.hayukleung.app.util.DirMgr;
import com.hayukleung.app.widget.paintpad.drawings.Drawing;
import com.hayukleung.app.widget.paintpad.helper.ScreenInfo;
import com.hayukleung.app.widget.paintpad.tools.Brush;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * This is our main View class.
 */
public class PaintPad extends View {
    private Bitmap mBitmap = null;
    private Canvas mCanvas = null;
    private boolean isMoving = false;
    private Drawing mDrawing = null;
    private Context mContext;

    /**
     * 是否开始绘制
     */
    private boolean isStartDraw = false;

    /**
     * Set the shape that is drawing.
     * 
     * @param drawing
     *            Which shape to drawing current.
     */
    public void setDrawing(Drawing drawing) {
        this.mDrawing = drawing;
    }

    public boolean isStartDraw() {
        return isStartDraw;
    }

    public void setStartDraw(boolean isStartDraw) {
        this.isStartDraw = isStartDraw;
    }

    public PaintPad(Context context) {
        this(context, null);
    }

    public PaintPad(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaintPad(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        this.isMoving = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap == null) {
            // Get the information about the screen.
            ScreenInfo screenInfo = new ScreenInfo((Activity) mContext);

            /**
             * Create a bitmap with the size of the screen.
             */
            int height = this.getMeasuredHeight();
            mBitmap = Bitmap.createBitmap(screenInfo.getWidthPixels(), height, Bitmap.Config.ARGB_8888);

            mCanvas = new Canvas(this.mBitmap);

            // Set the background color
            mCanvas.drawColor(getResources().getColor(android.R.color.white));
        }

        // Draw the bitmap
        canvas.drawBitmap(mBitmap, 0, 0, new Paint(Paint.DITHER_FLAG));

        // Call the drawing's draw() method.
        if (mDrawing != null && this.isMoving == true) {
            mDrawing.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            fingerDown(x, y);
            reDraw();
            break;
        case MotionEvent.ACTION_MOVE:
            fingerMove(x, y);
            reDraw();
            break;
        case MotionEvent.ACTION_UP:
            fingerUp(x, y);
            reDraw();
            break;
        }

        return true;
    }

    /**
     * Refresh the view, the view's onDraw() method will be called.
     */
    private void reDraw() {
        invalidate();
        if (Brush.getPen().getColor() != 0XFFFFFF && Brush.getPen().getColor() != 0XFFFFFFFF) {
            isStartDraw = true;
        }
    }

    /**
     * Handles the action of finger up.
     * 
     * @param x
     *            coordinate
     * @param y
     *            coordinate
     */
    private void fingerUp(float x, float y) {
        if (null != mDrawing) {
            mDrawing.fingerUp(x, y, mCanvas);
        }
        this.isMoving = false;
    }

    /**
     * Handles the action of finger Move.
     * 
     * @param x
     *            coordinate
     * @param y
     *            coordinate
     */
    private void fingerMove(float x, float y) {
        this.isMoving = true;
        if (null != mDrawing) {
            mDrawing.fingerMove(x, y, mCanvas);
        }
    }

    /**
     * Handles the action of finger down.
     * 
     * @param x
     *            coordinate
     * @param y
     *            coordinate
     */
    private void fingerDown(float x, float y) {
        this.isMoving = false;
        if (null != mDrawing) {
            mDrawing.fingerDown(x, y, mCanvas);
        }
    }

    /**
     * Check the sdcard is available or not.
     *
     * @param context
     * @return
     */
    public String saveBitmap(Context context) {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return saveToSdcard(context);
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Toast.makeText(this.mContext, "SDCard只读", Toast.LENGTH_LONG).show();
            return null;
        } else {
            Toast.makeText(this.mContext, "SDCard不可用", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void changeBgColor(int color) {
        this.mCanvas.drawColor(color);
        this.reDraw();
    }

    /**
     * Clear the drawing in the canvas.
     */
    public void clearCanvas() {
        this.mCanvas.drawColor(getResources().getColor(android.R.color.white));
        this.reDraw();
    }

    /**
     * Save the bitmap to sdcard.
     *
     * @param context
     * @return
     */
    private String saveToSdcard(Context context) {
        File file = new File(DirMgr.PATH_PAINTPAD);
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        long timeStamp = System.currentTimeMillis();
        String suffixName = ".png";
        String fullPath = DirMgr.PATH_PAINTPAD + "/" + timeStamp + suffixName;
        try {
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(fullPath));
            Toast.makeText(this.mContext, "保存成功：" + fullPath, Toast.LENGTH_SHORT).show();
            // 通知文件系统收录该图片
            Uri data = Uri.parse("file:///" + fullPath);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
            return fullPath;
        } catch (FileNotFoundException e) {
            Toast.makeText(this.mContext, "保存失败：" + fullPath, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return null;
        }
    }
}
