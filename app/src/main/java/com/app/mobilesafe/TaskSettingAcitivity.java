package com.app.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.app.mobilesafe.service.AutoCleanTaskService;
import com.app.mobilesafe.utils.ServiceUtils;

public class TaskSettingAcitivity extends BaseActivity {

	private CheckBox cb_show_system;
	private CheckBox cb_lock_clean;

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tasksetting);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		cb_show_system.setChecked(sp.getBoolean("showsystem", false));

		cb_show_system
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Editor edit = sp.edit();
						edit.putBoolean("showsystem", isChecked);
						edit.commit();
					}
				});

		cb_lock_clean = (CheckBox) findViewById(R.id.cb_lock_clean);
		cb_lock_clean.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Intent intent = new Intent(TaskSettingAcitivity.this, AutoCleanTaskService.class);
				
				if (isChecked) {
					startService(intent);
				} else {
					stopService(intent);
				}
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		boolean runing = ServiceUtils.isServiceRuning(TaskSettingAcitivity.this, "service.AutoCleanTaskService");
		cb_lock_clean.setChecked(runing);
	}
}
