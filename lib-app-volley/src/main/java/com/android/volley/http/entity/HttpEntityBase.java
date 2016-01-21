package com.android.volley.http.entity;

import com.android.volley.http.HTTP;

public abstract class HttpEntityBase implements HttpEntity {

    protected static final int OUTPUT_BUFFER_SIZE = 4096;

    protected Header mContentType;
    protected Header mContentEncoding;

    @Override
    public Header getContentType() {
        return mContentType;
    }

    public void setContentType(final String ctString) {
        Header h = null;
        if (ctString != null) {
            h = new Header(HTTP.CONTENT_TYPE, ctString);
        }
        setContentType(h);
    }

    public void setContentType(final Header contentType) {
        this.mContentType = contentType;
    }

    @Override
    public Header getContentEncoding() {
        return mContentEncoding;
    }

    public void setContentEncoding(final String ceString) {
        Header h = null;
        if (ceString != null) {
            h = new Header(HTTP.CONTENT_ENCODING, ceString);
        }
        setContentEncoding(h);
    }

    public void setContentEncoding(final Header contentEncoding) {
        this.mContentEncoding = contentEncoding;
    }
}
