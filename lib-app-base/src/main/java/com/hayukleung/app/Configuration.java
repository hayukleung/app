package com.hayukleung.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Configuration {

    private static final String PREF = "configuration.pref";
    private static final String TOKEN = "token";
    private static Configuration mInstance;
    private SharedPreferences mPreferences;
    private Editor mEditor;

    Configuration(Context context) {
        mPreferences = context.getApplicationContext().getSharedPreferences(PREF, Context.MODE_MULTI_PROCESS);
        mEditor = mPreferences.edit();
        mInstance = this;
    }

    public static Configuration Instance() {
        return mInstance;
    }

    public SharedPreferences getPreferences() {
        return mPreferences;
    }

    public String getToken() {
        return mPreferences.getString(TOKEN, null);
    }

    public void setToken(String token) {
        mEditor.putString(TOKEN, token);
        mEditor.commit();
    }
}
