package com.app.mobilesafe;


import android.content.Intent;
import android.os.Bundle;


/**
 * 手机防盗功能界面
 * @author Administrator
 *
 */
public class Setup1Activity extends BaseSetupActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
		
	}

	@Override
	protected void showNext() {
		Intent intent = new Intent(Setup1Activity.this, Setup2Activity.class);
		startActivity(intent);
		finish();
		//必须在finish或startActivity后面执行
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	@Override
	protected void showBack() {
		
	}
	
}
