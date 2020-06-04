package com.paozhuanyinyu.freshmember;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import paozhuanyinyu.com.freshmember.R;


public class MainActivity extends AppCompatActivity {
    private static final int OPEN_SERVICE = 92;
    private SwitchView sb_switch;
    private SwitchView sb_switch_notification;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(sp == null){
            sp = getSharedPreferences("fresh_member", Context.MODE_PRIVATE);
        }
        if((isAccessibilitySettingsOn(this) && !sb_switch.isOpened())  || (!isAccessibilitySettingsOn(this) && sb_switch.isOpened())){
            sb_switch.setOpened(isAccessibilitySettingsOn(this));
        }
        if((isAccessibilitySettingsOn(this)&&sp.getBoolean("notification_switch",false) && !sb_switch_notification.isOpened())  || (!(isAccessibilitySettingsOn(this)&&sp.getBoolean("notification_switch",false)) && sb_switch_notification.isOpened())){
            sb_switch_notification.setOpened(isAccessibilitySettingsOn(this)&&sp.getBoolean("notification_switch",false));
        }

    }

    @Override
    public void onBackPressed() {
        Intent launcherIntent =new Intent(Intent.ACTION_MAIN);
        launcherIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        startActivity(launcherIntent);
    }
    private void initView() {
        sb_switch = (SwitchView) findViewById(R.id.sb_switch);
        sb_switch_notification = (SwitchView) findViewById(R.id.sb_switch_notification);


        sb_switch.setOnStateChangedListener(new SwitchView.OnStateChangedListener(){

            @Override
            public void toggleToOn(View view) {
                sb_switch.toggleSwitch(true);
                if(!isAccessibilitySettingsOn(MainActivity.this)){
                    startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS),OPEN_SERVICE);
                }
            }

            @Override
            public void toggleToOff(View view) {
                if(!isAccessibilitySettingsOn(MainActivity.this)){
                    sb_switch.toggleSwitch(false);
                }else{
                    Toast.makeText(MainActivity.this,"服务是打开状态",Toast.LENGTH_SHORT).show();
                    sb_switch.toggleSwitch(true);
                }
            }
        });
        sp = getSharedPreferences("fresh_member", Context.MODE_PRIVATE);

        sb_switch_notification.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                if(isAccessibilitySettingsOn(MainActivity.this)){
                    sb_switch_notification.toggleSwitch(true);
                    sp.edit().putBoolean("notification_switch",true).commit();
                    NotificationUtils.showNotification(MainActivity.this,getComponentName().toShortString());
                }else{
                    Toast.makeText(MainActivity.this,"请先打开服务",Toast.LENGTH_SHORT).show();
                }

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
        final String service = getPackageName() + "/" + getPackageName() + ".MyService";
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
