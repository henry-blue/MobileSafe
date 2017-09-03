package com.app.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mobilesafe.utils.CommonUtil;

/**
 * 启动界面
 * @author Administrator
 *
 */
public class SplashActivity extends Activity {
	protected static final String LOG_TAG = "SplashActivity";

	protected static final int ENTER_MAIN_PAGE = 0;

	protected static final int SHOW_UPDATE_DIALOG = 1;

	protected static final int GET_UPDATEINFO_ERROR = 2;

	protected static final int ANALYZE_JSON_ERROR = 3;
	
	protected static final int DOWNLOAD_FILE_ERROR = 4;
	
	//新版本的描述信息
	private String mDescription;
	//新版本的下载地址
	private String mApkUrl;
	//显示下载更新包进度
	private TextView mShowUpdate;
	
	private SharedPreferences sp;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_UPDATE_DIALOG: //显示升级对话框
				Log.i(LOG_TAG, "show update dialog");
				showUpdateDialog();
				break;
			case ENTER_MAIN_PAGE:  //进入主界面
				Log.i(LOG_TAG, "enter main page");
				enterMainPage();
				break;
			case GET_UPDATEINFO_ERROR: //显示错误信息
				enterMainPage();
				Toast.makeText(getApplicationContext(), (String)msg.obj, Toast.LENGTH_SHORT).show();
				break;
			case ANALYZE_JSON_ERROR:
				enterMainPage();
				Toast.makeText(getApplicationContext(), "json analyze error", Toast.LENGTH_SHORT).show();
				break;
			case DOWNLOAD_FILE_ERROR:
				enterMainPage();
				Toast.makeText(getApplicationContext(), "download file error: " + (String)msg.obj, 
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mShowUpdate = (TextView) findViewById(R.id.tv_update);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		//将电话号码数据库copy到/data/data/package/files/
		copyDB("address.db");
		//将病毒数据库copy到/files/
		copyDB("antivirus.db");
		
		//在桌面创建快捷图标
		installShortCut();
		
		boolean isUpdate = sp.getBoolean("update", false);
		if (isUpdate) {
			//检查是否需要升级
			checkUpdate();
		} else {
			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					enterMainPage();
				}
			}, 2000);
		}
		
		//设置动画
		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(1000);
		View view = findViewById(R.id.rl_root_splash);
		view.startAnimation(aa);
	}
	
	/**
	 * 创建快捷图标
	 */
	private void installShortCut() {
		//判断是否创建过桌面图标
		boolean isShortcut = sp.getBoolean("shortcut", false);
		if (isShortcut) {
			return;
		}
		
		Editor edit = sp.edit();
		
		Intent intent = new Intent();
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
				BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机管家");
		
		//点击桌面图标对应意图
		Intent shortcutIntent = new Intent();
		shortcutIntent.setAction("android.intent.action.MAIN");
		shortcutIntent.addCategory("android.intent.category.LAUNCHER");
		shortcutIntent.setClassName(getPackageName(), "com.mobilesafe.app.SplashActivity");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		//发送广播,在桌面安装图标
		sendBroadcast(intent);
		
		edit.putBoolean("shortcut", true);
		edit.commit();
	}

	//数据库copy到/data/data/package/files/
	private void copyDB(String filename) {
		File file = new File(getFilesDir(), filename);
		if (file.exists() && file.length() > 0) {
			return;
		}

		try {
			InputStream is = getAssets().open(filename);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			is.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示升级对话框
	 */
	protected void showUpdateDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("提示升级");
		builder.setMessage(mDescription);
		builder.setPositiveButton("立刻升级", new OnClickListener() {
			
			//下载apk, 并替换安装
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (!downLoadApkFile()) {
					dialog.dismiss();
					enterMainPage();
				}
			}
		});
		
		builder.setNegativeButton("下次再说", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterMainPage();
			}
		});
		//当点击升级提示框外面或点返回键,调用，进入主界面
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//进入主界面
				enterMainPage();
				dialog.dismiss();
			}
		});
		builder.show();
	}

	/**
	 * 下载apk, 并替换安装
	 * @return
	 */
	protected boolean downLoadApkFile() {
		//判断SDcard是否存在
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			//使用afnal开源框架
			FinalHttp finalHttp = new FinalHttp();
			finalHttp.download(mApkUrl, 
					Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobileSafe.apk",
					new AjaxCallBack<File>() {

						@Override
						public void onFailure(Throwable t, int errorNo,
								String strMsg) {
							super.onFailure(t, errorNo, strMsg);
							Message msg = Message.obtain();
							msg.what = DOWNLOAD_FILE_ERROR;
							msg.obj = strMsg;
							mHandler.sendMessageDelayed(msg, 2000);
						}

						@Override
						public void onLoading(long count, long current) {
							super.onLoading(count, current);
							int progress = (int) (current * 100 / count);
							mShowUpdate.setText("下载进度: " + progress+"%");
						}

						@Override
						public void onSuccess(File t) {
							super.onSuccess(t);
							installAPK(t);  //安装apk
						}
						
					});
			
		} else {
			Toast.makeText(getApplicationContext(), "没有发现sdcard，请安装重试", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 安装指定的apk
	 * @param t
	 */
	protected void installAPK(File t) {
		//发送意图，调用系统安装apk
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
		startActivity(intent);
	}

	/**
	 * 进入主界面
	 */
	protected void enterMainPage() {
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 检查是否需要升级
	 */
	private void checkUpdate() {
		final Message msg = Message.obtain();
		final long startTime = System.currentTimeMillis();
		
		CommonUtil.sendHttpRequest(getString(R.string.serverurl),
				new CommonUtil.HttpCallBackListener() {

					@Override
					public void onFinish(String response) {
						HashMap<String, String> updateInfos = CommonUtil.getUpdateInfos(response);
						
						if (updateInfos.size() > 0) {
							String newVersionName = updateInfos.get("version");
							mDescription = updateInfos.get("description");
							mApkUrl = updateInfos.get("apkurl");

							Log.i(LOG_TAG, "version: " + newVersionName
									+ " description: " + mDescription
									+ " ApkUrl: " + mApkUrl);
							
							// 检查是否有新版本
							if (getAppVersionName().equals(newVersionName)) {
								// 版本一致，进入主界面
								msg.what = ENTER_MAIN_PAGE;

							} else { // 弹出升级对话框, 进行升级
								msg.what = SHOW_UPDATE_DIALOG;
							}
						} else {
							//json解析出错
							msg.what = ANALYZE_JSON_ERROR;
						}
						
						long endTime = System.currentTimeMillis();
						long dTime = endTime - startTime;
						// 保证splash界面显示3秒
						mHandler.sendMessageDelayed(msg, 3000 - dTime);
					}
					
					@Override
					public void onError(Exception e) {
						msg.what = GET_UPDATEINFO_ERROR;
						msg.obj = e.getMessage();
						long endTime = System.currentTimeMillis();
						long dTime = endTime - startTime;
						mHandler.sendMessageDelayed(msg, 3000 - dTime);
					}
				});
	}

	/**
	 * 获取应用程序的版本号
	 */
	private String getAppVersionName() {
		PackageManager packageManager = getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

}
