package com.hayukleung.app.widget.media.picasso;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class ResourceAction extends com.hayukleung.app.widget.media.picasso.Action<ResourceTarget> {
    ResourceAction(Picasso picasso, ResourceTarget target, Request request, int memoryPolicy, boolean noFade, int errorResId, Drawable errorDrawable, String key, Object tag) {
        super(picasso, target, request, memoryPolicy, noFade, errorResId, errorDrawable, key, tag);
    }

    ResourceAction(Picasso picasso, ResourceTarget target, String url, Object tag) {
        this(picasso, target, new Request.Builder(Uri.parse(url)).build(), 0, false, 0, null, url + "-download", tag);
    }

    @Override
    void progress(int progress) {
        ResourceTarget target = getTarget();
        if (target != null) {
            target.onProgress(progress);
        }
    }

    @Override
    void complete(IImage result, Picasso.LoadedFrom from) {
        if (result == null) {
            throw new AssertionError(
                    String.format("Attempted to complete action with no result!\n%s", this));
        }
        ResourceTarget target = getTarget();
        if (target != null) {
            target.onLoaded(((Resource) result).getPath(), from);
        }
    }

    @Override
    void error() {
        ResourceTarget target = getTarget();
        if (target != null) {
            target.onFailed();
        }
    }
}
