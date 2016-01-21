package com.android.volley.http;

import com.android.volley.http.entity.Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpGet extends HttpRequestBase {

    public final static String METHOD_NAME = "GET";

    private Map<String, String> mParameter;

    public HttpGet(final URL uri) {
        super();
        setURL(uri);
        mParameter = new HashMap<String, String>();
    }

    /**
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public HttpGet(final String url) {
        super();
        mParameter = new HashMap<String, String>();
        try {
            setURL(new URL(url));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public URL getURL() {
        if (mParameter == null || mParameter.size() == 0) {
            return super.getURL();
        } else {
            StringBuilder builder = new StringBuilder(super.getURL().toString());
            try {
                return new URL(builder.append("?").append(Utils.format(mParameter)).toString());
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

    public void addParameter(String key, String value) {
        mParameter.put(key, value);
    }

    public void removeParameter(String key) {
        mParameter.remove(key);
    }

    public  String getParameterString(){
        return com.android.volley.http.entity.Utils.format(mParameter);
    }
}