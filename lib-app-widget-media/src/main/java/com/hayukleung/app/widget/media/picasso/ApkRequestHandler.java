package com.hayukleung.app.widget.media.picasso;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.io.IOException;

import static com.hayukleung.app.widget.media.picasso.Picasso.LoadedFrom.DISK;
import static com.hayukleung.app.widget.media.picasso.Picasso.SCHEME_APK;

/**
 * 获取 apk 文件的 icon
 */
class ApkRequestHandler extends RequestHandler {

    private Context context;

    public ApkRequestHandler(Context context) {
        this.context = context;
    }

    @Override
    public boolean canHandleRequest(Request data) {
        if (data.uri == null) {
            return false;
        }
        return SCHEME_APK.equals(data.uri.getScheme());
    }

    @Override
    public IResource load(Request data) throws IOException {
        String path = data.uri.getPath();
        PackageManager pm = context.getPackageManager();
        PackageInfo pInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (pInfo != null) {
            ApplicationInfo aInfo = pInfo.applicationInfo;

            // Bug in SDK versions >= 8. See here:
            // http://code.google.com/p/android/issues/detail?id=9151
            if (Build.VERSION.SDK_INT >= 8) {
                aInfo.sourceDir = path;
                aInfo.publicSourceDir = path;
            }

            return new ApkResource(aInfo, pm);
        }
        return null;
    }

    private static class ApkResource implements IResource {

        private ApplicationInfo aInfo;
        private PackageManager pm;

        public ApkResource(ApplicationInfo aInfo, PackageManager pm) {
            this.aInfo = aInfo;
            this.pm = pm;
        }

        public static Bitmap drawable2Bitmap(Drawable drawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        }

        @Override
        public IImage decode() throws IOException {
            Bitmap bitmap = drawable2Bitmap(aInfo.loadIcon(pm));
            IImage image = null;
            if (bitmap != null) {
                image = new BitmapImage(bitmap);
            }
            return image;
        }

        @Override
        public ImageFormat getFormat() throws IOException {
            return ImageFormat.PNG;
        }

        @Override
        public Picasso.LoadedFrom getLoadedFrom() {
            return DISK;
        }

        @Override
        public int getExifOrientation() {
            return 0;
        }
    }
}
