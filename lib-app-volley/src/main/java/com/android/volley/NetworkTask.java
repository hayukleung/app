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

import com.android.volley.http.HttpRequestBase;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

/**
 * Base class for all network requests.
 *
 * @param <T> The type of parsed response this request expects.
 */
public abstract class NetworkTask<T> extends Task<T> {

    /**
     * Default encoding for POST or PUT parameters. See {@link #getParamsEncoding()}.
     */
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    private final HttpRequestBase mHttpUrlRequest;

    /**
     * Default tag for {@link android.net.TrafficStats}.
     */
    private final int mDefaultTrafficStatsTag;

    /**
     * The retry policy for this request.
     */
    private RetryPolicy mRetryPolicy;

    /**
     * When a request can be retrieved from cache but must be refreshed from the network, the cache entry will be stored here so that in the event of a "Not Modified" response, we can be sure it
     * hasn't been evicted from cache.
     */
    private Cache.Entry mCacheEntry = null;

    /**
     * Whether or not responses to this request should be cached.
     */
    private boolean mShouldCache = true;

    /**
     * Whether or not responses to this request always cached.
     */
    private boolean mAlwaysCache = false;

    /**
     * Whether or not responses this request only once
     */
    private boolean mSingleData = false;

    /**
     * Whether or not get responses from cache.
     */
    private boolean mSkipCache = false;

    /**
     * Creates a new request with the given method (one of the values from {@link java.lang.reflect.Method}), URL, and error listener. Note that the normal response listener is not provided here as delivery of
     * responses is provided by subclasses, who have a better idea of how to deliver an already-parsed response.
     */
    public NetworkTask(HttpRequestBase httpUrlRequest, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(listener, errorListener);
        this.mHttpUrlRequest = httpUrlRequest;
        setRetryPolicy(new DefaultRetryPolicy());

        mDefaultTrafficStatsTag = httpUrlRequest.getURL().getHost().hashCode();
    }

    /**
     * @return A tag for use with {@link android.net.TrafficStats#setThreadStatsTag(int)}
     */
    public int getTrafficStatsTag() {
        return mDefaultTrafficStatsTag;
    }

    /**
     * Returns the URL of this request.
     */
    public String getUrl() {
        return mHttpUrlRequest.getURL().toString();
    }

    /**
     * Returns the HttpUrlRequest of this request.
     */
    public HttpRequestBase getRequest() {
        return mHttpUrlRequest;
    }

    /**
     * Returns the cache key for this request. By default, this is the URL.
     */
    public String getCacheKey() {
        return getUrl();
    }

    /**
     * Returns the annotated cache entry, or null if there isn't one.
     */
    public Cache.Entry getCacheEntry() {
        return mCacheEntry;
    }

    /**
     * Annotates this request with an entry retrieved for it from cache. Used for cache coherency support.
     */
    public void setCacheEntry(Cache.Entry entry) {
        mCacheEntry = entry;
    }

    /**
     * Returns a list of extra HTTP headers to go along with this request. Can throw {@link AuthFailureError} as authentication may be required to provide these values.
     *
     * @throws AuthFailureError In the event of auth failure
     */
    public Map<String, String> getHeaders() throws AuthFailureError {
        return Collections.emptyMap();
    }

    /**
     * Returns a Map of POST parameters to be used for this request, or null if a simple GET should be used. Can throw {@link AuthFailureError} as authentication may be required to provide these
     * values.
     * <p/>
     * <p>
     * Note that only one of getPostParams() and getPostBody() can return a non-null value.
     * </p>
     *
     * @throws AuthFailureError In the event of auth failure
     * @deprecated Use {@link #getParams()} instead.
     */
    protected Map<String, String> getPostParams() throws AuthFailureError {
        return getParams();
    }

    /**
     * Returns which encoding should be used when converting POST parameters returned by {@link #getPostParams()} into a raw POST body.
     * <p>
     * <p>
     * This controls both encodings:
     * <ol>
     * <li>The string encoding used when converting parameter names and values into bytes prior to URL encoding them.</li>
     * <li>The string encoding used when converting the URL encoded parameters into a raw byte array.</li>
     * </ol>
     *
     * @deprecated Use {@link #getParamsEncoding()} instead.
     */
    protected String getPostParamsEncoding() {
        return getParamsEncoding();
    }

    /**
     * @deprecated Use {@link #getBodyContentType()} instead.
     */
    public String getPostBodyContentType() {
        return getBodyContentType();
    }

    /**
     * Returns the raw POST body to be sent.
     *
     * @throws AuthFailureError In the event of auth failure
     * @deprecated Use {@link #getBody()} instead.
     */
    public byte[] getPostBody() throws AuthFailureError {
        // Note: For compatibility with legacy clients of volley, this implementation must remain
        // here instead of simply calling the getBody() function because this function must
        // call getPostParams() and getPostParamsEncoding() since legacy clients would have
        // overridden these two member functions for POST requests.
        Map<String, String> postParams = getPostParams();
        if (postParams != null && postParams.size() > 0) {
            return encodeParameters(postParams, getPostParamsEncoding());
        }
        return null;
    }

    /**
     * Returns a Map of parameters to be used for a POST or PUT request. Can throw {@link AuthFailureError} as authentication may be required to provide these values.
     * <p/>
     * <p>
     * Note that you can directly override {@link #getBody()} for custom data.
     * </p>
     *
     * @throws AuthFailureError in the event of auth failure
     */
    protected Map<String, String> getParams() throws AuthFailureError {
        return null;
    }

    /**
     * Returns which encoding should be used when converting POST or PUT parameters returned by {@link #getParams()} into a raw POST or PUT body.
     * <p>
     * <p>
     * This controls both encodings:
     * <ol>
     * <li>The string encoding used when converting parameter names and values into bytes prior to URL encoding them.</li>
     * <li>The string encoding used when converting the URL encoded parameters into a raw byte array.</li>
     * </ol>
     */
    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    /**
     * Returns the raw POST or PUT body to be sent.
     *
     * @throws AuthFailureError in the event of auth failure
     */
    public byte[] getBody() throws AuthFailureError {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    /**
     * Set whether or not responses to this request should be cached.
     *
     * @return
     */
    public final NetworkTask<T> setShouldCache(boolean shouldCache) {
        mShouldCache = shouldCache;
        return this;
    }

    /**
     * Returns true if responses to this request should be cached.
     */
    public final boolean shouldCache() {
        return mShouldCache;
    }

    /**
     * Set whether or not responses to this request always cached.
     *
     * @return
     */
    public final NetworkTask<T> setAlwaysCache(boolean alwaysCache) {
        mAlwaysCache = alwaysCache;
        return this;
    }

    /**
     * Returns true if responses to this request always cached.
     */
    public final boolean alwaysCache() {
        return mAlwaysCache;
    }

    public final NetworkTask<T> setSingleResponse(boolean singleData) {
        mSingleData = singleData;
        return this;
    }

    public final boolean singleResponse() {
        return mSingleData;
    }

    public final boolean isSkipCache() {
        return mSkipCache;
    }

    public final NetworkTask<T> setSkipCache(boolean skipCache) {
        this.mSkipCache = skipCache;
        return this;
    }

    public final boolean needCache() {
        return mAlwaysCache || mShouldCache;
    }

    /**
     * Returns the socket timeout in milliseconds per retry attempt. (This value can be changed per retry attempt if a backoff is specified via backoffTimeout()). If there are no retry attempts
     * remaining, this will cause delivery of a {@link TimeoutError} error.
     */
    public final int getTimeoutMs() {
        return mRetryPolicy.getCurrentTimeout();
    }

    /**
     * Returns the retry policy that should be used for this request.
     */
    public RetryPolicy getRetryPolicy() {
        return mRetryPolicy;
    }

    /**
     * Sets the retry policy for this request.
     *
     * @return
     */
    public NetworkTask<T> setRetryPolicy(RetryPolicy retryPolicy) {
        mRetryPolicy = retryPolicy;
        return this;
    }

    /**
     * Subclasses must implement this to parse the raw network response and return an appropriate response type. This method will be called from a worker thread. The response will not be delivered if
     * you return null.
     *
     * @param response Response from the network
     * @return The parsed response, or null in the case of an error
     */
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(parse(data), HttpHeaderParser.parseCacheHeaders(response, alwaysCache()));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (ParseError e) {
            return Response.error(e);
        }
    }

    abstract protected T parse(String data) throws ParseError;

    /**
     * Subclasses can override this method to parse 'networkError' and return a more specific error.
     * <p/>
     * <p>
     * The default implementation just returns the passed 'networkError'.
     * </p>
     *
     * @param volleyError the error retrieved from the network
     * @return an NetworkError augmented with additional information
     */
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        return volleyError;
    }

    @Override
    public String toString() {
        String trafficStatsTag = "0x" + Integer.toHexString(getTrafficStatsTag());
        return (isCanceled() ? "[X] " : "[ ] ") + getUrl() + " " + trafficStatsTag + " " + getPriority() + " " + getSequence();
    }
}
