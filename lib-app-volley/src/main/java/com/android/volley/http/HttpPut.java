package com.android.volley.http;

import java.net.MalformedURLException;
import java.net.URL;


public class HttpPut extends HttpEntityEnclosingRequest {

    public final static String METHOD_NAME = "PUT";

    public HttpPut(final URL uri) {
        super();
        setURL(uri);
    }

    /**
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public HttpPut(final String url) {
        super();
        try {
            setURL(new URL(url));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

}