package com.android.volley.http.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InputStreamEntity extends HttpEntityBase {

    private final InputStream content;
    private final long length;

    /**
     * Creates an entity with an unknown length. Equivalent to {@code new InputStreamEntity(instream, -1)}.
     *
     * @param instream input stream
     * @throws IllegalArgumentException if {@code instream} is {@code null}
     */
    public InputStreamEntity(final InputStream instream) {
        this(instream, -1);
    }

    /**
     * @param instream    input stream
     * @param length      of the input stream, {@code -1} if unknown
     * @param contentType for specifying the {@code Content-Type} header, may be {@code null}
     * @throws IllegalArgumentException if {@code instream} is {@code null}
     */
    public InputStreamEntity(final InputStream instream, final long length) {
        super();
        this.content = instream;
        this.length = length;
    }

    /**
     * @return the content length or {@code -1} if unknown
     */
    public long getContentLength() {
        return this.length;
    }

    public InputStream getContent() throws IOException {
        return this.content;
    }

    /**
     * Writes bytes from the {@code InputStream} this entity was constructed with to an {@code OutputStream}. The
     * content length determines how many bytes are written. If the length is unknown ({@code -1}), the stream will be
     * completely consumed (to the end of the stream).
     */
    public void writeTo(final OutputStream outstream) throws IOException {
        final InputStream instream = this.content;
        try {
            final byte[] buffer = new byte[OUTPUT_BUFFER_SIZE];
            int l;
            if (this.length < 0) {
                // consume until EOF
                while ((l = instream.read(buffer)) != -1) {
                    outstream.write(buffer, 0, l);
                }
            } else {
                // consume no more than length
                long remaining = this.length;
                while (remaining > 0) {
                    l = instream.read(buffer, 0, (int) Math.min(OUTPUT_BUFFER_SIZE, remaining));
                    if (l == -1) {
                        break;
                    }
                    outstream.write(buffer, 0, l);
                    remaining -= l;
                }
            }
        } finally {
            instream.close();
        }
    }

    public boolean isStreaming() {
        return true;
    }

}
