package com.android.volley;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A request dispatch queue with a thread pool of dispatchers.
 * <p>
 * Calling {@link #add(Task)} will enqueue the given Request for dispatch, resolving from a worker thread, and then delivering a parsed response on the main thread.
 */
public abstract class TaskQueue<T extends Task> {
    /**
     * The set of all requests currently being processed by this RequestQueue. A Request will be in this set if it is waiting in any queue or currently being processed by any dispatcher.
     */
    protected final Set<T> mCurrentRequests = new HashSet<T>();
    /**
     * The queue of tasks that are actually going out to the request.
     */
    protected final PriorityBlockingQueue<T> mQueue = new PriorityBlockingQueue<T>();
    /**
     * Response delivery mechanism.
     */
    protected final ResponseDelivery mDelivery;
    /**
     * Used for generating monotonically-increasing sequence numbers for requests.
     */
    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    protected TaskQueue(ResponseDelivery mDelivery) {
        this.mDelivery = mDelivery;
    }

    /**
     * Gets a sequence number.
     */
    public int getSequenceNumber() {
        return mSequenceGenerator.incrementAndGet();
    }

    /**
     * Starts the dispatchers in this queue.
     */
    public abstract void start();

    /**
     * Stops the all dispatchers.
     */
    public abstract void stop();

    /**
     * Adds a Request to the dispatch queue.
     *
     * @param request The request to service
     * @return The passed-in request
     */
    public abstract T add(T request);

    public abstract Response request(T task);

    /**
     * Called from {@link Task#finish(String)}, indicating that processing of the given request has finished.
     * <p/>
     * <p>
     * Releases waiting requests for <code>request.getCacheKey()</code> if <code>request.shouldCache()</code>.
     * </p>
     */
    abstract void finish(T request);

    /**
     * Cancels all requests in this queue for which the given filter applies.
     *
     * @param filter The filtering function to use
     */
    public void cancelAll(RequestFilter filter) {
        synchronized (mCurrentRequests) {
            for (Task request : mCurrentRequests) {
                if (filter.apply(request)) {
                    request.cancel();
                }
            }
        }
    }

    /**
     * Cancels all requests in this queue with the given tag. Tag must be non-null and equality is by identity.
     */
    public void cancelAll(final Object tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Cannot cancelAll with a null tag");
        }
        cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Task request) {
                return request.getTag() == tag;
            }
        });
    }

    /**
     * A simple predicate or filter interface for Requests, for use by {@link com.android.volley.LocalTaskQueue#cancelAll(com.android.volley.TaskQueue.RequestFilter)}.
     */
    public interface RequestFilter {
        public boolean apply(Task request);
    }
}
