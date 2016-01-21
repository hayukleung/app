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
import android.graphics.Matrix;
import android.net.NetworkInfo;

import com.hayukleung.app.widget.media.picasso.Cache;
import com.hayukleung.app.widget.media.picasso.Downloader;
import com.hayukleung.app.widget.media.picasso.Picasso;
import com.hayukleung.app.widget.media.picasso.RequestHandler;
import com.hayukleung.app.widget.media.picasso.Transformation;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static android.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL;
import static android.media.ExifInterface.ORIENTATION_FLIP_VERTICAL;
import static android.media.ExifInterface.ORIENTATION_ROTATE_180;
import static android.media.ExifInterface.ORIENTATION_ROTATE_270;
import static android.media.ExifInterface.ORIENTATION_ROTATE_90;
import static android.media.ExifInterface.ORIENTATION_TRANSPOSE;
import static android.media.ExifInterface.ORIENTATION_TRANSVERSE;
import static com.hayukleung.app.widget.media.picasso.MemoryPolicy.shouldReadFromMemoryCache;
import static com.hayukleung.app.widget.media.picasso.Picasso.LoadedFrom.MEMORY;
import static com.hayukleung.app.widget.media.picasso.Picasso.Priority;
import static com.hayukleung.app.widget.media.picasso.Picasso.Priority.LOW;
import static com.hayukleung.app.widget.media.picasso.Utils.OWNER_HUNTER;
import static com.hayukleung.app.widget.media.picasso.Utils.VERB_DECODED;
import static com.hayukleung.app.widget.media.picasso.Utils.VERB_EXECUTING;
import static com.hayukleung.app.widget.media.picasso.Utils.VERB_JOINED;
import static com.hayukleung.app.widget.media.picasso.Utils.VERB_REMOVED;
import static com.hayukleung.app.widget.media.picasso.Utils.VERB_TRANSFORMED;
import static com.hayukleung.app.widget.media.picasso.Utils.getLogIdsForHunter;
import static com.hayukleung.app.widget.media.picasso.Utils.log;

class BitmapHunter implements Runnable {
    /**
     * Global lock for bitmap decoding to ensure that we are only are decoding one at a time. Since
     * this will only ever happen in background threads we help avoid excessive memory thrashing as
     * well as potential OOMs. Shamelessly stolen from Volley.
     */
    private static final Object DECODE_LOCK = new Object();

    private static final ThreadLocal<StringBuilder> NAME_BUILDER = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(Utils.THREAD_PREFIX);
        }
    };

    private static final AtomicInteger SEQUENCE_GENERATOR = new AtomicInteger();

    private static final RequestHandler ERRORING_HANDLER = new RequestHandler() {
        @Override
        public boolean canHandleRequest(Request data) {
            return true;
        }

        @Override
        public IResource load(Request data) throws IOException {
            throw new IllegalStateException("Unrecognized type of request: " + data);
        }
    };

    final int sequence;
    final Picasso picasso;
    final Dispatcher dispatcher;
    final com.hayukleung.app.widget.media.picasso.Cache cache;
    final LruDiskCache diskCache;
    final com.hayukleung.app.widget.media.picasso.Stats stats;
    final String key;
    final Request data;
    final int memoryPolicy;
    final RequestHandler requestHandler;

    com.hayukleung.app.widget.media.picasso.Action action;
    List<com.hayukleung.app.widget.media.picasso.Action> actions;
    IImage result;
    Future<?> future;
    Picasso.LoadedFrom loadedFrom;
    Exception exception;
    int exifRotation; // Determined during decoding of original resource.
    int retryCount;
    Priority priority;
    boolean needLock;

    BitmapHunter(Picasso picasso, Dispatcher dispatcher, com.hayukleung.app.widget.media.picasso.Cache cache, LruDiskCache diskCache, com.hayukleung.app.widget.media.picasso.Stats stats, com.hayukleung.app.widget.media.picasso.Action action,
                 RequestHandler requestHandler) {
        this.sequence = SEQUENCE_GENERATOR.incrementAndGet();
        this.picasso = picasso;
        this.dispatcher = dispatcher;
        this.cache = cache;
        this.diskCache = diskCache;
        this.stats = stats;
        this.action = action;
        this.key = action.getKey();
        this.data = action.getRequest();
        this.priority = action.getPriority();
        this.memoryPolicy = action.memoryPolicy;
        this.requestHandler = requestHandler;
        this.retryCount = requestHandler.getRetryCount();
        this.needLock = requestHandler.needLock();
    }

    static void updateThreadName(Request data) {
        String name = data.getName();

        StringBuilder builder = NAME_BUILDER.get();
        builder.ensureCapacity(Utils.THREAD_PREFIX.length() + name.length());
        builder.replace(Utils.THREAD_PREFIX.length(), builder.length(), name);

        Thread.currentThread().setName(builder.toString());
    }

    static com.hayukleung.app.widget.media.picasso.BitmapHunter forRequest(Picasso picasso, Dispatcher dispatcher, Cache cache, LruDiskCache diskCache, com.hayukleung.app.widget.media.picasso.Stats stats,
                                   com.hayukleung.app.widget.media.picasso.Action action) {
        if (action instanceof ResourceAction) {
            return new ResourceHunter(picasso, dispatcher, cache, diskCache, stats, action, picasso.resourceNetworkRequestHandler);
        }

        Request request = action.getRequest();
        List<RequestHandler> requestHandlers = picasso.getRequestHandlers();

        // Index-based loop to avoid allocating an iterator.
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, count = requestHandlers.size(); i < count; i++) {
            RequestHandler requestHandler = requestHandlers.get(i);
            if (requestHandler.canHandleRequest(request)) {
                return new com.hayukleung.app.widget.media.picasso.BitmapHunter(picasso, dispatcher, cache, diskCache, stats, action, requestHandler);
            }
        }

        return new com.hayukleung.app.widget.media.picasso.BitmapHunter(picasso, dispatcher, cache, diskCache, stats, action, ERRORING_HANDLER);
    }

    static Bitmap applyCustomTransformations(List<Transformation> transformations, Bitmap result) {
        return applyCustomTransformations(transformations, result, true);
    }

    static Bitmap applyCustomTransformations(List<Transformation> transformations, Bitmap result, boolean recycle) {
        for (int i = 0, count = transformations.size(); i < count; i++) {
            final Transformation transformation = transformations.get(i);
            Bitmap newResult;
            try {
                newResult = transformation.transform(result);
            } catch (final RuntimeException e) {
                Picasso.HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        throw new RuntimeException(
                                "Transformation " + transformation.key() + " crashed with exception.", e);
                    }
                });
                return null;
            }

            if (newResult == null) {
                final StringBuilder builder = new StringBuilder() //
                        .append("Transformation ")
                        .append(transformation.key())
                        .append(" returned null after ")
                        .append(i)
                        .append(" previous transformation(s).\n\nTransformation list:\n");
                for (Transformation t : transformations) {
                    builder.append(t.key()).append('\n');
                }
                Picasso.HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        throw new NullPointerException(builder.toString());
                    }
                });
                return null;
            }

            if (newResult == result && result.isRecycled()) {
                Picasso.HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        throw new IllegalStateException("Transformation "
                                + transformation.key()
                                + " returned input Bitmap but recycled it.");
                    }
                });
                return null;
            }

            if (newResult != result) {
                if (recycle) {
                    result.recycle();
                }
            }
            if (!recycle) {
                recycle = result != newResult;
            }

            result = newResult;
        }
        return result;
    }

    static Bitmap transformResult(Request data, Bitmap result, int exifOrientation) {
        return transformResult(data, result, exifOrientation, true);
    }

    static Bitmap transformResult(Request data, Bitmap result, int exifOrientation, boolean recycle) {
        int inWidth = result.getWidth();
        int inHeight = result.getHeight();
        boolean onlyScaleDown = data.onlyScaleDown;

        int drawX = 0;
        int drawY = 0;
        int drawWidth = inWidth;
        int drawHeight = inHeight;

        Matrix matrix = new Matrix();

        if (data.needsMatrixTransform() || exifOrientation != 0) {
            int targetWidth = data.targetWidth;
            int targetHeight = data.targetHeight;

            float targetRotation = data.rotationDegrees;
            if (targetRotation != 0) {
                double cosR = Math.cos(Math.toRadians(targetRotation));
                double sinR = Math.sin(Math.toRadians(targetRotation));
                if (data.hasRotationPivot) {
                    matrix.setRotate(targetRotation, data.rotationPivotX, data.rotationPivotY);
                    // Recalculate dimensions after rotation around pivot point
                    double x1T = data.rotationPivotX * (1.0 - cosR) + (data.rotationPivotY * sinR);
                    double y1T = data.rotationPivotY * (1.0 - cosR) - (data.rotationPivotX * sinR);
                    double x2T = x1T + (data.targetWidth * cosR);
                    double y2T = y1T + (data.targetWidth * sinR);
                    double x3T = x1T + (data.targetWidth * cosR) - (data.targetHeight * sinR);
                    double y3T = y1T + (data.targetWidth * sinR) + (data.targetHeight * cosR);
                    double x4T = x1T - (data.targetHeight * sinR);
                    double y4T = y1T + (data.targetHeight * cosR);

                    double maxX = Math.max(x4T, Math.max(x3T, Math.max(x1T, x2T)));
                    double minX = Math.min(x4T, Math.min(x3T, Math.min(x1T, x2T)));
                    double maxY = Math.max(y4T, Math.max(y3T, Math.max(y1T, y2T)));
                    double minY = Math.min(y4T, Math.min(y3T, Math.min(y1T, y2T)));
                    targetWidth = (int) Math.floor(maxX - minX);
                    targetHeight  = (int) Math.floor(maxY - minY);
                } else {
                    matrix.setRotate(targetRotation);
                    // Recalculate dimensions after rotation (around origin)
                    double x1T = 0.0;
                    double y1T = 0.0;
                    double x2T = (data.targetWidth * cosR);
                    double y2T = (data.targetWidth * sinR);
                    double x3T = (data.targetWidth * cosR) - (data.targetHeight * sinR);
                    double y3T = (data.targetWidth * sinR) + (data.targetHeight * cosR);
                    double x4T = -(data.targetHeight * sinR);
                    double y4T = (data.targetHeight * cosR);

                    double maxX = Math.max(x4T, Math.max(x3T, Math.max(x1T, x2T)));
                    double minX = Math.min(x4T, Math.min(x3T, Math.min(x1T, x2T)));
                    double maxY = Math.max(y4T, Math.max(y3T, Math.max(y1T, y2T)));
                    double minY = Math.min(y4T, Math.min(y3T, Math.min(y1T, y2T)));
                    targetWidth = (int) Math.floor(maxX - minX);
                    targetHeight  = (int) Math.floor(maxY - minY);
                }
            }

            // EXIf interpretation should be done before cropping in case the dimensions need to
            // be recalculated
            if (exifOrientation != 0) {
                int exifRotation = getExifRotation(exifOrientation);
                int exifTranslation = getExifTranslation(exifOrientation);
                if (exifRotation != 0) {
                    matrix.preRotate(exifRotation);
                    if (exifRotation == 90 || exifRotation == 270) {
                        // Recalculate dimensions after exif rotation
                        int tmpHeight = targetHeight;
                        targetHeight = targetWidth;
                        targetWidth = tmpHeight;
                    }
                }
                if (exifTranslation != 1) {
                    matrix.postScale(exifTranslation, 1);
                }
            }

            if (data.centerCrop) {
                // Keep aspect ratio if one dimension is set to 0
                float widthRatio =
                        targetWidth != 0 ? targetWidth / (float) inWidth : targetHeight / (float) inHeight;
                float heightRatio =
                        targetHeight != 0 ? targetHeight / (float) inHeight : targetWidth / (float) inWidth;
                float scaleX, scaleY;
                if (widthRatio > heightRatio) {
                    int newSize = (int) Math.ceil(inHeight * (heightRatio / widthRatio));
                    drawY = (inHeight - newSize) / 2;
                    drawHeight = newSize;
                    scaleX = widthRatio;
                    scaleY = targetHeight / (float) drawHeight;
                } else if (widthRatio < heightRatio) {
                    int newSize = (int) Math.ceil(inWidth * (widthRatio / heightRatio));
                    drawX = (inWidth - newSize) / 2;
                    drawWidth = newSize;
                    scaleX = targetWidth / (float) drawWidth;
                    scaleY = heightRatio;
                } else {
                    drawX = 0;
                    drawWidth = inWidth;
                    scaleX = scaleY = heightRatio;
                }
                if (shouldResize(onlyScaleDown, inWidth, inHeight, targetWidth, targetHeight)) {
                    matrix.preScale(scaleX, scaleY);
                }
            } else if (data.centerInside) {
                // Keep aspect ratio if one dimension is set to 0
                float widthRatio = targetWidth != 0 ? targetWidth / (float) inWidth : targetHeight / (float) inHeight;
                float heightRatio = targetHeight != 0 ? targetHeight / (float) inHeight : targetWidth / (float) inWidth;
                float scale = widthRatio < heightRatio ? widthRatio : heightRatio;
                if (shouldResize(onlyScaleDown, inWidth, inHeight, targetWidth, targetHeight)) {
                    matrix.preScale(scale, scale);
                }
            } else if ((targetWidth != 0 || targetHeight != 0) //
                    && (targetWidth != inWidth || targetHeight != inHeight)) {
                // If an explicit target size has been specified and they do not match the results bounds,
                // pre-scale the existing matrix appropriately.
                // Keep aspect ratio if one dimension is set to 0.
                float sx = targetWidth != 0 ? targetWidth / (float) inWidth : targetHeight / (float) inHeight;
                float sy = targetHeight != 0 ? targetHeight / (float) inHeight : targetWidth / (float) inWidth;
                if (shouldResize(onlyScaleDown, inWidth, inHeight, targetWidth, targetHeight)) {
                    matrix.preScale(sx, sy);
                }
            }
        }

        Bitmap newResult = Bitmap.createBitmap(result, drawX, drawY, drawWidth, drawHeight, matrix, true);
        if (newResult != result) {
            if (recycle) {
                result.recycle();
            }
            result = newResult;
        }

        return result;
    }

    private static boolean shouldResize(boolean onlyScaleDown, int inWidth, int inHeight, int targetWidth, int targetHeight) {
        return !onlyScaleDown || inWidth > targetWidth || inHeight > targetHeight;
    }

    @Override
    public void run() {
        try {
            updateThreadName(data);

            if (picasso.loggingEnabled) {
                log(OWNER_HUNTER, VERB_EXECUTING, getLogIdsForHunter(this));
            }

            result = hunt();

            if (result == null) {
                dispatcher.dispatchFailed(this);
            } else {
                dispatcher.dispatchComplete(this);
            }
        } catch (Downloader.ResponseException e) {
            if (e.responseCode != 504) {
                exception = e;
            }
            dispatcher.dispatchFailed(this);
        } catch (IOException e) {
            exception = e;
            dispatcher.dispatchRetry(this);
        } catch (OutOfMemoryError e) {
            StringWriter writer = new StringWriter();
            stats.createSnapshot().dump(new PrintWriter(writer));
            exception = new RuntimeException(writer.toString(), e);
            dispatcher.dispatchFailed(this);
        } catch (Exception e) {
            exception = e;
            dispatcher.dispatchFailed(this);
        } finally {
            Thread.currentThread().setName(Utils.THREAD_IDLE_NAME);
        }
    }

    IImage hunt() throws IOException {
        IImage image = null;

        if (shouldReadFromMemoryCache(memoryPolicy)) {
            image = cache.get(key);
            if (image != null) {
                stats.dispatchCacheHit();
                loadedFrom = MEMORY;
                if (picasso.loggingEnabled) {
                    log(OWNER_HUNTER, VERB_DECODED, data.logId(), "from cache");
                }
                return image;
            }
        }

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
            synchronized (DECODE_LOCK) {
                image = resource.decode();
                loadedFrom = resource.getLoadedFrom();
                exifRotation = resource.getExifOrientation();
                Bitmap bitmap = ((BitmapImage) image).getBitmap();
                if (picasso.loggingEnabled) {
                    log(OWNER_HUNTER, VERB_DECODED, data.logId());
                }
                stats.dispatchBitmapDecoded(bitmap);
                bitmap = transformBitmap(bitmap);
                if (bitmap != null) {
                    if (((BitmapImage) image).getBitmap() != bitmap) {
                        image = new BitmapImage(bitmap);
                    }
                    stats.dispatchBitmapTransformed(bitmap);
                } else {
                    image = null;
                }
            }
        }
        return image;
    }

    private Bitmap transformBitmap(Bitmap bitmap) {
        if (data.needsTransformation() || exifRotation != 0) {
            if (data.needsMatrixTransform() || exifRotation != 0) {
                bitmap = transformResult(data, bitmap, exifRotation);
                if (picasso.loggingEnabled) {
                    log(OWNER_HUNTER, VERB_TRANSFORMED, data.logId());
                }
            }
            if (data.hasCustomTransformations()) {
                bitmap = applyCustomTransformations(data.transformations, bitmap);
                if (picasso.loggingEnabled) {
                    log(OWNER_HUNTER, VERB_TRANSFORMED, data.logId(), "from custom transformations");
                }
            }
        }
        return bitmap;
    }

    void attach(com.hayukleung.app.widget.media.picasso.Action action) {
        boolean loggingEnabled = picasso.loggingEnabled;
        Request request = action.request;

        if (this.action == null) {
            this.action = action;
            if (loggingEnabled) {
                if (actions == null || actions.isEmpty()) {
                    log(OWNER_HUNTER, VERB_JOINED, request.logId(), "to empty hunter");
                } else {
                    log(OWNER_HUNTER, VERB_JOINED, request.logId(), getLogIdsForHunter(this, "to "));
                }
            }
            return;
        }

        if (actions == null) {
            actions = new ArrayList<com.hayukleung.app.widget.media.picasso.Action>(3);
        }

        actions.add(action);

        if (loggingEnabled) {
            log(OWNER_HUNTER, VERB_JOINED, request.logId(), getLogIdsForHunter(this, "to "));
        }

        Priority actionPriority = action.getPriority();
        if (actionPriority.ordinal() > priority.ordinal()) {
            priority = actionPriority;
        }
    }

    void detach(com.hayukleung.app.widget.media.picasso.Action action) {
        boolean detached = false;
        if (this.action == action) {
            this.action = null;
            detached = true;
        } else if (actions != null) {
            detached = actions.remove(action);
        }

        // The action being detached had the highest priority. Update this
        // hunter's priority with the remaining actions.
        if (detached && action.getPriority() == priority) {
            priority = computeNewPriority();
        }

        if (picasso.loggingEnabled) {
            log(OWNER_HUNTER, VERB_REMOVED, action.request.logId(), getLogIdsForHunter(this, "from "));
        }
    }

    private Priority computeNewPriority() {
        Priority newPriority = LOW;

        boolean hasMultiple = actions != null && !actions.isEmpty();
        boolean hasAny = action != null || hasMultiple;

        // Hunter has no requests, low priority.
        if (!hasAny) {
            return newPriority;
        }

        if (action != null) {
            newPriority = action.getPriority();
        }

        if (hasMultiple) {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, n = actions.size(); i < n; i++) {
                Priority actionPriority = actions.get(i).getPriority();
                if (actionPriority.ordinal() > newPriority.ordinal()) {
                    newPriority = actionPriority;
                }
            }
        }

        return newPriority;
    }

    boolean cancel() {
        return action == null
                && (actions == null || actions.isEmpty())
                && future != null
                && future.cancel(false);
    }

    boolean isCancelled() {
        return future != null && future.isCancelled();
    }

    boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
        boolean hasRetries = retryCount > 0;
        if (!hasRetries) {
            return false;
        }
        retryCount--;
        return requestHandler.shouldRetry(airplaneMode, info);
    }

    boolean supportsReplay() {
        return requestHandler.supportsReplay();
    }

    IImage getResult() {
        return result;
    }

    String getKey() {
        return key;
    }

    int getMemoryPolicy() {
        return memoryPolicy;
    }

    Request getData() {
        return data;
    }

    com.hayukleung.app.widget.media.picasso.Action getAction() {
        return action;
    }

    Picasso getPicasso() {
        return picasso;
    }

    List<com.hayukleung.app.widget.media.picasso.Action> getActions() {
        return actions;
    }

    Exception getException() {
        return exception;
    }

    Picasso.LoadedFrom getLoadedFrom() {
        return loadedFrom;
    }

    Priority getPriority() {
        return priority;
    }

    static int getExifRotation(int orientation) {
        int rotation;
        switch (orientation) {
            case ORIENTATION_ROTATE_90:
            case ORIENTATION_TRANSPOSE:
                rotation = 90;
                break;
            case ORIENTATION_ROTATE_180:
            case ORIENTATION_FLIP_VERTICAL:
                rotation = 180;
                break;
            case ORIENTATION_ROTATE_270:
            case ORIENTATION_TRANSVERSE:
                rotation = 270;
                break;
            default:
                rotation = 0;
        }
        return rotation;
    }

    static int getExifTranslation(int orientation)  {
        int translation;
        switch (orientation) {
            case ORIENTATION_FLIP_HORIZONTAL:
            case ORIENTATION_FLIP_VERTICAL:
            case ORIENTATION_TRANSPOSE:
            case ORIENTATION_TRANSVERSE:
                translation = -1;
                break;
            default:
                translation = 1;
        }
        return translation;
    }
}
