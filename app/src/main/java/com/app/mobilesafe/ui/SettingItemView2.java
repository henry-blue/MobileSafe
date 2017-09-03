package com.app.mobilesafe.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.mobilesafe.R;


public class SettingItemView2 extends RelativeLayout {

	private TextView tv_title;

	public SettingItemView2(Context context) {
		super(context);
		initView(context);
	}

	public SettingItemView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		setAttributeSet(context, attrs);
	}

	/**
	 * 初始化布局文件
	 * 
	 * @param context
	 */
	private void initView(Context context) {
		View.inflate(context, R.layout.setting_item_view2, this);
		tv_title = (TextView) this.findViewById(R.id.tv_update);
	}

	private void setAttributeSet(Context context, AttributeSet attrs) {
		TypedArray typeArray = context.obtainStyledAttributes(attrs,
				R.styleable.SettingItemView);
		final String title = typeArray
				.getString(R.styleable.SettingItemView_title);
		if (!TextUtils.isEmpty(title)) {
			setTitle(title);
		}

		typeArray.recycle();
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		tv_title.setText(title);
	}


	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:// 由组合控件来响应点击事件
			return true;
		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

}
