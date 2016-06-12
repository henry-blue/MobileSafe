package com.mobilesafe.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * 手机防盗设置向导界面的父类
 * @author Administrator
 *
 */
public abstract class BaseSetupActivity extends Activity {

	//手势识别器
	private GestureDetector detector;
	
	protected static SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

			@SuppressWarnings("deprecation")
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
				int width = wm.getDefaultDisplay().getWidth();
				//屏蔽斜滑
				if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
					return true;
				}
				//屏蔽在x轴滑动很慢的情况
				if (Math.abs(velocityX) < 200) {
					return true;
				}
				
				if ((e2.getRawX() - e1.getRawX()) > width / 2) {
					//显示上一个页面
					showBack();
					return true;
				}
				
				if ((e1.getRawX() - e2.getRawX()) > width / 2) {
					//显示下一个页面
					showNext();
					return true;
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	protected abstract void showNext();
	protected abstract void showBack();
	
	/**
	 * 下一步按钮点击事件
	 * @param view
	 */
	public void next(View view) {
		showNext();
	}
	
	/**
	 * 返回按钮点击事件
	 * @param view
	 */
	public void back(View view) {
		showBack();
	}
}
