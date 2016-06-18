package com.mobilesafe.app;

import service.AddressService;
import ui.SettingItemView;
import ui.SettingItemView2;
import utils.ServiceUtils;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 设置中心界面
 * 
 * @author Administrator
 * 
 */
public class SettingActivity extends BaseAcitivity {
	// 设置开启来电显示归属地
	private SettingItemView siv_phone_location;
	// 设置是否软件更新
	private SettingItemView siv_update;
	//设置来电显示背景
	private SettingItemView2 siv_change_bg;
	
	private SharedPreferences sp;

	private Intent showAddress;

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
	}

	@Override
	protected void onResume() {
		super.onResume();
		boolean isServiceRunning = ServiceUtils.isServiceRuning(
				SettingActivity.this, "service.AddressService");
		siv_phone_location.setChecked(isServiceRunning);
	}

}
