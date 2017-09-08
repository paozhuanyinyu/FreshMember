package com.paozhuanyinyu.freshmember;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by kay on 2017/4/2.
 */

public class OnePixelActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ScreenManager.getInstance(this).setActivity(OnePixelActivity.this);
		//设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Window window = getWindow();
		//放在左上角
		window.setGravity(Gravity.START | Gravity.TOP);
		WindowManager.LayoutParams attributes = window.getAttributes();
		//宽高设计为1个像素
		attributes.width = 1;
		attributes.height = 1;
		//起始坐标
		attributes.x = 0;
		attributes.y = 0;
		window.setAttributes(attributes);
	}
	public static void actionToLiveActivity(Context pContext) {

		Intent intent = new Intent(pContext, OnePixelActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		pContext.startActivity(intent);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(OnePixelActivity.class.getSimpleName(), "onDestroy");
	}
}
