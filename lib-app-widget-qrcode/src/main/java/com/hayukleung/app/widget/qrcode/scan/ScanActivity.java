package com.hayukleung.app.widget.qrcode.scan;

import com.hayukleung.app.Dialogs;
import com.hayukleung.app.view.widget.BlockDialog;
import com.hayukleung.app.widget.qrcode.CaptureActivity;

/**
 * 实现巨运宝的二维码结果解析
 *
 * @author HayukLeung
 */
public class ScanActivity extends CaptureActivity {

    @Override
    public void handleDecode(String result) {
        final BlockDialog dialog = Dialogs.block(ScanActivity.this);
        playBeepSoundAndVibrate();
        DecodeUtil.decode(ScanActivity.this, result, new DecodeUtil.DecodeListener() {

            @Override
            public void onPostDecode() {
                dialog.dismiss();
                ScanActivity.this.finish();
            }
        });
    }
}
