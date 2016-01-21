/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.volley.toolbox;

import android.content.Context;
import com.android.volley.LocalTaskQueue;
import com.android.volley.Network;
import com.android.volley.NetworkTaskQueue;

import java.io.File;

import static com.android.volley.NetworkTaskQueue.DEFAULT_NETWORK_THREAD_POOL_SIZE;

public class Volley {

    /**
     * Default on-disk cache directory.
     */
    private static final String DEFAULT_CACHE_DIR = "volley";

    /**
     * Creates a instance of the network worker pool and calls {@link com.android.volley.NetworkTaskQueue#start()} on it.
     *
     * @param context        A {@link android.content.Context} to use for creating the cache dir.
     * @param stack          An {@link HttpStack} to use for the network, or null for default.
     * @param threadPoolSize Number of network dispatcher threads to create
     * @return A started {@link com.android.volley.NetworkTaskQueue} instance.
     */
    public static NetworkTaskQueue newNetworkQueue(Context context, HttpStack stack, int threadPoolSize) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

        if (stack == null) {
            stack = new OkHttpStack();
        }

        Network network = new BasicNetwork(stack);

        NetworkTaskQueue queue = new NetworkTaskQueue(new DiskBasedCache(cacheDir), network, threadPoolSize);
        queue.start();

        return queue;
    }

    /**
     * Creates a instance of the network worker pool and calls {@link com.android.volley.NetworkTaskQueue#start()} on it.
     *
     * @param context A {@link android.content.Context} to use for creating the cache dir.
     * @param stack   An {@link HttpStack} to use for the network, or null for default.
     * @return A started {@link com.android.volley.NetworkTaskQueue} instance.
     */
    public static NetworkTaskQueue newNetworkQueue(Context context, HttpStack stack) {
        return newNetworkQueue(context, stack, DEFAULT_NETWORK_THREAD_POOL_SIZE);
    }

    /**
     * Creates a instance of the network worker pool and calls {@link com.android.volley.NetworkTaskQueue#start()} on it.
     *
     * @param context A {@link android.content.Context} to use for creating the cache dir.
     * @return A started {@link com.android.volley.NetworkTaskQueue} instance.
     */
    public static NetworkTaskQueue newNetworkQueue(Context context) {
        return newNetworkQueue(context, null);
    }

    /**
     * Creates a instance of the local worker pool and calls {@link com.android.volley.TaskQueue#start()} on it.
     *
     * @return A started {@link com.android.volley.NetworkTaskQueue} instance.
     */
    public static LocalTaskQueue newLocalQueue() {
        LocalTaskQueue queue = new LocalTaskQueue();
        queue.start();

        return queue;
    }
}
