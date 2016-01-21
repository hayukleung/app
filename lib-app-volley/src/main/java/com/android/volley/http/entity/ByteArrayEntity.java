package com.android.volley.http.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * A self contained, repeatable entity that obtains its content from a byte array.
 */
public class ByteArrayEntity extends HttpEntityBase {

    private final byte[] b;
    private final int off, len;

    public ByteArrayEntity(final byte[] b, final String contentType) {
        super();
        this.b = b;
        this.off = 0;
        this.len = this.b.length;
        setContentType(contentType);
    }

    public ByteArrayEntity(final byte[] b, final int off, final int len, final String contentType) {
        super();
        if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) < 0) || ((off + len) > b.length)) {
            throw new IndexOutOfBoundsException("off: " + off + " len: " + len + " b.length: " + b.length);
        }
        this.b = b;
        this.off = off;
        this.len = len;
        setContentType(contentType);
    }

    public ByteArrayEntity(final byte[] b) {
        this(b, null);
    }

    public ByteArrayEntity(final byte[] b, final int off, final int len) {
        this(b, off, len, null);
    }

    public boolean isRepeatable() {
        return true;
    }

    public long getContentLength() {
        return this.len;
    }

    public InputStream getContent() {
        return new ByteArrayInputStream(this.b, this.off, this.len);
    }

    public void writeTo(final OutputStream outstream) throws IOException {
        outstream.write(this.b, this.off, this.len);
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

} // class ByteArrayEntity
