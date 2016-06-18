package com.mobilesafe.app;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import dao.BlackNumberDao;
import domain.BlackNumberInfo;

/**
 * 通讯卫士界面
 * @author Administrator
 *
 */
public class CallSmsSafeActivity extends BaseAcitivity implements OnClickListener {

	private ListView call_sms_safe;
	private List<BlackNumberInfo> infos;
	private BlackNumberDao dao;
	
	private TextView addBlackNumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsmsafe);
		call_sms_safe = (ListView) findViewById(R.id.lv_call_sms_safe);
		addBlackNumber = (TextView) findViewById(R.id.add_black_number);
		addBlackNumber.setOnClickListener(this);
		
		dao = new BlackNumberDao(CallSmsSafeActivity.this);
		infos = dao.findAll();
		MyAdapter adapter = new MyAdapter();
		call_sms_safe.setAdapter(adapter);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_black_number:
			ShowAddBlackNumberDialog();
			break;
		default:
			break;
		}
	}
	
	private void ShowAddBlackNumberDialog() {
		
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.list_item_callsms, null);
				holder = new ViewHolder();
				holder.black_number = (TextView) view
						.findViewById(R.id.tv_black_number);
				holder.black_mode = (TextView) view
						.findViewById(R.id.tv_black_mode);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.black_number.setText(infos.get(position).getNumber());
			String mode = infos.get(position).getMode();
			if ("1".equals(mode)) {
				holder.black_mode.setText("电话拦截");
			} else if ("2".equals(mode)) {
				holder.black_mode.setText("短信拦截");
			} else if ("3".equals(mode)) {
				holder.black_mode.setText("全部拦截");
			}

			return view;
		}
		
		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}
		
		class ViewHolder {
			TextView black_number;
			TextView black_mode;
		}
		
	}
	
}
