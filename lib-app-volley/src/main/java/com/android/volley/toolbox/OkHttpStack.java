package com.android.volley.toolbox;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkTask;
import com.android.volley.http.HttpEntityEnclosingRequest;
import com.android.volley.http.HttpRequestBase;
import com.android.volley.http.HttpResponse;
import com.android.volley.http.entity.Header;
import com.android.volley.http.entity.HttpEntity;
import com.android.volley.http.entity.InputStreamEntity;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class OkHttpStack implements HttpStack {
    private OkUrlFactory mUrlFactory;

    public OkHttpStack() {
        mUrlFactory = new OkUrlFactory(new OkHttpClient());
    }

    @Override
    public HttpResponse performRequest(NetworkTask httprequest, Map<String, String> additionalHeaders)
            throws IOException, AuthFailureError {
        HttpRequestBase request = httprequest.getRequest();
        HttpURLConnection connection = mUrlFactory.open(request.getURL());
        int timeoutMs = httprequest.getTimeoutMs();
        connection.setConnectTimeout(timeoutMs);
        connection.setReadTimeout(timeoutMs);

        HashMap<String, String> map = new HashMap<String, String>();
        map.putAll(additionalHeaders);
        map.putAll(httprequest.getHeaders());
        for (String headerName : map.keySet()) {
            connection.addRequestProperty(headerName, map.get(headerName));
        }

        connection.setRequestMethod(request.getMethod());
        for (Header header : request.getAllHeaders()) {
            connection.addRequestProperty(header.getName(), header.getValue());
        }

        // Stream the request body.
        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
            if (entity != null) {
                connection.setDoOutput(true);
                Header type = entity.getContentType();
                if (type != null) {
                    connection.addRequestProperty(type.getName(), type.getValue());
                }
                Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    connection.addRequestProperty(encoding.getName(), encoding.getValue());
                }
                if (entity.getContentLength() < 0) {
                    connection.setChunkedStreamingMode(0);
                } else if (entity.getContentLength() <= 8192) {
                    // Buffer short, fixed-length request bodies. This costs memory, but permits the request
                    // to be transparently retried if there is a connection failure.
                    connection.addRequestProperty("Content-Length", Long.toString(entity.getContentLength()));
                } else {
                    connection.setFixedLengthStreamingMode((int) entity.getContentLength());
                }
                entity.writeTo(connection.getOutputStream());
            }
        }

        // Read the response headers.
        int responseCode = connection.getResponseCode();
        String message = connection.getResponseMessage();
        HttpResponse response = new HttpResponse(responseCode, message);
        // Get the response body ready to stream.
        InputStream responseBody = responseCode < HttpURLConnection.HTTP_BAD_REQUEST ? connection.getInputStream()
                : connection.getErrorStream();
        InputStreamEntity entity = new InputStreamEntity(responseBody, connection.getContentLength());
        for (int i = 0; true; i++) {
            String name = connection.getHeaderFieldKey(i);
            if (name == null) {
                break;
            }
            Header header = new Header(name, connection.getHeaderField(i));
            response.addHeader(header);
            if (name.equalsIgnoreCase("Content-Type")) {
                entity.setContentType(header);
            } else if (name.equalsIgnoreCase("Content-Encoding")) {
                entity.setContentEncoding(header);
            }
        }
        response.setEntity(entity);

        return response;
    }

}
