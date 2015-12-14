package com.hayukleung.app.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 日志工具
 */
public class LogUtil {

    /** 是否打开Log日志输出，true打开，false关闭 */
    private static boolean logOn = true;
    /**
     * 开启|关闭日志
     * 
     * @param enable
     */
    public static void setLogEnable(boolean enable) {
        logOn = enable;
    }

    private static final String DEFAULT_TAG = LogUtil.class.getSimpleName();
    
    /** 日志信息打印时每行最多字数 */
    private static final int MAX_COUNT_PER_LINE = 1000;

    /** 调试日志类型 */
    public static final int DEBUG = 111;
    /** 错误日志类型 */
    public static final int ERROR = 112;
    /** 信息日志类型 */
    public static final int INFO = 113;
    /** 详细信息日志类型 */
    public static final int VERBOSE = 114;
    /** 警告调试日志类型 */
    public static final int WARN = 115;
    
    /**
     * 默认 INFO 级别
     * 默认 DEFAULT_TAG
     * 
     * @param message
     */
    public static void showLog(String message) {
        showLog(null, message);
    }
    
    /**
     * 默认 INFO 级别
     * 
     * @param context
     * @param message
     */
    public static void showLog(Context context, String message) {
        showLog(context, message, INFO);
    }

    /**
     * 由于 LogCat 在 Eclipse 中一次最长只输出 4000 左右字符串 </br> 
     * 对于返回的较长的 JSON 字符串无法显示完全，故作分段处理 </br> 
     * 
     * @param context
     * @param message
     * @param style
     */
    public static void showLog(Context context, String message, int style) {
        if (!TextUtils.isEmpty(message)) {
            int start;
            int end;
            int lineCount = message.length() / MAX_COUNT_PER_LINE;
            for (int i = 0; i <= lineCount; i++) {
                start = i * MAX_COUNT_PER_LINE;
                end = (i + 1) * MAX_COUNT_PER_LINE;
                end = end > message.length() ? message.length() : end;
                printLog(context, message.substring(start, end), style);
            }
        }
    }
    
    /**
     * 往内存卡写日志
     * 
     * @param path 日志全路径
     * @param log 日志内容
     */
    public static void writeLog(String path, String log) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String datetime = tempDate.format(new java.util.Date());
            try {
                File file = new File(path);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file, true);
                fw.append("--" + datetime + "\n");
                fw.append(log);
                fw.append("\n================================\n\n");
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打印日志
     * 
     * @param context
     * @param message
     * @param style
     *            </br>DEBUG 调试日志类型 
     *            </br>ERROR 错误日志类型 
     *            </br>INFO 信息日志类型 
     *            </br>VERBOSE 详细日志类型 
     *            </br>WARN 警告日志类型
     */
    private static void printLog(Context context, String message, int style) {
        if (logOn) {
            String tag;
            if (null != context) {
                tag = context.getClass().getSimpleName();
            } else {
                tag = DEFAULT_TAG;
            }
            switch (style) {
            case DEBUG:
                Log.d(tag, message);
                break;
            case ERROR:
                Log.e(tag, message);
                break;
            case INFO:
                Log.i(tag, message);
                break;
            case VERBOSE:
                Log.v(tag, message);
                break;
            case WARN:
                Log.w(tag, message);
                break;
            default:
                Log.i(tag, message);
                break;
            }
        }
    }
}
