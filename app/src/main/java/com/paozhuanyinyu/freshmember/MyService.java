package com.paozhuanyinyu.freshmember;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by Administrator on 2017/9/6.
 */

public class MyService  extends AccessibilityService{
    private String currentActivityName;
    private SharedPreferences sp;
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        sp = getSharedPreferences("fresh_member", Context.MODE_PRIVATE);
        Log.d("MyService","onServiceConnected");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.d("MyService","onAccessibilityEvent");
        getCurrentActivityName(accessibilityEvent);
    }

    @Override
    public void onInterrupt() {
        Log.d("MyService","onInterrupt");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("MyService","onUnbind");

        return super.onUnbind(intent);
    }
    private void getCurrentActivityName(AccessibilityEvent event) {
//        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            return;
//        }
        try {
            String pkgName = event.getPackageName().toString();
            String className = event.getClassName().toString();
            ComponentName componentName = new ComponentName(pkgName, className);
            getPackageManager().getActivityInfo(componentName, 0);
            currentActivityName = componentName.flattenToShortString();
            Log.d("MyService", "cur=" + currentActivityName);
            if(sp.getBoolean("notification_switch",false)){
                NotificationUtils.updateNotification(this,currentActivityName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            //只是窗口变化，并无activity调转
            Log.d("MyService", "e=" + e.getLocalizedMessage());
        }
    }
}
