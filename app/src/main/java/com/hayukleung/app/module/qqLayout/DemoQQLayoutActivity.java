package com.hayukleung.app.module.qqLayout;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.View;

import com.hayukleung.app.BaseFragment;
import com.hayukleung.app.CommonActivity;
import com.hayukleung.app.R;
import com.hayukleung.app.view.Header;
import com.nineoldandroids.view.ViewHelper;

/**
 * 仿QQ5.2主页布局
 * <p>
 * http://blog.csdn.net/lmj623565791/article/details/41531475
 *
 * @author zhy
 */
public class DemoQQLayoutActivity extends CommonActivity {

    private Header mHeader;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_qq_layout);

        initView();
        initEvents();
    }

    @Override
    protected BaseFragment newFragment() {
        return null;
    }

    private void initEvents() {

        mDrawerLayout.setDrawerListener(new DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;

                if (drawerView.getTag().equals("LEFT")) {
                    // 左菜单动画
                    float leftScale = 1 - 0.3f * scale;

                    ViewHelper.setScaleX(mMenu, leftScale);
                    ViewHelper.setScaleY(mMenu, leftScale);
                    ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));

                    ViewHelper.setTranslationX(mContent, mMenu.getMeasuredWidth() * (1 - scale));
                    ViewHelper.setPivotX(mContent, 0);
                    ViewHelper.setPivotY(mContent, mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                    ViewHelper.setScaleX(mContent, rightScale);
                    ViewHelper.setScaleY(mContent, rightScale);

//                    mMenu.setScaleX(leftScale);
//                    mMenu.setScaleY(leftScale);
//                    mMenu.setAlpha(0.6f + 0.4f * (1 - scale));

//                    mContent.setTranslationX(mMenu.getMeasuredWidth() * (1 - scale));
//                    mContent.setPivotX(0);
//                    mContent.setPivotY(mContent.getMeasuredHeight() / 2);
//                    mContent.invalidate();
//                    mContent.setScaleX(rightScale);
//                    mContent.setScaleY(rightScale);

                } else {
                    // 右菜单动画
                    ViewHelper.setTranslationX(mContent, -mMenu.getMeasuredWidth() * slideOffset);
                    ViewHelper.setPivotX(mContent, mContent.getMeasuredWidth());
                    ViewHelper.setPivotY(mContent, mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                    ViewHelper.setScaleX(mContent, rightScale);
                    ViewHelper.setScaleY(mContent, rightScale);

                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
//                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
            }
        });
    }

    private void initView() {
        mHeader = (Header) findViewById(R.id.header);
        mHeader.setRightText("right", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
//        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
    }

}
