package com.hayukleung.app.util.screen.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.hayukleung.app.util.LogUtil;
import com.hayukleung.app.util.R;

/**
 * 屏幕适配
 *
 * Created by hayukleung on 12/14/15.
 */
public class ScreenDemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_screen);

        View view = findViewById(R.id.square);
        ViewGroup.LayoutParams params = view.getLayoutParams();

        LogUtil.showLog(String.format("view-width --> %d, view-height --> %d", params.width, params.height));
    }
}
