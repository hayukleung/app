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

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentResolver.SCHEME_CONTENT;
import static com.hayukleung.app.widget.media.picasso.Picasso.LoadedFrom.DISK;

class ContentStreamRequestHandler extends RequestHandler {
    final Context context;

    ContentStreamRequestHandler(Context context) {
        this.context = context;
    }

    @Override
    public boolean canHandleRequest(Request data) {
        return SCHEME_CONTENT.equals(data.uri.getScheme());
    }

    @Override
    public IResource load(Request data) throws IOException {
        return new ContentStreamResource(context, data, 0);
    }

    protected static class ContentStreamResource implements IResource {
        private Context context;
        private Request data;
        private int exifOrientation;
        private ImageFormat imageFormat;

        public ContentStreamResource(Context context, Request data, int exifOrientation) {
            this.context = context;
            this.data = data;
            this.exifOrientation = exifOrientation;
        }

        @Override
        public ImageFormat getFormat() throws IOException {
            if (imageFormat != null)
                return imageFormat;

            if (data.thumbnail)
                imageFormat = ImageFormat.PNG;

            if (imageFormat == null) {
                ContentResolver contentResolver = context.getContentResolver();
                InputStream is = null;
                try {
                    is = contentResolver.openInputStream(data.uri);
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
            return exifOrientation;
        }

        public BitmapImage decodeBitmap() throws IOException {
            BitmapImage bitmapImage = null;
            Bitmap bitmap = decodeContentStream();
            if (bitmap != null) {
                bitmapImage = new BitmapImage(bitmap);
            }
            return bitmapImage;
        }

        private Bitmap decodeContentStream() throws IOException {
            ContentResolver contentResolver = context.getContentResolver();
            final BitmapFactory.Options options = createBitmapOptions(data);
            if (requiresInSampleSize(options)) {
                InputStream is = null;
                try {
                    is = contentResolver.openInputStream(data.uri);
                    BitmapFactory.decodeStream(is, null, options);
                } finally {
                    Utils.closeQuietly(is);
                }
                calculateInSampleSize(data.targetWidth, data.targetHeight, options, data);
            }
            InputStream is = contentResolver.openInputStream(data.uri);
            try {
                return BitmapFactory.decodeStream(is, null, options);
            } finally {
                Utils.closeQuietly(is);
            }
        }

        @Override
        public IImage decode() throws IOException {
            IImage image;
            image = decodeBitmap();
            return image;
        }
    }
}
