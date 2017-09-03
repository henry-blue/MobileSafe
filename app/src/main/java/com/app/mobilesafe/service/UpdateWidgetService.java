package com.app.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.app.mobilesafe.R;
import com.app.mobilesafe.reciver.MyWidget;
import com.app.mobilesafe.utils.SystemInfoUtils;


public class UpdateWidgetService extends Service {

	protected static final String TAG = "UpdateWidgetService";
	private Timer timer;
	private TimerTask task;
	private AppWidgetManager awm;
	
	private ScreenOffReceiver offReceiver;
	private ScreenOnReceiver onReceiver;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		offReceiver = new ScreenOffReceiver();
		onReceiver = new ScreenOnReceiver();
		
		registerReceiver(offReceiver, 
				new IntentFilter(Intent.ACTION_SCREEN_OFF));
		registerReceiver(onReceiver, 
				new IntentFilter(Intent.ACTION_SCREEN_ON));
		
		super.onCreate();
		awm = AppWidgetManager.getInstance(this);
		
		startTimer();
	}

	private void startTimer() {
		if (timer == null && task == null) {
		timer = new Timer();
		task = new TimerTask() {
			
			@Override
			public void run() {
				//设置更新的组件
				ComponentName provider = new ComponentName(UpdateWidgetService.this,
						MyWidget.class);
				RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
				views.setTextViewText(R.id.process_count, 
						"正在运行的进程:" + SystemInfoUtils.getRunningProcessCount(getApplicationContext()));
				long availMem = SystemInfoUtils.getAvailMem(getApplicationContext());
				views.setTextViewText(R.id.process_memory, 
						"可用内存:" + Formatter.formatFileSize(getApplicationContext(), availMem));
				//设置一键清理的点击监听,发送自定义广播
				Intent intent = new Intent();
				intent.setAction("com.mobile.app.killprocess");
				
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 
						0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				awm.updateAppWidget(provider, views);
			}
		};
		timer.schedule(task, 0, 3000);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "destory updateWidgetService======");
		unregisterReceiver(offReceiver);
		unregisterReceiver(onReceiver);
		offReceiver = null;
		onReceiver = null;
		stopTimer();
	}

	private void stopTimer() {
		if (timer != null && task != null) {
		timer.cancel();
		task.cancel();
		timer = null;
		task = null;
		}
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			stopTimer();
		}
		
	}
	
	private class ScreenOnReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			startTimer();
		}
		
	}
}
