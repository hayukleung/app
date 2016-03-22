package com.hayukleung.fingerprint;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.hayukleung.mice.R;
import com.hayukleung.permissions.PermissionsRequiredActivity;
import com.hayukleung.permissions.PermissionsUtils;

/**
 * 小白鼠-指纹
 *
 * http://xnfood.com.tw/android-fingerprintmanager-api/
 */
public class FingerprintActivity extends PermissionsRequiredActivity {

    private KeyguardManager mKeyguardManager;
    private FingerprintManager mFingerprintManager;
    private FingerprintManagerCompat mFingerprintManagerCompat;
    private CancellationSignal mCancellationSignal;
    private android.support.v4.os.CancellationSignal mCancellationSignalCompat;

    @Override
    protected String[] requiredPermissions() {
        return new String[] { Manifest.permission.USE_FINGERPRINT };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mice);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mKeyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // or Activity.FINGERPRINT_SERVICE
            mFingerprintManager = getSystemService(FingerprintManager.class);
        } else {
            // 获取一个FingerPrintManagerCompat的实例
            mFingerprintManagerCompat = FingerprintManagerCompat.from(this);
        }

        if (!mKeyguardManager.isKeyguardSecure()) {
            // 没有设定 fingerprint screen lock
            Log.d(FingerprintActivity.class.getSimpleName(), "没有设定锁屏");
            return;
        }

        // 权限判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Log.d(FingerprintActivity.class.getSimpleName(), "没有指纹权限（API23）");
                return;
            }
        } else {
            if (PermissionsUtils.lacksPermissions(this, new String[] { Manifest.permission.USE_FINGERPRINT })) {
                Log.d(FingerprintActivity.class.getSimpleName(), "没有指纹权限");
                return;
            }
        }

        // 硬件判断
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!mFingerprintManager.isHardwareDetected()) {
                // 设备没有 fingerprint reader
                Log.d(FingerprintActivity.class.getSimpleName(), "没有指纹识别功能");
                return;
            }
        } else {
            if (!mFingerprintManagerCompat.isHardwareDetected()) {
                // 设备没有 fingerprint reader
                Log.d(FingerprintActivity.class.getSimpleName(), "没有指纹识别功能");
                return;
            }
        }

        // 是否录入指纹
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!mFingerprintManager.hasEnrolledFingerprints()) {
                // 设备没有录入任何指纹
                Log.d(FingerprintActivity.class.getSimpleName(), "没有录入指纹");
                return;
            }
        } else {
            if (!mFingerprintManagerCompat.hasEnrolledFingerprints()) {
                // 设备没有录入任何指纹
                Log.d(FingerprintActivity.class.getSimpleName(), "没有录入指纹");
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCancellationSignal = new CancellationSignal();
        } else {
            mCancellationSignalCompat = new android.support.v4.os.CancellationSignal();
        }
        authenticate();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (null != mCancellationSignal) {
                mCancellationSignal.cancel();
                mCancellationSignal = null;
            }
        } else {
            if (null != mCancellationSignalCompat) {
                mCancellationSignalCompat.cancel();
                mCancellationSignalCompat = null;
            }
        }
    }

    private void authenticate() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Log.d(FingerprintActivity.class.getSimpleName(), "没有指纹权限");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /**
             * 其中 crypto 為 Android 6.0中 crypto objects 的 wrapper class，可以透過它讓 authenticate 過程更為安全，但也可以不使用；
             * cancel 即用來取消 authenticate 的物件；
             * flags 為一個旗標，只能設為 0；
             * callback 用來接受 authenticate 成功與否，一共有三個 callback method；
             * 最後 handler 為 optional 的參數，如果有使用，則 FingerprintManager 可以透過它來傳遞訊息。
             */
            mFingerprintManager.authenticate(null, mCancellationSignal, 0, new CallBack1(), null);
        } else {
            /**
             * 开始验证，什么时候停止由系统来确定，
             * 如果验证成功，那么系统会关闭sensor，
             * 如果失败，则允许多次尝试，
             * 如果依旧失败，则会拒绝一段时间，然后关闭sensor，过一段时候之后再重新允许尝试
             *
             * 第四个参数为重点，需要传入一个FingerprintManagerCompat.AuthenticationCallback的子类
             * 并重写一些方法，不同的情况回调不同的函数
             */
            mFingerprintManagerCompat.authenticate(null, 0, mCancellationSignalCompat, new CallBack2(), null);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public class CallBack1 extends FingerprintManager.AuthenticationCallback {

        private static final String TAG = "MyCallBack1";

        // 当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            Log.d(TAG, "onAuthenticationError: " + errString);
        }

        // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
        @Override
        public void onAuthenticationFailed() {
            Log.d(TAG, "onAuthenticationFailed: " + "验证失败");
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            Log.d(TAG, "onAuthenticationHelp: " + helpString);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            Log.d(TAG, "onAuthenticationSucceeded: " + "验证成功");
            authenticate();
        }
    }

    public class CallBack2 extends FingerprintManagerCompat.AuthenticationCallback {

        private static final String TAG = "MyCallBack2";

        // 当出现错误的时候回调此函数，比如多次尝试都失败了的时候，errString是错误信息
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            Log.d(TAG, "onAuthenticationError: " + errString);
        }

        // 当指纹验证失败的时候会回调此函数，失败之后允许多次尝试，失败次数过多会停止响应一段时间然后再停止sensor的工作
        @Override
        public void onAuthenticationFailed() {
            Log.d(TAG, "onAuthenticationFailed: " + "验证失败");
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            Log.d(TAG, "onAuthenticationHelp: " + helpString);
        }

        // 当验证的指纹成功时会回调此函数，然后不再监听指纹sensor
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            Log.d(TAG, "onAuthenticationSucceeded: " + "验证成功");
            authenticate();
        }
    }
}
