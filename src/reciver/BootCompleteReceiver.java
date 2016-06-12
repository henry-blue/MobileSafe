package reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {

	private SharedPreferences sp;
	private TelephonyManager tm;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//读取保存的sim卡信息与当前sim卡信息比较
		sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		String saveSim = sp.getString("sim", null);
		
		tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
		String currSim = tm.getSimSerialNumber();
		
		if (saveSim.equals(currSim)) {
			//sim卡没有变更
		} else {
			//sim卡变更，发短信给安全号码
			Toast.makeText(context, "sim卡变更", 0).show();
		}
	}

}
