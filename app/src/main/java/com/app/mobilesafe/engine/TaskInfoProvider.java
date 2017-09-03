package com.app.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;
import android.support.v4.content.ContextCompat;

import com.app.mobilesafe.R;
import com.app.mobilesafe.domain.TaskInfo;

/**
 * 提供手机中进程信息
 * @author Administrator
 *
 */
public class TaskInfoProvider {

	/**
	 * 获取所有进程信息
	 * @param context context
	 * @return List<TaskInfo>
	 */
	public static List<TaskInfo> getTaskInfos(Context context) {
		List<TaskInfo> lists = new ArrayList<>();
		
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		for (RunningAppProcessInfo processInfo : processes) {
			TaskInfo info = new TaskInfo();
			
			String packageName = processInfo.processName;
			info.setPackName(packageName);
			
			MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{processInfo.pid});
			long memSize = memoryInfos[0].getTotalPrivateDirty() * 1024;
			info.setMemSize(memSize);
			
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
				Drawable icon = applicationInfo.loadIcon(pm);
				info.setIcon(icon);
				
				String name = applicationInfo.loadLabel(pm).toString();
				info.setName(name);
				
				boolean isUserTask;
				isUserTask = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
				info.setUserTask(isUserTask);
			} catch (NameNotFoundException e) {
//				e.printStackTrace();
				info.setIcon(ContextCompat.getDrawable(context, R.mipmap.ic_launcher));
				info.setName(packageName);
			}
			lists.add(info);
		}
		
		return lists;
	}
}
