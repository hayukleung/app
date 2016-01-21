/*
 * Copyright (C) 2014 Square, Inc.
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

import java.io.IOException;

/**
 * {@link com.hayukleung.app.widget.media.picasso.RequestHandler} allows you to extend Picasso to load images
 * in ways that are not supported by default in the library.
 * <p>
 * <h2>Usage</h2>
 * <p>{@link com.hayukleung.app.widget.media.picasso.RequestHandler} must be subclassed to be used. You will have to
 * override two methods ({@link #canHandleRequest(com.hayukleung.app.widget.media.picasso.Request)} and
 * {@link #load(com.hayukleung.app.widget.media.picasso.Request)}) with your custom logic to load images.</p>
 * <p>
 * <p>You should then register your {@link com.hayukleung.app.widget.media.picasso.RequestHandler} using
 * {@link com.hayukleung.app.widget.media.picasso.Picasso.Builder#addRequestHandler(com.hayukleung.app.widget.media.picasso.RequestHandler)}</p>
 * <p>
 * <b>NOTE:</b> This is a beta feature. The API is subject to change in a backwards
 * incompatible way at any time.
 *
 * @see com.hayukleung.app.widget.media.picasso.Picasso.Builder#addRequestHandler(com.hayukleung.app.widget.media.picasso.RequestHandler)
 */
public abstract class RequestHandler {
    /**
     * Lazily create {@link BitmapFactory.Options} based in given
     * {@link com.hayukleung.app.widget.media.picasso.Request}, only instantiating them if needed.
     */
    static BitmapFactory.Options createBitmapOptions(Request data) {
        final boolean justBounds = data.hasSize();
        final boolean hasConfig = data.config != null;
        BitmapFactory.Options options = null;
        if (justBounds || hasConfig) {
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = justBounds;
            if (hasConfig) {
                options.inPreferredConfig = data.config;
            }
        }
        return options;
    }

    static boolean requiresInSampleSize(BitmapFactory.Options options) {
        return options != null && options.inJustDecodeBounds;
    }

    static void calculateInSampleSize(int reqWidth, int reqHeight, BitmapFactory.Options options,
                                      Request request) {
        calculateInSampleSize(reqWidth, reqHeight, options.outWidth, options.outHeight, options,
                request);
    }

    static void calculateInSampleSize(int reqWidth, int reqHeight, int width, int height,
                                      BitmapFactory.Options options, Request request) {
        int sampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio;
            final int widthRatio;
            if (reqHeight == 0) {
                sampleSize = (int) Math.floor((float) width / (float) reqWidth);
            } else if (reqWidth == 0) {
                sampleSize = (int) Math.floor((float) height / (float) reqHeight);
            } else {
                heightRatio = (int) Math.floor((float) height / (float) reqHeight);
                widthRatio = (int) Math.floor((float) width / (float) reqWidth);
                sampleSize = request.centerInside
                        ? Math.max(heightRatio, widthRatio)
                        : Math.min(heightRatio, widthRatio);
            }
        }
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
    }

    /**
     * Whether or not this {@link com.hayukleung.app.widget.media.picasso.RequestHandler} can handle a request with the
     * given {@link com.hayukleung.app.widget.media.picasso.Request}.
     */
    public abstract boolean canHandleRequest(Request data);

    /**
     * Loads an image for the given {@link com.hayukleung.app.widget.media.picasso.Request}.
     *
     * @param data the {@link android.net.Uri} to load the image from.
     * @return A {@link IResource} instance representing the result.
     */
    public abstract IResource load(Request data) throws IOException;

    int getRetryCount() {
        return 0;
    }

    boolean needLock() {
        return false;
    }

    boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
        return false;
    }

    boolean supportsReplay() {
        return false;
    }

    public interface IResource {
        IImage decode() throws IOException;

        ImageFormat getFormat() throws IOException;

        Picasso.LoadedFrom getLoadedFrom();

        int getExifOrientation();
    }

    protected static class BitmapResource implements IResource {

        private Bitmap bitmap;
        private Picasso.LoadedFrom loadedFrom;
        private int exifOrientation;

        public BitmapResource(Bitmap bitmap, Picasso.LoadedFrom loadedFrom, int exifOrientation) {
            this.bitmap = bitmap;
            this.loadedFrom = loadedFrom;
            this.exifOrientation = exifOrientation;
        }

        @Override
        public IImage decode() throws IOException {
            return new BitmapImage(bitmap);
        }

        @Override
        public ImageFormat getFormat() {
            return ImageFormat.UNKNOWN;
        }

        @Override
        public Picasso.LoadedFrom getLoadedFrom() {
            return loadedFrom;
        }

        @Override
        public int getExifOrientation() {
            return exifOrientation;
        }
    }
}
