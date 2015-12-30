package com.hayukleung.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 一直被认为得到焦点的TextView，用于RecyclerView|ListView中的跑马灯效果
 *
 * Created by hayukleung on 11/11/15.
 */
public class AlwaysFocusedTextView extends TextView {

    public AlwaysFocusedTextView(Context context) {
        super(context);
    }

    public AlwaysFocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlwaysFocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public AlwaysFocusedTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(true, direction, previouslyFocusedRect);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    public boolean isSelected() {
        return true;
    }

    /**
     * 启动跑马灯
     */
    public void startMarquee() {
        setSingleLine(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
//        requestFocus();
        setSelected(true);
    }
}
