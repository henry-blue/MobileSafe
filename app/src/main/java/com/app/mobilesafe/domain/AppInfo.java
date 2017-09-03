package com.app.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * 应用程序信息数据
 * 
 * @author Administrator
 * 
 */
public class AppInfo {

	private Drawable appIcon;
	private String appName;
	private String packageName;
	private boolean isInstallRom;
	private boolean isUserApp;
	private long txData;
	private long rxData;

	
	public long getTxData() {
		return txData;
	}

	public void setTxData(long txData) {
		this.txData = txData;
	}

	public long getRxData() {
		return rxData;
	}

	public void setRxData(long rxData) {
		this.rxData = rxData;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isInstallRom() {
		return isInstallRom;
	}

	public void setInstallRom(boolean isInstallRom) {
		this.isInstallRom = isInstallRom;
	}

	public boolean isUserApp() {
		return isUserApp;
	}

	public void setUserApp(boolean isUserApp) {
		this.isUserApp = isUserApp;
	}

	@Override
	public String toString() {
		return "AppInfo [appIcon=" + appIcon + ", appName=" + appName
				+ ", packageName=" + packageName + ", isInstallRom="
				+ isInstallRom + ", isUserApp=" + isUserApp + "]";
	}

	
}
