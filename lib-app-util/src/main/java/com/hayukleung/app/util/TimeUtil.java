package com.hayukleung.app.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * 常用时间函数
 */
public class TimeUtil {

    /** 一分钟毫秒数 */
    public static final long MINUTE = 60 * 1000L;
    /** 一小时毫秒数 */
    public static final long HOUR = 60 * MINUTE;
    /** 一天毫秒数 */
    public static final long DAY = 24 * HOUR;
    /** 一星期毫秒数 */
    public static final long WEEK = 7 * DAY;
    /** 一年平均毫秒数 */
    public static final long YEAR = (long) (365.24 * DAY);

    /**
     * 毫秒时间戳
     * 
     * @return
     */
    public static long currentTimeMilliseconds() {
        return System.currentTimeMillis();
    }

    /**
     * 秒时间戳
     * 
     * @return
     */
    public static long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 毫秒时间戳转日期型字符串
     * 
     * @param template 日期格式
     * @param milliseconds 毫秒时间戳
     * @return
     */
    public static String toTimeString(String template, long milliseconds) {
        if (TextUtils.isEmpty(template)) {
            template = "yyyy-MM-dd HH:mm:ss";
        }
        return new SimpleDateFormat(template, Locale.getDefault()).format(new Date(milliseconds));
    }

    /**
     * 字符型串日期转毫秒时间戳
     * 
     * @param template 与dateString匹配的日期格式
     * @param dateString 字符串日期
     * @return
     */
    public static long toTimeMilliseconds(String template, String dateString) {

        if (TextUtils.isEmpty(dateString)) {
            return System.currentTimeMillis();
        }

        if (TextUtils.isEmpty(template)) {
            template = "yyyy-MM-dd HH:mm:ss";
        }

        try {
            return new SimpleDateFormat(template, Locale.getDefault()).parse(dateString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return System.currentTimeMillis();
    }

    /**
     * 判断是否润年
     * 
     * @param date
     * @return
     */
    public static boolean isLeapYear(Date date) {

        /**
         * 详细设计：
         * </br> 1.被400整除是闰年
         * </br> 2.不能被4整除则不是闰年
         * </br> 3.能被4整除同时不能被100整除则是闰年
         * </br> 4.能被4整除同时能被100整除则不是闰年
         */
        GregorianCalendar gregorianCalendar = (GregorianCalendar) Calendar.getInstance();
        gregorianCalendar.setTime(date);

        int year = gregorianCalendar.get(Calendar.YEAR);

        if ((year % 400) == 0) {
            return true;
        } else if ((year % 4) == 0) {
            if ((year % 100) == 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * 判断二个时间是否在同一个周
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static boolean inSameWeek(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                return true;
            }
        } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                return true;
            }
        } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据时间戳获取相对时间
     *
     * @param date
     * @return
     */
    public static String getDistanceTime(Date date) {

        long currentTime = System.currentTimeMillis();
        long millisecondsDelta = currentTime - date.getTime();

        if (millisecondsDelta < MINUTE)
            return "刚刚";

        if (millisecondsDelta < HOUR) {
            return String.format("%d分钟前", (int) (millisecondsDelta / MINUTE));
        }

        if (millisecondsDelta < DAY) {
            return String.format("%d小时前", (int) (millisecondsDelta / HOUR));
        }

        if (millisecondsDelta < WEEK) {
            return String.format("%d天前", (int) (millisecondsDelta / DAY));
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        if (simpleDateFormat.format(currentTime).equals(simpleDateFormat.format(date))) {
            simpleDateFormat = new SimpleDateFormat("MM-dd");
            return simpleDateFormat.format(date);
        }

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    /**
     * 根据毫秒数获取小时数
     * 
     * @param ms
     * @return
     */
    public static String getHH(long ms) {
        long hh = ms / (60 * 60 * 1000);
        hh = hh < 0 ? 0 : hh;
        
        return hh < 10 ? "0" + hh : "" + hh;
    }

    /**
     * 根据毫秒数获取分钟数
     * 
     * @param ms
     * @return
     */
    public static String getMM(long ms) {
        long mm = (ms % (60 * 60 * 1000)) / (60 * 1000);
        mm = mm < 0 ? 0 : mm;
        
        return mm < 10 ? "0" + mm : "" + mm;
    }

    /**
     * 根据毫秒数获取秒数
     * 
     * @param ms
     * @return
     */
    public static String getSS(long ms) {
        long ss = (ms % (60 * 1000)) / 1000;
        ss = ss < 0 ? 0 : ss;
        
        return ss < 10 ? "0" + ss : "" + ss;
    }
}
