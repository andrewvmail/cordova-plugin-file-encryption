package com.lateph;

import android.app.Application;
import android.util.Log;
import com.facebook.soloader.SoLoader;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        Log.d("MyApplication", "onCreate");
        super.onCreate();
        SoLoader.init(this, false);
    }
}
