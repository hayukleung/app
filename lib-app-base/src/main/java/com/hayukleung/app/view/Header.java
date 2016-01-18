package com.hayukleung.app.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hayukleung.app.base.R;
import com.hayukleung.app.view.refresh.SmoothProgressBar;
import com.hayukleung.app.view.widget.AutoScaleTextView;

public class Header extends LinearLayout {
    private FrameLayout mLeft, mCenter, mRight;
    private SmoothProgressBar mProgressBar;
    private View mRoot;

    private int mTextColorResId;

    public Header(Context context) {
        this(context, null);
    }

    public Header(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Header(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Header(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    public void init(final Context context, AttributeSet attrs, int defStyleAttr) {
        View view = LayoutInflater.from(context).inflate(R.layout.header, this, true);
        mRoot = view.findViewById(R.id.header_root);
        mLeft = (FrameLayout) view.findViewById(R.id.header_left);
        mCenter = (FrameLayout) view.findViewById(R.id.header_center);
        mRight = (FrameLayout) view.findViewById(R.id.header_right);
        mProgressBar = (SmoothProgressBar) view.findViewById(R.id.header_progressBar);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Header, defStyleAttr, 0);
        mRoot.setBackgroundResource(a.getResourceId(R.styleable.Header_headerBackground, R.drawable.bg_header));
        mTextColorResId = a.getResourceId(R.styleable.Header_headerTextColor, R.color.darker_gray);
        a.recycle();
    }

    public void removeLeft() {
        mLeft.removeAllViews();
    }

    public void setLeftText(int resId, OnClickListener listener) {
        setLeftText(getResources().getString(resId), listener);
    }

    public void setLeftText(String text, OnClickListener listener) {
        AutoScaleTextView view = new AutoScaleTextView(getContext());
        view.setSingleLine();
//        view.setMinTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
        view.setTextColor(getResources().getColor(mTextColorResId));
        view.setGravity(Gravity.CENTER);
        view.setText(text);

        mLeft.removeAllViews();
        mLeft.addView(view);
        mLeft.setOnClickListener(listener);
    }

    public void setLeftIcon(int resId, OnClickListener listener) {
        ImageView view = new ImageView(getContext());
        view.setImageResource(resId);

        mLeft.removeAllViews();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mLeft.addView(view, params);
        mLeft.setOnClickListener(listener);
    }

    public void setLeftView(View view, OnClickListener listener) {
        mLeft.removeAllViews();
        LayoutParams pa = (LayoutParams) mLeft.getLayoutParams();
//        pa.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        pa.width = getContext().getResources().getDimensionPixelSize(R.dimen.header_button_width);
        pa.height = getContext().getResources().getDimensionPixelSize(R.dimen.header_button_width);
        mLeft.setLayoutParams(pa);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mLeft.addView(view, params);
        mLeft.setOnClickListener(listener);
    }

    public void setLeftView(View view, int width, OnClickListener listener) {
        mLeft.removeAllViews();
        LayoutParams pa = (LayoutParams) mLeft.getLayoutParams();
        pa.width = width;
        mLeft.setLayoutParams(pa);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        mLeft.addView(view, params);
        mLeft.setOnClickListener(listener);
    }

    public void removeRight() {
        mRight.removeAllViews();
    }

    public void setRightText(int resId, OnClickListener listener) {
        setRightText(getResources().getString(resId), listener);
    }

    public void setRightText(String text, OnClickListener listener) {
        AutoScaleTextView view = new AutoScaleTextView(getContext());
        view.setSingleLine();
//        view.setMinTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
        view.setTextColor(getResources().getColor(mTextColorResId));
        view.setGravity(Gravity.CENTER);
        view.setText(text);

        mRight.removeAllViews();
        mRight.addView(view);
        mRight.setOnClickListener(listener);
    }

    public void setRightText(String text, int colorId, OnClickListener listener) {
        AutoScaleTextView view = new AutoScaleTextView(getContext());
        view.setSingleLine();
        view.setTextColor(getResources().getColor(colorId));
        view.setGravity(Gravity.CENTER);
        view.setText(text);

        mRight.removeAllViews();
        mRight.addView(view);
        mRight.setOnClickListener(listener);
    }

    public void setRightText(int resId, int colorId, OnClickListener listener) {
        AutoScaleTextView view = new AutoScaleTextView(getContext());
        view.setSingleLine();
        view.setTextColor(getResources().getColor(colorId));
        view.setGravity(Gravity.CENTER);
        view.setText(getResources().getString(resId));

        mRight.removeAllViews();
        mRight.addView(view);
        mRight.setOnClickListener(listener);
    }

    public void setRightIcon(int resId, OnClickListener listener) {
        ImageView view = new ImageView(getContext());
        view.setImageResource(resId);

        mRight.removeAllViews();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mRight.addView(view, params);
        mRight.setOnClickListener(listener);
    }

    public void setRightView(View view, OnClickListener listener) {
        mRight.removeAllViews();
        LayoutParams pa = (LayoutParams) mRight.getLayoutParams();
        pa.width = getContext().getResources().getDimensionPixelSize(R.dimen.header_button_width);
        pa.height = getContext().getResources().getDimensionPixelSize(R.dimen.header_button_width);
        mRight.setLayoutParams(pa);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mRight.addView(view, params);
        mRight.setOnClickListener(listener);
    }

    public void setRightView(View view, int width, OnClickListener listener) {
        mRight.removeAllViews();
        LayoutParams pa = (LayoutParams) mRight.getLayoutParams();
        pa.width = width;
        mRight.setLayoutParams(pa);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        mRight.addView(view, params);
        mRight.setOnClickListener(listener);
    }

    public void remove() {
        mCenter.removeAllViews();
    }

    public void setCenterText(int resId, OnClickListener listener) {
        setCenterText(getResources().getString(resId), listener);
    }

    public void setCenterText(String text, OnClickListener listener) {
        TextView view = new TextView(getContext());
        view.setSingleLine();
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        view.setTextColor(getResources().getColor(mTextColorResId));
        view.setGravity(Gravity.CENTER);
        // 加粗
        view.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        view.setShadowLayer(1, 1, 1, 0);
        view.setText(text);
        view.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        mCenter.removeAllViews();
        mCenter.addView(view);
        mCenter.setOnClickListener(listener);
    }

    public void setCenterIcon(int resId, OnClickListener listener) {
        ImageView view = new ImageView(getContext());
        view.setImageResource(resId);

        mCenter.removeAllViews();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mCenter.addView(view, params);
        mCenter.setOnClickListener(listener);
    }

    public void setCenterView(int resid, OnClickListener listener) {
        setCenterView(inflate(getContext(), resid, null), listener);
    }

    public FrameLayout getCenterLayout() {
        return mCenter;
    }

    public FrameLayout getRightLayout() {
        return mRight;
    }

    public FrameLayout getLeftLayout() {
        return mLeft;
    }

    public void setCenterView(View view, OnClickListener listener) {
        mCenter.removeAllViews();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mCenter.addView(view, params);
        mCenter.setOnClickListener(listener);
    }

    public void setTriggerProgress(float percent) {
        mProgressBar.setTriggerPercentage(percent);
    }

    public void setProgress(float percent) {
        mProgressBar.setPercentage(percent);
    }

    public void startProgress() {
        mProgressBar.start();
    }

    public void stopProgress() {
        mProgressBar.stop();
    }

    public Drawable getRootBackground() {
        return mRoot.getBackground();
    }

    public void setRootBackgroundColor(int color) {
        mRoot.setBackgroundColor(color);
    }

    public void setRootBackgroundResource(int resid) {
        mRoot.setBackgroundResource(resid);
    }

    public SmoothProgressBar getProgressBar() {
        return mProgressBar;
    }

    public View getRoot() {
        return mRoot;
    }
}
