package com.hayukleung.app.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 自定义Toast
 *
 * Created by hayukleung on 12/8/15.
 */
public class ToastUtil {

    public static void showToast(Context context, CharSequence text) {
        showToast(context, text, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, CharSequence text, int duration) {
        CustomToast toast = new CustomToast(context);
        toast.setDuration(duration);
        toast.setText(text);
        toast.show();
    }

    public static void showToast(Context context, int resId) {
        showToast(context, resId, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, int resId, int duration) {
        CustomToast toast = new CustomToast(context);
        toast.setDuration(duration);
        toast.setText(resId);
        toast.show();
    }

    public static class CustomToast extends Toast {

        private TextView mTxtContent;

        /**
         * @param context
         */
        public CustomToast(Context context) {
            super(context);
            View layout = LayoutInflater.from(context).inflate(R.layout.layout_custom_toast, null);
            mTxtContent = (TextView) layout.findViewById(R.id.LayoutCustomToast$txt_content);
            setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, 0);
            setDuration(Toast.LENGTH_SHORT);
            setView(layout);
        }

        @Override
        public void setText(CharSequence s) {
            mTxtContent.setText(s);
        }

        @Override
        public void setText(int resId) {
            mTxtContent.setText(resId);
        }
    }
}
