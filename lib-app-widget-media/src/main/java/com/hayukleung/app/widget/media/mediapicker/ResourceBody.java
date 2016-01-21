package com.hayukleung.app.widget.media.mediapicker;

import android.graphics.Bitmap;
import android.net.Uri;

import com.android.volley.http.entity.Utils;
import com.android.volley.http.entity.mime.content.FileBody;
import com.hayukleung.app.App;
import com.hayukleung.app.widget.media.picasso.BitmapImage;
import com.hayukleung.app.widget.media.picasso.IImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ResourceBody extends FileBody {
    private boolean mIsOriginal;
    private int mWidth = 1024, mHeight = 1024;
    private File mTempFile;

    public ResourceBody(File file, boolean isOriginal, String mimeType) {
        super(file, mimeType);
        this.mIsOriginal = isOriginal;
    }

    public void writeTo(final OutputStream out) throws IOException {
        ensureTempFile();
        if (out == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream in = new FileInputStream(mIsOriginal ? this.file : mTempFile);
        try {
            byte[] tmp = new byte[4096];
            int l;
            while ((l = in.read(tmp)) != -1) {
                out.write(tmp, 0, l);
            }
            out.flush();
        } finally {
            in.close();
        }

        if (mTempFile != null)
            mTempFile.delete();
    }

    @Override
    public long getContentLength() {
        ensureTempFile();
        return mIsOriginal ? this.file.length() : mTempFile.length();
    }

    private void ensureTempFile() {
        if (mTempFile != null || mIsOriginal)
            return;
        OutputStream out = null;
        try {
            IImage iImage = ImageLoader.Instance().load(Uri.fromFile(file)).resize(mWidth, mHeight).centerInside().onlyScaleDown().thumbnail().get();
            Bitmap bitmap = ((BitmapImage) iImage).getBitmap();
            mTempFile = FileUtils.getTmpFile(App.Instance().getCacheDir(), null);
            out = new FileOutputStream(mTempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            bitmap.recycle();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            Utils.closeQuietly(out);
        }
    }

}