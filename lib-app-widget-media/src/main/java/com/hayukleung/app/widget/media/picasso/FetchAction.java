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

class FetchAction extends com.hayukleung.app.widget.media.picasso.Action<Object> {

    private final Object target;
    private Callback callback;

    FetchAction(Picasso picasso, Request data, int memoryPolicy, String key, Object tag, Callback callback) {
        super(picasso, null, data, memoryPolicy, false, 0, null, key, tag);
        this.target = new Object();
        this.callback = callback;
    }

    @Override
    public void progress(int progress) {
        if (callback != null) {
            callback.onProgress(progress);
        }
    }

    @Override
    void complete(IImage result, Picasso.LoadedFrom from) {
        if (callback != null) {
            callback.onSuccess();
        }
    }

    @Override
    public void error() {
        if (callback != null) {
            callback.onError();
        }
    }

    @Override
    void cancel() {
        super.cancel();
        callback = null;
    }

    @Override
    Object getTarget() {
        return target;
    }
}
