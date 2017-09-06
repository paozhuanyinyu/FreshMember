package com.paozhuanyinyu.freshmember;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import paozhuanyinyu.com.freshmember.R;


public class MainActivity extends AppCompatActivity {
    private SwitchView sb_switch;
    private SwitchView sb_switch_notification;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        sb_switch = (SwitchView) findViewById(R.id.sb_switch);
        sb_switch_notification = (SwitchView) findViewById(R.id.sb_switch_notification);


        sb_switch.setOpened(isAccessibilitySettingsOn(this));
        sb_switch.setOnStateChangedListener(new SwitchView.OnStateChangedListener(){

            @Override
            public void toggleToOn(View view) {
                sb_switch.toggleSwitch(true);
            }

            @Override
            public void toggleToOff(View view) {
                sb_switch.toggleSwitch(false);
            }
        });
        sp = getSharedPreferences("fresh_member", Context.MODE_PRIVATE);
        sb_switch_notification.setOpened(sp.getBoolean("notification_switch",false));
        sb_switch_notification.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                sb_switch_notification.toggleSwitch(true);
                sp.edit().putBoolean("notification_switch",true).commit();
                NotificationUtils.showNotification(MainActivity.this);
            }

            @Override
            public void toggleToOff(View view) {
                sb_switch_notification.toggleSwitch(false);
                sp.edit().putBoolean("notification_switch",false).commit();
                NotificationUtils.hideNotification(MainActivity.this);
            }
        });
    }
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/.MyService";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v("MainActivity","accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("MainActivity","Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v("MainActivity","***ACCESSIBILIY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    Log.v("MainActivity","-------------- > accessabilityService :: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        Log.v("MainActivity","We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v("MainActivity","***ACCESSIBILIY IS DISABLED***");
        }
        return accessibilityFound;
    }
}
