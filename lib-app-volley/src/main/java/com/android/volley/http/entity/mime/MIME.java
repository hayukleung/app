package com.android.volley.http.entity.mime;

import com.android.volley.http.HTTP;

import java.nio.charset.Charset;

public final class MIME {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TRANSFER_ENC = "Content-Transfer-Encoding";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    public static final String ENC_8BIT = "8bit";
    public static final String ENC_BINARY = "binary";

    /**
     * The default character set to be used, i.e. "UTF_8"
     */
    public static final Charset DEFAULT_CHARSET = Charset.forName(HTTP.UTF_8);

}
