package com.app.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.app.mobilesafe.ui.SettingItemView;


/**
 * 设置完成界面
 * 
 * @author Administrator
 * 
 */
public class Setup4Activity extends BaseSetupActivity {
	private SharedPreferences sp;
	private SettingItemView itemView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		itemView = (SettingItemView) findViewById(R.id.siv_safe);
		
		boolean protecting = sp.getBoolean("protecting", false);
		itemView.setChecked(protecting);
		
		itemView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (itemView.isChecked()) {
					itemView.setChecked(false);
				} else {
					itemView.setChecked(true);
				}
				Editor edit = sp.edit();
				edit.putBoolean("protecting", itemView.isChecked());
				edit.commit();
			}
		});
	}

	@Override
	protected void showNext() {
		Editor edit = sp.edit();
		edit.putBoolean("configed", true);
		edit.commit();

		Intent intent = new Intent(Setup4Activity.this,
				MobileSafeActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);

	}

	@Override
	protected void showBack() {
		Intent intent = new Intent(Setup4Activity.this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
}
