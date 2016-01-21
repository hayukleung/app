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

import java.util.concurrent.Executor;

/**
 * Delivers responses and errors.
 */
public class ExecutorDelivery implements ResponseDelivery {
    /**
     * Used for posting responses, typically to the main thread.
     */
    private final Executor mResponsePoster;

    /**
     * Creates a new response delivery interface.
     *
     * @param handler {@link android.os.Handler} to post responses on
     */
    public ExecutorDelivery(final Handler handler) {
        // Make an Executor that just wraps the handler.
        mResponsePoster = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }

    @Override
    public void postResponse(Task request, Response response) {
        postResponse(request, response, null);
    }

    @Override
    public void postResponse(Task request, Response response, Runnable runnable) {
        request.markDelivered();
        request.addMarker("post-response");
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response, runnable));
    }

    @Override
    public void postError(Task request, VolleyError error) {
        request.addMarker("post-error");
        Response response = Response.error(error);
        mResponsePoster.execute(new ResponseDeliveryRunnable(request, response, null));
    }

    /**
     * A Runnable used for delivering network responses to a listener on the main thread.
     */
    @SuppressWarnings("rawtypes")
    private class ResponseDeliveryRunnable implements Runnable {
        private final Task mTask;
        private final Response mResponse;
        private final Runnable mRunnable;

        public ResponseDeliveryRunnable(Task request, Response response, Runnable runnable) {
            mTask = request;
            mResponse = response;
            mRunnable = runnable;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void run() {
            // If this request has canceled, finish it and don't deliver.
            if (mTask.isCanceled()) {
                mTask.finish("canceled-at-delivery");
                return;
            }

            mResponse.extra = mTask.getExtra();
            // Deliver a normal response or error, depending.
            if (mResponse.isSuccess()) {
                mTask.deliverResponse(mResponse);
            } else {
                // If has next response, don't delever error
                if (!mResponse.intermediate) {
                    mTask.deliverError(mResponse.error);
                }
            }

            // If this is an intermediate response, add a marker, otherwise we're done
            // and the request can be finished.
            if (mResponse.intermediate) {
                mTask.addMarker("intermediate-response");
            } else {
                mTask.finish("done");
            }

            // If we have been provided a post-delivery runnable, run it.
            if (mRunnable != null) {
                mRunnable.run();
            }
        }
    }
}
