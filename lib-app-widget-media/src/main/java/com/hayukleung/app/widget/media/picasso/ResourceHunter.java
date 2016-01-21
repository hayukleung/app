package com.hayukleung.app.widget.media.picasso;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class ResourceHunter extends BitmapHunter {

    ResourceHunter(Picasso picasso, Dispatcher dispatcher, Cache cache, LruDiskCache diskCache, com.hayukleung.app.widget.media.picasso.Stats stats, com.hayukleung.app.widget.media.picasso.Action action, RequestHandler requestHandler) {
        super(picasso, dispatcher, cache, diskCache, stats, action, requestHandler);
    }

    @Override
    IImage hunt() throws IOException {
        IImage image = null;

        data.loadFromLocalCacheOnly = (retryCount == 0 || picasso.denyNetworkDownload());
        ReentrantLock uriLock = null;
        RequestHandler.IResource resource;
        try {
            if (needLock) {
                uriLock = dispatcher.getLockForUri(this);
                uriLock.lock();
            }
            resource = requestHandler.load(data);
        } finally {
            if (uriLock != null) {
                uriLock.unlock();
            }
        }
        if (resource != null) {
            image = resource.decode();
            loadedFrom = resource.getLoadedFrom();
        }
        return image;
    }
}
