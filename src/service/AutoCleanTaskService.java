package service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class AutoCleanTaskService extends Service {

	private static final String TAG = "AutoCleanTaskService";
	
	private ScreenOffReceiver receiver;
	private ActivityManager am;
	
	private class ScreenOffReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "create====ScreenOffReceiver===");
			List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
			for (RunningAppProcessInfo info : processes) {
				am.killBackgroundProcesses(info.processName);
			}
		}
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		receiver = new ScreenOffReceiver();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "destroy====ScreenOffReceiver===");
		unregisterReceiver(receiver);
		receiver = null;
	}
}
