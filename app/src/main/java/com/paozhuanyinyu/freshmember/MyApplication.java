package com.paozhuanyinyu.freshmember;

import android.app.Application;
import android.util.Log;
import com.gyf.cactus.Cactus;
import paozhuanyinyu.com.freshmember.BuildConfig;

/**
 * Created by Administrator on 2017/9/8.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyApplication","Application onCreate");
        Cactus.getInstance()
                .isDebug(BuildConfig.DEBUG)
       .register(this);
    }
}
