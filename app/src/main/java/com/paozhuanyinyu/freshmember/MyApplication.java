package com.paozhuanyinyu.freshmember;

import android.app.Application;
import android.util.Log;

/**
 * Created by Administrator on 2017/9/8.
 */

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyApplication","Application onCreate");
    }
}
