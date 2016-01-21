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

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

import static com.hayukleung.app.widget.media.picasso.Picasso.LoadedFrom.DISK;
import static com.hayukleung.app.widget.media.picasso.Picasso.SCHEME_ASSET;

class AssetRequestHandler extends RequestHandler {

    private final AssetManager assetManager;

    public AssetRequestHandler(Context context) {
        assetManager = context.getAssets();
    }

    @Override
    public boolean canHandleRequest(Request data) {
        if (data.uri == null) {
            return false;
        }
        return SCHEME_ASSET.equals(data.uri.getScheme()) && data.uri.getPath().length() > 0;
    }

    @Override
    public IResource load(Request data) throws IOException {
        String filePath = data.uri.getPath().substring(1);
        return new AssetResource(assetManager, data, filePath);
    }

    private static class AssetResource implements IResource {

        private AssetManager assetManager;
        private Request data;
        private String file;
        private ImageFormat imageFormat;

        public AssetResource(AssetManager assetManager, Request data, String file) {
            this.assetManager = assetManager;
            this.data = data;
            this.file = file;
        }

        @Override
        public IImage decode() throws IOException {
            IImage image = null;
            Bitmap bitmap = decodeAsset(data, file);
            if (bitmap != null) {
                image = new BitmapImage(bitmap);
            }
            return image;
        }

        Bitmap decodeAsset(Request data, String filePath) throws IOException {
            final BitmapFactory.Options options = createBitmapOptions(data);
            if (requiresInSampleSize(options)) {
                InputStream is = null;
                try {
                    is = assetManager.open(filePath);
                    BitmapFactory.decodeStream(is, null, options);
                } finally {
                    Utils.closeQuietly(is);
                }
                calculateInSampleSize(data.targetWidth, data.targetHeight, options, data);
            }
            InputStream is = assetManager.open(filePath);
            try {
                return BitmapFactory.decodeStream(is, null, options);
            } finally {
                Utils.closeQuietly(is);
            }
        }

        @Override
        public ImageFormat getFormat() throws IOException {
            if (imageFormat != null)
                return imageFormat;

            if (data.thumbnail)
                imageFormat = ImageFormat.PNG;

            if (imageFormat == null) {
                InputStream is = null;
                try {
                    is = assetManager.open(file);
                    if (Utils.isGifFile(is))
                        imageFormat = ImageFormat.GIF;
                } finally {
                    Utils.closeQuietly(is);
                }
            }

            if (imageFormat == null)
                imageFormat = ImageFormat.PNG;
            return imageFormat;
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
