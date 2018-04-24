package com.abt.key;

import android.app.Application;

import com.abt.key.util.Utils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * Created by hwq on 2018/4/24.
 */
public class KeyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

}
