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

import android.os.Handler;
import android.os.Looper;

/**
 * A request dispatch queue with a thread pool of dispatchers.
 * <p>
 * Calling {@link #add(LocalTask)} will enqueue the given Request for dispatch, resolving from a worker thread, and then delivering a parsed response on the main thread.
 */
public class LocalTaskQueue extends TaskQueue<LocalTask> {

    /**
     * The dispatcher.
     */
    private LocalDispatcher mDispatcher;

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     *
     * @param delivery A ResponseDelivery interface for posting responses and errors
     */
    public LocalTaskQueue(ResponseDelivery delivery) {
        super(delivery);
    }

    /**
     * Creates the worker pool. Processing will not begin until {@link #start()} is called.
     */
    public LocalTaskQueue() {
        this(new ExecutorDelivery(new Handler(Looper.getMainLooper())));
    }

    @Override
    public void start() {
        stop(); // Make sure any currently running dispatchers are stopped.
        // Create the local dispatcher and start it.
        mDispatcher = new LocalDispatcher(mQueue, mDelivery);
        mDispatcher.start();
    }

    @Override
    public void stop() {
        if (mDispatcher != null) {
            mDispatcher.quit();
        }
    }

    @Override
    public LocalTask add(LocalTask request) {
        // Tag the request as belonging to this queue and add it to the set of current requests.
        request.setRequestQueue(this);
        synchronized (mCurrentRequests) {
            mCurrentRequests.add(request);
        }

        request.addMarker("add-to-queue");
        request.setSequence(getSequenceNumber());
        mQueue.add(request);
        return request;
    }

    @Override
    public Response request(LocalTask task) {
        throw new UnsupportedOperationException();
    }

    @Override
    void finish(LocalTask request) {
        // Remove from the set of requests currently being processed.
        synchronized (mCurrentRequests) {
            mCurrentRequests.remove(request);
        }
    }
}
