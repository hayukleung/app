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

import android.os.Process;

import com.android.volley.utils.Ln;

import java.util.concurrent.BlockingQueue;

/**
 * Provides a thread for performing network dispatch from a queue of requests.
 * <p>
 * Requests added to the specified queue are processed from the network via a specified {@link Network} interface. Responses are committed to cache, if eligible, using a specified {@link Cache}
 * interface. Valid responses and errors are posted back to the caller via a {@link ResponseDelivery}.
 */
public class LocalDispatcher extends Dispatcher {
    /**
     * The queue of requests to service.
     */
    private final BlockingQueue<LocalTask> mQueue;
    /**
     * For posting responses and errors.
     */
    private final ResponseDelivery mDelivery;

    /**
     * Creates a new network dispatcher thread. You must call {@link #start()} in order to begin processing.
     *
     * @param queue    Queue of incoming requests for triage
     * @param delivery Delivery interface to use for posting responses
     */
    public LocalDispatcher(BlockingQueue<LocalTask> queue, ResponseDelivery delivery) {
        mQueue = queue;
        mDelivery = delivery;
        setName("LocalDispatcher");
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        LocalTask request;
        while (true) {
            try {
                // Take a request from the queue.
                request = mQueue.take();
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (mQuit) {
                    return;
                }
                continue;
            }

            try {
                request.addMarker("local-queue-take");

                // If the request was cancelled already, do not perform the local request.
                if (request.isCanceled()) {
                    request.finish("local-discard-cancelled");
                    continue;
                }

                // Parse the response here on the worker thread.
                Response response = Response.success(request.perform(), null);
                request.addMarker("local-complete");

                // Post the response back.
                request.markDelivered();
                mDelivery.postResponse(request, response);
            } catch (Exception e) {
                Ln.e(e, "Unhandled exception %s", e.toString());
                mDelivery.postError(request, new VolleyError(e));
            }
        }
    }

}
