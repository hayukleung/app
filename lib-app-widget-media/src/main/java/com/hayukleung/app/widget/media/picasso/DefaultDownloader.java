package com.hayukleung.app.widget.media.picasso;

import android.net.Uri;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DefaultDownloader implements Downloader {
    private OkUrlFactory urlFactory;

    public DefaultDownloader() {
        urlFactory = new OkUrlFactory(new OkHttpClient());
    }

    public DefaultDownloader(OkUrlFactory factory) {
        this.urlFactory = factory;
    }

    protected HttpURLConnection openConnection(Uri uri) throws MalformedURLException {
        HttpURLConnection connection = urlFactory.open(new URL(uri.toString()));
        connection.setConnectTimeout(com.hayukleung.app.widget.media.picasso.Utils.DEFAULT_CONNECT_TIMEOUT);
        connection.setReadTimeout(com.hayukleung.app.widget.media.picasso.Utils.DEFAULT_READ_TIMEOUT);
        return connection;
    }

    public Response load(Uri uri) throws IOException {
        HttpURLConnection connection = openConnection(uri);
        connection.setUseCaches(false);
        int responseCode = connection.getResponseCode();
        if (responseCode >= 300) {
            connection.disconnect();
            throw new ResponseException(responseCode + " " + connection.getResponseMessage(), responseCode);
        }
        return new Response(connection.getContentLength(), connection.getInputStream());
    }

    public void shutdown() {
    }

}