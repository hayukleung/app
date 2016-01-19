package com.hayukleung.app.module.tabhost2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.hayukleung.app.BaseFragment;
import com.hayukleung.app.CommonActivity;
import com.hayukleung.app.R;
import com.hayukleung.app.util.LogUtil;

/**
 * 微信6.0底部
 */
public class DemoTabHost2Activity extends CommonActivity {

    private Footer2 mFooter2;
    private ViewPager mViewPager;
    private MainTab2[] mMainTab2s;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_tab_host_2);
        initContentView();
    }

    @Override
    protected BaseFragment newFragment() {
        return null;
    }

    public void initContentView() {
        // 底部
        mFooter2 = (Footer2) findViewById(R.id.main_footer);

        mMainTab2s = MainTab2.values();
        final int size = mMainTab2s.length;
        for (int i = 0; i < size; i++) {
            mFooter2.addItem(mMainTab2s[i].getResIconOff(), mMainTab2s[i].getResIconOn(), mMainTab2s[i].getResName());
        }
        // 主体
        mViewPager = (ViewPager) findViewById(R.id.main_body);
        mViewPager.setAdapter(new BodyPagerAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                LogUtil.showLog(String.format("position --> %d positionOffset --> %f positionOffsetPixels --> %d", position, positionOffset, positionOffsetPixels));
                mFooter2.itemChangeChecked(position, position + 1, positionOffset);
            }
        });

        mFooter2.setOnItemChangedListener(new Footer2.OnItemChangedListener() {

            @Override
            public void onItemChanged(int position) {

                mViewPager.setCurrentItem(position, false);
            }
        });
        mFooter2.setCheckedIndex(0);
        /**
         * BUG :显示不出数字。数字尺寸太大
         */
        mFooter2.setItemNewsCount(1, 10); // 设置消息数量
    }

    class BodyPagerAdapter extends FragmentPagerAdapter {

        public BodyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return Fragment.instantiate(DemoTabHost2Activity.this, mMainTab2s[position].getClz().getName(), null);
        }

        @Override
        public int getCount() {
            return mMainTab2s.length;
        }
    }
}
