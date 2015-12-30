package com.hayukleung.app;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hayukleung.app.base.R;
import com.hayukleung.app.view.widget.BlockDialog;

/**
 * 对话框集合
 */
public class Dialogs {

    public interface DialogCallback {
        void callback();
    }

    /**
     * 是或否
     *
     * @param activity
     * @param yesCallback
     * @param noCallback
     * @return
     */
    public static Dialog showYesOrNo(final Activity activity, final String content, final DialogCallback yesCallback, final DialogCallback noCallback) {
        final Dialog dialog = new Dialog(activity, R.style.dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_yes_or_no);

        ((TextView) dialog.findViewById(R.id.content)).setText(content);

        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != noCallback) {
                    noCallback.callback();
                }
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != yesCallback) {
                    yesCallback.callback();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }

    public static BlockDialog block(Activity activity, int res) {
        return block(activity, activity.getString(res));
    }

    public static BlockDialog block(Activity activity) {
        return block(activity, null);
    }

    public static BlockDialog block(Activity activity, String content) {
        if (TextUtils.isEmpty(content)) {
            content = "加载中";
        }
        final BlockDialog dialog = BlockDialog.create(activity).setContent(content);
        dialog.show();
        return dialog;
    }
}
