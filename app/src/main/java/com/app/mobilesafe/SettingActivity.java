package com.app.mobilesafe;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.app.mobilesafe.service.AddressService;
import com.app.mobilesafe.service.CallSmsSafeService;
import com.app.mobilesafe.ui.SettingItemView;
import com.app.mobilesafe.ui.SettingItemView2;
import com.app.mobilesafe.utils.ServiceUtils;


/**
 * 设置中心界面
 * 
 * @author Administrator
 * 
 */
public class SettingActivity extends BaseActivity {
	// 设置开启来电显示归属地
	private SettingItemView siv_phone_location;
	// 设置是否软件更新
	private SettingItemView siv_update;
	// 开启黑名单号码拦截
	private SettingItemView siv_blacknumber;
	//设置来电显示背景
	private SettingItemView2 siv_change_bg;
	
	private SharedPreferences sp;

	private Intent showAddress;
	private Intent callSmsSafeIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		init();
	}

	private void init() {
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		siv_phone_location = (SettingItemView) findViewById(R.id.siv_show_phone_location);
		showAddress = new Intent(SettingActivity.this, AddressService.class);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		boolean isUpdate = sp.getBoolean("update", false);
		siv_update.setChecked(isUpdate);

		boolean isServiceRunning = ServiceUtils.isServiceRuning(
				SettingActivity.this, "service.AddressService");
		siv_phone_location.setChecked(isServiceRunning);
		
		//开启软件更新提醒
		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor edit = sp.edit();
				// 判断是否选中
				if (siv_update.isChecked()) {
					siv_update.setChecked(false);
					edit.putBoolean("update", false);
				} else {
					siv_update.setChecked(true);
					edit.putBoolean("update", true);
				}
				edit.commit();
			}
		});
		//开启来电显示
		siv_phone_location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor edit = sp.edit();
				// 判断是否选中
				if (siv_phone_location.isChecked()) {
					siv_phone_location.setChecked(false);
					stopService(showAddress);
				} else {
					siv_phone_location.setChecked(true);
					startService(showAddress);
				}
				edit.commit();
			}
		});
		//设置归属地背景风格
		siv_change_bg = (SettingItemView2) findViewById(R.id.siv_change_bg);
		siv_change_bg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int which = sp.getInt("which", 0);
				
				String[] items = {"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("来电显示提示框风格");
				builder.setSingleChoiceItems(items, which, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Editor edit = sp.edit();
						edit.putInt("which", which);
						edit.commit();
						
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", null);
				builder.show();
			}
		});
		
		//开启黑名单设置
		siv_blacknumber = (SettingItemView) findViewById(R.id.siv_callsms_safe);
		callSmsSafeIntent = new Intent(SettingActivity.this, CallSmsSafeService.class);
		siv_blacknumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断是否选中
				if (siv_blacknumber.isChecked()) {
					siv_blacknumber.setChecked(false);
					stopService(callSmsSafeIntent);
				} else {
					siv_blacknumber.setChecked(true);
					startService(callSmsSafeIntent);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		boolean isServiceRunning = ServiceUtils.isServiceRuning(
				SettingActivity.this, "service.AddressService");
		siv_phone_location.setChecked(isServiceRunning);
		
		boolean isCallSmsServiceRunning = ServiceUtils.isServiceRuning(
				SettingActivity.this, "service.CallSmsSafeService");
		siv_blacknumber.setChecked(isCallSmsServiceRunning);
	}

}
