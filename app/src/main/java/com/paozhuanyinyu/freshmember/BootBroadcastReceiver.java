package com.paozhuanyinyu.freshmember;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/8/18.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences sp = context.getSharedPreferences("fresh_member", Context.MODE_PRIVATE);
            if(sp.getBoolean("notification_switch",false)){
                Intent service = new Intent(context, MyService.class);
                context.startService(service);
            }

        }
    }
}
