package com.hayukleung.app.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;

/**
 * 目录管理
 * 
 * @author HayukLeung
 * 
 */
public class DirMgr {

    /** 根目录 */
    public static final String ROOT = Environment.getExternalStorageDirectory().getPath() + "/app";
    /** 日志保存路径 */
    public static final String PATH_LOG         = ROOT + "/log";
    /** 相机照片目录 */
    public static final String PATH_CAMERA      = ROOT + "/camera";
    /** 剪裁后的图片目录 */
    public static final String PATH_CROP        = ROOT + "/crop";
    /** 图片保存路径 */
    public static final String PATH_IMG_SAVE    = ROOT + "/img";
    /** 安装包目录 */
    public static final String PATH_APK         = ROOT + "/apk";
    /** 社交分享 */
    public static final String PATH_SHARE_CACHE = ROOT + "/shareImg";
    /** 文件保存目录 */
    public static final String PATH_FILE        = ROOT + "/file";
    /** 绘画板图片保存路径 */
    public static final String PATH_PAINTPAD = ROOT + "/paintpad";
    
    /**
     * 初始化目录
     */
    public static void initDirs() {
        mkdir(ROOT);
        mkdir(PATH_CAMERA);
        mkdir(PATH_CROP);
        mkdir(PATH_IMG_SAVE);
        mkdir(PATH_APK);
        mkdir(PATH_SHARE_CACHE);
        mkdir(PATH_PAINTPAD);
    }

    /**
     * 新建目录
     * 
     * @param path
     */
    public static void mkdir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    /**
     * 查看是否存在sdcard
     * 
     * @param context
     * @return
     */
    public static boolean hasSDCard(Context context) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.d(context.getPackageName(), "SDCard is unavailable ...");
            return false;
        } else {
            Log.d(context.getPackageName(), "SDCard is available ...");
            return true;
        }
    }
    
    /**
     * 判断SD卡空间是否够用
     * 
     * @return
     */
    public static boolean isSDCardSpaceEnough() {
        StatFs fs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        // keep one free block
        return fs.getAvailableBlocks() > 1;
    }
}
