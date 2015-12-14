package com.hayukleung.app.util;

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
}
