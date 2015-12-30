package com.hayukleung.app.widget.qrcode.scan;

import com.hayukleung.app.BaseActivity;

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
    }

    public interface DecodeListener {
        /**
         * 解码结束
         */
        public void onPostDecode();
    }
}
