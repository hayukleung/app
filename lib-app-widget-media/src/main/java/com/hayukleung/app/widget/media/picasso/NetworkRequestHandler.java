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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.NetworkInfo;

import java.io.File;
import java.io.IOException;

import static com.hayukleung.app.widget.media.picasso.Downloader.Response;
import static com.hayukleung.app.widget.media.picasso.Picasso.LoadedFrom.DISK;
import static com.hayukleung.app.widget.media.picasso.Picasso.LoadedFrom.NETWORK;
import static com.hayukleung.app.widget.media.picasso.Utils.isGifFile;

class NetworkRequestHandler extends RequestHandler {
    static final int RETRY_COUNT = 2;

    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";

    private final Downloader downloader;
    private final LruDiskCache diskCache;
    private final Stats stats;

    public NetworkRequestHandler(Downloader downloader, LruDiskCache diskCache, Stats stats) {
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

        return new NetworkResource(file.getAbsolutePath(), data, loadedFrom);
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
        private Request data;
        private Picasso.LoadedFrom loadedFrom;
        private ImageFormat imageFormat;

        public NetworkResource(String file, Request data, Picasso.LoadedFrom loadedFrom) {
            this.file = file;
            this.data = data;
            this.loadedFrom = loadedFrom;
        }

        @Override
        public IImage decode() throws IOException {
            return decodeFile();
        }

        @Override
        public ImageFormat getFormat() {
            if (imageFormat != null)
                return imageFormat;

            if (data.thumbnail)
                imageFormat = ImageFormat.PNG;

            if (imageFormat == null) {
                if (isGifFile(file))
                    imageFormat = ImageFormat.GIF;
            }

            if (imageFormat == null)
                imageFormat = ImageFormat.PNG;

            return imageFormat;
        }

        @Override
        public Picasso.LoadedFrom getLoadedFrom() {
            return loadedFrom;
        }

        @Override
        public int getExifOrientation() {
            return 0;
        }

        private IImage decodeFile() throws IOException {
            IImage image;
            final BitmapFactory.Options options = createBitmapOptions(data);
            final boolean calculateSize = requiresInSampleSize(options);
            if (calculateSize) {
                BitmapFactory.decodeFile(file, options);
                calculateInSampleSize(data.targetWidth, data.targetHeight, options, data);
            }
            Bitmap bitmap = BitmapFactory.decodeFile(file, options);
            if (bitmap == null) {
                // Treat null as an IO exception, we will eventually retry.
                throw new IOException("Failed to decode stream.");
            }
            image = new BitmapImage(bitmap);
            return image;
        }

    }
}
