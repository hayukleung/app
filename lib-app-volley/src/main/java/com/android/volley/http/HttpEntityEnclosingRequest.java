package com.android.volley.http;

import com.android.volley.http.entity.HttpEntity;

public abstract class HttpEntityEnclosingRequest extends HttpRequestBase {
    private HttpEntity mEntity;

    public HttpEntityEnclosingRequest() {
        super();
    }

    public HttpEntity getEntity() {
        return mEntity;
    }

    public void setEntity(HttpEntity entity) {
        this.mEntity = entity;
    }

}
