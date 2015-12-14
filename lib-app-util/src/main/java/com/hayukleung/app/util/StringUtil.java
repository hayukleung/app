package com.hayukleung.app.util;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理
 *
 */
public class StringUtil {

    // 未归类的字符串处理方法，开始 --------------------------------------------------------------------

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
    // 未归类的字符串处理方法，结束 --------------------------------------------------------------------

    // 身份证校验，开始 ------------------------------------------------------------------------------

    /** wi = 2(n-1)(mod 11) 加权因子 */
    private static final int[] wi = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };
    /** 校验码 */
    private static final int[] vi = { 1, 0, 'X', 9, 8, 7, 6, 5, 4, 3, 2 };
    private static int[] ai = new int[18];

    /**
     * 15位转18位
     *
     * @param fifteen
     * @return
     */
    private static String upToEighteen(String fifteen) {
        StringBuffer eighteen = new StringBuffer(fifteen);
        eighteen = eighteen.insert(6, "19");
        return eighteen.toString();
    }

    /**
     * 计算最后一位校验值
     *
     * @param eighteen
     * @return
     * @throws NumberFormatException
     */
    private static String getVerify(String eighteen) throws NumberFormatException {
        int remain = 0;
        if (eighteen.length() == 18) {
            eighteen = eighteen.substring(0, 17);
        }
        if (eighteen.length() == 17) {
            int sum = 0;
            for (int i = 0; i < 17; i++) {
                String k = eighteen.substring(i, i + 1);
                ai[i] = Integer.valueOf(k);
            }
            for (int i = 0; i < 17; i++) {
                sum += wi[i] * ai[i];
            }
            remain = sum % 11;
        }
        // System.out.println("remain=>"+remain);
        return remain == 2 ? "X" : String.valueOf(vi[remain]);
    }

    /**
     * 校验身份证的校验码
     *
     * @param identity
     * @return
     */
    public static boolean isIdentity(String identity) {
        if (identity != null) {
            identity = identity.toUpperCase(Locale.US);
        }
        if (identity.length() == 15) {
            identity = upToEighteen(identity);
        }
        if (identity.length() != 18) {
            return false;
        }
        String verify = identity.substring(17, 18);
        try {
            if (verify.equals(getVerify(identity))) {
                return true;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 身份证校验，结束 ------------------------------------------------------------------------------

    // 正则式验证手机、电话、邮箱、链接、话题标签，开始 ---------------------------------------------------

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


    // 正则式验证手机、电话、邮箱、链接、话题标签，结束 ---------------------------------------------------

    // 全文查找标签，开始 -----------------------------------------------------------------------------

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


    // 全文查找标签，结束 -----------------------------------------------------------------------------

}
