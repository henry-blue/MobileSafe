package reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 读取保存的sim卡信息与当前sim卡信息比较
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean protecting = sp.getBoolean("protecting", false);
		if (!protecting) { // 没有启动防盗保护 返回
			return;
		}

		String saveSim = sp.getString("sim", null);

		tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String currSim = tm.getSimSerialNumber();

		if (saveSim.equals(currSim)) {
			// sim卡没有变更
		} else {
			// sim卡变更，发短信给安全号码
			SmsManager smsManager = SmsManager.getDefault();
			String safenumber = sp.getString("safenumber", "");
			if (!TextUtils.isEmpty(safenumber)) {
				smsManager.sendTextMessage(safenumber, null, "sim卡变更,请确认此操作",
						null, null);
			}
		}
	}

}
