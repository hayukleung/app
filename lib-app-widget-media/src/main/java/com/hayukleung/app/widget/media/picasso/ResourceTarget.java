package com.hayukleung.app.widget.media.picasso;

public interface ResourceTarget {
    void onLoaded(String path, Picasso.LoadedFrom from);

    void onFailed();

    void onProgress(int progress);
}
