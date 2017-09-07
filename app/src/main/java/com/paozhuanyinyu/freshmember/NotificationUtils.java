package com.paozhuanyinyu.freshmember;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import paozhuanyinyu.com.freshmember.R;

/**
 * Created by kay on 2017/9/6.
 */

public class NotificationUtils {
	private static final String MY_ACTION = "com.notifications.intent.action.ButtonClick";
	private static NotificationCompat.Builder mBuilder;
	private static NotificationManager mNotificationManager;
	public static void showNotification(final Context context, final String contentText){
		//创建一个NotificationManager的引用
		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager)context.getSystemService(ns);
		// 定义Notification的各种属性
		int icon = R.mipmap.ic_launcher; //通知图标
		CharSequence tickerText = "Hello"; //状态栏显示的通知文本提示
		long when = System.currentTimeMillis(); //通知产生的时间，会在通知信息里显示
		//用上面的属性初始化 Nofification
		mBuilder = new NotificationCompat.Builder(context);
		mBuilder.setSmallIcon(icon);
		mBuilder.setTicker(tickerText);
		mBuilder.setWhen(when);
		/*
		* 添加声音
		* notification.defaults |=Notification.DEFAULT_SOUND;
		* 或者使用以下几种方式
		* notification.sound = Uri.parse("file:///sdcard/notification/ringer.mp3");
		* notification.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");
		* 如果想要让声音持续重复直到用户对通知做出反应，则可以在notification的flags字段增加"FLAG_INSISTENT"
		* 如果notification的defaults字段包括了"DEFAULT_SOUND"属性，则这个属性将覆盖sound字段中定义的声音
		*/
		/*
		* 添加振动
		* notification.defaults |= Notification.DEFAULT_VIBRATE;
		* 或者可以定义自己的振动模式：
		* long[] vibrate = {0,100,200,300}; //0毫秒后开始振动，振动100毫秒后停止，再过200毫秒后再次振动300毫秒
		* notification.vibrate = vibrate;
		* long数组可以定义成想要的任何长度
		* 如果notification的defaults字段包括了"DEFAULT_VIBRATE",则这个属性将覆盖vibrate字段中定义的振动
		*/
		/*
		* 添加LED灯提醒
		* notification.defaults |= Notification.DEFAULT_LIGHTS;
		* 或者可以自己的LED提醒模式:
		* notification.ledARGB = 0xff00ff00;
		* notification.ledOnMS = 300; //亮的时间
		* notification.ledOffMS = 1000; //灭的时间
		* notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		*/
		/*
		* 更多的特征属性
		* notification.flags |= FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
		* notification.flags |= FLAG_INSISTENT; //重复发出声音，直到用户响应此通知
		* notification.flags |= FLAG_ONGOING_EVENT; //将此通知放到通知栏的"Ongoing"即"正在运行"组中
		* notification.flags |= FLAG_NO_CLEAR; //表明在点击了通知栏中的"清除通知"后，此通知不清除，
		* //经常与FLAG_ONGOING_EVENT一起使用
		* notification.number = 1; //number字段表示此通知代表的当前事件数量，它将覆盖在状态栏图标的顶部
		* //如果要使用此字段，必须从1开始
		* notification.iconLevel = ; //
		*/

		//设置通知的事件消息
		CharSequence contentTitle = "当前界面的全限定类名"; //通知栏标题
		Intent notificationIntent = new Intent(context,MainActivity.class); //点击该通知后要跳转的Activity
		PendingIntent contentIntent = PendingIntent.getActivity(context,0,notificationIntent,0);
		RemoteViews view_custom = getRemoteViews(context, contentText);

		PendingIntent pendButtonIntent = getPendingIntent(context, contentText);
		view_custom.setOnClickPendingIntent(R.id.btn, pendButtonIntent);

		mBuilder.setCustomContentView(view_custom);
		mBuilder.setContentIntent(contentIntent);
		mBuilder.setOngoing(true);
		//把Notification传递给 NotificationManager
		mNotificationManager.notify(0,mBuilder.build());
	}

	@NonNull
	private static RemoteViews getRemoteViews(Context context, String contentText) {
		RemoteViews view_custom = new RemoteViews(context.getPackageName(), R.layout.notification);
		view_custom.setTextViewText(R.id.content,contentText);
		return view_custom;
	}

	private static PendingIntent getPendingIntent(Context context, String contentText) {
		final int id = (int)System.currentTimeMillis();
		BroadcastReceiver onClickReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(MY_ACTION + "_" + id)) {
					//获取剪贴板管理器：
					ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
					cm.setText(intent.getStringExtra("content"));
					Log.d("NotificationUtils","content: " + intent.getStringExtra("content"));
					Toast.makeText(context,"已复制到粘贴板: " + intent.getStringExtra("content"),Toast.LENGTH_SHORT).show();
				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(MY_ACTION + "_" + id);
		context.registerReceiver(onClickReceiver, filter);
		Intent buttonIntent = new Intent(MY_ACTION + "_" + id);
		buttonIntent.putExtra("content",contentText);
		return PendingIntent.getBroadcast(context, id, buttonIntent, 0);
	}

	public static void hideNotification(Context context){
		mNotificationManager.cancel(0);
	}
	public static void updateNotification(Context context,String contentText){
		if(mBuilder==null||mNotificationManager==null){
			showNotification(context,contentText);
		}else{
			RemoteViews remoteViews = getRemoteViews(context,contentText);
			PendingIntent pendButtonIntent = getPendingIntent(context, contentText);
			remoteViews.setOnClickPendingIntent(R.id.btn, pendButtonIntent);
			mBuilder.setCustomContentView(remoteViews);
			mNotificationManager.notify(0,mBuilder.build());
		}

	}
}
