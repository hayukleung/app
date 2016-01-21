package com.hayukleung.app.widget.media.picasso;

import android.content.Context;
import android.widget.ImageView;

public class Resource implements IImage {
    private String mPath;

    public Resource(String path) {
        this.mPath = path;
    }

    public String getPath() {
        return mPath;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public boolean isRecycled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void recycle() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setImage(ImageView target, Context context, Picasso.LoadedFrom loadedFrom, boolean noFade, boolean debugging) {
        throw new UnsupportedOperationException();
    }

}
