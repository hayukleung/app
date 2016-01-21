package com.hayukleung.app.widget.media.mediapicker;

import android.content.Context;

import com.hayukleung.app.Library;
import com.hayukleung.app.widget.media.picasso.DefaultDownloader;
import com.hayukleung.app.widget.media.picasso.Picasso;
import com.squareup.okhttp.OkUrlFactory;

public class ImageLoader {
    private static Picasso Instance;

    private ImageLoader() {
    }

    public static Picasso Instance() {
        if (Instance == null) {
            Context context = Library.Instance().getContext();
            Picasso.Builder builder = new Picasso.Builder(context).downloader(new DefaultDownloader(new OkUrlFactory(Utils.getDefaultHttpClient())));
            builder.loggingEnabled(Library.Instance().isDebug());
            init(builder);
        }
        return Instance;
    }

    public static void init() {
        init(new Picasso.Builder(Library.Instance().getContext()));
    }

    public static void init(Picasso.Builder builder) {
        Instance = builder.build();
    }
}
