package com.paozhuanyinyu.freshmember;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import paozhuanyinyu.com.freshmember.R;

/**
 * Created by Administrator on 2017/9/6.
 */

public class MyService  extends AccessibilityService{
    private String currentActivityName;
    private SharedPreferences sp;
    public static final int NOTIFICATION_ID=0x11;
    @Override
    public void onCreate() {
        super.onCreate();
        //API 18以下，直接发送Notification并将其置为前台
        if (Build.VERSION.SDK_INT <Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startForeground(NOTIFICATION_ID, new Notification());
        } else {
            //API 18以上，发送Notification并将其置为前台后，启动InnerService
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            startForeground(NOTIFICATION_ID, builder.build());
            startService(new Intent(this, InnerService.class));
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public  static class InnerService extends JobService {
        @Override
        public boolean onStartJob(JobParameters jobParameters) {
            return false;
        }

        @Override
        public boolean onStopJob(JobParameters jobParameters) {
            return false;
        }


        @Override
        public void onCreate() {
            super.onCreate();
            startJobSheduler();
            //发送与KeepLiveService中ID相同的Notification，然后将其取消并取消自己的前台显示
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            startForeground(NOTIFICATION_ID, builder.build());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopForeground(true);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel(NOTIFICATION_ID);
                    stopSelf();
                }
            }, 100);

        }
        public void startJobSheduler() {
            try {
                JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(getPackageName(), MyService.class.getName()));
                builder.setPeriodic(5);
                builder.setPersisted(true);
                JobScheduler jobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.schedule(builder.build());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
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
        NotificationUtils.hideNotification(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("MyService","onUnbind");
        NotificationUtils.hideNotification(this);
        return super.onUnbind(intent);
    }
    private void getCurrentActivityName(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return;
        }
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
