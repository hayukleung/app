package com.hayukleung.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class Library {

    private static Library mLibrary;
    Context mContext;

    private Library(Context context) {
        mContext = context;
    }

    public static void init(Context context) {
        if (mLibrary != null) {
            return;
        }
        mLibrary = new Library(context.getApplicationContext());
    }

    public static Library Instance() {
        return mLibrary;
    }

    public boolean isDebug() {
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            int flags = packageInfo.applicationInfo.flags;
            return (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public Context getContext() {
        return mContext;
    }
}
