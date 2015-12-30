package com.hayukleung.app.view.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hayukleung.app.base.R;

/**
 * 阻塞式 Dialog
 */
public class BlockDialog extends Dialog {
    private Runnable mFinish;
    private final Runnable mCancel = new Runnable() {
        @Override
        public void run() {
            dismiss();
            if (mFinish != null) {
                mFinish.run();
            }
        }
    };
    private View mProgressLayout;
    private ProgressBar mProgressBar;
    private TextView mContent;
    private TextView mResult;

    private BlockDialog(Context context) {
        this(context, R.style.dialog);
    }

    private BlockDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public static BlockDialog create(Context context) {
        return new BlockDialog(context);
    }

    private void init() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        super.setContentView(R.layout.dialog_block);
        mProgressLayout = findViewById(R.id.progress);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mContent = (TextView) findViewById(R.id.content);
        mResult = (TextView) findViewById(R.id.result);
    }

    public BlockDialog setContent(int resId) {
        mContent.setText(resId);
        return this;
    }

    public BlockDialog setContent(int resId, Object... objects) {
        mContent.setText(getContext().getResources().getString(resId, objects));
        return this;
    }

    public BlockDialog setContent(String content) {
        mContent.setText(content);
        return this;
    }

    public BlockDialog pop() {
        show();
        return this;
    }

    public void setResult(String result, Runnable finish) {
        mResult.setText(result);
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
        mResult.startAnimation(fadeIn);
        mResult.setVisibility(View.VISIBLE);
        mProgressLayout.startAnimation(fadeOut);
        mProgressLayout.setVisibility(View.GONE);
        mFinish = finish;
        mResult.postDelayed(mCancel, 2000);
    }

    public void setResult(String result, Runnable finish, long delay) {
        mResult.setText(result);
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out);
        mResult.startAnimation(fadeIn);
        mResult.setVisibility(View.VISIBLE);
        mProgressLayout.startAnimation(fadeOut);
        mProgressLayout.setVisibility(View.GONE);
        mFinish = finish;
        mResult.postDelayed(mCancel, delay);
    }

    public void setResult(String result) {
        setResult(result, null);
    }

    public void setResult(String result, long delay) {
        setResult(result, null, delay);
    }

    @Override
    public void dismiss() {
        mResult.removeCallbacks(mCancel);
        super.dismiss();
    }

    @Override
    public void setContentView(View view) {
        throw new IllegalStateException("Can not call setContentView method");
    }

    @Override
    public void setContentView(int layoutResID) {
        throw new IllegalStateException("Can not call setContentView method");
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        throw new IllegalStateException("Can not call setContentView method");
    }
}
