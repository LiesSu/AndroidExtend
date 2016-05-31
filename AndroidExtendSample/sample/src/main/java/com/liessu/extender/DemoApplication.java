package com.liessu.extender;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by Administrator on 2016/3/25.
 */
public class DemoApplication extends Application{
    public static String TAG = "DemoApplication";
    private static Context mContext;

    public static Context getContext() {
        if (mContext == null) {
            throw new RuntimeException("Unknown Error");
        }
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Demo Application onCreate...");
        mContext = getApplicationContext();
    }
}
