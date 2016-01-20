package com.hayukleung.app.widget.paintpad.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    private float MIN_PAINT_WIDTH;
    /**
     * 线宽增量
     */
    private float DELTA_PAINT_WIDTH;
    /**
     * 画笔
     */
    private Drawing mDrawing;
    /**
     * 当前线宽
     */
    private float mPaintWidth;
    /**
     * 画板
     */
    private PaintPad mPaintPad;
    /**
     * 线宽控制
     */
    private SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_paint_pad);
        mPaintPad = (PaintPad) findViewById(R.id.paint_pad);
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mDrawing = new DrawingFactory().createDrawing(DrawingId.DRAWING_PATHLINE);
        mPaintPad.setDrawing(mDrawing);
        Brush.getPen().reset(getResources().getColor(R.color.default_drawing_color), mPaintWidth = MIN_PAINT_WIDTH = DELTA_PAINT_WIDTH = getResources().getDimensionPixelSize(R.dimen.xp1_0));
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
    }
}
