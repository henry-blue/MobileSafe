package com.app.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

import com.app.mobilesafe.domain.AppInfo;

/**
 * 提供手机里安装的所有应用信息
 * @author Administrator
 *
 */
public class AppInfoProvider {

	/**
	 * 获取所有安装的应用程序信息
	 * @param context 上下文
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		List<AppInfo> appInfos = new ArrayList<>();
		PackageManager pm = context.getPackageManager();
		//获取所有安装在系统上的应用程序包信息
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		for (PackageInfo info : packages) {
			AppInfo appInfo = new AppInfo();
			appInfo.setPackageName(info.packageName);
			appInfo.setAppIcon(info.applicationInfo.loadIcon(pm));
			String appName = info.applicationInfo.loadLabel(pm).toString();
			appInfo.setAppName(appName);
			int flags = info.applicationInfo.flags; //应用程序的标识
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				//用户程序
				appInfo.setUserApp(true);
			} else {
				//系统程序
				appInfo.setUserApp(false);
			}
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				//存在手机的内存
				appInfo.setInstallRom(true);
			} else {
				//存在外存储设备
				appInfo.setInstallRom(false);
			}
			
			int uid = info.applicationInfo.uid;
			long txBytes = TrafficStats.getUidTxBytes(uid); //上传的数据
			long rxBytes = TrafficStats.getUidRxBytes(uid); //下载的数据
			appInfo.setRxData(rxBytes);
			appInfo.setTxData(txBytes);
			appInfos.add(appInfo);
		}
		
		return appInfos;
	}
}
