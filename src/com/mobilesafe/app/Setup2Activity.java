package com.mobilesafe.app;

import ui.SettingItemView;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

/**
 * sim卡绑定设置界面
 * @author Administrator
 *
 */
public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView itemView;
	private TelephonyManager tm;      //读取手机sim卡信息
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setup2);
		itemView = (SettingItemView) findViewById(R.id.siv_bindsim);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		String ssim = sp.getString("sim", null);
		itemView.setChecked(!TextUtils.isEmpty(ssim));
		
		itemView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String sim = null;
				if (itemView.isChecked()) {
					itemView.setChecked(false);
				} else {
					itemView.setChecked(true);
					//获得sim卡序列号
					sim = tm.getSimSerialNumber(); 
				}
 
				Editor edit = sp.edit();
				edit.putString("sim", sim);
				edit.commit();
				
			}
		});
	}

	@Override
	protected void showNext() {
		//判断是否绑定sim卡
		String sim = sp.getString("sim", null);
		if (TextUtils.isEmpty(sim)) {
			Toast.makeText(getApplicationContext(), "请绑定sim卡", 0).show();
			return;
		}
		Intent intent = new Intent(Setup2Activity.this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);

	}

	@Override
	protected void showBack() {
		Intent intent = new Intent(Setup2Activity.this, Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);

	}

}
