package com.android.volley.http.entity;

import com.android.volley.http.HTTP;

import java.io.*;

/**
 * A self contained, repeatable entity that obtains its content from a {@link String}.
 */
public class StringEntity extends HttpEntityBase {

    protected final byte[] content;

    public StringEntity(final String string) {
        this(string, null, null);
    }

    /**
     * Creates a StringEntity with the specified content, MIME type and charset
     *
     * @param string   content to be used. Not {@code null}.
     * @param mimeType MIME type to be used. May be {@code null}, in which case the default is {@link HTTP#PLAIN_TEXT_TYPE}
     *                 i.e. "text/plain"
     * @param charset  character set to be used. May be {@code null}, in which case the default is
     *                 {@link HTTP#DEFAULT_CONTENT_CHARSET} i.e. "UTF_8"
     * @throws java.io.UnsupportedEncodingException If the named charset is not supported.
     */
    public StringEntity(final String string, final String mimeType, final String charset) {
        super();
        try {
            final String mt = mimeType != null ? mimeType : HTTP.PLAIN_TEXT_TYPE;
            final String cs = charset != null ? charset : HTTP.DEFAULT_CONTENT_CHARSET;
            this.content = string.getBytes(cs);
            setContentType(mt + HTTP.CHARSET_PARAM + cs);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public boolean isRepeatable() {
        return true;
    }

    public long getContentLength() {
        return this.content.length;
    }

    public InputStream getContent() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    public void writeTo(final OutputStream outstream) throws IOException {
        outstream.write(this.content);
        outstream.flush();
    }

    /**
     * Tells that this entity is not streaming.
     *
     * @return <code>false</code>
     */
    public boolean isStreaming() {
        return false;
    }

} // class StringEntity
