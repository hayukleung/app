package com.hayukleung.app.module.tabhost2;

import com.hayukleung.app.R;

/**
 * 配置模块入口
 *
 * MainTab2.java
 * <p>
 * Created by hayukleung on 1/15/16.
 */
public enum MainTab2 {

    /** 第N模块 */
    a(0, R.string.tab_a, R.drawable.main_footer_a_on, R.drawable.main_footer_a_off, FragmentA.class),
    b(1, R.string.tab_b, R.drawable.main_footer_b_on, R.drawable.main_footer_b_off, FragmentB.class),
    c(2, R.string.tab_c, R.drawable.main_footer_c_on, R.drawable.main_footer_c_off, FragmentC.class),
    d(3, R.string.tab_d, R.drawable.main_footer_d_on, R.drawable.main_footer_d_off, FragmentD.class),;

    private int idx;
    private int resName;
    private int resIconOn;
    private int resIconOff;
    private Class<?> clz;

    MainTab2(int idx, int resName, int resIconOn, int resIconOff, Class<?> clz) {
        this.idx = idx;
        this.resName = resName;
        this.resIconOn = resIconOn;
        this.resIconOff = resIconOff;
        this.clz = clz;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getResName() {
        return resName;
    }

    public void setResName(int resName) {
        this.resName = resName;
    }

    public int getResIconOn() {
        return resIconOn;
    }

    public void setResIconOn(int resIconOn) {
        this.resIconOn = resIconOn;
    }

    public int getResIconOff() {
        return resIconOff;
    }

    public void setResIconOff(int resIconOff) {
        this.resIconOff = resIconOff;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }
}
