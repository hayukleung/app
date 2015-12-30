package com.hayukleung.app.widget.qrcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Hashtable;

public class QRCodeDecoder {

    public static Result scanImageUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小

        Bitmap resultBitmap = null;

        try {
            resultBitmap = BitmapFactory.decodeStream(new URL(url).openStream(), null, options);
        } catch (MalformedURLException malformedURLException) {
            malformedURLException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0) {
            sampleSize = 1;
        }
        options.inSampleSize = sampleSize;

        try {
            resultBitmap = BitmapFactory.decodeStream(new URL(url).openStream(), null, options);
        } catch (MalformedURLException malformedURLException) {
            malformedURLException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        if (null == resultBitmap) {
            return null;
        }

        RGBLuminanceSource source = null;
        try {
            source = new RGBLuminanceSource(resultBitmap);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(binaryBitmap, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 扫描本地图片
     *
     * @param context
     * @param path    sdcard绝对路径|content://xxx/xxx/id
     * @return
     */
    public static Result scanImagePath(Activity context, String path) {

        if (TextUtils.isEmpty(path)) {
            return null;
        }

        // content://media/external/images/media/153185
        if (path.startsWith("content://")) {
//            path = AlbumUtil.uriId2Path(context, AlbumUtil.uriString2UriId(path));
        }

        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小

        Bitmap resultBitmap = null;

        resultBitmap = BitmapFactory.decodeFile(path, options);

        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0) {
            sampleSize = 1;
        }
        options.inSampleSize = sampleSize;

        resultBitmap = BitmapFactory.decodeFile(path, options);

        if (null == resultBitmap) {
            return null;
        }

        RGBLuminanceSource source = new RGBLuminanceSource(resultBitmap);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(binaryBitmap, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String recode(String str) {
        String formart = "";
        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder().canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
                Log.d(QRCodeDecoder.class.getSimpleName(), "ISO8859-1 --> " + formart);
            } else {
                formart = str;
                Log.d(QRCodeDecoder.class.getSimpleName(), "stringExtra --> " + str);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return formart;
    }
}
