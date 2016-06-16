package reciver;

import service.GPSService;
import utils.deviceUtil;

import com.mobilesafe.app.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

public class SmsReceiver extends BroadcastReceiver {

	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		String safenumber = sp.getString("safenumber", null);

		Object[] object = (Object[]) intent.getExtras().get("pdus");

		for (Object b : object) {
			// 获取某一条短信
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) b);
			// 获得短信的发送者
			String sender = sms.getOriginatingAddress();
			//判断信息是否是安全号码发送过来的
			if (sender.contains(safenumber) && !TextUtils.isEmpty(safenumber)) {
				// 获得短信内容
				String body = sms.getMessageBody();

				if ("#*location*#".equals(body)) { // GPS追踪
					//启动监听gps位置服务
					Intent i = new Intent(context, GPSService.class);
					context.startService(i);
					SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
					String lastlocation = sp.getString("lastlocation", null);
					if (TextUtils.isEmpty(lastlocation)) {
						//位置没有得到
						SmsManager.getDefault().sendTextMessage(sender, null, 
								"geting location...please wait", null, null);
					} else {
						SmsManager.getDefault().sendTextMessage(sender, null, 
								lastlocation, null, null);
					}
					// 终止广播
					abortBroadcast();

				} else if ("#*alarm*#".equals(body)) { // 播放报警音乐
					MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
					player.setVolume(1.0f, 1.0f);
					player.start();
					abortBroadcast();

				} else if ("#*wipedata*#".equals(body)) { // 数据销毁
					Intent i =new Intent(context, deviceUtil.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.putExtra("cmd", "lock");
					context.startActivity(i);
					abortBroadcast();

				} else if ("#*lockscreen*#".equals(body)) { // 远程锁屏
					Intent i =new Intent(context, deviceUtil.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.putExtra("cmd", "wipe");
					context.startActivity(i);
					abortBroadcast();
				}
			}
		}
	}

}
