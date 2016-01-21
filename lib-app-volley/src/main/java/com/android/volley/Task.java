package com.android.volley;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.utils.Ln;

public abstract class Task<T> implements Comparable<Task<T>> {
    /**
     * Threshold at which we should log the request (even when debug logging is not enabled).
     */
    private static final long SLOW_REQUEST_THRESHOLD_MS = 3000;
    /**
     * An event log tracing the lifetime of this request; for debugging.
     */
    private final Ln.MarkerLog mEventLog = Ln.MarkerLog.ENABLED ? new Ln.MarkerLog() : null;
    private final Response.Listener<T> mListener;
    private final Response.ErrorListener mErrorListener;
    // A cheap variant of request tracing used to dump slow requests.
    private long mRequestBirthTime = 0;
    /**
     * Sequence number of this request, used to enforce FIFO ordering.
     */
    private Integer mSequence;
    /**
     * The request queue this request is associated with.
     */
    private TaskQueue mRequestQueue;
    /**
     * Whether or not this request has been canceled.
     */
    private boolean mCanceled = false;
    /**
     * An opaque token tagging this request; used for bulk cancellation.
     */
    private Object mTag;

    /**
     * The extra value.
     */
    private Object mExtra;
    /**
     * Whether or not a response has been delivered for this request yet.
     */
    private boolean mResponseDelivered = false;

    public Task(Listener<T> listener, ErrorListener errorListener) {
        this.mListener = listener;
        this.mErrorListener = errorListener;
    }

    /**
     * Returns the sequence number of this request.
     */
    public final int getSequence() {
        if (mSequence == null) {
            throw new IllegalStateException("getSequence called before setSequence");
        }
        return mSequence;
    }

    /**
     * Sets the sequence number of this request. Used by {@link NetworkTaskQueue}.
     */
    public final void setSequence(int sequence) {
        mSequence = sequence;
    }

    /**
     * Returns the request queue of this request.
     */
    public TaskQueue getRequestQueue() {
        return mRequestQueue;
    }

    /**
     * Associates this request with the given queue. The request queue will be notified when this request has finished.
     */
    public void setRequestQueue(TaskQueue requestQueue) {
        mRequestQueue = requestQueue;
    }

    /**
     * Mark this request as canceled. No callback will be delivered.
     */
    public void cancel() {
        mCanceled = true;
    }

    /**
     * Returns true if this request has been canceled.
     */
    public boolean isCanceled() {
        return mCanceled;
    }

    /**
     * Returns this request's tag.
     *
     * @see Task#setTag(Object)
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * Set a tag on this request. Can be used to cancel all requests with this tag by {@link NetworkTaskQueue#cancelAll(Object)}.
     */
    public Task setTag(Object tag) {
        mTag = tag;
        return this;
    }

    public Object getExtra() {
        return mExtra;
    }

    /**
     * Set the extra value.
     *
     * @param extra extra value for response
     */
    public void setExtra(Object extra) {
        this.mExtra = extra;
    }

    /**
     * Mark this request as having a response delivered on it. This can be used later in the request's lifetime for suppressing identical responses.
     */
    public void markDelivered() {
        mResponseDelivered = true;
    }

    /**
     * Returns true if this request has had a response delivered for it.
     */
    public boolean hasHadResponseDelivered() {
        return mResponseDelivered;
    }

    /**
     * Adds an event to this request's event log; for debugging.
     */
    public void addMarker(String tag) {
        if (Ln.MarkerLog.ENABLED) {
            mEventLog.add(tag, Thread.currentThread().getId());
        } else if (mRequestBirthTime == 0) {
            mRequestBirthTime = SystemClock.elapsedRealtime();
        }
    }

    /**
     * Notifies the request queue that this request has finished (successfully or with error).
     * <p/>
     * <p>
     * Also dumps all events from this request's event log; for debugging.
     * </p>
     */
    void finish(final String tag) {
        if (getRequestQueue() != null) {
            getRequestQueue().finish(this);
        }
        if (Ln.MarkerLog.ENABLED) {
            final long threadId = Thread.currentThread().getId();
            if (Looper.myLooper() != Looper.getMainLooper()) {
                // If we finish marking off of the main thread, we need to
                // actually do it on the main thread to ensure correct ordering.
                Handler mainThread = new Handler(Looper.getMainLooper());
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mEventLog.add(tag, threadId);
                        mEventLog.finish(this.toString());
                    }
                });
                return;
            }

            mEventLog.add(tag, threadId);
            mEventLog.finish(this.toString());
        } else {
            long requestTime = SystemClock.elapsedRealtime() - mRequestBirthTime;
            if (requestTime >= SLOW_REQUEST_THRESHOLD_MS) {
                Ln.d("%d ms: %s", requestTime, this.toString());
            }
        }
    }

    /**
     * Subclasses must implement this to perform delivery of the parsed response to their listeners. The given response is guaranteed to be non-null; responses that fail to parse are not delivered.
     *
     * @param response The parsed response returned by {@link NetworkTask#parseNetworkResponse(NetworkResponse)} or {@link LocalTask#perform()}
     */
    protected void deliverResponse(Response<T> response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    /**
     * Delivers error message to the ErrorListener that the Request was initialized with.
     *
     * @param error Error details
     */
    protected void deliverError(VolleyError error) {
        if (mErrorListener != null) {
            mErrorListener.onErrorResponse(error);
        }
    }

    /**
     * Returns the {@link com.android.volley.Task.Priority} of this request; {@link com.android.volley.Task.Priority#NORMAL} by default.
     */
    public Priority getPriority() {
        return Priority.NORMAL;
    }

    /**
     * Our comparator sorts from high to low priority, and secondarily by sequence number to provide FIFO ordering.
     */
    @Override
    public int compareTo(Task<T> other) {
        Priority left = this.getPriority();
        Priority right = other.getPriority();

        // High-priority requests are "lesser" so they are sorted to the front.
        // Equal priorities are sorted by sequence number to provide FIFO ordering.
        return left == right ? this.mSequence - other.mSequence : right.ordinal() - left.ordinal();
    }

    /**
     * Priority values. Requests will be processed from higher priorities to lower priorities, in FIFO order.
     */
    public enum Priority {
        LOW, NORMAL, HIGH, IMMEDIATE
    }

}
