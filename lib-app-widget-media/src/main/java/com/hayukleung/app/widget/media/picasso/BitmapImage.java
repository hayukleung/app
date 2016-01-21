package com.hayukleung.app.widget.media.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class BitmapImage implements IImage {
    private final Bitmap bitmap;

    public BitmapImage(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public int getSize() {
        return com.hayukleung.app.widget.media.picasso.Utils.getBitmapBytes(bitmap);
    }

    @Override
    public boolean isRecycled() {
        return bitmap.isRecycled();
    }

    @Override
    public void recycle() {
        bitmap.recycle();
    }

    @Override
    public void setImage(ImageView target, Context context, Picasso.LoadedFrom loadedFrom, boolean noFade, boolean debugging) {
        com.hayukleung.app.widget.media.picasso.PicassoDrawable.setBitmap(target, context, bitmap, loadedFrom, noFade, debugging);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
