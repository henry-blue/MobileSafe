package com.app.mobilesafe.service;

import java.lang.reflect.Method;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.app.mobilesafe.dao.BlackNumberDao;

public class CallSmsSafeService extends Service {

	private InnerSmsReceiver receiver;

	private BlackNumberDao dao;
	
	private TelephonyManager tm;
	
	private MyListener listener;

	/**
	 * 内部广播接收,拦截黑名单号码发送的短信
	 * 
	 * @author Administrator
	 * 
	 */
	private class InnerSmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : objs) {
				SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
				// 得到短信发送人
				String render = message.getOriginatingAddress();
				String mode = dao.findMode(render);
				// 拦截短信
				if ("2".equals(mode) || "3".equals(mode)) {
					abortBroadcast();
				}
			}
		}

	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dao = new BlackNumberDao(this);
		receiver = new InnerSmsReceiver();
		listener = new MyListener();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		//将优先级设置为最高
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
	}
	
	private class MyListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String mode = dao.findMode(incomingNumber);
				if ("1".equals(mode) || "3".equals(mode)) {
					Log.i("CallSmsSafeService", "hangup call phone========");
					//观察呼叫记录的内容变化, 变化后删除黑名单号码的呼叫记录
					Uri uri = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(uri, false, 
							new CallLogObserver(incomingNumber, new Handler()));
					//挂断电话
					endCall();
				}
				break;
			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
		
	}

	/**
	 * 观察通话记录数据库内容变化的内部类
	 * @author Administrator
	 *
	 */
	private class CallLogObserver extends ContentObserver {
		private String incomingNumber;
		
		public CallLogObserver(String incomingNumber, Handler handler) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}

		@Override
		public void onChange(boolean selfChange) {
			Log.i("CallSmsSafeService", "call log changed======");
			//删除掉呼叫记录
			deleteCallLog(incomingNumber);
			//将当前呼叫记录观察者销毁
			getContentResolver().unregisterContentObserver(this);
			super.onChange(selfChange);
		}
		
		
		
	}
	
	/**
	 * 挂断电话
	 */
	public void endCall() {
		try {
			//加载ServiceManager的字节码
			Class<?> clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
			Method method = clazz.getDeclaredMethod("getService", String.class);
			IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
			ITelephony.Stub.asInterface(ibinder).endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 利用内容提供器删除呼叫记录
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		ContentResolver resolver = getContentResolver();
		//呼叫记录的uri路径
		Uri uri = Uri.parse("content://call_log/calls");
		resolver.delete(uri, "number=?", new String[]{incomingNumber});
	}
	
}
