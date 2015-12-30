package com.hayukleung.app.util;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;

/**
 * Html.fromHtml方法集
 *
 * Created by hayukleung on 12/10/15.
 */
public class HtmlUtil {

    /**
     * 返回着色代码包装后的Html代码
     *
     * @param content
     * @param color 颜色值如0xffff0000
     * @return html代码
     */
    public static String toHtml(String content, int color) {
        if (null == content) {
            content = "";
        }
        return String.format("<font color='%d'>%s</font>", color, content);
    }

    /**
     * 返回着色代码包装后的Html代码
     *
     * @param activity
     * @param content
     * @param colorResId 颜色资源ID如R.color.red
     * @return
     */
    @ColorInt
    public static String toHtml(Activity activity, String content, @ColorRes int colorResId) {
        return toHtml(content, activity.getResources().getColor(colorResId));
    }
}
