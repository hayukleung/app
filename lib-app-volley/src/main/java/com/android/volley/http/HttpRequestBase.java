package com.android.volley.http;

import java.net.URL;

public abstract class HttpRequestBase extends AbstractHttpMessage {

    private URL mUrl;

    public HttpRequestBase() {
        super();
    }

    public abstract String getMethod();

    public URL getURL() {
        return this.mUrl;
    }

    public void setURL(final URL url) {
        this.mUrl = url;
    }

}
