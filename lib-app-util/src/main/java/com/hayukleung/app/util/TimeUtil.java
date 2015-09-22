package com.hayukleung.app.util;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

/**
 * 常用时间函数
 * 
 * @author http://www.eoeandroid.com/thread-235320-1-1.html
 * 
 */
public class TimeUtil {

    /**
     * 获取时间戳，毫秒
     * 
     * @return
     */
    public static long getTimestampMS(Context context) {
        return System.currentTimeMillis();
    }

    /**
     * 获取时间戳，秒
     * 
     * @return
     */
    public static long getTimestampSec() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 将时间戮转成日期型字符串 方法名：getDate 返回类型:String 开发人员：wzj@cndatacom.com 创建时间：2013-6-15
     * 
     * @param style
     *            返回日期的格式
     * @param datestr
     *            时间戮
     * @return
     */
    public static String getFormatDate(String style, long datestr) {
        String date = "";
        if (style == null || "".equalsIgnoreCase(style)) {
            style = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat format = new SimpleDateFormat(style, Locale.CHINA);
        Date d = new Date(datestr);
        date = format.format(d);
        return date;
    }

    /**
     * 格式yyyy-MM-dd HH24:mi:ss转时间戳
     * 
     * @param context
     * @param str
     * @return
     */
    public static long getTimestampFromString(Context context, String str) {
        if (str == null || str.equals("")) {
            return getTimestampMS(context);
        }
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date date = null;// 用于时间转化
        try {
            date = f.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getTimestampMS(context);
    }

    /**
     * 格式yyyy-MM-dd HH24:mi:ss:SSS转时间戳
     * 
     * @param context
     * @param str
     * @return
     */
    public static long getTimestampFromStringMillis(Context context, String str) {
        if (str == null || str.equals("")) {
            return getTimestampMS(context);
        }
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA);
        Date date = null;// 用于时间转化
        try {
            date = f.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getTimestampMS(context);
    }

    /**
     * 生成版权日期字符
     * 
     * @param byear
     * @return
     */
    public static String getCopyRight(int byear) {
        String str = "Copyright © 1997–2014";
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        str = "Copyright © " + byear + "–" + year;
        return str;
    }

    /**
     * 获取现在时间
     * 
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static Date getNowDate() {
        Date currentTime = new Date();
        DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        return formatter.parse(dateString, pos);
    }

    /**
     * 获取现在时间
     * 
     * @return yyyy-MM-dd
     */
    public static Date getNowDateShort() {
        Date currentTime = new Date();
        DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        return formatter.parse(dateString, pos);
    }

    /**
     * 获取现在时间
     * 
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        Date currentTime = new Date();
        DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取现在时间
     * 
     * @return yyyy-MM-dd
     */
    public static String getStringDateShort() {
        Date currentTime = new Date();
        DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取现在时间
     * 
     * @return HH:mm:ss
     */
    public static String getStringTimeShort() {
        DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat("HH:mm:ss");
        Date currentTime = new Date();
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 将长时间格式字符串转换为时间
     * 
     * @param strDate
     * @param format
     * @return
     */
    public static Date strToDateLong(String strDate, String format) {
        DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat(format);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将长时间格式时间转换为字符串
     * 
     * @param dateDate
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String dateToStrLong(Date dateDate) {
        DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式时间转换为字符串
     * 
     * @param dateDate
     * @return yyyy-MM-dd
     */
    public static String dateToStr(Date dateDate) {
        DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     * 
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 得到现在时间
     * 
     * @return
     */
    public static Date getNow() {
        Date currentTime = new Date();
        return currentTime;
    }

    /**
     * 提取一个月中的最后一天
     * 
     * @param day
     * @return
     */
    public static Date getLastDate(long day) {
        Date date = new Date();
        long date_3_hm = date.getTime() - 3600000 * 34 * day;
        Date date_3_hm_date = new Date(date_3_hm);
        return date_3_hm_date;
    }

    /**
     * 得到现在时间
     * 
     * @return 字符串 yyyyMMdd HHmmss
     */
    public static String getStringToday() {
        Date currentTime = new Date();
        DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat("yyyyMMdd HHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 得到现在小时
     * 
     * @return
     */
    public static String getHour() {
        Date currentTime = new Date();
        DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String hour;
        hour = dateString.substring(11, 13);
        return hour;
    }

    /**
     * 得到现在分钟
     * 
     * @return
     */
    public static String getTime() {
        Date currentTime = new Date();
        DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String min;
        min = dateString.substring(14, 16);
        return min;
    }

    /**
     * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMdd，注意字母y不能大写。
     * 
     * @param sformat
     *            yyyyMMddhhmmss
     * @return
     */
    public static String getUserDate(String sformat) {
        Date currentTime = new Date();
        DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat(sformat);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 二个小时时间间的差值,必须保证二个时间都是"HH:MM"的格式，返回字符型的分钟
     * 
     * @param st1
     * @param st2
     * @return
     */
    public static String getTwoHour(String st1, String st2) {
        String[] kk = null;
        String[] jj = null;
        kk = st1.split(":");
        jj = st2.split(":");
        if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0]))
            return "0";
        else {
            double y = Double.parseDouble(kk[0]) + Double.parseDouble(kk[1]) / 60;
            double u = Double.parseDouble(jj[0]) + Double.parseDouble(jj[1]) / 60;
            if ((y - u) > 0)
                return y - u + "";
            else
                return "0";
        }
    }

    /**
     * 得到二个日期间的间隔天数
     * 
     * @param sj1
     * @param sj2
     * @return
     */
    public static String getTwoDay(String sj1, String sj2) {
        DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat("yyyy-MM-dd");
        long day = 0;
        try {
            Date date = formatter.parse(sj1);
            Date mydate = formatter.parse(sj2);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return "";
        }
        return day + "";
    }

    /**
     * 得到一个时间延后或前移几天的时间
     * 
     * @param nowdate
     *            参考时间
     * @param delay
     *            前移或后延的天数
     * @return
     */
    public static String getNextDay(String nowdate, String delay) {
        try {
            DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat("yyyy-MM-dd");
            String mdate = "";
            Date d = strToDate(nowdate);
            long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
            d.setTime(myTime * 1000);
            mdate = formatter.format(d);
            return mdate;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 判断是否润年
     * 
     * @param ddate
     * @return
     */
    public static boolean isLeapYear(String ddate) {

        /**
         * 详细设计：</br> 1.被400整除是闰年 </br> 否则：</br> 2.不能被4整除则不是闰年 </br> 3.能被4整除同时不能被100整除则是闰年 </br> 3.能被4整除同时能被100整除则不是闰年 </br>
         */
        Date d = strToDate(ddate);
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(d);
        int year = gc.get(Calendar.YEAR);
        if ((year % 400) == 0)
            return true;
        else if ((year % 4) == 0) {
            if ((year % 100) == 0)
                return false;
            else
                return true;
        } else
            return false;
    }

    /**
     * 返回美国时间格式 26 Apr 2006
     * 
     * @param str
     * @return
     */
    public static String getEDate(String str) {
        DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(str, pos);
        String j = strtodate.toString();
        String[] k = j.split(" ");
        return k[2] + k[1].toUpperCase(Locale.CHINA) + k[5].substring(2, 4);
    }

    /**
     * 获取一个月的最后一天
     * 
     * @param dat
     * @return
     */
    public static String getEndDateOfMonth(String dat) {// yyyy-MM-dd
        String str = dat.substring(0, 8);
        String month = dat.substring(5, 7);
        int mon = Integer.parseInt(month);
        if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) {
            str += "31";
        } else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
            str += "30";
        } else {
            if (isLeapYear(dat)) {
                str += "29";
            } else {
                str += "28";
            }
        }
        return str;
    }

    /**
     * 判断二个时间是否在同一个周
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameWeekDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        return false;
    }

    /**
     * 产生周序列,即得到当前时间所在的年度是第几周
     * 
     * @return
     */
    public static String getSeqWeek() {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
        if (week.length() == 1)
            week = "0" + week;
        String year = Integer.toString(c.get(Calendar.YEAR));
        return year + week;
    }

    /**
     * 获得一个日期所在的周的星期几的日期，如要找出2002年2月3日所在周的星期一是几号
     * 
     * @param sdate
     * @param num
     * @return
     */
    public static String getWeek(String sdate, String num) {
        // 再转换为时间
        Date dd = TimeUtil.strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(dd);
        if (num.equals("1")) // 返回星期一所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        else if (num.equals("2")) // 返回星期二所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        else if (num.equals("3")) // 返回星期三所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        else if (num.equals("4")) // 返回星期四所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        else if (num.equals("5")) // 返回星期五所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        else if (num.equals("6")) // 返回星期六所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        else if (num.equals("0")) // 返回星期日所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return new DefaultLocaleDateFormat("yyyy-MM-dd").format(c.getTime());
    }

    /**
     * 根据一个日期，返回是星期几的字符串
     * 
     * @param sdate
     * @return
     */
    public static String getWeek(String sdate) {
        // 再转换为时间
        Date date = TimeUtil.strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return new DefaultLocaleDateFormat("EEEE").format(c.getTime());
    }

    public static String getWeekStr(String sdate) {
        String str = "";
        str = TimeUtil.getWeek(sdate);
        if ("1".equals(str)) {
            str = "星期日";
        } else if ("2".equals(str)) {
            str = "星期一";
        } else if ("3".equals(str)) {
            str = "星期二";
        } else if ("4".equals(str)) {
            str = "星期三";
        } else if ("5".equals(str)) {
            str = "星期四";
        } else if ("6".equals(str)) {
            str = "星期五";
        } else if ("7".equals(str)) {
            str = "星期六";
        }
        return str;
    }

    /**
     * 两个时间之间的天数
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static long getDays(String date1, String date2) {
        if (date1 == null || date1.equals(""))
            return 0;
        if (date2 == null || date2.equals(""))
            return 0;
        // 转换为标准时间
        DefaultLocaleDateFormat formatter = new DefaultLocaleDateFormat("yyyy-MM-dd");
        Date date = null;
        Date mydate = null;
        try {
            date = formatter.parse(date1);
            mydate = formatter.parse(date2);
        } catch (Exception e) {
        }
        long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        return day;
    }

    /**
     * 形成如下的日历 ， 根据传入的一个时间返回一个结构 星期日 星期一 星期二 星期三 星期四 星期五 星期六 下面是当月的各个时间 此函数返回该日历第一行星期日所在的日期
     * 
     * @param sdate
     * @return
     */
    public static String getNowMonth(String sdate) {
        // 取该时间所在月的一号
        sdate = sdate.substring(0, 8) + "01";

        // 得到这个月的1号是星期几
        Date date = TimeUtil.strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int u = c.get(Calendar.DAY_OF_WEEK);
        String newday = TimeUtil.getNextDay(sdate, (1 - u) + "");
        return newday;
    }

    /**
     * 取得数据库主键 生成格式为yyyymmddhhmmss+k位随机数
     * 
     * @param k
     *            表示是取几位随机数，可以自己定
     */

    public static String getNo(int k) {

        return getUserDate("yyyyMMddhhmmss") + getRandom(k);
    }

    /**
     * 返回一个随机数
     * 
     * @param i
     * @return
     */
    public static String getRandom(int i) {
        Random jjj = new Random();
        // int suiJiShu = jjj.nextInt(9);
        if (i == 0)
            return "";
        String jj = "";
        for (int k = 0; k < i; k++) {
            jj = jj + jjj.nextInt(9);
        }
        return jj;
    }

    /**
     * 根据时间戳获取相对时间 </br> 用于微聊消息列表时间显示 </br> http://hi.baidu.com/su350380433/item/b9a34be88524a9f9e0a5d4d8 </br>
     * 
     * @param timestampInSec
     *            -- 单位为秒的时间戳
     */
    @SuppressWarnings("deprecation")
    public static String getDistanceTime(long timestampInSec) {
        // Calendar.get(Calendar.DATE)
        Date now = new Date();
        long day = 0; // 天数
        long hour = 0; // 小时
        long min = 0; // 分钟
        @SuppressWarnings("unused")
        long sec = 0; // 秒
        try {
            long curTimestampInMillisec = now.getTime();
            timestampInSec = timestampInSec * 1000l;
            long diff;
            if (curTimestampInMillisec < timestampInSec) {
                diff = timestampInSec - curTimestampInMillisec;
            } else {
                diff = curTimestampInMillisec - timestampInSec;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000));
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String rs = "";
        if (hour == 0) {
            rs = min + "分钟前";
            return rs;
        }
        if (day == 0 && hour <= 4) {
            rs = hour + "小时前";
            return rs;
        }
        DefaultLocaleDateFormat format = new DefaultLocaleDateFormat("MM-dd HH:mm");
        String d = format.format(timestampInSec);
        Date date = null;
        try {
            // 把字符类型的转换成日期类型的！
            date = format.parse(d);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        if (now.getDate() - date.getDate() == 0) {
            // 当前时间和时间戳转换来的时间的天数对比
            DateFormat df2 = new DefaultLocaleDateFormat("HH:mm");
            rs = "今天  " + df2.format(timestampInSec);
            return rs;
        } else if (now.getDate() - date.getDate() == 1) {
            DateFormat df2 = new DefaultLocaleDateFormat("HH:mm");
            rs = "昨天  " + df2.format(timestampInSec);
            return rs;
        } else {
            DateFormat df2 = new DefaultLocaleDateFormat("MM-dd HH:mm");
            rs = df2.format(timestampInSec);
            return rs;
        }
    }

    /**
     * 根据时间戳获取相对时间 </br> 用于微聊消息列表时间显示 </br> http://hi.baidu.com/su350380433/item/b9a34be88524a9f9e0a5d4d8 </br>
     * 
     * @param timestampInSec
     *            -- 单位为秒的时间戳
     * @param deltaTime
     *            -- 修正本地与服务器的时间差，用于相对时间调整，绝对时间不需要调整
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getDistanceTime(long timestampInSec, final long deltaTime) {
        // Calendar.get(Calendar.DATE)
        Date now = new Date();
        long day = 0; // 天数
        long hour = 0; // 小时
        long min = 0; // 分钟
        // long sec = 0; // 秒
        try {
            long curTimestampInMillisec = now.getTime() + deltaTime;
            timestampInSec = timestampInSec * 1000l;
            long diff;
            if (curTimestampInMillisec < timestampInSec) {
                diff = timestampInSec - curTimestampInMillisec;
            } else {
                diff = curTimestampInMillisec - timestampInSec;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000));
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            // sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String rs = "";
        if (hour == 0) {
            rs = min + "分钟前";
            return rs;
        }
        if (day == 0 && hour <= 4) {
            rs = hour + "小时前";
            return rs;
        }
        DefaultLocaleDateFormat format = new DefaultLocaleDateFormat("MM-dd HH:mm");
        String d = format.format(timestampInSec);
        Date date = null;
        try {
            // 把字符类型的转换成日期类型的！
            date = format.parse(d);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        if (now.getDate() - date.getDate() == 0) {
            // 当前时间和时间戳转换来的时间的天数对比
            DateFormat df2 = new DefaultLocaleDateFormat("HH:mm");
            rs = "今天  " + df2.format(timestampInSec);
            return rs;
        } else if (now.getDate() - date.getDate() == 1) {
            DateFormat df2 = new DefaultLocaleDateFormat("HH:mm");
            rs = "昨天  " + df2.format(timestampInSec);
            return rs;
        } else {
            DateFormat df2 = new DefaultLocaleDateFormat("MM-dd HH:mm");
            rs = df2.format(timestampInSec);
            return rs;
        }
    }

    /**
     * 根据年月日获取星期
     * 
     * @param year
     * @param month
     * @param day
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String getWeekOfDate(int year, int month, int day) {
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        Calendar cal = Calendar.getInstance();
        Date curDate = null;
        if (year >= 2000) {
            curDate = new Date(year, month, day - 1);
        } else {
            curDate = new Date(year, month, day - 2);
        }

        cal.setTime(curDate);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[curDate.getDay()];
    }

    /**
     * 默认Locale.CHINA的SimpleDateFormat内部类
     * 
     * @author HayukLeung
     * 
     */
    private static class DefaultLocaleDateFormat extends SimpleDateFormat {
        private static final long serialVersionUID = -2496520038803137265L;

        public DefaultLocaleDateFormat(String format) {
            // zh_CN
            super(format, Locale.CHINA);
        }
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
    
    /**
     * 格式yyyy-MM-dd HH24:mi:ss转时间戳
     * 
     * @param str
     * @return
     */
    public static long getTimestampFromString(String str) {
        if (str == null || str.equals("")) {
            return System.currentTimeMillis();
        }
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null; // 用于时间转化
        try {
            date = f.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }
    
    /**
     * 格式yyyy-MM-dd HH24:mi:ss:SSS转时间戳
     * 
     * @param str
     * @return
     */
    public static long getTimestampFromStringMillis(String str) {
        if (str == null || str.equals("")) {
            return System.currentTimeMillis();
        }
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA);
        Date date = null;// 用于时间转化
        try {
            date = f.parse(str);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }
    
    /**
     * 根据template模板将毫秒数转为字符串
     * 
     * @param template
     * @param timestampMS
     * @return
     */
    public static String timestampMS2String(String template, long timestampMS) {
        return new SimpleDateFormat(template, Locale.getDefault()).format(new Date(timestampMS));
    }
    
    public static String getDate(String month, String day) {
        // 24h
        DefaultLocaleDateFormat sdf = new DefaultLocaleDateFormat("yyyy-MM-dd HH:mm:ss");
        String today = sdf.format(new Date()).substring(8, 10);
        String result = null;

        int temp = Integer.parseInt(today) - Integer.parseInt(day);
        switch (temp) {
        case 0:
            result = "今天";
            break;
        case 1:
            result = "昨天";
            break;
        case 2:
            result = "前天";
            break;
        default:
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.parseInt(month) + "月");
            sb.append(Integer.parseInt(day) + "日");
            result = sb.toString();
            break;
        }
        return result;
    }
    
}
