package com.hayukleung.app.module.tabhost2;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.hayukleung.app.BaseFragment;
import com.hayukleung.app.CommonActivity;
import com.hayukleung.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信6.0底部
 */
public class DemoTabHost2Activity extends CommonActivity {

    private CustomRadioGroup footer;
    private ViewPager body;
    private int[] itemImage = {
            R.drawable.main_footer_message,
            R.drawable.main_footer_contanct,
            R.drawable.main_footer_discovery,
            R.drawable.main_footer_me
    };
    private int[] itemCheckedImage = {
            R.drawable.main_footer_message_selected,
            R.drawable.main_footer_contanct_selected,
            R.drawable.main_footer_discovery_selected,
            R.drawable.main_footer_me_selected
    };
    private String[] itemText = {
            "微信",
            "通讯录",
            "发现",
            "我"
    };

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
        footer = (CustomRadioGroup) findViewById(R.id.main_footer);
        for (int i = 0; i < itemImage.length; i++) {
            footer.addItem(itemImage[i], itemCheckedImage[i], itemText[i]);
        }
        // 主体
        body = (ViewPager) findViewById(R.id.main_body);
        body.setAdapter(new BodyPageAdapter());
        final MainBodyPageChangeListener bodyChangeListener = new MainBodyPageChangeListener(footer);
        body.addOnPageChangeListener(bodyChangeListener);

        footer.setCheckedIndex(body.getCurrentItem());
        footer.setOnItemChangedListener(new CustomRadioGroup.OnItemChangedListener() {
            public void onItemChanged() {
                body.setCurrentItem(footer.getCheckedIndex(), false);
            }
        });
        /**
         * BUG :显示不出数字。数字尺寸太大
         */
        footer.setItemNewsCount(1, 10); // 设置消息数量

    }

    class BodyPageAdapter extends PagerAdapter {
        private int[] pageLayouts = {
                R.layout.main_body_page_a,
                R.layout.main_body_page_b,
                R.layout.main_body_page_c,
                R.layout.main_body_page_d
        };
        private List<View> lists = new ArrayList<View>();

        public BodyPageAdapter() {
            for (int i = 0; i < pageLayouts.length; i++) {
                View v = getLayoutInflater().inflate(pageLayouts[i], null);
                lists.add(v);
            }
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = lists.get(position);
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(lists.get(position));
        }
    }
}
