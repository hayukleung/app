package com.hayukleung.app.module.tabhost1;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.hayukleung.app.CommonActivity;
import com.hayukleung.app.BaseFragment;
import com.hayukleung.app.R;

/**
 * 常规分页底部
 *
 * DemoTabHost1Activity.java
 * <p>
 * Created by hayukleung on 1/15/16.
 */
public class DemoTabHost1Activity extends CommonActivity {
    /**
     * FragmentTabHost
     */
    private Footer1 mFooter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_tab_host_1);

        initView();
    }

    @Override
    protected BaseFragment newFragment() {
        return null;
    }

    /**
     * 初始化选项卡
     */
    private void initTabs() {
        // 设置首页模块
        MainTab1[] tabs = MainTab1.values();
        final int size = tabs.length;
        for (int i = 0; i < size; i++) {
            MainTab1 mainTab = tabs[i];
            TabHost.TabSpec tab = mFooter.newTabSpec(getString(mainTab.getResName()));
            View indicator = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_tab_host, null);
            TextView title = (TextView) indicator.findViewById(R.id.tab);
            Drawable drawable = this.getResources().getDrawable(mainTab.getResIcon());
            title.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
//            if (i == 2) {
//                indicator.setVisibility(View.INVISIBLE);
//                mFooter.setNoTabChangedTag(getString(mainTab.getResName()));
//            }
            title.setText(getString(mainTab.getResName()));
            tab.setIndicator(indicator);
            tab.setContent(new TabHost.TabContentFactory() {

                @Override
                public View createTabContent(String tag) {
                    return new View(DemoTabHost1Activity.this);
                }
            });
            mFooter.addTab(tab, mainTab.getClz(), null);

//            if (mainTab.equals(MainTab2.ME)) {
//                View cn = indicator.findViewById(R.id.tab_mes);
//                mBvNotice = new BadgeView(DemoTabHost2Activity.this, cn);
//                mBvNotice.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
//                mBvNotice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
//                mBvNotice.setBackgroundResource(R.drawable.notification_bg);
//                mBvNotice.setGravity(Gravity.CENTER);
//            }

            mFooter.getTabWidget().getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO
                    return false;
                }
            });
        }
    }

    /**
     * 初始化组件
     */
    private void initView() {
        // 找到TabHost
        mFooter = (Footer1) findViewById(android.R.id.tabhost);
        mFooter.setup(this, getSupportFragmentManager(), R.id.real_tab_content);
        if (android.os.Build.VERSION.SDK_INT > 10) {
            mFooter.getTabWidget().setShowDividers(0);
        }

        initTabs();

        mFooter.setCurrentTab(0);
        mFooter.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                // TODO

                final int size = mFooter.getTabWidget().getTabCount();
                for (int i = 0; i < size; i++) {
                    View v = mFooter.getTabWidget().getChildAt(i);
                    if (i == mFooter.getCurrentTab()) {
                        v.setSelected(true);
                    } else {
                        v.setSelected(false);
                    }
                }
//                if (tabId.equals(getString(MainTab2.ME.getResName()))) {
//                    mBvNotice.setText("");
//                    mBvNotice.hide();
//                }
                supportInvalidateOptionsMenu();
            }
        });
    }

}
