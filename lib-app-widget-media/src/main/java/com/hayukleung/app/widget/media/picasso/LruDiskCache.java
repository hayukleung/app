/*******************************************************************************
 * Copyright 2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.hayukleung.app.widget.media.picasso;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.hayukleung.app.widget.media.picasso.Dispatcher.HUNTER_UPDATE;

/**
 * Disk cache based on "Least-Recently Used" principle. Adapter pattern, adapts
 *
 * @see FileNameGenerator
 * @since 1.9.2
 */
public class LruDiskCache {
    /**
     * {@value
     */
    public static final int DEFAULT_BUFFER_SIZE = 1024; // 1 Kb
    /**
     * {@value
     */
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    /**
     * {@value
     */
    public static final int DEFAULT_COMPRESS_QUALITY = 100;
    private static final long MIN_PROGRESS_TIME = 200;
    private static final String ERROR_ARG_NULL = " argument must be not null";
    private static final String ERROR_ARG_NEGATIVE = " argument must be positive number";
    protected final FileNameGenerator fileNameGenerator;
    protected int bufferSize = DEFAULT_BUFFER_SIZE;
    protected Bitmap.CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
    protected int compressQuality = DEFAULT_COMPRESS_QUALITY;
    protected DiskLruCache cache;
    private File reserveCacheDir;
    public Handler handler;

    /**
     * @param cacheDir          Directory for file caching
     * @param fileNameGenerator Generated names must match the regex <strong>[a-z0-9_-]{1,64}</strong>
     * @param cacheMaxSize      Max cache size in bytes. <b>0</b> means cache size is unlimited.
     */
    public LruDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator, long cacheMaxSize) throws IOException {
        this(cacheDir, reserveCacheDir, fileNameGenerator, cacheMaxSize, 0);
    }

    /**
     * @param cacheDir          Directory for file caching
     * @param fileNameGenerator Generated names must match the regex
     *                          <strong>[a-z0-9_-]{1,64}</strong>
     * @param cacheMaxSize      Max cache size in bytes. <b>0</b> means cache size is unlimited.
     * @param cacheMaxFileCount Max file count in cache. <b>0</b> means file count is unlimited.
     */
    public LruDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator, long cacheMaxSize, int cacheMaxFileCount) throws IOException {
        if (cacheDir == null) {
            throw new IllegalArgumentException("cacheDir" + ERROR_ARG_NULL);
        }
        if (cacheMaxSize < 0) {
            throw new IllegalArgumentException("cacheMaxSize" + ERROR_ARG_NEGATIVE);
        }
        if (cacheMaxFileCount < 0) {
            throw new IllegalArgumentException("cacheMaxFileCount" + ERROR_ARG_NEGATIVE);
        }
        if (fileNameGenerator == null) {
            throw new IllegalArgumentException("fileNameGenerator" + ERROR_ARG_NULL);
        }

        if (cacheMaxSize == 0) {
            cacheMaxSize = Long.MAX_VALUE;
        }
        if (cacheMaxFileCount == 0) {
            cacheMaxFileCount = Integer.MAX_VALUE;
        }

        this.fileNameGenerator = fileNameGenerator;
        this.reserveCacheDir = reserveCacheDir;
        initCache(cacheDir, reserveCacheDir, cacheMaxSize, cacheMaxFileCount);
    }

    private void initCache(File cacheDir, File reserveCacheDir, long cacheMaxSize, int cacheMaxFileCount) throws IOException {
        try {
            cache = DiskLruCache.open(cacheDir, 1, 1, cacheMaxSize, cacheMaxFileCount);
        } catch (IOException e) {
            e.printStackTrace();
            if (reserveCacheDir != null) {
                initCache(reserveCacheDir, null, cacheMaxSize, cacheMaxFileCount);
            } else {
                throw e;
            }
        }
    }

    public File getDirectory() {
        return cache.getDirectory();
    }

    public File get(String imageUri) {
        try {
            return cache.get(getKey(imageUri), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean save(Uri uri, long length, InputStream in) throws IOException {
        DiskLruCache.Editor editor = cache.edit(getKey(uri.toString()));
        if (editor == null) {
            return false;
        }

        OutputStream os = new BufferedOutputStream(editor.newOutputStream(0), bufferSize);
        boolean copied = false;
        try {
            long lastNotification = SystemClock.elapsedRealtime();
            int offset = 0;
            int count;
            byte[] buffer = new byte[bufferSize];
            while ((count = in.read(buffer)) != -1) {
                os.write(buffer, 0, count);
                if (handler != null) {
                    lastNotification = reportProgress(lastNotification, offset += count, length, handler, uri);
                }
            }
            copied = true;
        } finally {
            Utils.closeQuietly(os);
            if (copied) {
                editor.commit();
            } else {
                editor.abort();
            }
        }
        return copied;
    }

    /**
     * Report download progress through the database if necessary.
     */
    private long reportProgress(long lastNotification, long offset, long length, Handler handler, Uri uri) {
        final long now = SystemClock.elapsedRealtime();

        if (now - lastNotification > MIN_PROGRESS_TIME || offset == length) {
            final int progress = (int) (length > 0 ? (offset * 1f / length) * 10000 : 0);
            handler.sendMessage(handler.obtainMessage(HUNTER_UPDATE, progress, 0, uri));
            lastNotification = now;
        }
        return lastNotification;
    }

    public boolean save(String imageUri, Bitmap bitmap) throws IOException {
        DiskLruCache.Editor editor = cache.edit(getKey(imageUri));
        if (editor == null) {
            return false;
        }

        OutputStream os = new BufferedOutputStream(editor.newOutputStream(0), bufferSize);
        boolean savedSuccessfully = false;
        try {
            savedSuccessfully = bitmap.compress(compressFormat, compressQuality, os);
        } finally {
            Utils.closeQuietly(os);
        }
        if (savedSuccessfully) {
            editor.commit();
        } else {
            editor.abort();
        }
        return savedSuccessfully;
    }

    public boolean remove(String imageUri) {
        try {
            return cache.remove(getKey(imageUri));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        try {
            cache.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cache = null;
    }

    public void clear() throws IOException {
        try {
            cache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            initCache(cache.getDirectory(), reserveCacheDir, cache.getMaxSize(), cache.getMaxFileCount());
        }
    }

    private String getKey(String imageUri) {
        return fileNameGenerator.generate(imageUri);
    }

    /**
     * @param reserveCacheDir null-ok; Reserve directory for file caching. It's used when the primary directory isn't available.
     */
    public void setReserveCacheDir(File reserveCacheDir) {
        this.reserveCacheDir = reserveCacheDir;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setCompressFormat(Bitmap.CompressFormat compressFormat) {
        this.compressFormat = compressFormat;
    }

    public void setCompressQuality(int compressQuality) {
        this.compressQuality = compressQuality;
    }

    public DiskLruCache.Editor getEditor(String key) throws IOException {
        return cache.edit(getKey(key));
    }
}
