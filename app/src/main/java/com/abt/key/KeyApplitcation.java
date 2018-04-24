package com.abt.key;

import android.app.Application;

/**
 * Created by hwq on 2018/4/24.
 */
public class KeyApplitcation extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.addLogAdapter(new AndroidLogAdapter());
    }

}
