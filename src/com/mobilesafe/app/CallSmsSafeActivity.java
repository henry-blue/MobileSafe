package com.mobilesafe.app;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import dao.BlackNumberDao;
import domain.BlackNumberInfo;

/**
 * 通讯卫士界面
 * 
 * @author Administrator
 * 
 */
public class CallSmsSafeActivity extends BaseAcitivity implements
		OnClickListener {

	private ListView call_sms_safe;
	private List<BlackNumberInfo> infos;
	private BlackNumberDao dao;

	private TextView addBlackNumber;
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_callsmsafe);
		call_sms_safe = (ListView) findViewById(R.id.lv_call_sms_safe);
		addBlackNumber = (TextView) findViewById(R.id.add_black_number);
		addBlackNumber.setOnClickListener(this);

		dao = new BlackNumberDao(CallSmsSafeActivity.this);
		infos = dao.findAll();
		adapter = new MyAdapter();
		call_sms_safe.setAdapter(adapter);

		call_sms_safe.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> root, View view,
					int position, long arg3) {
				ShowBlackNumberDialog("update", position);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_black_number:
			ShowBlackNumberDialog("add", 0);
			break;
		default:
			break;
		}
	}

	private EditText et_blacknumber;
	private TextView blacknumber_title;
	private CheckBox cb_phone;
	private CheckBox cb_sms;
	private Button bt_ok;
	private Button bt_cancel;

	/**
	 * 显示对话框
	 * @param changeMode 显示对话框的功能-更新,添加
	 * @param pos 点击的位置
	 */
	private void ShowBlackNumberDialog(final String changeMode, final int pos) {
		AlertDialog.Builder builder = new Builder(CallSmsSafeActivity.this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(CallSmsSafeActivity.this,
				R.layout.dialog_add_blacknumber, null);
		blacknumber_title = (TextView) view
				.findViewById(R.id.tv_blacknumber_title);
		et_blacknumber = (EditText) view.findViewById(R.id.et_blacknumber);
		cb_phone = (CheckBox) view.findViewById(R.id.cb_phone_limit);
		cb_sms = (CheckBox) view.findViewById(R.id.cb_sms_limit);
		bt_ok = (Button) view.findViewById(R.id.bt_ok);
		bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		
		if (changeMode.equals("update")) {
			blacknumber_title.setText("修改黑名单号码");
			et_blacknumber.setKeyListener(null);
			et_blacknumber.setText(infos.get(pos).getNumber());
		}

		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

		bt_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		bt_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String blacknumber = et_blacknumber.getText().toString().trim();
				if (TextUtils.isEmpty(blacknumber)) {
					Toast.makeText(getApplicationContext(), "黑名单号码不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					String mode;
					if (cb_phone.isChecked() && cb_sms.isChecked()) {
						// 全部拦截
						mode = "3";
					} else if (cb_phone.isChecked()) {
						// 电话拦截
						mode = "1";
					} else if (cb_sms.isChecked()) {
						// 短信拦截
						mode = "2";
					} else {
						Toast.makeText(getApplicationContext(), "请选择拦截模式",
								Toast.LENGTH_SHORT).show();
						return;
					}
					if (changeMode.equals("add")) {
						addBlackNumber(blacknumber, mode);
					} else if (changeMode.equals("update")) {
						updateBlackNumber(blacknumber, mode, pos);
					}
				}
				dialog.dismiss();
			}
		});
	}

	/**
	 * 添加黑名单号码
	 * @param number
	 * @param mode
	 */
	private void addBlackNumber(String number, String mode) {
		// 添加到数据库
		dao.insert(number, mode);
		BlackNumberInfo info = new BlackNumberInfo();
		info.setNumber(number);
		info.setMode(mode);
		infos.add(0, info);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 更新黑名单号码的拦截模式
	 * @param number
	 * @param mode
	 * @param location
	 */
	private void updateBlackNumber(String number, String mode, int location) {
		// 添加到数据库
		dao.update(number, mode);
		infos.get(location).setMode(mode);
		adapter.notifyDataSetChanged();
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
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
				holder.delete_number = (ImageView) view
						.findViewById(R.id.iv_delete_number);
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

			holder.delete_number.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new Builder(
							CallSmsSafeActivity.this);
					builder.setTitle("警告");
					builder.setMessage("确定要删除这条记录？");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dao.delete(infos.get(position).getNumber());
									infos.remove(position);
									adapter.notifyDataSetChanged();
								}
							});

					builder.setNegativeButton("取消", null);
					builder.show();
				}
			});

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
			ImageView delete_number;
		}

	}

}
