package com.android.volley.http.entity.mime;

/**
 * Minimal MIME field.
 */
public class MinimalField {

    private final String name;
    private final String value;

    MinimalField(final String name, final String value) {
        super();
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getBody() {
        return this.value;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(this.name);
        buffer.append(": ");
        buffer.append(this.value);
        return buffer.toString();
    }

}
