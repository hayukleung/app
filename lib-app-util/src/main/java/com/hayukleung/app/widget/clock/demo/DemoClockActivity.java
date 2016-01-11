package com.hayukleung.app.widget.clock.demo;

import android.app.Activity;
import android.os.Bundle;

import com.hayukleung.app.util.R;
import com.hayukleung.app.widget.clock.AnalogClockView;

/**
 * 展示{@link AnalogClockView}
 * 
 * @author HayukLeung
 *
 */
public class DemoClockActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_clock);
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
