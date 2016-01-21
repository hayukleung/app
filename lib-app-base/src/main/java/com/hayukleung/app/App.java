package com.hayukleung.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

public class App extends MultiDexApplication {

    public static final Handler MAIN = new Handler();
    public static Activity ACTIVITY;
    public static boolean VISIBLE;

    private static App mInstance;
    private ObjectMapper mObjectMapper;
    private String mToken;
    private SharedPreferences preferences;

    public static App Instance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        mInstance = this;
        Library.init(this);
        new Configuration(this);
    }

    public SharedPreferences getPreferences() {
        return getSharedPreferences(getPackageName(), Context.MODE_MULTI_PROCESS);
    }

    public String getToken() {
        if (mToken == null) {
            mToken = Configuration.Instance().getToken();
        }
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
        Configuration.Instance().setToken(token);
    }

    public ObjectMapper getObjectMapper() {
        if (mObjectMapper == null) {
            mObjectMapper = new ObjectMapper();
            mObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mObjectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        }
        return mObjectMapper;
    }
}
