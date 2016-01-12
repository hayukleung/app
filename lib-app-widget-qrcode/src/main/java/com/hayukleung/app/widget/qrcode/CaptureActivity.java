package com.hayukleung.app.widget.qrcode;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.hayukleung.app.BaseFragment;
import com.hayukleung.app.CommonActivity;
import com.hayukleung.app.view.Header;

import java.io.IOException;
import java.util.Vector;

/**
 * 负责二维码扫描的activity，首先通过ViewfinderView绘制扫描框，再通过SurfaceView打
 * 开相机，在循环自动对焦的状态下进行条码扫描，扫描完成后，跳转到ResultActivity处理
 * 扫描结果。
 * <p/>
 * 调用方式：startActivity(new Intent(YourActivity.this, CaptureActivity.class));
 * YourActivity为你需要调用二维码的activity。
 * <p/>
 * 返回：在handleDecode(Result obj, Bundle bundle) {}函数中obj为返回的解析的结果，
 * bundle带有截取的图片。
 *
 * @author ming
 */
public abstract class CaptureActivity extends CommonActivity implements Callback {

    private static final int REQ_ALBUM = 0x0001;
    /**
     * 播放的音量百分比：0.1最大音量的10%
     */
    private static final float BEEP_VOLUME = 0.10f;
    /**
     * 振动时间：200ms
     */
    private static final long VIBRATE_DURATION = 200L;
    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
    private CaptureActivityHandler handler;
    /**
     * 绘制扫描框的类
     */
    private ViewfinderView viewfinderView;
    /**
     * SurfaceView创建标志：true已创建，false未创建
     */
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private TextView txtResult;
    // private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    /**
     * 播放哔哔声的标志：true播放，false不播放
     */
    private boolean playBeep;
    /**
     * 振动的标志：true振动，false不振动
     */
    private boolean vibrate;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        Header header = (Header) findViewById(R.id.header);
        header.setLeftIcon(R.drawable.ic_arrow_back_white_36dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        header.setCenterText("二维码扫描", null);

        // 加上title bar之後，掃描長寬比不正確，需要作調整
//        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.ActivityCapture$framelayout);
//        ViewGroup.LayoutParams params = frameLayout.getLayoutParams();
//        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        // int sbar = 0;
        // try {
        // Class<?> c = Class.forName("com.android.internal.R$dimen");
        // Object obj = c.newInstance();
        // Field field = c.getField("status_bar_height");
        // sbar = getResources().getDimensionPixelSize(Integer.parseInt(field.get(obj).toString()));
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // params.height = displayMetrics.heightPixels - sbar - (int) getResources().getDimension(R.dimen.distance_px_120);
        // params.width = (int) ((float) params.height * (float) displayMetrics.widthPixels / (float) displayMetrics.heightPixels);
//        params.width = displayMetrics.widthPixels;
//        params.height = (int) ((float) displayMetrics.heightPixels * (float) params.width / (float) displayMetrics.widthPixels);
//        frameLayout.setLayoutParams(params);

        // Rect rect = new Rect();
        // Window window = getWindow();
        // // 状态栏的高度
        // int statusBarHeight = rect.top;
        // // 标题栏跟状态栏的总体高度
        // int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        // // 标题栏的高度：用上面的值减去状态栏的高度及为标题栏高度
        // int titleBarHeight = contentViewTop - statusBarHeight;
        // System.out.println(statusBarHeight+"..."+contentViewTop+"..."+titleBarHeight);


        // 创建CameraManager对象
        CameraManager.init(getApplication());

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        txtResult = (TextView) findViewById(R.id.txtResult);
        hasSurface = false;
        // inactivityTimer = new InactivityTimer(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        params.width = (int) getResources().getDimension(R.dimen.xp100);
        params.height = (int) getResources().getDimension(R.dimen.yp100);
        surfaceView.setLayoutParams(params);

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        // surface绘制完成才初始化相机，没有绘制完成则为surfaceHolder添加回调
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        // 默认发出哔哔声设为真，如果判断铃声模式不是一般模式则设为假
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        // inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    protected BaseFragment newFragment() {
        return null;
    }

    /**
     * 初始化相机。
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            // 尝试打开相机并初始化硬件参数
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            // surface绘制完成后初始化相机
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    /**
     * 处理解码的结果。
     *
     * @param obj    解码的结果
     * @param bundle
     */
    public void handleDecode(Result obj, Bundle bundle) {
        // inactivityTimer.onActivity();
        // 播放哔哔声并振动
        playBeepSoundAndVibrate();

        // 跳转到ResultActivity处理解码的结果
        // bundle.putString("result", obj.getBarcodeFormat().toString() + ":" + obj.getText());
        // startActivity(new Intent().putExtras(bundle).setClass(CaptureActivity.this, ResultActivity.class));
        txtResult.setText(obj.getBarcodeFormat().toString() + ":" + obj.getText());
    }

    /**
     * 处理解码的结果
     *
     * @param result
     */
    public abstract void handleDecode(String result);

    /**
     * 根据playbeep初始化哔哔声。
     */
    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            // 如果playBeep为真则创建MediaPlayer对象并为播放哔哔声作准备
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.qrcode_completed);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    /**
     * 根据标志播放哔哔声并振动。
     */
    protected void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
//        switch (arg0) {
//            case REQ_ALBUM:
//                if (RESULT_OK == arg1 && null != arg2) {
//                    // 上传图片
//                    Result result = QRCodeDecoder.scanImagePath(CaptureActivity.this, arg2.getStringExtra(AlbumConstants.KEY_PHOTO_PATH));
//                    if (result == null) {
//                        // 不是二维码图片
//                        ToastUtil.showToast(CaptureActivity.this, "您选择的图片不是二维码图片");
//                    } else {
//                        // 是二维码图片，有数据返回
//                        showLoadingDialog(false);
//                        DecodeUtil.decode(CaptureActivity.this, QRCodeDecoder.recode(result.toString()), new DecodeUtil.DecodeListener() {
//
//                            @Override
//                            public void onPostDecode() {
//                                hideLoadingDialog();
//                                CaptureActivity.this.finish();
//                            }
//                        });
//                    }
//                }
//                break;
//            default:
//                break;
//        }
    }
}