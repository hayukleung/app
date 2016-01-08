package com.hayukleung.app.analogclock;

import android.app.Activity;
import android.os.Bundle;

import com.hayukleung.app.R;

/**
 * 展示{@link com.hayukleung.app.analogclock.AnalogClockView}
 * 
 * @author HayukLeung
 *
 */
public class ClockActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        // AnalogClockView analogClockView = (AnalogClockView) findViewById(R.id.ActivityHelloView$analog_clock_view);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }
}
