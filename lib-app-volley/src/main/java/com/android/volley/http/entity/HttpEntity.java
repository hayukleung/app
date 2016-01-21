package com.android.volley.http.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface HttpEntity {

    /**
     * Tells the length of the content, if known.
     *
     * @return the number of bytes of the content, or a negative number if unknown. If the content length is known but
     * exceeds {@link Long#MAX_VALUE Long.MAX_VALUE}, a negative number is returned.
     */
    long getContentLength();

    /**
     * Obtains the Content-Type header, if known. This is the header that should be used when sending the entity, or the
     * one that was received with the entity. It can include a charset attribute.
     *
     * @return the Content-Type header for this entity, or <code>null</code> if the content type is unknown
     */
    Header getContentType();

    /**
     * Obtains the Content-Encoding header, if known. This is the header that should be used when sending the entity, or
     * the one that was received with the entity. Wrapping entities that modify the content encoding should adjust this
     * header accordingly.
     *
     * @return the Content-Encoding header for this entity, or <code>null</code> if the content encoding is unknown
     */
    Header getContentEncoding();

    /**
     * Returns a content stream of the entity. {@link #isRepeatable Repeatable} entities are expected to create a new
     * instance of {@link java.io.InputStream} for each invocation of this method and therefore can be consumed multiple times.
     * Entities that are not {@link #isRepeatable repeatable} are expected to return the same {@link java.io.InputStream}
     * instance and therefore may not be consumed more than once.
     * <p>
     * IMPORTANT: Please note all entity implementations must ensure that all allocated resources are properly
     * deallocated after the {@link java.io.InputStream#close()} method is invoked.
     *
     * @return content stream of the entity.
     * @throws java.io.IOException   if the stream could not be created
     * @throws IllegalStateException if content stream cannot be created.
     * @see #isRepeatable()
     */
    InputStream getContent() throws IOException, IllegalStateException;

    /**
     * Writes the entity content out to the output stream.
     * <p>
     * <p>
     * IMPORTANT: Please note all entity implementations must ensure that all allocated resources are properly
     * deallocated when this method returns.
     *
     * @param outstream the output stream to write entity content to
     * @throws java.io.IOException if an I/O error occurs
     */
    void writeTo(OutputStream outstream) throws IOException;

}
