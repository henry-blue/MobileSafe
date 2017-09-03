package com.app.mobilesafe;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.mobilesafe.dao.AntivirusDao;

public class AntiVirusActivity extends BaseActivity {

	protected static final int SCANING = 0;
	protected static final int SCAN_END = 1;
	private TextView scanStatus;
	private LinearLayout ll_anti_status;
	private ImageView act_scan;
	private Button btn_virus;
	private PackageManager pm;
	private int virusNum = 0;
	private List<String> virusList;
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANING:
				ScanInfo scanInfo = (ScanInfo) msg.obj;
				scanStatus.setText("正在扫描: " + scanInfo.name);
				TextView tv = new TextView(getApplicationContext());
				if (scanInfo.isvirus) {
					tv.setTextColor(Color.RED);
					tv.setText("发现可疑病毒: " + scanInfo.name);
					virusList.add(scanInfo.packname);
				} else {
					tv.setTextColor(Color.WHITE);
					tv.setText("扫描安全: " + scanInfo.name);
				}
				ll_anti_status.addView(tv, 0);
				break;
			case SCAN_END:
				scanStatus.setText("扫描完成, 共发现 " + virusNum + " 个病毒");
				if (virusNum > 0) {
					btn_virus.setText("立刻清理病毒");
				} else {
					btn_virus.setText("完成");
				}
				btn_virus.setVisibility(View.VISIBLE);
				act_scan.clearAnimation();
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		virusList = new ArrayList<String>();
		
		scanStatus = (TextView) findViewById(R.id.tv_scan_state);
		ll_anti_status = (LinearLayout) findViewById(R.id.ll_anti_status);
		act_scan = (ImageView)findViewById(R.id.iv_scanning_act);
		btn_virus = (Button)findViewById(R.id.btn_virus);
		btn_virus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (virusNum <= 0) {
					finish();
				} else {
					btn_virus.setVisibility(View.GONE);
					clearVirus();
				}
			}

		});
		RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(1000);
		ra.setRepeatCount(Animation.INFINITE);
		act_scan.startAnimation(ra);
		
		scanVirus();
	}
	
	/**
	 * 清理缓存
	 */
	private void clearVirus() {
		if (virusNum > 0) {
			Intent intent = new Intent();
        	intent.setAction("android.intent.action.VIEW");
        	intent.setAction("android.intent.action.DELETE");
        	intent.addCategory("android.intent.category.DEFAULT");
        	intent.setData(Uri.parse("package:" + virusList.get(virusNum)));
        	startActivityForResult(intent, 0);
		} else {
			btn_virus.setText("病毒清理完成");
			btn_virus.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		virusNum--;
		clearVirus();
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 扫描病毒
	 */
	private void scanVirus() {
		pm = getPackageManager();
		scanStatus.setText("正在初始化杀毒引擎...");
		new Thread() {
			public void run() {
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				try {
					Thread.sleep(300);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				for (PackageInfo info : infos) {
					//apk文件路径
					String sourceDir = info.applicationInfo.sourceDir;
					String md5 = getFileMD5(sourceDir);  //获取文件md5信息
					ScanInfo scanInfo = new ScanInfo();
					scanInfo.name = info.applicationInfo.loadLabel(pm).toString();
					scanInfo.packname = info.packageName;
					
					//查询md5值是否存在病毒数据库中
					if (AntivirusDao.isVirus(md5)) {
						//发现病毒
						scanInfo.isvirus = true;
						virusNum++;
					} else {
						//扫描安全
						scanInfo.isvirus = false;
					}
					Message msg = Message.obtain();
					msg.obj = scanInfo;
					msg.what = SCANING;
					handler.sendMessageDelayed(msg, 300);
				}
				Message msg = Message.obtain();
				msg.what = SCAN_END;
				handler.sendMessageDelayed(msg, 300);
			};
		}.start();
	}
	
	/**
	 * 扫描信息内部类
	 * @author Administrator
	 *
	 */
	class ScanInfo {
		String packname;
		String name;
		boolean isvirus;
	}
	
	/**
	 * 获取文件md5值
	 * @param path 文件全路径名称
	 * @return md5值
	 */
	private String getFileMD5(String path) {
		File file = new File(path);
		StringBuffer sb = new StringBuffer();
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = fis.read(buffer)) != -1) {
				digest.update(buffer, 0, len);
			}
			byte[] result = digest.digest();
			for (byte b : result) {
				int number = b & 0xff;
				String str = Integer.toHexString(number);
				if (str.length() == 1) {
					sb.append("0");
				}
				sb.append(str);
			}
		} catch (Exception e) {
			return null;
		}
		return sb.toString();
	}
}
