package com.android.volley.http;

import com.android.volley.http.entity.HttpEntity;

public class HttpResponse extends AbstractHttpMessage {

    private int mResponseCode;
    private String mMessage;

    private HttpEntity mEntity;

    public HttpResponse(int responseCode, String message) {
        super();
        this.mResponseCode = responseCode;
        this.mMessage = message;
    }

    public int getResponseCode() {
        return mResponseCode;
    }

    public String getMessage() {
        return mMessage;
    }

    public HttpEntity getEntity() {
        return this.mEntity;
    }

    public void setEntity(HttpEntity entity) {
        this.mEntity = entity;
    }

}
