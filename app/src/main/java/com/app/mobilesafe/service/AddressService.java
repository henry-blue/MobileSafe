package com.app.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.mobilesafe.R;
import com.app.mobilesafe.dao.NumberQureyUtils;


public class AddressService extends Service {
	
	private WindowManager wm;
	private View view;

	private TelephonyManager tm;
	private MyListenerPhone listenerPhone;
	
	private OutCallReceiver receiver;
	private boolean isRemoveView = false;

	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String phone = getResultData();
			String address = NumberQureyUtils.qureyNumber(phone);
			
			myToast(address);
		}

	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		listenerPhone = new MyListenerPhone();
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_CALL_STATE);
		
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	}

	private class MyListenerPhone extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String address = NumberQureyUtils
						.qureyNumber(incomingNumber);
				
				myToast(address);
				break;
				
			case TelephonyManager.CALL_STATE_IDLE:
				if(view != null && !isRemoveView){
					wm.removeView(view);
					isRemoveView = true;
				}
			
				break;

			default:
				break;
			}
		}

	}
	
	private int startX;
	private int startY;
	private WindowManager.LayoutParams params;
	private SharedPreferences sp;
	private long mHits[] = new long[2];
	
	public void myToast(String address) {
	    view =   View.inflate(this, R.layout.address_show, null);
	    TextView textview  = (TextView) view.findViewById(R.id.tv_address);
	    view.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= SystemClock.uptimeMillis() - 500) {
					//双击居中
					params.x = (wm.getDefaultDisplay().getWidth() / 2) -
							(view.getWidth() / 2);
					wm.updateViewLayout(view, params);
					Editor edit = sp.edit();
					edit.putInt("lastx", params.x);
					edit.commit();
				}
			}
		});
	    
	    //设置自定义view的触摸事件监听
	    view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return onTouchEvent(v, event);
			}
		});
	    
	    int [] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue
	    ,R.drawable.call_locate_gray,R.drawable.call_locate_green};
	    sp = getSharedPreferences("config", MODE_PRIVATE);
	    view.setBackgroundResource(ids[sp.getInt("which", 0)]);
	    textview.setText(address);
		
		 params = new WindowManager.LayoutParams();
		 
         params.height = WindowManager.LayoutParams.WRAP_CONTENT;
         params.width = WindowManager.LayoutParams.WRAP_CONTENT;
         
         params.x = sp.getInt("lastx", 50);
         params.y = sp.getInt("lasty", 50);
         
         params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                 | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
         params.format = PixelFormat.TRANSLUCENT;
         //需要添加权限
         params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
         isRemoveView = false;
		wm.addView(view, params);
		
		//3秒后关闭吐司
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (view != null && !isRemoveView) {
					wm.removeView(view);
					isRemoveView = true;
				}
			}
		}, 3000);
	}
	
	@SuppressWarnings("deprecation")
	protected boolean onTouchEvent(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = (int) event.getRawX();
			startY = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			int newX = (int) event.getRawX();
			int newY = (int) event.getRawY();
			int dx = newX - startX;
			int dy = newY - startY;
			params.x += dx;
			params.y += dy;
			if (params.x < 0) {
				params.x = 0;
			}
			if (params.y < 0) {
				params.y = 0;
			}
			if (params.x > (wm.getDefaultDisplay().getWidth() - view.getWidth())) {
				params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
			}
			if (params.y > (wm.getDefaultDisplay().getHeight() - view.getHeight())) {
				params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
			}
			wm.updateViewLayout(view, params);
			
			startX = newX;
			startY = newY;
			break;
		case MotionEvent.ACTION_UP:
			//记录控件在屏幕的位置
			Editor edit = sp.edit();
			edit.putInt("lastx", params.x);
			edit.putInt("lasty", params.y);
			edit.commit();
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//取消监听来电
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_NONE);
		listenerPhone = null;
		//取消监听去电
		unregisterReceiver(receiver);
		receiver = null;

	}

}
