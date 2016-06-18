package com.mobilesafe.app;

import dao.NumberQureyUtils;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 号码归属地查询界面
 * @author Administrator
 *
 */
public class NumberAddressQueryActivity extends BaseAcitivity {

	private EditText ed_phone;
	private Button bt_reault;
	private TextView tv_show;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_query);
		ed_phone = (EditText) findViewById(R.id.et_get_input_number);
		bt_reault = (Button) findViewById(R.id.bt_reault);
		tv_show = (TextView) findViewById(R.id.tv_show_result);
		
		bt_reault.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String phone = ed_phone.getText().toString().trim();
				if (TextUtils.isEmpty(phone)) {
					Toast.makeText(getApplicationContext(), "号码为空", 
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					//去数据库查询号码归属地
					String qureyNumber = NumberQureyUtils.qureyNumber(phone);
					tv_show.setText(qureyNumber);
				}
			}
		});
		
		ed_phone.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s != null && s.length() >= 3) {
					String qureyNumber = NumberQureyUtils.qureyNumber(s.toString());
					tv_show.setText(qureyNumber);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}
}
