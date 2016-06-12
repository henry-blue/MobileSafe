package com.mobilesafe.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * 手机防盗界面
 * @author Administrator
 *
 */
public class MobileSafeActivity extends Activity {
	
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		boolean isconfiged = sp.getBoolean("configed", false);
		if (isconfiged) {
		setContentView(R.layout.activity_mobile_safe);
		} else {
			//进入设置向导界面
			Intent intent = new Intent(MobileSafeActivity.this, Setup1Activity.class);
			startActivity(intent);
			finish();
		}
		
	}
	
	/**
	 * 重新进入设置向导界面textView点击事件
	 * @param view
	 */
	public void resetSetup(View view) {
		//进入设置向导界面
		Intent intent = new Intent(MobileSafeActivity.this, Setup1Activity.class);
		startActivity(intent);
		finish();
	}
}
