package com.hayukleung.app.widget.media.mediapicker;

import android.os.Environment;

import com.hayukleung.app.view.util.RandomStringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 文件操作类
 */
public class FileUtils {

    public static File getTmpFile() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return getTmpFile(null);
        }
        throw new IllegalStateException("The media not mounted.");
    }

    public synchronized static File getTmpFile(File parent, String random) {
        if (!parent.exists()) {
            parent.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        StringBuilder builder = new StringBuilder();
        builder.append("tmp_");
        builder.append(timeStamp);
        if (random != null)
            builder.append("_").append(random);
        String fileName = builder.toString();
        File file = new File(parent, fileName);
        if (file.exists()) {
            file = getTmpFile(parent, RandomStringUtils.randomAlphabetic(4));
        }
        return file;
    }

    private synchronized static File getTmpFile(String random) {
        File pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!pic.exists()) {
            pic.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        StringBuilder builder = new StringBuilder();
        builder.append("media_");
        builder.append(timeStamp);
        if (random != null)
            builder.append("_").append(random);
        builder.append(".jpg");
        String fileName = builder.toString();
        File file = new File(pic, fileName);
        if (file.exists()) {
            file = getTmpFile(RandomStringUtils.randomAlphabetic(4));
        }
        return file;
    }
}
