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
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.hayukleung.app.widget.media.picasso.Cache;
import com.hayukleung.app.widget.media.picasso.Downloader;
import com.hayukleung.app.widget.media.picasso.LruCache;
import com.hayukleung.app.widget.media.picasso.RequestHandler;
import com.hayukleung.app.widget.media.picasso.StatsSnapshot;
import com.hayukleung.app.widget.media.picasso.Target;

import java.io.File;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;

import static android.content.ContentResolver.SCHEME_FILE;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static com.hayukleung.app.widget.media.picasso.Dispatcher.HUNTER_BATCH_COMPLETE;
import static com.hayukleung.app.widget.media.picasso.Dispatcher.HUNTER_UPDATE;
import static com.hayukleung.app.widget.media.picasso.Dispatcher.REQUEST_BATCH_RESUME;
import static com.hayukleung.app.widget.media.picasso.Dispatcher.REQUEST_GCED;
import static com.hayukleung.app.widget.media.picasso.MemoryPolicy.shouldReadFromMemoryCache;
import static com.hayukleung.app.widget.media.picasso.Picasso.LoadedFrom.DISK;
import static com.hayukleung.app.widget.media.picasso.Picasso.LoadedFrom.MEMORY;
import static com.hayukleung.app.widget.media.picasso.Utils.OWNER_MAIN;
import static com.hayukleung.app.widget.media.picasso.Utils.THREAD_PREFIX;
import static com.hayukleung.app.widget.media.picasso.Utils.VERB_CANCELED;
import static com.hayukleung.app.widget.media.picasso.Utils.VERB_COMPLETED;
import static com.hayukleung.app.widget.media.picasso.Utils.VERB_ERRORED;
import static com.hayukleung.app.widget.media.picasso.Utils.VERB_RESUMED;
import static com.hayukleung.app.widget.media.picasso.Utils.checkMain;
import static com.hayukleung.app.widget.media.picasso.Utils.log;

/**
 * Image downloading, transformation, and caching manager.
 * <p/>
 * Use {@link #with(Context)} for the global singleton instance or construct your
 * own instance with {@link com.hayukleung.app.widget.media.picasso.Picasso.Builder}.
 */
public class Picasso {
    public static final String SCHEME_APK = "android_apk";
    public static final String SCHEME_ASSET = "android_asset";

    public static Uri apkUri(String path) {
        return apkUri(new File(path));
    }

    public static Uri apkUri(File file) {
        return Uri.parse(Uri.fromFile(file).toString().replaceFirst(SCHEME_FILE, SCHEME_APK));
    }

    public static Uri assetUri(String path) {
        return assetUri(new File(path));
    }

    public static Uri assetUri(File file) {
        return Uri.parse(Uri.fromFile(file).toString().replaceFirst(SCHEME_FILE, SCHEME_ASSET));
    }

    static final String TAG = "Picasso";
    volatile boolean loggingEnabled;
    static com.hayukleung.app.widget.media.picasso.Picasso singleton = null;
    final Context context;
    final Dispatcher dispatcher;
    final Cache cache;
    final LruDiskCache diskCache;
    final com.hayukleung.app.widget.media.picasso.Stats stats;
    final Map<Object, com.hayukleung.app.widget.media.picasso.Action> targetToAction;
    final Map<ImageView, com.hayukleung.app.widget.media.picasso.DeferredRequestCreator> targetToDeferredRequestCreator;
    final ReferenceQueue<Object> referenceQueue;
    private final Listener listener;
    private final RequestTransformer requestTransformer;
    private final CleanupThread cleanupThread;
    private final List<RequestHandler> requestHandlers;
    boolean indicatorsEnabled;
    boolean denyNetworkDownload;
    final ResourceNetworkRequestHandler resourceNetworkRequestHandler;

    static final Handler HANDLER = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HUNTER_UPDATE: {
                    Set<BitmapHunter> batch = (Set<BitmapHunter>) msg.obj;
                    int progress = msg.arg1;
                    for (BitmapHunter hunter : batch) {
                        hunter.picasso.progress(hunter, progress);
                    }
                    break;
                }
                case HUNTER_BATCH_COMPLETE: {
                    @SuppressWarnings("unchecked") List<BitmapHunter> batch = (List<BitmapHunter>) msg.obj;
                    //noinspection ForLoopReplaceableByForEach
                    for (int i = 0, n = batch.size(); i < n; i++) {
                        BitmapHunter hunter = batch.get(i);
                        hunter.picasso.complete(hunter);
                    }
                    break;
                }
                case REQUEST_GCED: {
                    com.hayukleung.app.widget.media.picasso.Action action = (com.hayukleung.app.widget.media.picasso.Action) msg.obj;
                    if (action.getPicasso().loggingEnabled) {
                        log(OWNER_MAIN, VERB_CANCELED, action.request.logId(), "target got garbage collected");
                    }
                    action.picasso.cancelExistingRequest(action.getTarget());
                    break;
                }
                case REQUEST_BATCH_RESUME:
                    @SuppressWarnings("unchecked") List<com.hayukleung.app.widget.media.picasso.Action> batch = (List<com.hayukleung.app.widget.media.picasso.Action>) msg.obj;
                    //noinspection ForLoopReplaceableByForEach
                    for (int i = 0, n = batch.size(); i < n; i++) {
                        com.hayukleung.app.widget.media.picasso.Action action = batch.get(i);
                        action.picasso.resumeAction(action);
                    }
                    break;
                default:
                    throw new AssertionError("Unknown handler message received: " + msg.what);
            }
        }
    };
    boolean shutdown;

    Picasso(Context context, Dispatcher dispatcher, Cache cache, LruDiskCache diskCache, Listener listener,
            RequestTransformer requestTransformer, List<RequestHandler> extraRequestHandlers,
            com.hayukleung.app.widget.media.picasso.Stats stats, boolean indicatorsEnabled, boolean loggingEnabled) {
        this.context = context;
        this.dispatcher = dispatcher;
        this.cache = cache;
        this.diskCache = diskCache;
        this.listener = listener;
        this.requestTransformer = requestTransformer;
        this.resourceNetworkRequestHandler = new ResourceNetworkRequestHandler(dispatcher.downloader, diskCache, stats);

        int builtInHandlers = 8; // Adjust this as internal handlers are added or removed.
        int extraCount = (extraRequestHandlers != null ? extraRequestHandlers.size() : 0);
        List<RequestHandler> allRequestHandlers =
                new ArrayList<RequestHandler>(builtInHandlers + extraCount);

        // ResRequestHandler needs to be the first in the list to avoid
        // forcing other RequestHandlers to perform null checks on request.uri
        // to cover the (request.resourceId != 0) case.
        allRequestHandlers.add(new ResRequestHandler(context));
        if (extraRequestHandlers != null) {
            allRequestHandlers.addAll(extraRequestHandlers);
        }
        allRequestHandlers.add(new com.hayukleung.app.widget.media.picasso.ContactsPhotoRequestHandler(context));
        allRequestHandlers.add(new com.hayukleung.app.widget.media.picasso.MediaStoreRequestHandler(context));
        allRequestHandlers.add(new com.hayukleung.app.widget.media.picasso.ContentStreamRequestHandler(context));
        allRequestHandlers.add(new com.hayukleung.app.widget.media.picasso.AssetRequestHandler(context));
        allRequestHandlers.add(new com.hayukleung.app.widget.media.picasso.FileRequestHandler(context));
        allRequestHandlers.add(new com.hayukleung.app.widget.media.picasso.NetworkRequestHandler(dispatcher.downloader, diskCache, stats));
        allRequestHandlers.add(new ApkRequestHandler(context));
        requestHandlers = Collections.unmodifiableList(allRequestHandlers);

        this.stats = stats;
        this.targetToAction = new WeakHashMap<Object, com.hayukleung.app.widget.media.picasso.Action>();
        this.targetToDeferredRequestCreator = new WeakHashMap<ImageView, com.hayukleung.app.widget.media.picasso.DeferredRequestCreator>();
        this.indicatorsEnabled = indicatorsEnabled;
        this.loggingEnabled = loggingEnabled;
        this.referenceQueue = new ReferenceQueue<Object>();
        this.cleanupThread = new CleanupThread(referenceQueue, HANDLER);
        this.cleanupThread.start();
    }

    /**
     * The global default {@link com.hayukleung.app.widget.media.picasso.Picasso} instance.
     * <p/>
     * This instance is automatically initialized with defaults that are suitable to most
     * implementations.
     * <ul>
     * <li>LRU memory cache of 15% the available application RAM</li>
     * <li>Disk cache of 2% storage space up to 50MB but no less than 5MB. (Note: this is only
     * available on API 14+ <em>or</em> if you are using a standalone library that provides a disk
     * cache on all API levels like OkHttp)</li>
     * <li>Three download threads for disk and network access.</li>
     * </ul>
     * <p/>
     * If these settings do not meet the requirements of your application you can construct your own
     * with full control over the configuration by using {@link com.hayukleung.app.widget.media.picasso.Picasso.Builder} to create a
     * {@link com.hayukleung.app.widget.media.picasso.Picasso} instance. You can either use this directly or by setting it as the global
     * instance with {@link #setSingletonInstance}.
     */
    public static com.hayukleung.app.widget.media.picasso.Picasso with(Context context) {
        if (singleton == null) {
            synchronized (com.hayukleung.app.widget.media.picasso.Picasso.class) {
                if (singleton == null) {
                    singleton = new Builder(context).build();
                }
            }
        }
        return singleton;
    }

    /**
     * Set the global instance returned from {@link #with}.
     * <p/>
     * This method must be called before any calls to {@link #with} and may only be called once.
     */
    public static void setSingletonInstance(com.hayukleung.app.widget.media.picasso.Picasso picasso) {
        synchronized (com.hayukleung.app.widget.media.picasso.Picasso.class) {
            if (singleton != null) {
                throw new IllegalStateException("Singleton instance already exists.");
            }
            singleton = picasso;
        }
    }

    /**
     * Cancel any existing requests for the specified target {@link ImageView}.
     */
    public void cancelRequest(ImageView view) {
        cancelExistingRequest(view);
    }

    /**
     * Cancel any existing requests for the specified {@link com.hayukleung.app.widget.media.picasso.Target} instance.
     */
    public void cancelRequest(Target target) {
        cancelExistingRequest(target);
    }

    /**
     * Cancel any existing requests for the specified {@link RemoteViews} target with the given {@code
     * viewId}.
     */
    public void cancelRequest(RemoteViews remoteViews, int viewId) {
        cancelExistingRequest(new RemoteViewsAction.RemoteViewsTarget(remoteViews, viewId));
    }

    /**
     * Cancel any existing requests with given tag. You can set a tag
     * on new requests with {@link RequestCreator#tag(Object)}.
     *
     * @see RequestCreator#tag(Object)
     */
    public void cancelTag(Object tag) {
        checkMain();
        List<com.hayukleung.app.widget.media.picasso.Action> actions = new ArrayList<com.hayukleung.app.widget.media.picasso.Action>(targetToAction.values());
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, n = actions.size(); i < n; i++) {
            com.hayukleung.app.widget.media.picasso.Action action = actions.get(i);
            if (action.getTag().equals(tag)) {
                cancelExistingRequest(action.getTarget());
            }
        }
    }

    /**
     * Pause existing requests with the given tag. Use {@link #resumeTag(Object)}
     * to resume requests with the given tag.
     *
     * @see #resumeTag(Object)
     * @see RequestCreator#tag(Object)
     */
    public void pauseTag(Object tag) {
        dispatcher.dispatchPauseTag(tag);
    }

    /**
     * Resume paused requests with the given tag. Use {@link #pauseTag(Object)}
     * to pause requests with the given tag.
     *
     * @see #pauseTag(Object)
     * @see RequestCreator#tag(Object)
     */
    public void resumeTag(Object tag) {
        dispatcher.dispatchResumeTag(tag);
    }

    /**
     * Start an image request using the specified URI.
     * <p/>
     * Passing {@code null} as a {@code uri} will not trigger any request but will set a placeholder,
     * if one is specified.
     *
     * @see #load(File)
     * @see #load(String)
     * @see #load(int)
     */
    public RequestCreator load(Uri uri) {
        return new RequestCreator(this, uri, 0);
    }

    /**
     * Start an image request using the specified path. This is a convenience method for calling
     * {@link #load(Uri)}.
     * <p/>
     * This path may be a remote URL, file resource (prefixed with {@code file:}), content resource
     * (prefixed with {@code content:}), or android resource (prefixed with {@code
     * android.resource:}.
     * <p/>
     * Passing {@code null} as a {@code path} will not trigger any request but will set a
     * placeholder, if one is specified.
     *
     * @throws IllegalArgumentException if {@code path} is empty or blank string.
     * @see #load(Uri)
     * @see #load(File)
     * @see #load(int)
     */
    public RequestCreator load(String path) {
        if (path == null) {
            return new RequestCreator(this, null, 0);
        }
        if (path.trim().length() == 0) {
            throw new IllegalArgumentException("Path must not be empty.");
        }
        return load(Uri.parse(path));
    }

    /**
     * Start an image request using the specified image file. This is a convenience method for
     * calling {@link #load(Uri)}.
     * <p/>
     * Passing {@code null} as a {@code file} will not trigger any request but will set a
     * placeholder, if one is specified.
     * <p/>
     * Equivalent to calling {@link #load(Uri) load(Uri.fromFile(file))}.
     *
     * @see #load(Uri)
     * @see #load(String)
     * @see #load(int)
     */
    public RequestCreator load(File file) {
        if (file == null) {
            return new RequestCreator(this, null, 0);
        }
        return load(Uri.fromFile(file));
    }

    /**
     * Start an image request using the specified drawable resource ID.
     *
     * @see #load(Uri)
     * @see #load(String)
     * @see #load(File)
     */
    public RequestCreator load(int resourceId) {
        if (resourceId == 0) {
            throw new IllegalArgumentException("Resource ID must not be zero.");
        }
        return new RequestCreator(this, null, resourceId);
    }

    public void load(String url, ResourceTarget target) {
        load(url, target, null);
    }

    public void load(String url, ResourceTarget target, Object tag) {
        File file = diskCache.get(url);
        if (file != null) {
            target.onLoaded(file.getAbsolutePath(), DISK);
            return;
        }
        com.hayukleung.app.widget.media.picasso.Action action = new ResourceAction(this, target, url, tag);
        enqueueAndSubmit(action);
    }

    /**
     * Invalidate all memory cached images for the specified {@code uri}.
     *
     * @see #invalidate(String)
     * @see #invalidate(File)
     */
    public void invalidate(Uri uri) {
        if (uri == null) {
            throw new IllegalArgumentException("uri == null");
        }
        cache.clearKeyUri(uri.toString());
    }

    /**
     * Invalidate all memory cached images for the specified {@code path}. You can also pass a
     * {@linkplain RequestCreator#stableKey stable key}.
     *
     * @see #invalidate(Uri)
     * @see #invalidate(File)
     */
    public void invalidate(String path) {
        if (path == null) {
            throw new IllegalArgumentException("path == null");
        }
        invalidate(Uri.parse(path));
    }

    /**
     * Invalidate all memory cached images for the specified {@code file}.
     *
     * @see #invalidate(Uri)
     * @see #invalidate(String)
     */
    public void invalidate(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file == null");
        }
        invalidate(Uri.fromFile(file));
    }

    /**
     * {@code true} if debug display, logging, and statistics are enabled.
     * <p/>
     *
     * @deprecated Use {@link #areIndicatorsEnabled()} and {@link #isLoggingEnabled()} instead.
     */
    @SuppressWarnings("UnusedDeclaration")
    @Deprecated
    public boolean isDebugging() {
        return areIndicatorsEnabled() && isLoggingEnabled();
    }

    /**
     * Toggle whether debug display, logging, and statistics are enabled.
     * <p/>
     *
     * @deprecated Use {@link #setIndicatorsEnabled(boolean)} and {@link #setLoggingEnabled(boolean)}
     * instead.
     */
    @SuppressWarnings("UnusedDeclaration")
    @Deprecated
    public void setDebugging(boolean debugging) {
        setIndicatorsEnabled(debugging);
    }

    /**
     * Toggle whether to display debug indicators on images.
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setIndicatorsEnabled(boolean enabled) {
        indicatorsEnabled = enabled;
    }

    /**
     * {@code true} if debug indicators should are displayed on images.
     */
    @SuppressWarnings("UnusedDeclaration")
    public boolean areIndicatorsEnabled() {
        return indicatorsEnabled;
    }

    /**
     * {@code true} if debug logging is enabled.
     */
    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    /**
     * Toggle whether debug logging is enabled.
     * <p/>
     * <b>WARNING:</b> Enabling this will result in excessive object allocation. This should be only
     * be used for debugging Picasso behavior. Do NOT pass {@code BuildConfig.DEBUG}.
     */
    @SuppressWarnings("UnusedDeclaration") // Public API.
    public void setLoggingEnabled(boolean enabled) {
        loggingEnabled = enabled;
    }

    /**
     * Creates a {@link StatsSnapshot} of the current stats for this instance.
     * <p/>
     * <b>NOTE:</b> The snapshot may not always be completely up-to-date if requests are still in
     * progress.
     */
    @SuppressWarnings("UnusedDeclaration")
    public StatsSnapshot getSnapshot() {
        return stats.createSnapshot();
    }

    /**
     * Stops this instance from accepting further requests.
     */
    public void shutdown() {
        if (this == singleton) {
            throw new UnsupportedOperationException("Default singleton instance cannot be shutdown.");
        }
        if (shutdown) {
            return;
        }
        cache.clear();
        diskCache.close();
        cleanupThread.shutdown();
        stats.shutdown();
        dispatcher.shutdown();
        for (com.hayukleung.app.widget.media.picasso.DeferredRequestCreator deferredRequestCreator : targetToDeferredRequestCreator.values()) {
            deferredRequestCreator.cancel();
        }
        targetToDeferredRequestCreator.clear();
        shutdown = true;
    }

    List<RequestHandler> getRequestHandlers() {
        return requestHandlers;
    }

    Request transformRequest(Request request) {
        Request transformed = requestTransformer.transformRequest(request);
        if (transformed == null) {
            throw new IllegalStateException("Request transformer "
                    + requestTransformer.getClass().getCanonicalName()
                    + " returned null for "
                    + request);
        }
        return transformed;
    }

    void defer(ImageView view, com.hayukleung.app.widget.media.picasso.DeferredRequestCreator request) {
        targetToDeferredRequestCreator.put(view, request);
    }

    void enqueueAndSubmit(com.hayukleung.app.widget.media.picasso.Action action) {
        Object target = action.getTarget();
        if (target != null && targetToAction.get(target) != action) {
            // This will also check we are on the main thread.
            cancelExistingRequest(target);
            targetToAction.put(target, action);
        }
        submit(action);
    }

    void submit(com.hayukleung.app.widget.media.picasso.Action action) {
        dispatcher.dispatchSubmit(action);
    }

    IImage quickMemoryCacheCheck(String key) {
        IImage cached = cache.get(key);
        if (cached != null) {
            stats.dispatchCacheHit();
        } else {
            stats.dispatchCacheMiss();
        }
        return cached;
    }

    void progress(BitmapHunter hunter, int progress) {
        com.hayukleung.app.widget.media.picasso.Action single = hunter.getAction();
        List<com.hayukleung.app.widget.media.picasso.Action> joined = hunter.getActions();

        boolean hasMultiple = joined != null && !joined.isEmpty();
        boolean shouldDeliver = single != null || hasMultiple;

        if (!shouldDeliver) {
            return;
        }

        if (single != null) {
            updateAction(progress, single);
        }

        if (hasMultiple) {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, n = joined.size(); i < n; i++) {
                com.hayukleung.app.widget.media.picasso.Action join = joined.get(i);
                updateAction(progress, join);
            }
        }
    }

    void complete(BitmapHunter hunter) {
        com.hayukleung.app.widget.media.picasso.Action single = hunter.getAction();
        List<com.hayukleung.app.widget.media.picasso.Action> joined = hunter.getActions();

        boolean hasMultiple = joined != null && !joined.isEmpty();
        boolean shouldDeliver = single != null || hasMultiple;

        if (!shouldDeliver) {
            return;
        }

        Uri uri = hunter.getData().uri;
        Exception exception = hunter.getException();
        IImage result = hunter.getResult();
        LoadedFrom from = hunter.getLoadedFrom();

        if (single != null) {
            deliverAction(result, from, single);
        }

        if (hasMultiple) {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, n = joined.size(); i < n; i++) {
                com.hayukleung.app.widget.media.picasso.Action join = joined.get(i);
                deliverAction(result, from, join);
            }
        }

        if (listener != null && exception != null) {
            listener.onImageLoadFailed(this, uri, exception);
        }
    }

    void resumeAction(com.hayukleung.app.widget.media.picasso.Action action) {
        IImage image = null;
        if (shouldReadFromMemoryCache(action.memoryPolicy)) {
            image = quickMemoryCacheCheck(action.getKey());
        }

        if (image != null) {
            // Resumed action is cached, complete immediately.
            deliverAction(image, MEMORY, action);
            if (loggingEnabled) {
                log(OWNER_MAIN, VERB_COMPLETED, action.request.logId(), "from " + MEMORY);
            }
        } else {
            // Re-submit the action to the executor.
            enqueueAndSubmit(action);
            if (loggingEnabled) {
                log(OWNER_MAIN, VERB_RESUMED, action.request.logId());
            }
        }
    }

    private void updateAction(int progress, com.hayukleung.app.widget.media.picasso.Action action) {
        if (action.isCancelled()) {
            return;
        }
        action.progress(progress);
    }

    private void deliverAction(IImage result, LoadedFrom from, com.hayukleung.app.widget.media.picasso.Action action) {
        if (action.isCancelled()) {
            return;
        }
        if (!action.willReplay()) {
            targetToAction.remove(action.getTarget());
        }
        if (result != null) {
            if (from == null) {
                throw new AssertionError("LoadedFrom cannot be null.");
            }
            action.complete(result, from);
            if (loggingEnabled) {
                log(OWNER_MAIN, VERB_COMPLETED, action.request.logId(), "from " + from);
            }
        } else {
            action.error();
            if (loggingEnabled) {
                log(OWNER_MAIN, VERB_ERRORED, action.request.logId());
            }
        }
    }

    private void cancelExistingRequest(Object target) {
        checkMain();
        com.hayukleung.app.widget.media.picasso.Action action = targetToAction.remove(target);
        if (action != null) {
            action.cancel();
            dispatcher.dispatchCancel(action);
        }
        if (target instanceof ImageView) {
            ImageView targetImageView = (ImageView) target;
            com.hayukleung.app.widget.media.picasso.DeferredRequestCreator deferredRequestCreator =
                    targetToDeferredRequestCreator.remove(targetImageView);
            if (deferredRequestCreator != null) {
                deferredRequestCreator.cancel();
            }
        }
    }

    public boolean denyNetworkDownload(boolean deny) {
        return denyNetworkDownload = deny;
    }

    public boolean denyNetworkDownload() {
        return denyNetworkDownload;
    }

    public LruDiskCache getDiskCache() {
        return diskCache;
    }

    /**
     * The priority of a request.
     *
     * @see RequestCreator#priority(com.hayukleung.app.widget.media.picasso.Picasso.Priority)
     */
    public enum Priority {
        LOW,
        NORMAL,
        HIGH
    }

    /**
     * Describes where the image was loaded from.
     */
    public enum LoadedFrom {
        MEMORY(Color.GREEN),
        DISK(Color.BLUE),
        NETWORK(Color.RED);

        final int debugColor;

        private LoadedFrom(int debugColor) {
            this.debugColor = debugColor;
        }
    }

    /**
     * Callbacks for Picasso events.
     */
    public interface Listener {
        /**
         * Invoked when an image has failed to load. This is useful for reporting image failures to a
         * remote analytics service, for example.
         */
        void onImageLoadFailed(com.hayukleung.app.widget.media.picasso.Picasso picasso, Uri uri, Exception exception);
    }

    /**
     * A transformer that is called immediately before every request is submitted. This can be used to
     * modify any information about a request.
     * <p/>
     * For example, if you use a CDN you can change the hostname for the image based on the current
     * location of the user in order to get faster download speeds.
     * <p/>
     * <b>NOTE:</b> This is a beta feature. The API is subject to change in a backwards incompatible
     * way at any time.
     */
    public interface RequestTransformer {
        /**
         * A {@link com.hayukleung.app.widget.media.picasso.Picasso.RequestTransformer} which returns the original request.
         */
        RequestTransformer IDENTITY = new RequestTransformer() {
            @Override
            public Request transformRequest(Request request) {
                return request;
            }
        };

        /**
         * Transform a request before it is submitted to be processed.
         *
         * @return The original request or a new request to replace it. Must not be null.
         */
        Request transformRequest(Request request);
    }

    private static class CleanupThread extends Thread {
        private final ReferenceQueue<?> referenceQueue;
        private final Handler handler;

        CleanupThread(ReferenceQueue<?> referenceQueue, Handler handler) {
            this.referenceQueue = referenceQueue;
            this.handler = handler;
            setDaemon(true);
            setName(THREAD_PREFIX + "refQueue");
        }

        @Override
        public void run() {
            Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);
            while (true) {
                try {
                    com.hayukleung.app.widget.media.picasso.Action.RequestWeakReference<?> remove = (com.hayukleung.app.widget.media.picasso.Action.RequestWeakReference<?>) referenceQueue.remove();
                    handler.sendMessage(handler.obtainMessage(REQUEST_GCED, remove.action));
                } catch (InterruptedException e) {
                    break;
                } catch (final Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            throw new RuntimeException(e);
                        }
                    });
                    break;
                }
            }
        }

        void shutdown() {
            interrupt();
        }
    }

    /**
     * Fluent API for creating {@link com.hayukleung.app.widget.media.picasso.Picasso} instances.
     */
    @SuppressWarnings("UnusedDeclaration") // Public API.
    public static class Builder {
        private final Context context;
        private Downloader downloader;
        private ExecutorService service;
        private Cache cache;
        private LruDiskCache diskCache;
        private Listener listener;
        private RequestTransformer transformer;
        private List<RequestHandler> requestHandlers;

        private boolean indicatorsEnabled;
        private boolean loggingEnabled;

        /**
         * Start building a new {@link com.hayukleung.app.widget.media.picasso.Picasso} instance.
         */
        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context.getApplicationContext();
        }

        /**
         * Specify the {@link Downloader} that will be used for downloading images.
         */
        public Builder downloader(Downloader downloader) {
            if (downloader == null) {
                throw new IllegalArgumentException("Downloader must not be null.");
            }
            if (this.downloader != null) {
                throw new IllegalStateException("Downloader already set.");
            }
            this.downloader = downloader;
            return this;
        }

        /**
         * Specify the executor service for loading images in the background.
         * <p/>
         * Note: Calling {@link com.hayukleung.app.widget.media.picasso.Picasso#shutdown() shutdown()} will not shutdown supplied executors.
         */
        public Builder executor(ExecutorService executorService) {
            if (executorService == null) {
                throw new IllegalArgumentException("Executor service must not be null.");
            }
            if (this.service != null) {
                throw new IllegalStateException("Executor service already set.");
            }
            this.service = executorService;
            return this;
        }

        /**
         * Specify the memory cache used for the most recent images.
         */
        public Builder memoryCache(Cache memoryCache) {
            if (memoryCache == null) {
                throw new IllegalArgumentException("Memory cache must not be null.");
            }
            if (this.cache != null) {
                throw new IllegalStateException("Memory cache already set.");
            }
            this.cache = memoryCache;
            return this;
        }

        public Builder diskCache(LruDiskCache diskCache) {
            if (diskCache == null) {
                throw new IllegalArgumentException("Disk cache must not be null.");
            }
            if (this.diskCache != null) {
                throw new IllegalStateException("Disk cache already set.");
            }
            this.diskCache = diskCache;
            return this;
        }

        /**
         * Specify a listener for interesting events.
         */
        public Builder listener(Listener listener) {
            if (listener == null) {
                throw new IllegalArgumentException("Listener must not be null.");
            }
            if (this.listener != null) {
                throw new IllegalStateException("Listener already set.");
            }
            this.listener = listener;
            return this;
        }

        /**
         * Specify a transformer for all incoming requests.
         * <p/>
         * <b>NOTE:</b> This is a beta feature. The API is subject to change in a backwards incompatible
         * way at any time.
         */
        public Builder requestTransformer(RequestTransformer transformer) {
            if (transformer == null) {
                throw new IllegalArgumentException("Transformer must not be null.");
            }
            if (this.transformer != null) {
                throw new IllegalStateException("Transformer already set.");
            }
            this.transformer = transformer;
            return this;
        }

        /**
         * Register a {@link RequestHandler}.
         */
        public Builder addRequestHandler(RequestHandler requestHandler) {
            if (requestHandler == null) {
                throw new IllegalArgumentException("RequestHandler must not be null.");
            }
            if (requestHandlers == null) {
                requestHandlers = new ArrayList<RequestHandler>();
            }
            if (requestHandlers.contains(requestHandler)) {
                throw new IllegalStateException("RequestHandler already registered.");
            }
            requestHandlers.add(requestHandler);
            return this;
        }

        /**
         * @deprecated Use {@link #indicatorsEnabled(boolean)} instead.
         * Whether debugging is enabled or not.
         */
        @Deprecated
        public Builder debugging(boolean debugging) {
            return indicatorsEnabled(debugging);
        }

        /**
         * Toggle whether to display debug indicators on images.
         */
        public Builder indicatorsEnabled(boolean enabled) {
            this.indicatorsEnabled = enabled;
            return this;
        }

        /**
         * Toggle whether debug logging is enabled.
         * <p/>
         * <b>WARNING:</b> Enabling this will result in excessive object allocation. This should be only
         * be used for debugging purposes. Do NOT pass {@code BuildConfig.DEBUG}.
         */
        public Builder loggingEnabled(boolean enabled) {
            this.loggingEnabled = enabled;
            return this;
        }

        /**
         * Create the {@link com.hayukleung.app.widget.media.picasso.Picasso} instance.
         */
        public com.hayukleung.app.widget.media.picasso.Picasso build() {
            Context context = this.context;

            if (downloader == null) {
                downloader = Utils.createDefaultDownloader();
            }
            if (cache == null) {
                cache = new LruCache(context);
            }
            if (diskCache == null) {
                diskCache = Utils.createDefaultLruDiskCache(context);
            }
            if (service == null) {
                service = new PicassoExecutorService();
            }
            if (transformer == null) {
                transformer = RequestTransformer.IDENTITY;
            }

            com.hayukleung.app.widget.media.picasso.Stats stats = new com.hayukleung.app.widget.media.picasso.Stats(cache);

            Dispatcher dispatcher = new Dispatcher(context, service, HANDLER, downloader, cache, diskCache, stats);
            diskCache.handler = dispatcher.handler;

            return new com.hayukleung.app.widget.media.picasso.Picasso(context, dispatcher, cache, diskCache, listener, transformer,
                    requestHandlers, stats, indicatorsEnabled, loggingEnabled);
        }
    }
}
