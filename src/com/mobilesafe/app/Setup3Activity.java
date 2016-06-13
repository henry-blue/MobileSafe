package com.mobilesafe.app;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 设置安全号码界面
 * @author Administrator
 *
 */
public class Setup3Activity extends BaseSetupActivity {

	private Button bt_select_contact;
	private EditText et_set_phone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		et_set_phone = (EditText) findViewById(R.id.et_set_phone);
		et_set_phone.setText(sp.getString("safenumber", ""));
		
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
		String phone = et_set_phone.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(getApplicationContext(), "请设置安全号码", 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		Editor edit = sp.edit();
		edit.putString("safenumber", phone);
		edit.commit();
		
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}
		
		String phone = data.getStringExtra("phone").replace("-", "");
		phone = phone.replace(" ", "");
		et_set_phone.setText(phone);
	}
}
