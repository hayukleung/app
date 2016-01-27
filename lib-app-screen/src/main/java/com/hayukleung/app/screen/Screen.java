/**
 * 
 */
package com.hayukleung.app.screen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * 屏幕模型，对DisplayMetrics进行封装
 * 
 * @author hayukleung
 * 
 */
public class Screen {

    /** 宽 */
    public int widthPx;
    /** 高 */
    public int heightPx;
    
    /** 密度dpi */
    public int densityDpi;
    /** 缩放系数densityDpi/160 */
    public float densityScale;
    /** 文字缩放系数 */
    public float fontScale;
    
    /** 屏幕朝向 */
    public int orientation;
    /** 竖屏 */
    public final static int ORIENTATION_VERTICAL = 0x0001;
    /** 横屏 */
    public final static int ORIENTATION_HORIZONTAL = 0x0002;

    private static Screen singleInstance;

    /**
     * 私有构造方法
     * 
     * @param context
     */
    private Screen(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        this.widthPx = metrics.widthPixels;
        this.heightPx = metrics.heightPixels;
        this.densityDpi = metrics.densityDpi;
        this.densityScale = metrics.density;
        this.fontScale = metrics.scaledDensity;
        this.orientation = heightPx > widthPx ? ORIENTATION_VERTICAL : ORIENTATION_HORIZONTAL;
        Log.d(context.getString(R.string.app_name), String.format("width --> %dpx height --> %dpx", this.widthPx, this.heightPx));

        Point point = new Point();
        ((Activity) context).getWindowManager().getDefaultDisplay().getSize(point);
        Log.d(context.getString(R.string.app_name), "the screen size is " + point.toString());
    }

    /**
     * 获取实例
     * 
     * @param context
     * @return
     */
    public static Screen getInstance(Context context) {
        if (singleInstance == null) {
            singleInstance = new Screen(context);
        }
        return singleInstance;
    }
    
    /**
     * 根据设备屏幕密度将px转换为dp
     * 
     * @param valuePx
     * @return
     */
    public int px2dp(float valuePx) {
        return (int) (valuePx / densityScale + 0.5f);
    }

    /**
     * 根据设备屏幕密度将dp转换为px
     * 
     * @param valueDp
     * @return
     */
    public int dp2px(float valueDp) {
        return (int) (valueDp * densityScale + 0.5f);
    }

    /**
     * 根据设备屏幕密度将px转换为sp
     * 
     * @param valuePx
     * @return
     */
    public int px2sp(float valuePx) {
        return (int) (valuePx / fontScale + 0.5f);
    }

    /**
     * 根据设备屏幕密度将sp转换为px
     * 
     * @param valueSp
     * @return
     */
    public int sp2px(float valueSp) {
        return (int) (valueSp * fontScale + 0.5f);
    }

    /**
     * 将px值转换为dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @param scale
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dp(float pxValue, float scale) {
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dp值转换为px值，保证尺寸大小不变
     *
     * @param dpValue
     * @param scale
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int dp2px(float dpValue, float scale) {
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float pxValue, float fontScale) {
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(float spValue, float fontScale) {
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕尺寸
     *
     * Created by zhy on 15/12/4.<br/>
     * from http://stackoverflow.com/questions/1016896/get-screen-dimensions-in-pixels/15699681#15699681
     *
     * @param context
     * @param useDeviceSize 是否读取设备屏幕物理尺寸
     * @return
     */
    public static int[] getScreenSize(Context context, boolean useDeviceSize) {

        int[] size = new int[2];

        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        if (!useDeviceSize) {
            size[0] = widthPixels;
            size[1] = heightPixels;
            return size;
        }

        // includes window decorations (status bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
        // includes window decorations (status bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        size[0] = widthPixels;
        size[1] = heightPixels;
        return size;
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;

        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }

        return statusBarHeight;
    }



}
