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
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

class ImageViewAction extends com.hayukleung.app.widget.media.picasso.Action<ImageView> {

    Callback callback;

    ImageViewAction(Picasso picasso, ImageView imageView, Request data, int memoryPolicy,
                    boolean noFade, int errorResId, Drawable errorDrawable, String key, Object tag,
                    Callback callback) {
        super(picasso, imageView, data, memoryPolicy, noFade, errorResId, errorDrawable, key, tag);
        this.callback = callback;
    }

    @Override
    public void progress(int progress) {
        ImageView target = this.target.get();
        if (target == null) {
            return;
        }

        if (callback != null) {
            callback.onProgress(progress);
        }
    }

    @Override
    public void complete(IImage result, Picasso.LoadedFrom from) {
        if (result == null) {
            throw new AssertionError(
                    String.format("Attempted to complete action with no result!\n%s", this));
        }

        ImageView target = this.target.get();
        if (target == null) {
            return;
        }

        Context context = picasso.context;
        boolean indicatorsEnabled = picasso.indicatorsEnabled;
        result.setImage(target, context, from, noFade, indicatorsEnabled);
        if (callback != null) {
            callback.onSuccess();
        }
    }

    @Override
    public void error() {
        ImageView target = this.target.get();
        if (target == null) {
            return;
        }
        if (errorResId != 0) {
            target.setImageResource(errorResId);
        } else if (errorDrawable != null) {
            target.setImageDrawable(errorDrawable);
        }

        if (callback != null) {
            callback.onError();
        }
    }

    @Override
    void cancel() {
        super.cancel();
        callback = null;
    }
}
