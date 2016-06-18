package utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;


public class ServiceUtils {

	/**
	 * 判断某个服务是否还在运行
	 * @param serviceName
	 * @return
	 */
	public static boolean isServiceRuning(Context context, String serviceName) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> services = am.getRunningServices(100);
		for (RunningServiceInfo info : services) {
			if ((info.service.getClassName()).equals(serviceName)) {
				return true;
			}
		}
		
		return false;
	}
}
