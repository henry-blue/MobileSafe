package utils;

import reciver.Myadmin;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

/**
 * 提供锁屏，销毁数据的页面
 * @author Administrator
 *
 */
public class deviceUtil extends Activity {

	private DevicePolicyManager dpm;
	private String cmd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dpm = (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
		cmd = getIntent().getStringExtra("cmd");
		openDeviceAdmin();
	}
	
	@Override
	protected void onResume() {
		super.onResume();

	}
	public void openDeviceAdmin() {
		//创建intent
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		//激活谁
        ComponentName mDeviceAdminSample = new ComponentName(this, Myadmin.class);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "开启锁屏权限");
        startActivityForResult(intent, 0);
	}
	
	private void lockScreen() {
		dpm.lockNow();
	}
	
	private void wipeData() {
		//清除sd卡数据
//		dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
		//恢复出场设置
		dpm.wipeData(0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (cmd.equals("lock")) {
			lockScreen();
		} else if (cmd.equals("wipe")) {
			wipeData();
		}
		finish();
		super.onActivityResult(requestCode, resultCode, data);
	}
}
