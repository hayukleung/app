package com.hayukleung.app.util.screen.demo;

import android.app.Activity;
import android.os.Bundle;

import com.hayukleung.app.util.R;

/**
 * 屏幕适配
 *
 * Created by hayukleung on 12/14/15.
 */
public class DemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_screen);
    }
}
