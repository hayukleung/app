package com.hayukleung.app.dice;

import android.app.Service;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 色子
 * ------------------------------------------
 * 传感器|震动|随机数|帧动画|防止Handler泄露的写法
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager = null;
    private Sensor mSensor;
    private Vibrator mVibrator = null;

    private volatile static boolean isRequest = false;

    private float lastX;
    private float lastY;
    private float lastZ;
    private long lastUpdateTime;

    /** 这个值越大需要越大的力气来摇晃手机 */
    private static final float SPEED_THRESHOLD = 0.8f;
    private static final int UPDATE_INTERVAL_TIME = 1000;

    private DiceHandler mDiceHandler = new DiceHandler(MainActivity.this);

    @InjectView(R.id.dice)
    ImageView mImageDice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mImageDice.setImageResource(R.mipmap.dice_1);

        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        mVibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSensorManager != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        mDiceHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

//        Log.i("dice --> ", "sensor changed");

        long currentUpdateTime = System.currentTimeMillis();
        long timeInterval = currentUpdateTime - lastUpdateTime;
        if (timeInterval < UPDATE_INTERVAL_TIME) {
            return;
        }
        lastUpdateTime = currentUpdateTime;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;

        lastX = x;
        lastY = y;
        lastZ = z;

        double speed = (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval) * 100;
        Log.i("dice --> ", "speed: " + speed);
        if (speed >= SPEED_THRESHOLD && !isRequest) {
            mVibrator.vibrate(300);
            isRequest = true;
            mImageDice.setImageResource(R.drawable.action_dice);
            ((AnimationDrawable) mImageDice.getDrawable()).start();
            mDiceHandler.sendEmptyMessageDelayed(0, (int) (speed * 1200f));
        } else {
//            Log.i("dice --> ", "speed not enough");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private static class DiceHandler extends Handler {

        private WeakReference<Context> mReference;

        public DiceHandler(Context context) {
            mReference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity context = (MainActivity) mReference.get();
            if (null != context) {

                context.mImageDice.setImageResource(R.drawable.action_dice);
                ((AnimationDrawable) context.mImageDice.getDrawable()).stop();

                int result = getRandomInt();
                switch (result) {
                    case 1:
                        context.mImageDice.setImageResource(R.mipmap.dice_1);
                        break;
                    case 2:
                        context.mImageDice.setImageResource(R.mipmap.dice_2);
                        break;
                    case 3:
                        context.mImageDice.setImageResource(R.mipmap.dice_3);
                        break;
                    case 4:
                        context.mImageDice.setImageResource(R.mipmap.dice_4);
                        break;
                    case 5:
                        context.mImageDice.setImageResource(R.mipmap.dice_5);
                        break;
                    case 6:
                        context.mImageDice.setImageResource(R.mipmap.dice_6);
                        break;
                }
                context.isRequest = false;
            }
        }
    }

    public static int getRandomInt() {
        Random random = new Random(SystemClock.elapsedRealtime());
        return random.nextInt(6) + 1;
    }

}
