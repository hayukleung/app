package com.android.volley.http;

import java.net.MalformedURLException;
import java.net.URL;


public class HttpPost extends HttpEntityEnclosingRequest {

    public final static String METHOD_NAME = "POST";

    public HttpPost(final URL uri) {
        super();
        setURL(uri);
    }

    /**
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public HttpPost(final String url) {
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