package com.hayukleung.app.util;

import java.util.Locale;

/**
 * Created by hayukleung on 12/10/15.
 */
public class VerificationUtil {

    /**
     * 校验身份证的校验码
     *
     * @param id 待校验的身份证号码
     * @return
     */
    public static boolean isIdCardNumber(String id) {
        if (id != null) {
            id = id.toUpperCase(Locale.US);
        }
        if (id.length() == 15) {
            id = upToEighteen(id);
        }
        if (id.length() != 18) {
            return false;
        }
        String verify = id.substring(17, 18);
        try {
            if (verify.equals(getVerify(id))) {
                return true;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

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
}
