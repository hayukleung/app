/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hayukleung.app.widget.media.picasso;

import android.net.NetworkInfo;

import java.io.File;
import java.io.IOException;

import static com.hayukleung.app.widget.media.picasso.Downloader.Response;
import static com.hayukleung.app.widget.media.picasso.Picasso.LoadedFrom.DISK;
import static com.hayukleung.app.widget.media.picasso.Picasso.LoadedFrom.NETWORK;

class ResourceNetworkRequestHandler extends RequestHandler {
    static final int RETRY_COUNT = 2;

    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";

    private final Downloader downloader;
    private final LruDiskCache diskCache;
    private final com.hayukleung.app.widget.media.picasso.Stats stats;

    public ResourceNetworkRequestHandler(Downloader downloader, LruDiskCache diskCache, com.hayukleung.app.widget.media.picasso.Stats stats) {
        this.downloader = downloader;
        this.diskCache = diskCache;
        this.stats = stats;
    }

    @Override
    public boolean canHandleRequest(Request data) {
        String scheme = data.uri.getScheme();
        return (SCHEME_HTTP.equals(scheme) || SCHEME_HTTPS.equals(scheme));
    }

    @Override
    public IResource load(Request data) throws IOException {
        File file = diskCache.get(data.uri.toString());
        Picasso.LoadedFrom loadedFrom = DISK;
        if (file == null && !data.loadFromLocalCacheOnly) {
            Response response = downloader.load(data.uri);
            if (response != null) {
                try {
                    diskCache.save(data.uri, response.getLength(), response.getInputStream());
                } finally {
                    response.getInputStream().close();
                }
                file = diskCache.get(data.uri.toString());
                loadedFrom = NETWORK;
            }
        }

        if (file == null) {
            return null;
        }

        if (loadedFrom == NETWORK) {
            if (file.length() > 0) {
                stats.dispatchDownloadFinished(file.length());
            }
        }

        return new NetworkResource(file.getAbsolutePath(), loadedFrom);
    }

    @Override
    int getRetryCount() {
        return RETRY_COUNT;
    }

    @Override
    boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
        return info == null || info.isConnected();
    }

    @Override
    boolean supportsReplay() {
        return true;
    }

    @Override
    boolean needLock() {
        return true;
    }

    private static class NetworkResource implements IResource {
        private String file;
        private Picasso.LoadedFrom loadedFrom;

        public NetworkResource(String file, Picasso.LoadedFrom loadedFrom) {
            this.file = file;
            this.loadedFrom = loadedFrom;
        }

        @Override
        public IImage decode() throws IOException {
            return new Resource(file);
        }

        @Override
        public ImageFormat getFormat() {
            throw new IllegalAccessError("Unsupported method");
        }

        @Override
        public Picasso.LoadedFrom getLoadedFrom() {
            return loadedFrom;
        }

        @Override
        public int getExifOrientation() {
            throw new IllegalAccessError("Unsupported method");
        }

    }
}
