package com.android.volley;

/**
 * Provides a thread for performing network dispatch from a queue of requests.
 */
public class Dispatcher extends Thread {

    /**
     * Used for telling us to die.
     */
    protected volatile boolean mQuit = false;

    /**
     * Forces this dispatcher to quit immediately. If any requests are still in the queue, they are not guaranteed to be processed.
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }
}
