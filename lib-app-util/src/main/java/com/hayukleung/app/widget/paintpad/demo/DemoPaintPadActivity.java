package com.hayukleung.app.widget.paintpad.demo;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;

import com.hayukleung.app.util.R;
import com.hayukleung.app.widget.paintpad.PaintPad;
import com.hayukleung.app.widget.paintpad.drawings.Drawing;
import com.hayukleung.app.widget.paintpad.drawings.DrawingFactory;
import com.hayukleung.app.widget.paintpad.drawings.DrawingId;
import com.hayukleung.app.widget.paintpad.tools.Brush;

/**
 * DemoPaintPadActivity.java
 * <p>
 * Created by hayukleung on 1/20/16.
 */
public class DemoPaintPadActivity extends AppCompatActivity {

    /**
     * 最小线宽
     */
    private float MIN_PAINT_WIDTH = 0;
    /**
     * 线宽增量
     */
    private float DELTA_PAINT_WIDTH = 0;
    /**
     * 画笔
     */
    private Drawing mDrawing;
    /**
     * 当前线宽
     */
    private float mPaintWidth = 0;
    /**
     * 画板
     */
    private PaintPad mPaintPad;
    /**
     * 线宽控制
     */
    private SeekBar mSeekBar;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 强制竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_demo_paint_pad);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mPaintPad = (PaintPad) findViewById(R.id.paint_pad);
//        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mDrawing = new DrawingFactory().createDrawing(DrawingId.DRAWING_PATHLINE);
        mPaintPad.setDrawing(mDrawing);

        if (null != savedInstanceState) {
            MIN_PAINT_WIDTH = savedInstanceState.getFloat("min_paint_width");
            DELTA_PAINT_WIDTH = savedInstanceState.getFloat("delta_paint_width");
            mPaintWidth = savedInstanceState.getFloat("paint_width");
        }

        if (0 >= MIN_PAINT_WIDTH || 0 >= DELTA_PAINT_WIDTH || 0 >= mPaintWidth) {
            mPaintWidth = MIN_PAINT_WIDTH = DELTA_PAINT_WIDTH = getResources().getDimensionPixelSize(R.dimen.xp1_0);
        }

        Brush.getPen().reset(getResources().getColor(R.color.default_drawing_color), mPaintWidth);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        mSeekBar = (SeekBar) navigationView.getHeaderView(0).findViewById(R.id.seek_bar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Brush.getPen().setPaintWidth(mPaintWidth = MIN_PAINT_WIDTH + DELTA_PAINT_WIDTH * ((float) seekBar.getProgress()) / 3f);
            }
        });

        navigationView.getHeaderView(0).findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存
                mPaintPad.saveBitmap(DemoPaintPadActivity.this);
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putFloat("min_paint_width", MIN_PAINT_WIDTH);
        outState.putFloat("delta_paint_width", DELTA_PAINT_WIDTH);
        outState.putFloat("paint_width", mPaintWidth);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        MIN_PAINT_WIDTH = savedInstanceState.getFloat("min_paint_width");
        DELTA_PAINT_WIDTH = savedInstanceState.getFloat("delta_paint_width");
        mPaintWidth = savedInstanceState.getFloat("paint_width");
    }
}
