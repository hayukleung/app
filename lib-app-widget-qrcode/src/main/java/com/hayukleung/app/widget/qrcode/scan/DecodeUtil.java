package com.hayukleung.app.widget.qrcode.scan;

import com.hayukleung.app.BaseActivity;
import com.hayukleung.app.util.ToastUtil;

public class DecodeUtil {

    /**
     * 解析二维码
     *
     * @param context
     * @param result
     * @param decodeListener
     */
    public static void decode(BaseActivity context, String result, DecodeListener decodeListener) {
        // TODO
        ToastUtil.showToast(context, result);
        if (null != decodeListener) {
            decodeListener.onPostDecode();
        }
    }

    public interface DecodeListener {
        /**
         * 解码结束
         */
        void onPostDecode();
    }
}
