package com.hayukleung.app.util;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理
 *
 */
public class StringUtil {

    /**
     * 获取缩略的字符串
     *
     * @param original
     * @param length
     * @return
     */
    public static String getOmittedString(String original, int length) {
        if (null == original) {
            return "...";
        }
        if (0 >= length) {
            length = 5;
        }
        if (original.length() <= length) {
            return original;
        }
        return original.substring(0, length) + "...";
    }

    /**
     * 半角转换为全角
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 是否为纯数字字符串
     *
     * @param number
     * @return
     */
    public static boolean isDigitsOnly(String number) {
        return TextUtils.isDigitsOnly(number);
    }

    /**
     * TODO 验证字符串是否可见 </br> 输入字符串为null、空格、进位格等属于不可见字符串
     *
     * @param string
     * @return
     */
    public static boolean isVisibleStr(String string) {

        if (TextUtils.isEmpty(string))
            return false;

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return true;
            }
        }
        return false;
    }

    /**
     * 字符是否相等
     *
     * @param a
     * @param b
     * @return if a and b are equals
     */
    public static boolean isEqual(String a, String b) {
        if (a == null && b == null)
            return true;
        if (a == null && b != null)
            return false;
        if (a != null && b == null)
            return false;
        return a.equals(b);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * 字符是否为中国大陆手机号码
     *
     * @param mobile
     * @return
     */
    public static boolean isMobile(String mobile) {
        return isMatch(mobile, RegexConstant.REG_MOBILE);
    }

    /**
     * 是否是中国大陆电话号码
     *
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone) {
        return isMatch(phone, RegexConstant.REG_PHONE);
    }

    /**
     * 字符是否为邮箱
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        return isMatch(email, RegexConstant.REG_EMAIL);
    }

    /**
     * 判断是否是url链接
     *
     * @param url
     * @return
     */
    public static boolean isUrl(String url) {
        return isMatch(url, RegexConstant.REG_URL);
    }

    /**
     * 是否是话题标签
     *
     * @param label
     * @return
     */
    public static boolean isLabel(String label) {
        return isMatch(label, RegexConstant.REG_LABEL);
    }

    /**
     * string是否匹配regex
     *
     * @param string
     * @param regex
     * @return
     */
    private static boolean isMatch(String string, String regex) {
        if (null == string || null == regex) {
            return false;
        } else {
            return string.matches(regex);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * 全文匹配，获取标签数目
     *
     * @param string
     * @return
     */
    public static int getLabelCount(String string) {
        return getSubStringCount(string, RegexConstant.REG_CTX_LABEL);
    }

    /**
     * 全文匹配，获取string中的标签
     *
     * @param string
     * @return
     */
    public static List<String> getLabels(String string) {
        Pattern p = Pattern.compile(RegexConstant.REG_CTX_LABEL);
        Matcher m = p.matcher(string);
        List<String> listLabel = new ArrayList<>();
        while (m.find()) {
            listLabel.add(m.group());
        }
        return listLabel;
    }

    /**
     * 全文匹配，获取des中符合reg的子串的数目
     *
     * @param des 目标字符串
     * @param reg 正则表达式
     * @return how many strings are there in the des that match reg
     */
    public static int getSubStringCount(String des, String reg) {
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(des);
        int count = 0; // 记录个数
        while (m.find()) {
            count++;
        }
        return count;
    }

}
