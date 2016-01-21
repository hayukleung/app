package com.android.volley.http;

import com.android.volley.http.entity.Header;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractHttpMessage {

    protected final List<Header> mHeaders;

    public AbstractHttpMessage() {
        mHeaders = new ArrayList<Header>(16);
    }

    public void addHeader(String name, String value) {
        this.mHeaders.add(new Header(name, value));
    }

    public void addHeader(final Header header) {
        if (header == null) {
            return;
        }
        mHeaders.add(header);
    }

    /**
     * Removes the given header.
     *
     * @param header the header to remove
     */
    public void removeHeader(final Header header) {
        if (header == null) {
            return;
        }
        mHeaders.remove(header);
    }

    public void removeHeaders(final String name) {
        if (name == null) {
            return;
        }
        for (Iterator<Header> it = mHeaders.iterator(); it.hasNext(); ) {
            Header header = it.next();
            if (name.equalsIgnoreCase(header.getName())) {
                it.remove();
            }
        }
    }

    /**
     * Replaces the first occurence of the header with the same name. If no header with the same name is found the given
     * header is added to the end of the list.
     *
     * @param header the new header that should replace the first header with the same name if present in the list.
     */
    public void updateHeader(final Header header) {
        if (header == null) {
            return;
        }
        // HTTPCORE-361 : we don't use the for-each syntax, i.e.
        // for (Header header : headers)
        // as that creates an Iterator that needs to be garbage-collected
        for (int i = 0; i < this.mHeaders.size(); i++) {
            final Header current = this.mHeaders.get(i);
            if (current.getName().equalsIgnoreCase(header.getName())) {
                this.mHeaders.set(i, header);
                return;
            }
        }
        this.mHeaders.add(header);
    }

    public Header[] getHeaders(final String name) {
        final List<Header> headersFound = new ArrayList<Header>();
        // HTTPCORE-361 : we don't use the for-each syntax, i.e.
        // for (Header header : headers)
        // as that creates an Iterator that needs to be garbage-collected
        for (int i = 0; i < this.mHeaders.size(); i++) {
            final Header header = this.mHeaders.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                headersFound.add(header);
            }
        }

        return headersFound.toArray(new Header[headersFound.size()]);
    }

    public Header[] getAllHeaders() {
        return mHeaders.toArray(new Header[mHeaders.size()]);
    }

}