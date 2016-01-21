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

import android.app.Notification;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.hayukleung.app.widget.media.picasso.Callback;
import com.hayukleung.app.widget.media.picasso.MemoryPolicy;
import com.hayukleung.app.widget.media.picasso.Picasso;
import com.hayukleung.app.widget.media.picasso.Request;
import com.hayukleung.app.widget.media.picasso.Target;
import com.hayukleung.app.widget.media.picasso.Transformation;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.hayukleung.app.widget.media.picasso.BitmapHunter.forRequest;
import static com.hayukleung.app.widget.media.picasso.MemoryPolicy.NO_CACHE;
import static com.hayukleung.app.widget.media.picasso.MemoryPolicy.NO_STORE;
import static com.hayukleung.app.widget.media.picasso.MemoryPolicy.shouldReadFromMemoryCache;
import static com.hayukleung.app.widget.media.picasso.Picasso.LoadedFrom.MEMORY;
import static com.hayukleung.app.widget.media.picasso.Picasso.Priority;
import static com.hayukleung.app.widget.media.picasso.PicassoDrawable.setPlaceholder;
import static com.hayukleung.app.widget.media.picasso.Utils.OWNER_MAIN;
import static com.hayukleung.app.widget.media.picasso.Utils.VERB_CHANGED;
import static com.hayukleung.app.widget.media.picasso.Utils.VERB_COMPLETED;
import static com.hayukleung.app.widget.media.picasso.Utils.VERB_CREATED;
import static com.hayukleung.app.widget.media.picasso.Utils.checkMain;
import static com.hayukleung.app.widget.media.picasso.Utils.checkNotMain;
import static com.hayukleung.app.widget.media.picasso.Utils.createKey;
import static com.hayukleung.app.widget.media.picasso.Utils.log;

/**
 * Fluent API for building an image download request.
 */
@SuppressWarnings("UnusedDeclaration") // Public API.
public class RequestCreator {
    private static final AtomicInteger nextId = new AtomicInteger();

    private final com.hayukleung.app.widget.media.picasso.Picasso picasso;
    private final Request.Builder data;

    private boolean noFade;
    private boolean deferred;
    private boolean setPlaceholder = true;
    private int placeholderResId;
    private int errorResId;
    private int memoryPolicy;
    private Drawable placeholderDrawable;
    private Drawable errorDrawable;
    private Object tag;

    RequestCreator(Picasso picasso, Uri uri, int resourceId) {
        if (picasso.shutdown) {
            throw new IllegalStateException(
                    "Picasso instance already shut down. Cannot submit new requests.");
        }
        this.picasso = picasso;
        this.data = new Request.Builder(uri, resourceId);
    }

    /**
     * Explicitly opt-out to having a placeholder set when calling {@code into}.
     * <p/>
     * By default, Picasso will either set a supplied placeholder or clear the target
     * {@link ImageView} in order to ensure behavior in situations where views are recycled. This
     * method will prevent that behavior and retain any already set image.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator noPlaceholder() {
        if (placeholderResId != 0) {
            throw new IllegalStateException("Placeholder resource already set.");
        }
        if (placeholderDrawable != null) {
            throw new IllegalStateException("Placeholder image already set.");
        }
        setPlaceholder = false;
        return this;
    }

    /**
     * A placeholder drawable to be used while the image is being loaded. If the requested image is
     * not immediately available in the memory cache then this resource will be set on the target
     * {@link ImageView}.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator placeholder(int placeholderResId) {
        if (!setPlaceholder) {
            throw new IllegalStateException("Already explicitly declared as no placeholder.");
        }
        if (placeholderResId == 0) {
            throw new IllegalArgumentException("Placeholder image resource invalid.");
        }
        if (placeholderDrawable != null) {
            throw new IllegalStateException("Placeholder image already set.");
        }
        this.placeholderResId = placeholderResId;
        return this;
    }

    /**
     * A placeholder drawable to be used while the image is being loaded. If the requested image is
     * not immediately available in the memory cache then this resource will be set on the target
     * {@link ImageView}.
     * <p/>
     * If you are not using a placeholder image but want to clear an existing image (such as when
     * used in an {@link android.widget.Adapter adapter}), pass in {@code null}.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator placeholder(Drawable placeholderDrawable) {
        if (!setPlaceholder) {
            throw new IllegalStateException("Already explicitly declared as no placeholder.");
        }
        if (placeholderResId != 0) {
            throw new IllegalStateException("Placeholder image already set.");
        }
        this.placeholderDrawable = placeholderDrawable;
        return this;
    }

    /**
     * An error drawable to be used if the request image could not be loaded.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator error(int errorResId) {
        if (errorResId == 0) {
            throw new IllegalArgumentException("Error image resource invalid.");
        }
        if (errorDrawable != null) {
            throw new IllegalStateException("Error image already set.");
        }
        this.errorResId = errorResId;
        return this;
    }

    /**
     * An error drawable to be used if the request image could not be loaded.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator error(Drawable errorDrawable) {
        if (errorDrawable == null) {
            throw new IllegalArgumentException("Error image may not be null.");
        }
        if (errorResId != 0) {
            throw new IllegalStateException("Error image already set.");
        }
        this.errorDrawable = errorDrawable;
        return this;
    }

    /**
     * Assign a tag to this request. Tags are an easy way to logically associate
     * related requests that can be managed together e.g. paused, resumed,
     * or canceled.
     * <p/>
     * You can either use simple {@link String} tags or objects that naturally
     * define the scope of your requests within your app such as a
     * {@link android.content.Context}, an {@link android.app.Activity}, or a
     * {@link android.app.Fragment}.
     * <p/>
     * <strong>WARNING:</strong>: Picasso will keep a reference to the tag for
     * as long as this tag is paused and/or has active requests. Look out for
     * potential leaks.
     *
     * @see com.hayukleung.app.widget.media.picasso.Picasso#cancelTag(Object)
     * @see com.hayukleung.app.widget.media.picasso.Picasso#pauseTag(Object)
     * @see com.hayukleung.app.widget.media.picasso.Picasso#resumeTag(Object)
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator tag(Object tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag invalid.");
        }
        if (this.tag != null) {
            throw new IllegalStateException("Tag already set.");
        }
        this.tag = tag;
        return this;
    }

    /**
     * Attempt to resize the image to fit exactly into the target {@link ImageView}'s bounds. This
     * will result in delayed execution of the request until the {@link ImageView} has been laid out.
     * <p/>
     * <em>Note:</em> This method works only when your target is an {@link ImageView}.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator fit() {
        deferred = true;
        return this;
    }

    /**
     * Internal use only. Used by {@link com.hayukleung.app.widget.media.picasso.DeferredRequestCreator}.
     */
    com.hayukleung.app.widget.media.picasso.RequestCreator unfit() {
        deferred = false;
        return this;
    }

    /**
     * Resize the image to the specified dimension size.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator resizeDimen(int targetWidthResId, int targetHeightResId) {
        Resources resources = picasso.context.getResources();
        int targetWidth = resources.getDimensionPixelSize(targetWidthResId);
        int targetHeight = resources.getDimensionPixelSize(targetHeightResId);
        return resize(targetWidth, targetHeight);
    }

    /**
     * Resize the image to the specified size in pixels.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator resize(int targetWidth, int targetHeight) {
        data.resize(targetWidth, targetHeight);
        return this;
    }

    /**
     * Crops an image inside of the bounds specified by {@link #resize(int, int)} rather than
     * distorting the aspect ratio. This cropping technique scales the image so that it fills the
     * requested bounds and then crops the extra.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator centerCrop() {
        data.centerCrop();
        return this;
    }

    /**
     * Centers an image inside of the bounds specified by {@link #resize(int, int)}. This scales
     * the image so that both dimensions are equal to or less than the requested bounds.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator centerInside() {
        data.centerInside();
        return this;
    }

    /**
     * Only resize an image if the original image size is bigger than the target size
     * specified by {@link #resize(int, int)}.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator onlyScaleDown() {
        data.onlyScaleDown();
        return this;
    }

    public com.hayukleung.app.widget.media.picasso.RequestCreator maxSize(int maxSize) {
        data.maxSize(maxSize);
        return this;
    }

    /**
     * Just thumbnail
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator thumbnail() {
        data.thumbnail();
        return this;
    }

    /**
     * Rotate the image by the specified degrees.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator rotate(float degrees) {
        data.rotate(degrees);
        return this;
    }

    /**
     * Rotate the image by the specified degrees around a pivot point.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator rotate(float degrees, float pivotX, float pivotY) {
        data.rotate(degrees, pivotX, pivotY);
        return this;
    }

    /**
     * Attempt to decode the image using the specified config.
     * <p/>
     * Note: This value may be ignored by {@link android.graphics.BitmapFactory}. See
     * {@link android.graphics.BitmapFactory.Options#inPreferredConfig its documentation} for more details.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator config(Bitmap.Config config) {
        data.config(config);
        return this;
    }

    /**
     * Sets the stable key for this request to be used instead of the URI or resource ID when
     * caching. Two requests with the same value are considered to be for the same resource.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator stableKey(String stableKey) {
        data.stableKey(stableKey);
        return this;
    }

    /**
     * Set the priority of this request.
     * <p/>
     * This will affect the order in which the requests execute but does not guarantee it.
     * By default, all requests have {@link com.hayukleung.app.widget.media.picasso.Picasso.Priority#NORMAL} priority, except for
     * {@link #fetch()} requests, which have {@link com.hayukleung.app.widget.media.picasso.Picasso.Priority#LOW} priority by default.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator priority(Priority priority) {
        data.priority(priority);
        return this;
    }

    /**
     * Add a custom transformation to be applied to the image.
     * <p/>
     * Custom transformations will always be run after the built-in transformations.
     */
    // TODO show example of calling resize after a transform in the javadoc
    public com.hayukleung.app.widget.media.picasso.RequestCreator transform(Transformation transformation) {
        data.transform(transformation);
        return this;
    }

    /**
     * @deprecated Use {@link #memoryPolicy(com.hayukleung.app.widget.media.picasso.MemoryPolicy, com.hayukleung.app.widget.media.picasso.MemoryPolicy...)} instead.
     */
    @Deprecated
    public com.hayukleung.app.widget.media.picasso.RequestCreator skipMemoryCache() {
        return memoryPolicy(NO_CACHE, NO_STORE);
    }

    /**
     * Specifies the {@link com.hayukleung.app.widget.media.picasso.MemoryPolicy} to use for this request. You may specify additional policy
     * options using the varargs parameter.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator memoryPolicy(MemoryPolicy policy, MemoryPolicy... additional) {
        if (policy == null) {
            throw new IllegalArgumentException("Memory policy cannot be null.");
        }
        this.memoryPolicy |= policy.index;
        if (additional == null) {
            throw new IllegalArgumentException("Memory policy cannot be null.");
        }
        if (additional.length > 0) {
            for (MemoryPolicy memoryPolicy : additional) {
                if (memoryPolicy == null) {
                    throw new IllegalArgumentException("Memory policy cannot be null.");
                }
                this.memoryPolicy |= memoryPolicy.index;
            }
        }
        return this;
    }

    /**
     * Disable brief fade in of images loaded from the disk cache or network.
     */
    public com.hayukleung.app.widget.media.picasso.RequestCreator noFade() {
        noFade = true;
        return this;
    }

    /**
     * Synchronously fulfill this request. Must not be called from the main thread.
     * <p/>
     * <em>Note</em>: The result of this operation is not cached in memory because the underlying
     * {@link com.hayukleung.app.widget.media.picasso.Cache} implementation is not guaranteed to be thread-safe.
     */
    public IImage get() throws IOException {
        long started = System.nanoTime();
        checkNotMain();

        if (deferred) {
            throw new IllegalStateException("Fit cannot be used with get.");
        }
        if (!data.hasImage()) {
            return null;
        }

        Request finalData = createRequest(started);
        String key = createKey(finalData, new StringBuilder());

        com.hayukleung.app.widget.media.picasso.Action action = new com.hayukleung.app.widget.media.picasso.GetAction(picasso, finalData, memoryPolicy, key, tag);
        return forRequest(picasso, picasso.dispatcher, picasso.cache, picasso.diskCache, picasso.stats, action).hunt();
    }

    /**
     * Asynchronously fulfills the request without a {@link ImageView} or {@link Target}. This is
     * useful when you want to warm up the cache with an image.
     * <p/>
     * <em>Note:</em> It is safe to invoke this method from any thread.
     */
    public void fetch() {
        fetch(null);
    }

    /**
     * Asynchronously fulfills the request without a {@link ImageView} or {@link Target},
     * and invokes the target {@link Callback} with the result. This is useful when you want to warm
     * up the cache with an image.
     * <p/>
     * <em>Note:</em> The {@link Callback} param is a strong reference and will prevent your
     * {@link android.app.Activity} or {@link android.app.Fragment} from being garbage collected
     * until the request is completed.
     */
    public void fetch(Callback callback) {
        long started = System.nanoTime();

        if (deferred) {
            throw new IllegalStateException("Fit cannot be used with fetch.");
        }
        if (data.hasImage()) {
            // Fetch requests have lower priority by default.
            if (!data.hasPriority()) {
                data.priority(Priority.LOW);
            }

            Request request = createRequest(started);
            String key = createKey(request, new StringBuilder());

            if (shouldReadFromMemoryCache(memoryPolicy)) {
                IImage image = picasso.quickMemoryCacheCheck(key);
                if (image != null) {
                    if (picasso.loggingEnabled) {
                        log(OWNER_MAIN, VERB_COMPLETED, request.plainId(), "from " + MEMORY);
                    }
                    if (callback != null) {
                        callback.onSuccess();
                    }
                    return;
                }
            }

            com.hayukleung.app.widget.media.picasso.Action action = new com.hayukleung.app.widget.media.picasso.FetchAction(picasso, request, memoryPolicy, key, tag, callback);
            picasso.submit(action);
        }
    }

    /**
     * Asynchronously fulfills the request into the specified {@link com.hayukleung.app.widget.media.picasso.Target}. In most cases, you
     * should use this when you are dealing with a custom {@link android.view.View View} or view
     * holder which should implement the {@link com.hayukleung.app.widget.media.picasso.Target} interface.
     * <p/>
     * Implementing on a {@link android.view.View View}:
     * <blockquote><pre>
     * public class ProfileView extends FrameLayout implements Target {
     *   {@literal @}Override public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
     *     setBackgroundDrawable(new BitmapDrawable(bitmap));
     *   }
     * <p/>
     *   {@literal @}Override public void onBitmapFailed() {
     *     setBackgroundResource(R.drawable.profile_error);
     *   }
     * <p/>
     *   {@literal @}Override public void onPrepareLoad(Drawable placeHolderDrawable) {
     *     frame.setBackgroundDrawable(placeHolderDrawable);
     *   }
     * }
     * </pre></blockquote>
     * Implementing on a view holder object for use inside of an adapter:
     * <blockquote><pre>
     * public class ViewHolder implements Target {
     *   public FrameLayout frame;
     *   public TextView name;
     * <p/>
     *   {@literal @}Override public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
     *     frame.setBackgroundDrawable(new BitmapDrawable(bitmap));
     *   }
     * <p/>
     *   {@literal @}Override public void onBitmapFailed() {
     *     frame.setBackgroundResource(R.drawable.profile_error);
     *   }
     * <p/>
     *   {@literal @}Override public void onPrepareLoad(Drawable placeHolderDrawable) {
     *     frame.setBackgroundDrawable(placeHolderDrawable);
     *   }
     * }
     * </pre></blockquote>
     * <p/>
     * <em>Note:</em> This method keeps a weak reference to the {@link com.hayukleung.app.widget.media.picasso.Target} instance and will be
     * garbage collected if you do not keep a strong reference to it. To receive callbacks when an
     * image is loaded use {@link #into(ImageView, com.hayukleung.app.widget.media.picasso.Callback)}.
     */
    public void into(Target target) {
        long started = System.nanoTime();
        checkMain();

        if (target == null) {
            throw new IllegalArgumentException("Target must not be null.");
        }
        if (deferred) {
            throw new IllegalStateException("Fit cannot be used with a Target.");
        }

        if (!data.hasImage()) {
            picasso.cancelRequest(target);
            target.onPrepareLoad(setPlaceholder ? getPlaceholderDrawable() : null);
            return;
        }

        Request request = createRequest(started);
        String requestKey = createKey(request);

        if (shouldReadFromMemoryCache(memoryPolicy)) {
            IImage image = picasso.quickMemoryCacheCheck(requestKey);
            if (image != null) {
                picasso.cancelRequest(target);
                target.onBitmapLoaded(image, MEMORY);
                return;
            }
        }

        target.onPrepareLoad(setPlaceholder ? getPlaceholderDrawable() : null);

        com.hayukleung.app.widget.media.picasso.Action action =
                new com.hayukleung.app.widget.media.picasso.TargetAction(picasso, target, request, memoryPolicy, errorResId, errorDrawable,
                        requestKey, tag);
        picasso.enqueueAndSubmit(action);
    }

    /**
     * Asynchronously fulfills the request into the specified {@link RemoteViews} object with the
     * given {@code viewId}. This is used for loading bitmaps into a {@link Notification}.
     */
    public void into(RemoteViews remoteViews, int viewId, int notificationId,
                     Notification notification) {
        long started = System.nanoTime();

        if (remoteViews == null) {
            throw new IllegalArgumentException("RemoteViews must not be null.");
        }
        if (notification == null) {
            throw new IllegalArgumentException("Notification must not be null.");
        }
        if (deferred) {
            throw new IllegalStateException("Fit cannot be used with RemoteViews.");
        }
        if (placeholderDrawable != null || placeholderResId != 0 || errorDrawable != null) {
            throw new IllegalArgumentException(
                    "Cannot use placeholder or error drawables with remote views.");
        }

        Request request = createRequest(started);
        String key = createKey(request, new StringBuilder()); // Non-main thread needs own builder.

        com.hayukleung.app.widget.media.picasso.RemoteViewsAction action =
                new com.hayukleung.app.widget.media.picasso.RemoteViewsAction.NotificationAction(picasso, request, remoteViews, viewId, notificationId, notification,
                        memoryPolicy, errorResId, key, tag);

        performRemoteViewInto(action);
    }

    /**
     * Asynchronously fulfills the request into the specified {@link RemoteViews} object with the
     * given {@code viewId}. This is used for loading bitmaps into all instances of a widget.
     */
    public void into(RemoteViews remoteViews, int viewId, int[] appWidgetIds) {
        long started = System.nanoTime();

        if (remoteViews == null) {
            throw new IllegalArgumentException("remoteViews must not be null.");
        }
        if (appWidgetIds == null) {
            throw new IllegalArgumentException("appWidgetIds must not be null.");
        }
        if (deferred) {
            throw new IllegalStateException("Fit cannot be used with remote views.");
        }
        if (placeholderDrawable != null || placeholderResId != 0 || errorDrawable != null) {
            throw new IllegalArgumentException(
                    "Cannot use placeholder or error drawables with remote views.");
        }

        Request request = createRequest(started);
        String key = createKey(request, new StringBuilder()); // Non-main thread needs own builder.

        com.hayukleung.app.widget.media.picasso.RemoteViewsAction action =
                new com.hayukleung.app.widget.media.picasso.RemoteViewsAction.AppWidgetAction(picasso, request, remoteViews, viewId, appWidgetIds, memoryPolicy,
                        errorResId, key, tag);

        performRemoteViewInto(action);
    }

    /**
     * Asynchronously fulfills the request into the specified {@link ImageView}.
     * <p/>
     * <em>Note:</em> This method keeps a weak reference to the {@link ImageView} instance and will
     * automatically support object recycling.
     */
    public void into(ImageView target) {
        into(target, null);
    }

    /**
     * Asynchronously fulfills the request into the specified {@link ImageView} and invokes the
     * target {@link com.hayukleung.app.widget.media.picasso.Callback} if it's not {@code null}.
     * <p/>
     * <em>Note:</em> The {@link com.hayukleung.app.widget.media.picasso.Callback} param is a strong reference and will prevent your
     * {@link android.app.Activity} or {@link android.app.Fragment} from being garbage collected. If
     * you use this method, it is <b>strongly</b> recommended you invoke an adjacent
     * {@link com.hayukleung.app.widget.media.picasso.Picasso#cancelRequest(ImageView)} call to prevent temporary leaking.
     */
    public void into(ImageView target, Callback callback) {
        long started = System.nanoTime();
        checkMain();

        if (target == null) {
            throw new IllegalArgumentException("Target must not be null.");
        }

        if (!data.hasImage()) {
            picasso.cancelRequest(target);
            if (setPlaceholder) {
                setPlaceholder(target, getPlaceholderDrawable());
            }
            return;
        }

        if (deferred) {
            if (data.hasSize()) {
                throw new IllegalStateException("Fit cannot be used with resize.");
            }
            if (setPlaceholder) {
                setPlaceholder(target, getPlaceholderDrawable());
            }
            picasso.defer(target, new com.hayukleung.app.widget.media.picasso.DeferredRequestCreator(this, target, callback));
            return;
        }

        Request request = createRequest(started);
        String requestKey = createKey(request);

        if (shouldReadFromMemoryCache(memoryPolicy)) {
            IImage image = picasso.quickMemoryCacheCheck(requestKey);
            if (image != null) {
                picasso.cancelRequest(target);
                image.setImage(target, picasso.context, MEMORY, noFade, picasso.indicatorsEnabled);
                if (picasso.loggingEnabled) {
                    log(OWNER_MAIN, VERB_COMPLETED, request.plainId(), "from " + MEMORY);
                }
                if (callback != null) {
                    callback.onSuccess();
                }
                return;
            }
        }

        if (setPlaceholder) {
            setPlaceholder(target, getPlaceholderDrawable());
        }

        com.hayukleung.app.widget.media.picasso.Action action =
                new com.hayukleung.app.widget.media.picasso.ImageViewAction(picasso, target, request, memoryPolicy, noFade, errorResId,
                        errorDrawable, requestKey, tag, callback);

        picasso.enqueueAndSubmit(action);
    }

    private Drawable getPlaceholderDrawable() {
        if (placeholderResId != 0) {
            return picasso.context.getResources().getDrawable(placeholderResId);
        } else {
            return placeholderDrawable; // This may be null which is expected and desired behavior.
        }
    }

    /**
     * Create the request optionally passing it through the request transformer.
     */
    private Request createRequest(long started) {
        int id = nextId.getAndIncrement();

        Request request = data.build();
        request.id = id;
        request.started = started;

        boolean loggingEnabled = picasso.loggingEnabled;
        if (loggingEnabled) {
            log(OWNER_MAIN, VERB_CREATED, request.plainId(), request.toString());
        }

        Request transformed = picasso.transformRequest(request);
        if (transformed != request) {
            // If the request was changed, copy over the id and timestamp from the original.
            transformed.id = id;
            transformed.started = started;

            if (loggingEnabled) {
                log(OWNER_MAIN, VERB_CHANGED, transformed.logId(), "into " + transformed);
            }
        }

        return transformed;
    }

    private void performRemoteViewInto(com.hayukleung.app.widget.media.picasso.RemoteViewsAction action) {
        if (shouldReadFromMemoryCache(memoryPolicy)) {
            IImage image = picasso.quickMemoryCacheCheck(action.getKey());
            if (image != null) {
                action.complete(image, MEMORY);
                return;
            }
        }

        if (placeholderResId != 0) {
            action.setImageResource(placeholderResId);
        }

        picasso.enqueueAndSubmit(action);
    }
}
