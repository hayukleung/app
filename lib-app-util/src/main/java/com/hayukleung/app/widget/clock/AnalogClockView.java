package com.hayukleung.app.widget.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.hayukleung.app.util.LogUtil;
import com.hayukleung.app.util.R;

import java.util.TimeZone;

/**
 * 数字时钟
 * 
 * @author HayukLeung
 * 
 */
public class AnalogClockView extends ViewGroup {

    private Context mContext;
//    private SimpleDateFormat mSimpleDateFormat;
//    private Date mDate;

    private Time mCalendar;
    private boolean mAttached;

    public AnalogClockView(Context context) {
        this(context, null);
    }

    public AnalogClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnalogClockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init(attrs);
    }


    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    private int sp2px(float spValue, float fontScale) {
        return (int) (spValue * fontScale + 0.5f);
    }

    private CenterTextView txt1, txt2, txt3, txt4, txt5, txt6, txt7, txt8, txt9, txt10, txt11, txt12;
    private CenterTextView txtLogo;
    private int txtSizeBig, txtSizeSmall;
    private final float MAX_TXT_SIZE_BIG_SP = 17;
    private final float MAX_TXT_SIZE_SMALL_SP = 14;

    private String clock01, clock02, clock03, clock04, clock05, clock06, clock07, clock08, clock09, clock10, clock11, clock12;

    private void init(AttributeSet attrs) {

        if (attrs != null) {
            // 自定义属性
            TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.AnalogClockView);
            clock01 = a.getString(R.styleable.AnalogClockView_clock01);
            clock02 = a.getString(R.styleable.AnalogClockView_clock02);
            clock03 = a.getString(R.styleable.AnalogClockView_clock03);
            clock04 = a.getString(R.styleable.AnalogClockView_clock04);
            clock05 = a.getString(R.styleable.AnalogClockView_clock05);
            clock06 = a.getString(R.styleable.AnalogClockView_clock06);
            clock07 = a.getString(R.styleable.AnalogClockView_clock07);
            clock08 = a.getString(R.styleable.AnalogClockView_clock08);
            clock09 = a.getString(R.styleable.AnalogClockView_clock09);
            clock10 = a.getString(R.styleable.AnalogClockView_clock10);
            clock11 = a.getString(R.styleable.AnalogClockView_clock11);
            clock12 = a.getString(R.styleable.AnalogClockView_clock12);
            a.recycle();
        } else {
            clock01 = "•";
            clock02 = "•";
            clock03 = "•";
            clock04 = "•";
            clock05 = "•";
            clock06 = "•";
            clock07 = "•";
            clock08 = "•";
            clock09 = "•";
            clock10 = "•";
            clock11 = "•";
            clock12 = "•";
        }
        
//        mSimpleDateFormat = new SimpleDateFormat("HH-mm-ss", Locale.getDefault());
//        mDate = new Date();

        txtSizeBig = sp2px(MAX_TXT_SIZE_BIG_SP, mContext.getResources().getDisplayMetrics().scaledDensity);
        txtSizeSmall = sp2px(MAX_TXT_SIZE_SMALL_SP, mContext.getResources().getDisplayMetrics().scaledDensity);
        final int txtColor = mContext.getResources().getColor(android.R.color.holo_green_light);

        int padding = 0;

        txt1 = new CenterTextView(mContext);
        txt1.setText(clock01);
        txt1.setTextSize(txtSizeBig);
        txt1.setTextColor(txtColor);
        txt1.setBackgroundResource(0);
        txt1.setPadding(padding, padding, padding, padding);
        addView(txt1, 0);

        txt2 = new CenterTextView(mContext);
        txt2.setText(clock02);
        txt2.setTextSize(txtSizeBig);
        txt2.setTextColor(txtColor);
        txt2.setBackgroundResource(0);
        txt2.setPadding(padding, padding, padding, padding);
        addView(txt2, 1);

        txt3 = new CenterTextView(mContext);
        txt3.setText(clock03);
        txt3.setTextSize(txtSizeBig);
        txt3.setTextColor(txtColor);
        txt3.setBackgroundResource(0);
        txt3.setPadding(padding, padding, padding, padding);
        addView(txt3, 2);

        txt4 = new CenterTextView(mContext);
        txt4.setText(clock04);
        txt4.setTextSize(txtSizeBig);
        txt4.setTextColor(txtColor);
        txt4.setBackgroundResource(0);
        txt4.setPadding(padding, padding, padding, padding);
        addView(txt4, 3);

        txt5 = new CenterTextView(mContext);
        txt5.setText(clock05);
        txt5.setTextSize(txtSizeBig);
        txt5.setTextColor(txtColor);
        txt5.setBackgroundResource(0);
        txt5.setPadding(padding, padding, padding, padding);
        addView(txt5, 4);

        txt6 = new CenterTextView(mContext);
        txt6.setText(clock06);
        txt6.setTextSize(txtSizeBig);
        txt6.setTextColor(txtColor);
        txt6.setBackgroundResource(0);
        txt6.setPadding(padding, padding, padding, padding);
        addView(txt6, 5);

        txt7 = new CenterTextView(mContext);
        txt7.setText(clock07);
        txt7.setTextSize(txtSizeBig);
        txt7.setTextColor(txtColor);
        txt7.setBackgroundResource(0);
        txt7.setPadding(padding, padding, padding, padding);
        addView(txt7, 6);

        txt8 = new CenterTextView(mContext);
        txt8.setText(clock08);
        txt8.setTextSize(txtSizeBig);
        txt8.setTextColor(txtColor);
        txt8.setBackgroundResource(0);
        txt8.setPadding(padding, padding, padding, padding);
        addView(txt8, 7);

        txt9 = new CenterTextView(mContext);
        txt9.setText(clock09);
        txt9.setTextSize(txtSizeBig);
        txt9.setTextColor(txtColor);
        txt9.setBackgroundResource(0);
        txt9.setPadding(padding, padding, padding, padding);
        addView(txt9, 8);

        txt10 = new CenterTextView(mContext);
        txt10.setText(clock10);
        txt10.setTextSize(txtSizeBig);
        txt10.setTextColor(txtColor);
        txt10.setBackgroundResource(0);
        txt10.setPadding(padding, padding, padding, padding);
        addView(txt10, 9);

        txt11 = new CenterTextView(mContext);
        txt11.setText(clock11);
        txt11.setTextSize(txtSizeBig);
        txt11.setTextColor(txtColor);
        txt11.setBackgroundResource(0);
        txt11.setPadding(padding, padding, padding, padding);
        addView(txt11, 10);

        txt12 = new CenterTextView(mContext);
        txt12.setText(clock12);
        txt12.setTextSize(txtSizeBig);
        txt12.setTextColor(txtColor);
        txt12.setBackgroundResource(0);
        txt12.setPadding(padding, padding, padding, padding);
        addView(txt12, 11);

        handHour = new ClockHandView(mContext);
        handHour.run();
        addView(handHour, 12);
        
        handMinute = new ClockHandView(mContext);
        handMinute.run();
        addView(handMinute, 13);

        handSecond = new ClockHandView(mContext);
        handSecond.run();
        addView(handSecond, 14);

        txtLogo = new CenterTextView(mContext);
        // txtLogo.setText(R.string.app_name);
        txtLogo.setText("•");
        txtLogo.setTextColor(mContext.getResources().getColor(android.R.color.holo_green_light));
        txtLogo.setTextSize(txtSizeBig);
        addView(txtLogo, 15);
        
        // setBackgroundColor(0xFFFFFFFF);
        // setBackground(null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter();

            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

            getContext().registerReceiver(mIntentReceiver, filter);
        }

        // NOTE: It's safe to do these after registering the receiver since the receiver always runs
        // in the main thread, therefore the receiver can't run before this method returns.

        // The time zone may have changed while the receiver wasn't registered, so update the Time
        mCalendar = new Time();

        // Make sure we update to the current time
        onTimeChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            getContext().unregisterReceiver(mIntentReceiver);
            mAttached = false;
        }
    }

    private void onTimeChanged() {
        mCalendar.setToNow();
        updateContentDescription(mCalendar);
    }

    private void updateContentDescription(Time time) {
        final int flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR;
        String contentDescription = DateUtils.formatDateTime(mContext, time.toMillis(false), flags);
        setContentDescription(contentDescription);
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                String tz = intent.getStringExtra("time-zone");
                mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
            }

            onTimeChanged();

            // 更正指针角度
            handSecond.setStartAngle((float) getCurrentSSAngle());
            handMinute.setStartAngle((float) getCurrentMMAngle());
            handHour.setStartAngle((float) getCurrentHHAngle());

            LogUtil.showLog(mContext, "更正指针角度");

//            invalidate();
//            LogUtil.showLog(mContext, "invalidate");
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // match screen width ======================================================================
//        // 获取该ViewGroup的实际长和宽
//        // 涉及到MeasureSpec类的使用
//        int specSizeWidth = MeasureSpec.getSize(widthMeasureSpec);
//        int specSizeHeight = MeasureSpec.getSize(heightMeasureSpec);
//        // 设置本ViewGroup的宽高
//        int minSize = specSizeWidth < specSizeHeight ? specSizeWidth : specSizeHeight;
//        setMeasuredDimension(minSize, minSize);
    }
    
    /**
     * 获取表盘数字X坐标
     * 
     * @param centerX
     * @param radius
     * @param hour
     * @return
     */
    private double getChildX(double centerX, double radius, int hour) {
        double angle = 2 * Math.PI / 12 * hour;
        double sin = Math.sin(angle);
        return centerX + radius * sin;
    }

    /**
     * 获取表盘数字Y坐标
     * 
     * @param centerY
     * @param radius
     * @param hour
     * @return
     */
    private double getChildY(double centerY, double radius, int hour) {
        double angle = 2 * Math.PI / 12 * hour;
        double cos = Math.cos(angle);
        return centerY - radius * cos;
    }

    private ClockHandView handSecond, handMinute, handHour;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        LogUtil.showLog("changed --> " + changed + " l --> " + l + " t --> " + t + " r --> " + r + " b --> " + b);
        // 表盘宽度
        final double clockWidth = r - l;
        // 表盘高度
        final double clockHeight = b - t;
        // 表盘数字宽度
        final double childWidth = (clockWidth < clockHeight ? clockWidth : clockHeight) / 6;
        // 表盘数字高度
        final double childHeight = childWidth;
        // 半径
        final double radius = (clockWidth - childWidth) / 2;
        for (int i = 0; i < 12; i++) {
            View child = getChildAt(i);
            double childX = getChildX(clockWidth / 2, radius, i + 1);
            double childY = getChildY(clockHeight / 2, radius, i + 1);
            // 表盘数字左边界
            int childL = (int) (childX - childWidth / 2);
            // 表盘数字上边界
            int childT = (int) (childY - childHeight / 2);
            // 表盘数字右边界
            int childR = (int) (childX + childWidth / 2);
            // 表盘数字下边界
            int childB = (int) (childY + childHeight / 2);
            child.layout(childL, childT, childR, childB);
            // child.setBackgroundColor(mContext.getResources().getColor(android.R.color.holo_red_light));
//            ((CenterTextView) child).setText(String.valueOf(i + 1));
            // ((CenterTextView) child).setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            ((CenterTextView) child).setTextAlign(CenterTextView.TEXT_ALIGN_CENTER_HORIZONTAL | CenterTextView.TEXT_ALIGN_CENTER_VERTICAL);
        }

        // 字体大小调整
//        float clockWidth = (float) getWidth();
        float screenWidth = (float) getResources().getDisplayMetrics().widthPixels;
        txtSizeBig = sp2px(MAX_TXT_SIZE_BIG_SP / screenWidth * (float) clockWidth, mContext.getResources().getDisplayMetrics().scaledDensity);
        txtSizeSmall = sp2px(MAX_TXT_SIZE_SMALL_SP / screenWidth * (float) clockWidth, mContext.getResources().getDisplayMetrics().scaledDensity);
        txt1.setTextSize(txtSizeBig);
        txt2.setTextSize(txtSizeBig);
        txt3.setTextSize(txtSizeBig);
        txt4.setTextSize(txtSizeBig);
        txt5.setTextSize(txtSizeBig);
        txt6.setTextSize(txtSizeBig);
        txt7.setTextSize(txtSizeBig);
        txt8.setTextSize(txtSizeBig);
        txt9.setTextSize(txtSizeBig);
        txt10.setTextSize(txtSizeBig);
        txt11.setTextSize(txtSizeBig);
        txt12.setTextSize(txtSizeBig);
        txtLogo.setTextSize(txtSizeSmall);
        
        handHour = (ClockHandView) getChildAt(12);
        handHour.setCycle(60000 * 60 * 12);
        handHour.setRadiusScale(0.6f);
        handHour.setStartAngle((float) getCurrentHHAngle());
        handHour.setHandColor(0xff, 0x00, 0x00, 0xff);
        handHour.setHandStroke(6);
        handHour.layout((int) childWidth, (int) childHeight, (int) (clockWidth - childWidth), (int) (clockHeight - childHeight));

        handMinute = (ClockHandView) getChildAt(13);
        handMinute.setCycle(60000 * 60);
        handMinute.setRadiusScale(0.8f);
        handMinute.setStartAngle((float) getCurrentMMAngle());
        handMinute.setHandColor(0xff, 0x00, 0xff, 0x00);
        handMinute.setHandStroke(4);
        handMinute.layout((int) childWidth, (int) childHeight, (int) (clockWidth - childWidth), (int) (clockHeight - childHeight));

        handSecond = (ClockHandView) getChildAt(14);
        handSecond.setCycle(60000);
        handSecond.setRadiusScale(1);
        handSecond.setStartAngle((float) getCurrentSSAngle());
        handSecond.setHandColor(0xff, 0xff, 0x00, 0x00);
        handSecond.setHandStroke(2);
        handSecond.layout((int) childWidth, (int) childHeight, (int) (clockWidth - childWidth), (int) (clockHeight - childHeight));

        View childLogo = getChildAt(15);
        int logoW = (int) clockWidth;
        int logoH = (int) clockHeight;
        childLogo.layout((int) (clockWidth - logoW) / 2, (int) (clockHeight - logoH) / 2, (int) (clockWidth - logoW) / 2 + logoW, (int) (clockHeight - logoH) / 2 + logoH);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LogUtil.showLog(mContext, "onDraw");
    }
    
    /**
     * 获取当前时针角度
     * 
     * @return
     */
    private double getCurrentHHAngle() {
//        String currentTime = mSimpleDateFormat.format(mDate);
//        int hh = Integer.parseInt(currentTime.substring(0, 2));
//        int mm = Integer.parseInt(currentTime.substring(3, 5));
//        int ss = Integer.parseInt(currentTime.substring(6, 8));
        mCalendar.setToNow();
        int hh = mCalendar.hour;
        int mm = mCalendar.minute;
        int ss = mCalendar.second;
        return 2 * Math.PI / 12 * hh + 2 * Math.PI / 12 / 60 * mm + 2 * Math.PI / 12 / 60 / 60 * ss;
    }
    
    /**
     * 获取当前分针角度
     * 
     * @return
     */
    private double getCurrentMMAngle() {
//        String currentTime = mSimpleDateFormat.format(mDate);
//        int mm = Integer.parseInt(currentTime.substring(3, 5));
//        int ss = Integer.parseInt(currentTime.substring(6, 8));
        mCalendar.setToNow();
        int mm = mCalendar.minute;
        int ss = mCalendar.second;
        return 2 * Math.PI / 60 * mm + 2 * Math.PI / 60 / 60 * ss;
    }
    
    /**
     * 获取当前秒针角度
     * 
     * @return
     */
    private double getCurrentSSAngle() {
//        String currentTime = mSimpleDateFormat.format(mDate);
//        int ss = Integer.parseInt(currentTime.substring(6, 8));
        mCalendar.setToNow();
        int ss = mCalendar.second;
        return 2 * Math.PI / 60 * ss;
    }

}
