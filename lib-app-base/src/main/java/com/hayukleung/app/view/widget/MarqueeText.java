package com.hayukleung.app.view.widget;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeText extends TextView {
    public MarqueeText(Context con) {
        super(con);
        init();
    }

    public MarqueeText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarqueeText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setEllipsize(TruncateAt.MARQUEE);
        setSingleLine();
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
    }
}
