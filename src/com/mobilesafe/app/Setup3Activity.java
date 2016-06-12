package com.mobilesafe.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

/**
 * 设置安全号码界面
 * @author Administrator
 *
 */
public class Setup3Activity extends BaseSetupActivity {

	private Button bt_select_contact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setup3);
		bt_select_contact = (Button) findViewById(R.id.bt_select_contact);
		bt_select_contact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
             //选择已有的联系人
			 Intent intent = new Intent(Setup3Activity.this, SelectContactActivity.class);
			 startActivityForResult(intent, 0);
			}
		});
	}

	@Override
	protected void showNext() {
		Intent intent = new Intent(Setup3Activity.this, Setup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);

	}

	@Override
	protected void showBack() {
		Intent intent = new Intent(Setup3Activity.this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);

	}
}
