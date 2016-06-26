package com.mobilesafe.app;

import ui.AtoolsItemView;
import utils.SmsUtils;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * 高级工具界面
 * 
 * @author Administrator
 * 
 */
public class AToolsActivity extends BaseAcitivity implements OnClickListener {

	private AtoolsItemView findNumber;
	private AtoolsItemView smsBackup;
	private AtoolsItemView smsRestore;
	private AtoolsItemView softLock;
	private ProgressDialog pb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);

		findNumber = (AtoolsItemView) findViewById(R.id.aiv_find_number);
		findNumber.setOnClickListener(this);

		smsBackup = (AtoolsItemView) findViewById(R.id.aiv_sms_save);
		smsBackup.setOnClickListener(this);

		smsRestore = (AtoolsItemView) findViewById(R.id.aiv_sms_restore);
		smsRestore.setOnClickListener(this);
		
		softLock = (AtoolsItemView) findViewById(R.id.aiv_soft_lock);
		softLock.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.aiv_find_number: // 进入手机归属地查询页面
			enterAppointPage(NumberAddressQueryActivity.class);
			break;
		case R.id.aiv_sms_save: // 短信备份
			smsBackup();
			break;
		case R.id.aiv_sms_restore: // 短信还原
			smsRestore();
			break;
		case R.id.aiv_soft_lock: //软件锁
			enterSoftLockPage();
			break;
		default:
			break;
		}
	}

	//进入软件锁界面
	private void enterSoftLockPage() {
		Intent intent = new Intent(AToolsActivity.this, SoftwareLockActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

	/**
	 * 实现短信备份功能
	 */
	private void smsBackup() {
		pb = new ProgressDialog(AToolsActivity.this);
		pb.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pb.setMessage("正在备份短信");
		pb.show();

		new Thread() {
			public void run() {
				try {
					SmsUtils.backupSms(AToolsActivity.this,
							new SmsUtils.BackupCallBack() {

								@Override
								public void onSmsBackup(int progress) {
									pb.setProgress(progress);
								}

								@Override
								public void beforeBackup(int max) {
									pb.setMax(max);
								}
							});

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(AToolsActivity.this, "备份成功",
									Toast.LENGTH_SHORT).show();
						}
					});

				} catch (Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(AToolsActivity.this, "备份失败",
									Toast.LENGTH_SHORT).show();
						}
					});
				} finally {
					pb.dismiss();
				}
			};
		}.start();
	}

	/**
	 * 实现短信还原功能
	 */
	private void smsRestore() {
		pb = new ProgressDialog(AToolsActivity.this);
		pb.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pb.setMessage("正在还原短信数据");
		pb.show();

		new Thread() {
			public void run() {
				try {
					SmsUtils.restoreSms(AToolsActivity.this, false, new SmsUtils.RestoreCallBack() {
						
						@Override
						public void onSmsRestore(int progress) {
							pb.setProgress(progress);
						}
						
						@Override
						public void beforeRestore(int max) {
							pb.setMax(max);
						}
					});
					
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(AToolsActivity.this, "还原成功",
									Toast.LENGTH_SHORT).show();
						}
					});

				} catch (Exception e) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(AToolsActivity.this, "还原失败",
									Toast.LENGTH_SHORT).show();
						}
					});
				} finally {
					pb.dismiss();
				}
			};
		}.start();
	}

	private void enterAppointPage(Class<?> class1) {
		Intent intent = new Intent(AToolsActivity.this, class1);
		startActivity(intent);
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
	}

}
