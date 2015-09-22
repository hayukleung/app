package com.hayukleung.app.util;

/**
 * 正则表达式常量
 *
 * http://tool.oschina.net/regex#
 * https://jex.im/regulex
 *
 * Created by hayukleung on 15/9/21.
 */
public class RegexConstant {

    /**
     * 移动号码（全字符匹配）
     *
     * 1xxxxxxxxxx
     * 1xx-xxxxxxxx
     * 1xx xxxxxxxx
     * ......
     */
    public static final String REG_MOBILE = "(^(1[- ]?[3,4,5,7,8])(([- ]?\\d{1})){9}$)";

    /**
     * 电话号码（包括移动号码）（全字符匹配）
     *
     * 1xxxxxxxxxx
     * 1xx-xxxxxxxx
     * 1xx xxxxxxxx
     * 0xx-xxxxxxxx
     * 0xx xxxxxxxx
     * (xxx)xxxxxxxx
     * xxxx-xxxxxxx
     * xxxx xxxxxxx
     * (xxxx)xxxxxxx
     * xxxxx
     * 106x...
     * ......
     */
    public static final String REG_PHONE = "(^(1[- ]?[3,4,5,7,8])(([- ]?\\d{1})){9}$)|^106\\d{5,10}$|^\\d{5}$|^0\\d{2}[- ]?\\d{8}$|^\\(0\\d{2}\\)[- ]?\\d{8}$|^0\\d{3}[- ]?\\d{7}$|^\\(0\\d{3}\\)[- ]?\\d{7}$";

    /**
     * 链接地址（全字符匹配）
     */
    public static final String REG_URL = "^(([h,H][t,T][t,T][p,P][s,S]?|[f,F][t,T][p,P]|[r,R][t,T][s,S][p,P]|[m,M][m,M][s,S])://)?(([0-9a-zA-Z_!~*'().&=+$%-]+:)?[0-9a-zA-Z_!~*'().&=+$%-]+@)?(([0-9]{1,3}\\.){3}[0-9]{1,3}|([0-9a-zA-Z_!~*'()-]+\\.)*([0-9a-zA-Z][0-9a-z-A-Z-]{0,61})?[0-9a-zA-Z]\\.[a-zA-Z]{2,6})(:[0-9]{1,4})?((/?)|(/[0-9a-zA-Z_!~*'().;?:@&=+$,%#-]+)+/?)$";

    /**
     * 电子邮箱（全字符匹配）
     */
    public static final String REG_EMAIL = "^([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\\_|\\.]?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,3}$";

    /**
     * 身份证号码（全字符匹配）
     */
    public static final String REG_IDENTITY = "";
    /**
     * 身份证号码（全文匹配）
     */
    public static final String REG_CTX_IDENTITY = "";

    /**
     * 话题标签（全字符匹配）
     * #xxx...#
     */
    public static final String REG_LABEL = "^#((?!#).)*#$";
    /**
     * 话题标签（全文匹配）
     */
    public static final String REG_CTX_LABEL = "#.*?#";

}
