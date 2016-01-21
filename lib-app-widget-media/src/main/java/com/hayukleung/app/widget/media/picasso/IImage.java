package com.hayukleung.app.widget.media.picasso;

import android.content.Context;
import android.widget.ImageView;

public interface IImage {
    int getSize();

    boolean isRecycled();

    void recycle();

    void setImage(ImageView target, Context context, Picasso.LoadedFrom loadedFrom, boolean noFade, boolean debugging);
}
