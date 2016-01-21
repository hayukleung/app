/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.volley;

import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.android.volley.http.HttpEntityEnclosingRequest;
import com.android.volley.utils.Ln;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * A request dispatch queue with a thread pool of dispatchers.
 * <p>
 * Calling {@link #add(NetworkTask)} will enqueue the given Request for dispatch, resolving from either cache or network on a worker thread, and then delivering a parsed response on the main thread.
 */
public class NetworkTaskQueue extends TaskQueue<NetworkTask> {

    /**
     * Number of network request dispatcher threads to start.
     */
    public static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;
    /**
     * Staging area for requests that already have a duplicate request in flight.
     * <p>
     * <ul>
     * <li>containsKey(cacheKey) indicates that there is a request in flight for the given cache key.</li>
     * <li>get(cacheKey) returns waiting requests for the given cache key. The in flight request is <em>not</em> contained in that list. Is null if no requests are staged.</li>
     * </ul>
     */
    private final Map<String, Queue<NetworkTask>> mWaitingRequests = new HashMap<String, Queue<NetworkTask>>();
    /**
     * The cache triage queue.
     */
    private final PriorityBlockingQueue<NetworkTask> mCacheQueue = new PriorityBlockingQueue<NetworkTask>();
    /**
     * Cache interface for retrieving and storing respones.
     */
    private final Cache mCache;

    /**
     * Network interface for performing requests.
     */
    private final Network mNetwork;

    /**
     * The network dispatchers.
     */
    private NetworkDispatcher[] mDispatchers;

    /**
     * The cache dispatcher.
     */
    private CacheDispatcher mCacheDispatcher;

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param cache          A Cache to use for persisting responses to disk
     * @param network        A Network interface for performing HTTP requests
     * @param threadPoolSize Number of network dispatcher threads to create
     * @param delivery       A ResponseDelivery interface for posting responses and errors
     */
    public NetworkTaskQueue(Cache cache, Network network, int threadPoolSize, ResponseDelivery delivery) {
        super(delivery);
        mCache = cache;
        mNetwork = network;
        mDispatchers = new NetworkDispatcher[threadPoolSize];
    }

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param cache          A Cache to use for persisting responses to disk
     * @param network        A Network interface for performing HTTP requests
     * @param threadPoolSize Number of network dispatcher threads to create
     */
    public NetworkTaskQueue(Cache cache, Network network, int threadPoolSize) {
        this(cache, network, threadPoolSize, new ExecutorDelivery(new Handler(Looper.getMainLooper())));
    }

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param cache   A Cache to use for persisting responses to disk
     * @param network A Network interface for performing HTTP requests
     */
    public NetworkTaskQueue(Cache cache, Network network) {
        this(cache, network, DEFAULT_NETWORK_THREAD_POOL_SIZE);
    }

    @Override
    public void start() {
        stop(); // Make sure any currently running dispatchers are stopped.
        // Create the cache dispatcher and start it.
        mCacheDispatcher = new CacheDispatcher(mCacheQueue, mQueue, mCache, mDelivery);
        mCacheDispatcher.start();

        // Create network dispatchers (and corresponding threads) up to the pool size.
        for (int i = 0; i < mDispatchers.length; i++) {
            NetworkDispatcher networkDispatcher = new NetworkDispatcher(mQueue, mNetwork, mCache, mDelivery);
            mDispatchers[i] = networkDispatcher;
            networkDispatcher.start();
        }
    }

    @Override
    public void stop() {
        if (mCacheDispatcher != null) {
            mCacheDispatcher.quit();
        }
        for (int i = 0; i < mDispatchers.length; i++) {
            if (mDispatchers[i] != null) {
                mDispatchers[i].quit();
            }
        }
    }

    /**
     * Gets the {@link Cache} instance being used.
     */
    public Cache getCache() {
        return mCache;
    }

    @Override
    public NetworkTask add(NetworkTask request) {
        // Tag the request as belonging to this queue and add it to the set of current requests.
        request.setRequestQueue(this);
        synchronized (mCurrentRequests) {
            mCurrentRequests.add(request);
        }

        request.addMarker("add-to-queue");
        // Process requests in the order they are added.
        request.setSequence(getSequenceNumber());

        // If the request is uncacheable, skip the cache queue and go straight to the network.
        if (!request.needCache() || request.isSkipCache()) {
            mQueue.add(request);
            return request;
        }

        // Insert request into stage if there's already a request with the same cache key in flight.
        synchronized (mWaitingRequests) {
            String cacheKey = request.getCacheKey();
            if (mWaitingRequests.containsKey(cacheKey) && !(request.getRequest() instanceof HttpEntityEnclosingRequest)) {
                // There is already a request in flight. Queue up.
                Queue<NetworkTask> stagedRequests = mWaitingRequests.get(cacheKey);
                if (stagedRequests == null) {
                    stagedRequests = new LinkedList<NetworkTask>();
                }
                stagedRequests.add(request);
                mWaitingRequests.put(cacheKey, stagedRequests);
                Ln.v("Request for cacheKey=%s is in flight, putting on hold.", cacheKey);
            } else {
                // Insert 'null' queue for this cacheKey, indicating there is now a request in flight.
                mWaitingRequests.put(cacheKey, null);
                mCacheQueue.add(request);
            }
        }
        return request;
    }

    @Override
    public Response request(NetworkTask request) {
        Response response;
        try {
            request.addMarker("network-queue-take");

            // Tag the request (if API >= 14)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                TrafficStats.setThreadStatsTag(request.getTrafficStatsTag());
            }

            // Perform the network request.
            request.setSequence(getSequenceNumber());
            NetworkResponse networkResponse = mNetwork.performRequest(request);
            request.addMarker("network-http-complete");

            // Parse the response here on the worker thread.
            response = request.parseNetworkResponse(networkResponse);
            request.addMarker("network-parse-complete");

            // Write to cache if applicable.
            if (request.needCache() && response.cacheEntry != null) {
                mCache.put(request.getCacheKey(), response.cacheEntry);
                request.addMarker("network-cache-written");
            }
        } catch (VolleyError volleyError) {
            response = Response.error(volleyError);
        } catch (Exception e) {
            Ln.e(e, "Unhandled exception %s", e.toString());
            response = Response.error(new VolleyError(e));
        }
        request.finish("done");
        return response;
    }

    @Override
    public void finish(NetworkTask request) {
        // Remove from the set of requests currently being processed.
        synchronized (mCurrentRequests) {
            mCurrentRequests.remove(request);
        }

        if (request.needCache()) {
            synchronized (mWaitingRequests) {
                String cacheKey = request.getCacheKey();
                Queue<NetworkTask> waitingRequests = mWaitingRequests.remove(cacheKey);
                if (waitingRequests != null) {
                    Ln.v("Releasing %d waiting requests for cacheKey=%s.", waitingRequests.size(), cacheKey);
                    // Process all queued up requests. They won't be considered as in flight, but
                    // that's not a problem as the cache has been primed by 'request'.
                    mCacheQueue.addAll(waitingRequests);
                }
            }
        }
    }

}
