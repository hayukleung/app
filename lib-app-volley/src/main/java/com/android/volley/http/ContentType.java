package com.android.volley.http;

/**
 * Content type information consisting of a MIME type and an optional charset.
 * <p>
 * This class makes no attempts to verify validity of the MIME type.
 * The input parameters of the {@link #create(String, String)} method, however, may not
 * contain characters <">, <;>, <,> reserved by the HTTP specification.
 */
public final class ContentType {

    // constants
    public static final String APPLICATION_ATOM_XML = "application/atom+xml";
    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String APPLICATION_SVG_XML = "application/svg+xml";
    public static final String APPLICATION_XHTML_XML = "application/xhtml+xml";
    public static final String APPLICATION_XML = "application/xml";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_XML = "text/xml";
    public static final String WILDCARD = "*/*";

}
