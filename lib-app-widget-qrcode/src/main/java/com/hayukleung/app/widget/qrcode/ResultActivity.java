package com.hayukleung.app.widget.qrcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 负责处理条码扫描结果
 * <p/>
 * 调用方式：String url = getTextResult(bundle);和Bitmap bitmap = getImgResult(bundle); bundle均为CaptureActivity传过来的数据。
 * <p/>
 * 返回：getTextResult此方法返回二维码解析后的url字符串；getImgResult此方法返回二 维码截取的图像。
 *
 * @author ming
 */
public class ResultActivity extends Activity {

    ImageView mImgResult;
    TextView mTxtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_result);
        //
        // mImgResult = (ImageView) findViewById(R.id.ActivityResult$img_result);
        // mTxtResult = (TextView) findViewById(R.id.ActivityResult$txt_result);

        Bundle bundle = ResultActivity.this.getIntent().getExtras();

        mTxtResult.setText(getTextResult(bundle));
        // mImgResult.setImageBitmap(getImgResult(bundle));
        mImgResult.setImageBitmap(QRCodeEncoder.createQRImage("http://blog.csdn.net/xiaorenwu1206/article/details/38684983"));

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    /**
     * 获取扫码解析后的url字符串
     *
     * @param bundle
     * @return 二维码解析后的url字符串
     */
    private String getTextResult(Bundle bundle) {
        if (null != bundle) {
            String txtResult = bundle.getString("result");
            return txtResult;
        } else {
            return "解码失败，请检查您的网址！";
        }
    }

    /**
     * 获取扫码截取的图片
     *
     * @param bundle
     * @return 二维码截取的图像
     */
    @SuppressWarnings("unused")
    private Bitmap getImgResult(Bundle bundle) {
        if (null != bundle) {
            Bitmap barcode = bundle == null ? null : (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);
            return barcode;
        } else {
            return null;
        }
    }

}
