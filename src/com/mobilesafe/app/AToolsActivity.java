package com.mobilesafe.app;

import ui.AtoolsItemView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 高级工具界面
 * @author Administrator
 *
 */
public class AToolsActivity extends BaseAcitivity implements OnClickListener {

	private AtoolsItemView findNumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		
		findNumber = (AtoolsItemView) findViewById(R.id.aiv_find_number);
		findNumber.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.aiv_find_number: //进入手机归属地查询页面
			enterAppointPage(NumberAddressQueryActivity.class);
			break;
		default:
			break;
		}
	}

	private void enterAppointPage(Class<?> class1) {
		Intent intent = new Intent(AToolsActivity.this, class1);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}
	
	
}
