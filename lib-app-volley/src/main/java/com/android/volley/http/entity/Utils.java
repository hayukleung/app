package com.android.volley.http.entity;

import com.android.volley.http.HTTP;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

public class Utils {

    public static String format(Map<String, String> params) {
        try {
            final StringBuilder result = new StringBuilder();
            Set<String> keys = params.keySet();
            for (final String key : keys) {
                final String value = params.get(key);
                if (value == null)
                    continue;
                if (result.length() > 0)
                    result.append("&");
                result.append(URLEncoder.encode(key, HTTP.DEFAULT_CONTENT_CHARSET));
                result.append("=");
                result.append(URLEncoder.encode(value, HTTP.DEFAULT_CONTENT_CHARSET));
            }
            return result.toString();
        } catch (UnsupportedEncodingException e) {
            // Impossible!
            throw new IllegalArgumentException(e);
        }
    }

    public static void safeClose(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                // Silent
            }
        }
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }
}
