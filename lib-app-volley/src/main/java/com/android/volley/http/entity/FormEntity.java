package com.android.volley.http.entity;

import com.android.volley.http.ContentType;

import java.util.HashMap;
import java.util.Map;

public class FormEntity extends StringEntity {

    public FormEntity(String string) {
        this(string, null);
    }

    public FormEntity(String string, String charset) {
        super(string, ContentType.APPLICATION_FORM_URLENCODED, charset);
    }

    public static class Builder {
        private Map<String, String> mParameter;

        public Builder() {
            mParameter = new HashMap<String, String>();
        }

        public Builder addParameter(String key, String value) {
            mParameter.put(key, value);
            return this;
        }

        public Builder removeParameter(String key) {
            mParameter.remove(key);
            return this;
        }

        public FormEntity build() {
            return build(null);
        }

        public FormEntity build(String charset) {
            return new FormEntity(Utils.format(mParameter), charset);
        }

    }

}
