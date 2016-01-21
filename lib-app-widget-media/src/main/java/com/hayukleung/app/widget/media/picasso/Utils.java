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

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Looper;
import android.os.Process;
import android.os.StatFs;
import android.provider.Settings;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ThreadFactory;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.HONEYCOMB_MR1;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.provider.Settings.System.AIRPLANE_MODE_ON;
import static com.hayukleung.app.widget.media.picasso.Picasso.TAG;
import static java.lang.String.format;

final class Utils {
    static final String THREAD_PREFIX = "Picasso-";
    static final String THREAD_IDLE_NAME = THREAD_PREFIX + "Idle";
    static final int DEFAULT_READ_TIMEOUT = 20 * 1000; // 20s
    static final int DEFAULT_CONNECT_TIMEOUT = 15 * 1000; // 15s
    static final char KEY_SEPARATOR = '\n';
    /**
     * Thread confined to main thread for key creation.
     */
    static final StringBuilder MAIN_THREAD_KEY_BUILDER = new StringBuilder();
    /**
     * Logging
     */
    static final String OWNER_MAIN = "Main";
    static final String OWNER_DISPATCHER = "Dispatcher";
    static final String OWNER_HUNTER = "Hunter";
    static final String VERB_CREATED = "created";
    static final String VERB_CHANGED = "changed";
    static final String VERB_IGNORED = "ignored";
    static final String VERB_ENQUEUED = "enqueued";
    static final String VERB_CANCELED = "canceled";
    static final String VERB_BATCHED = "batched";
    static final String VERB_RETRYING = "retrying";
    static final String VERB_EXECUTING = "executing";
    static final String VERB_DECODED = "decoded";
    static final String VERB_TRANSFORMED = "transformed";
    static final String VERB_JOINED = "joined";
    static final String VERB_REMOVED = "removed";
    static final String VERB_DELIVERED = "delivered";
    static final String VERB_REPLAYING = "replaying";
    static final String VERB_COMPLETED = "completed";
    static final String VERB_ERRORED = "errored";
    static final String VERB_PAUSED = "paused";
    static final String VERB_RESUMED = "resumed";
    static final Charset US_ASCII = Charset.forName("US-ASCII");
    static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final String PICASSO_CACHE = "picasso-cache";
    private static final int KEY_PADDING = 50; // Determined by exact science.
    private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    /* WebP file header
       0                   1                   2                   3
       0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |      'R'      |      'I'      |      'F'      |      'F'      |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |                           File Size                           |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |      'W'      |      'E'      |      'B'      |      'P'      |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    */
    private static final int WEBP_FILE_HEADER_SIZE = 12;
    private static final byte[] WEBP_FILE_HEADER_RIFF = asciiBytes("RIFF");
    private static final byte[] WEBP_FILE_HEADER_WEBP = asciiBytes("WEBP");

    /**
     * Every gif image starts with "GIF" bytes followed by
     * bytes indicating version of gif standard
     */
    private static final int GIF_HEADER_LENGTH = 6;
    private static final byte[] GIF_HEADER_87A = asciiBytes("GIF87a");
    private static final byte[] GIF_HEADER_89A = asciiBytes("GIF89a");


    private Utils() {
        // No instances.
    }

    static int getBitmapBytes(Bitmap bitmap) {
        int result;
        if (SDK_INT >= HONEYCOMB_MR1) {
            result = BitmapHoneycombMR1.getByteCount(bitmap);
        } else {
            result = bitmap.getRowBytes() * bitmap.getHeight();
        }
        if (result < 0) {
            throw new IllegalStateException("Negative size: " + bitmap);
        }
        return result;
    }

    static void checkNotMain() {
        if (isMain()) {
            throw new IllegalStateException("Method call should not happen from the main thread.");
        }
    }

    static void checkMain() {
        if (!isMain()) {
            throw new IllegalStateException("Method call should happen from the main thread.");
        }
    }

    static boolean isMain() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    static String getLogIdsForHunter(com.hayukleung.app.widget.media.picasso.BitmapHunter hunter) {
        return getLogIdsForHunter(hunter, "");
    }

    static String getLogIdsForHunter(com.hayukleung.app.widget.media.picasso.BitmapHunter hunter, String prefix) {
        StringBuilder builder = new StringBuilder(prefix);
        com.hayukleung.app.widget.media.picasso.Action action = hunter.getAction();
        if (action != null) {
            builder.append(action.request.logId());
        }
        List<com.hayukleung.app.widget.media.picasso.Action> actions = hunter.getActions();
        if (actions != null) {
            for (int i = 0, count = actions.size(); i < count; i++) {
                if (i > 0 || action != null) builder.append(", ");
                builder.append(actions.get(i).request.logId());
            }
        }
        return builder.toString();
    }

    static void log(String owner, String verb, String logId) {
        log(owner, verb, logId, "");
    }

    static void log(String owner, String verb, String logId, String extras) {
        Log.d(TAG, format("%1$-11s %2$-12s %3$s %4$s", owner, verb, logId, extras));
    }

    static String createKey(Request data) {
        String result = createKey(data, MAIN_THREAD_KEY_BUILDER);
        MAIN_THREAD_KEY_BUILDER.setLength(0);
        return result;
    }

    static String createKey(Request data, StringBuilder builder) {
        if (data.stableKey != null) {
            builder.ensureCapacity(data.stableKey.length() + KEY_PADDING);
            builder.append(data.stableKey);
        } else if (data.uri != null) {
            String path = data.uri.toString();
            builder.ensureCapacity(path.length() + KEY_PADDING);
            builder.append(path);
        } else {
            builder.ensureCapacity(KEY_PADDING);
            builder.append(data.resourceId);
        }
        builder.append(KEY_SEPARATOR);

        if (data.rotationDegrees != 0) {
            builder.append("rotation:").append(data.rotationDegrees);
            if (data.hasRotationPivot) {
                builder.append('@').append(data.rotationPivotX).append('x').append(data.rotationPivotY);
            }
            builder.append(KEY_SEPARATOR);
        }
        if (data.hasSize()) {
            builder.append("resize:").append(data.targetWidth).append('x').append(data.targetHeight);
            builder.append(KEY_SEPARATOR);
        }
        if (data.centerCrop) {
            builder.append("centerCrop").append(KEY_SEPARATOR);
        } else if (data.centerInside) {
            builder.append("centerInside").append(KEY_SEPARATOR);
        } else if (data.onlyScaleDown) {
            builder.append("onlyScaleDown").append(KEY_SEPARATOR);
        }

        if (data.transformations != null) {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = data.transformations.size(); i < count; i++) {
                builder.append(data.transformations.get(i).key());
                builder.append(KEY_SEPARATOR);
            }
        }

        return builder.toString();
    }

    static void closeQuietly(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }

    /**
     * Returns {@code true} if header indicates the response body was loaded from the disk cache.
     */
    static boolean parseResponseSourceHeader(String header) {
        if (header == null) {
            return false;
        }
        String[] parts = header.split(" ", 2);
        if ("CACHE".equals(parts[0])) {
            return true;
        }
        if (parts.length == 1) {
            return false;
        }
        try {
            return "CONDITIONAL_CACHE".equals(parts[0]) && Integer.parseInt(parts[1]) == 304;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    static Downloader createDefaultDownloader() {
        return new DefaultDownloader();
    }

    static LruDiskCache createDefaultLruDiskCache(Context context) {
        File dir = com.hayukleung.app.widget.media.picasso.Utils.createDefaultCacheDir(context);
        File reserveDir = com.hayukleung.app.widget.media.picasso.Utils.createReserveCacheDir(context);
        long size = com.hayukleung.app.widget.media.picasso.Utils.calculateDiskCacheSize(dir);
        LruDiskCache diskCache = null;
        try {
            diskCache = new LruDiskCache(dir, reserveDir, new FileNameGenerator(), size, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return diskCache;
    }

    static File createDefaultCacheDir(Context context) {
        File cache = new File(context.getApplicationContext().getExternalCacheDir(), PICASSO_CACHE);
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }
        return cache;
    }

    static File createReserveCacheDir(Context context) {
        File cache = new File(context.getApplicationContext().getCacheDir(), PICASSO_CACHE);
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }
        return cache;
    }

    static long calculateDiskCacheSize(File dir) {
        long size = MIN_DISK_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
            // Target 2% of the total space.
            size = available / 50;
        } catch (IllegalArgumentException ignored) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
    }

    static int calculateMemoryCacheSize(Context context) {
        ActivityManager am = getService(context, ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        int memoryClass = am.getMemoryClass();
        if (largeHeap && SDK_INT >= HONEYCOMB) {
            memoryClass = ActivityManagerHoneycomb.getLargeMemoryClass(am);
        }
        // Target ~15% of the available heap.
        return 1024 * 1024 * memoryClass / 10;
    }

    static boolean isAirplaneModeOn(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            return Settings.System.getInt(contentResolver, AIRPLANE_MODE_ON, 0) != 0;
        } catch (NullPointerException e) {
            // https://github.com/square/picasso/issues/761, some devices might crash here, assume that
            // airplane mode is off.
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T getService(Context context, String service) {
        return (T) context.getSystemService(service);
    }

    static boolean hasPermission(Context context, String permission) {
        return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n;
        while (-1 != (n = input.read(buffer))) {
            byteArrayOutputStream.write(buffer, 0, n);
        }
        return byteArrayOutputStream.toByteArray();
    }

    static boolean isWebPFile(final byte[] imageHeaderBytes) throws IOException {
        return matchBytePattern(imageHeaderBytes, 0, WEBP_FILE_HEADER_RIFF) &&
                matchBytePattern(imageHeaderBytes, 8, WEBP_FILE_HEADER_WEBP);
    }

    static boolean isGifFile(final String file) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
            return isGifFile(stream);
        } catch (IOException ignored) {
        } finally {
            closeQuietly(stream);
        }
        return false;
    }

    static boolean isGifFile(InputStream stream) throws IOException {
        byte[] fileHeaderBytes = new byte[GIF_HEADER_LENGTH];
        boolean isGifFile = false;
        if (stream.read(fileHeaderBytes, 0, GIF_HEADER_LENGTH) == GIF_HEADER_LENGTH) {
            isGifFile = isGifFile(fileHeaderBytes);
        }
        return isGifFile;
    }

    /**
     * Checks if first headerSize bytes of imageHeaderBytes constitute a valid header for a gif image.
     * Details on GIF header can be found <a href="http://www.w3.org/Graphics/GIF/spec-gif89a.txt">
     * on page 7</a>
     *
     * @return true if imageHeaderBytes is a valid header for a gif image
     */
    static boolean isGifFile(final byte[] imageHeaderBytes) {
        return matchBytePattern(imageHeaderBytes, 0, GIF_HEADER_87A) ||
                matchBytePattern(imageHeaderBytes, 0, GIF_HEADER_89A);
    }

    /**
     * Checks if byteArray interpreted as sequence of bytes has a subsequence equal to pattern
     * starting at position equal to offset.
     *
     * @return true if match succeeds, false otherwise
     */
    private static boolean matchBytePattern(
            final byte[] byteArray,
            final int offset,
            final byte[] pattern) {
        if (pattern.length + offset > byteArray.length) {
            return false;
        }
        for (int i = 0; i < pattern.length; ++i) {
            if (byteArray[i + offset] != pattern[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper method that transforms provided string into it's byte representation
     * using ASCII encoding
     *
     * @param value
     * @return byte array representing ascii encoded value
     */
    private static byte[] asciiBytes(String value) {
        try {
            return value.getBytes("ASCII");
        } catch (UnsupportedEncodingException uee) {
            // won't happen
            throw new RuntimeException("ASCII not found!", uee);
        }
    }

    static int getResourceId(Resources resources, Request data) throws FileNotFoundException {
        if (data.resourceId != 0 || data.uri == null) {
            return data.resourceId;
        }

        String pkg = data.uri.getAuthority();
        if (pkg == null) throw new FileNotFoundException("No package provided: " + data.uri);

        int id;
        List<String> segments = data.uri.getPathSegments();
        if (segments == null || segments.isEmpty()) {
            throw new FileNotFoundException("No path segments: " + data.uri);
        } else if (segments.size() == 1) {
            try {
                id = Integer.parseInt(segments.get(0));
            } catch (NumberFormatException e) {
                throw new FileNotFoundException("Last path segment is not a resource ID: " + data.uri);
            }
        } else if (segments.size() == 2) {
            String type = segments.get(0);
            String name = segments.get(1);

            id = resources.getIdentifier(name, type, pkg);
        } else {
            throw new FileNotFoundException("More than two path segments: " + data.uri);
        }
        return id;
    }

    static Resources getResources(Context context, Request data) throws FileNotFoundException {
        if (data.resourceId != 0 || data.uri == null) {
            return context.getResources();
        }

        String pkg = data.uri.getAuthority();
        if (pkg == null) throw new FileNotFoundException("No package provided: " + data.uri);
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getResourcesForApplication(pkg);
        } catch (PackageManager.NameNotFoundException e) {
            throw new FileNotFoundException("Unable to obtain resources for package: " + data.uri);
        }
    }

    static String readFully(Reader reader) throws IOException {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[1024];
            int count;
            while ((count = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, count);
            }
            return writer.toString();
        } finally {
            reader.close();
        }
    }

    /**
     * Deletes the contents of {@code dir}. Throws an IOException if any file
     * could not be deleted, or if {@code dir} is not a readable directory.
     */
    static void deleteContents(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new IOException("not a readable directory: " + dir);
        }
        for (File file : files) {
            if (file.isDirectory()) {
                deleteContents(file);
            }
            if (!file.delete()) {
                throw new IOException("failed to delete file: " + file);
            }
        }
    }

    @TargetApi(HONEYCOMB)
    private static class ActivityManagerHoneycomb {
        static int getLargeMemoryClass(ActivityManager activityManager) {
            return activityManager.getLargeMemoryClass();
        }
    }

    static class PicassoThreadFactory implements ThreadFactory {
        @SuppressWarnings("NullableProblems")
        public Thread newThread(Runnable r) {
            return new PicassoThread(r);
        }
    }

    private static class PicassoThread extends Thread {
        public PicassoThread(Runnable r) {
            super(r);
        }

        @Override
        public void run() {
            Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);
            super.run();
        }
    }

    @TargetApi(HONEYCOMB_MR1)
    private static class BitmapHoneycombMR1 {
        static int getByteCount(Bitmap bitmap) {
            return bitmap.getByteCount();
        }
    }
}
