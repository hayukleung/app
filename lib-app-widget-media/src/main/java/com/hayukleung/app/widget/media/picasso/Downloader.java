package com.hayukleung.app.widget.media.picasso;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

/**
 * A mechanism to load images from external resources such as a disk cache and/or the internet.
 */
public interface Downloader {
    /**
     * Download the specified image {@code url} from the internet.
     *
     * @param uri            Remote image URL.
     * @return {@link Response} containing either a {@link Bitmap} representation of the request or an
     * {@link InputStream} for the image data. {@code null} can be returned to indicate a problem
     * loading the bitmap.
     * @throws IOException if the requested URL cannot successfully be loaded.
     */
    Response load(Uri uri) throws IOException;

    /**
     * Allows to perform a clean up for this {@link com.hayukleung.app.widget.media.picasso.Downloader} including closing the disk cache and
     * other resources.
     */
    void shutdown();

    /**
     * Response stream or bitmap and info.
     */
    static class Response {
        private final int length;
        private final InputStream inputStream;

        Response(int length, InputStream inputStream) {
            this.length = length;
            this.inputStream = inputStream;
        }

        public int getLength() {
            return length;
        }

        public InputStream getInputStream() {
            return inputStream;
        }
    }

    /**
     * Thrown for non-2XX responses.
     */
    class ResponseException extends IOException {
        final int responseCode;

        public ResponseException(String message, int responseCode) {
            super(message);
            this.responseCode = responseCode;
        }
    }
}