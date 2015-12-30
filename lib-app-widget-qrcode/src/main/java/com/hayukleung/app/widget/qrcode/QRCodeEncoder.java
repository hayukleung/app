package com.hayukleung.app.widget.qrcode;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

/**
 * 根据给予的url字符串生成二维码
 *
 * @author ming
 */
public final class QRCodeEncoder {

    /**
     * 二维码图像默认宽度
     */
    private static final int QR_WIDTH = 300;
    /**
     * 二维码图像默认高度
     */
    private static final int QR_HEIGHT = 300;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    public static Bitmap createQRImage(String contentsToEncode) {
        return createQRImage(contentsToEncode, QR_WIDTH, QR_HEIGHT);
    }

    /**
     * 将传入的url字符串编码成二维码
     *
     * @param contentsToEncode 需要生成二维码的url字符串
     * @param width            二维码图片的宽度
     * @param height           二维码图片的高度
     * @return
     */
    public static Bitmap createQRImage(String contentsToEncode, int width,
                                       int height) {
        // 判断URL合法性
        if (contentsToEncode == null || "".equals(contentsToEncode)
                || contentsToEncode.length() < 1) {
            return null;
        }

        Hashtable<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new Hashtable<EncodeHintType, Object>();
            // 指定编码格式
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
            // 指定纠错级别(L--7%,M--15%,Q--25%,H--30%)
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        }

        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(contentsToEncode,
                    BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
            return null;
        }

        // 按照二维码的算法，逐个生成二维码的图片
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        // 根据像素数组创建Bitmap对象
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, width);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
}
