package com.android.volley.http.entity.mime;

import com.android.volley.http.entity.mime.content.ContentBody;

/**
 * FormBodyPart class represents a content body that can be used as a part of multipart encoded
 * entities. This class automatically populates the header with standard fields based on
 * the content description of the enclosed body.
 */
public class FormBodyPart {

    private final String name;
    private final Header header;

    private final ContentBody body;

    public FormBodyPart(final String name, final ContentBody body) {
        super();
        if (name == null) {
            throw new IllegalArgumentException("Name may not be null");
        }
        if (body == null) {
            throw new IllegalArgumentException("Body may not be null");
        }
        this.name = name;
        this.body = body;
        this.header = new Header();

        generateContentDisp(body);
        generateContentType(body);
        generateTransferEncoding(body);
    }

    public String getName() {
        return this.name;
    }

    public ContentBody getBody() {
        return this.body;
    }

    public Header getHeader() {
        return this.header;
    }

    public void addField(final String name, final String value) {
        if (name == null) {
            throw new IllegalArgumentException("Field name may not be null");
        }
        this.header.addField(new MinimalField(name, value));
    }

    protected void generateContentDisp(final ContentBody body) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("form-data; name=\"");
        buffer.append(getName());
        buffer.append("\"");
        if (body.getFilename() != null) {
            buffer.append("; filename=\"");
            buffer.append(body.getFilename());
            buffer.append("\"");
        }
        addField(MIME.CONTENT_DISPOSITION, buffer.toString());
    }

    protected void generateContentType(final ContentBody body) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(body.getMimeType()); // MimeType cannot be null
        if (body.getCharset() != null) { // charset may legitimately be null
            buffer.append("; charset=");
            buffer.append(body.getCharset());
        }
        addField(MIME.CONTENT_TYPE, buffer.toString());
    }

    protected void generateTransferEncoding(final ContentBody body) {
        addField(MIME.CONTENT_TRANSFER_ENC, body.getTransferEncoding()); // TE cannot be null
    }

}
