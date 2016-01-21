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

package com.android.volley.toolbox;

import com.android.volley.NetworkTask;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.http.HttpRequestBase;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class StringRequest extends NetworkTask<String> {

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link Method} to use
     * @param url    URL to fetch the string at
     */
    public StringRequest(HttpRequestBase httpRequest, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(httpRequest, listener, errorListener);
    }

    @Override
    protected String parse(String data) throws ParseError {
        return data;
    }
}
