package com.hayukleung.app.view;

import android.content.Context;
import android.support.v4.app.FragmentTabHost;
import android.util.AttributeSet;

/**
 * Footer.java
 * <p>
 * Created by hayukleung on 1/14/16.
 */
public class Footer extends FragmentTabHost {

    private String mCurrentTag;

    /**
     * 无效的tag，点击该tag不进行fragment切换
     */
    private String mNoTabChangedTag;

    public Footer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onTabChanged(String tag) {

        if (tag.equals(mNoTabChangedTag)) {
            setCurrentTabByTag(mCurrentTag);
        } else {
            super.onTabChanged(tag);
            mCurrentTag = tag;
        }
    }

    /**
     * 设置无效的tag
     *
     * @param tag
     */
    public void setNoTabChangedTag(String tag) {
        this.mNoTabChangedTag = tag;
    }
}
