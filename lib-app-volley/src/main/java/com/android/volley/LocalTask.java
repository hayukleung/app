package com.android.volley;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public abstract class LocalTask<T> extends Task<T> {

    public LocalTask(Listener<T> listener, ErrorListener errorListener) {
        super(listener, errorListener);
    }

    public abstract T perform();

}
