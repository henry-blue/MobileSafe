package com.app.mobilesafe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 清理缓存界面
 * 
 * @author Henry
 * 
 */
public class CleanCacheActivity extends BaseActivity implements
		OnClickListener {

	private LinearLayout ll_container;
	private ListView listView;
	private List<Map<String, String>> cacheInfo = new ArrayList<>();
	private List<Drawable> appLogos = new ArrayList<>();

	private ProgressBar pb; // 扫描进度条
	private TextView scan_state; // 显示扫描的状态
	private Button bt_cleanCaches; // 清理按钮
	private Button bt_finish_cleanCaches; // 完成按钮

	private PackageManager pm; // 包管理
	private MyAdapter adapter;
	private boolean isHaveCahce = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);

		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		pb = (ProgressBar) findViewById(R.id.pb_scan);
		scan_state = (TextView) findViewById(R.id.tv_scan_state);
		bt_cleanCaches = (Button) findViewById(R.id.bt_clean_cache);
		bt_cleanCaches.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.lv_scan_cache);
		adapter = new MyAdapter();
		listView.setAdapter(adapter);
		scanCaches();
	}

	/**
	 * 扫描手机里面所有应用程序缓存
	 */
	private void scanCaches() {
		pm = getPackageManager();
		// 开启线程扫描
		new Thread() {
			public void run() {
				Method getPackageSizeInfoMethod = null;
				// 获取packageManager提供的所有方法
				Method[] methods = PackageManager.class.getMethods();
				// 遍历所有方法找到getPackSizeInfo方法
				for (Method method : methods) {
					if ("getPackageSizeInfo".equals(method.getName())) {
						getPackageSizeInfoMethod = method;
						break;
					}
				}
				// 获取所有安装的包信息
				List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
				// 设置进度条大小
				pb.setMax(packageInfos.size());
				int progress = 0;
				// 遍历所有包, 获取缓存信息
				for (PackageInfo info : packageInfos) {
					try {
						getPackageSizeInfoMethod.invoke(pm, info.packageName,
								new MyDataObserver());
						// 防止扫描过快
						Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
					progress++;
					pb.setProgress(progress);
				}
				// 扫描结束
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						scan_state.setText("扫描完毕");
						if (cacheInfo.size() <= 0) {
							bt_cleanCaches.setText("没有缓存清理返回");
							isHaveCahce = false;
						}
						// 扫描完成,显示清理按钮
						bt_cleanCaches.setVisibility(View.VISIBLE);
					}
				});
			};
		}.start();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.bt_clean_cache:
			if (isHaveCahce) {
			    bt_cleanCaches.setVisibility(View.GONE);
				CleanAllCaches();
			} else {
				finish();
			}
			break;
		case R.id.bt_finish_clean_cache:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 清理缓存
	 */
	private void CleanAllCaches() {
		Method[] methods = PackageManager.class.getMethods();
		for (Method method : methods) {
			if ("freeStorageAndNotify".equals(method.getName())) {
				try {
					method.invoke(pm, Integer.MAX_VALUE,
							new MyPackDataObserver());
				} catch (InvocationTargetException e) {
					e.getCause().printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
        showFinishCleanCache();
	}

	private class MyDataObserver extends IPackageStatsObserver.Stub {

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			final long cache = pStats.cacheSize;
			final String packname = pStats.packageName;
			final ApplicationInfo appInfo;
			try {
				appInfo = pm.getApplicationInfo(packname, 0);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						scan_state.setText("正在扫描:" + appInfo.loadLabel(pm));
						if (cache > 0) {
							Map<String, String> map = new HashMap<String, String>();
							appLogos.add(appInfo.loadIcon(pm));
							map.put("name", (String) appInfo.loadLabel(pm));
							map.put("size", Formatter.formatFileSize(
									getApplicationContext(), cache));
							Log.i("CleanCache", "name" + appInfo.loadLabel(pm));
							cacheInfo.add(map);
							adapter.notifyDataSetChanged();
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private void showFinishCleanCache() {
		ll_container.removeAllViews();
		ll_container.setBackgroundColor(Color.parseColor("#17cd6e"));
		View view = View.inflate(getApplicationContext(),
				R.layout.finish_clean_cache, null);
		bt_finish_cleanCaches = (Button) view
				.findViewById(R.id.bt_finish_clean_cache);
		bt_finish_cleanCaches.setOnClickListener(this);
		ll_container.addView(view);
	}

	private class MyPackDataObserver extends IPackageDataObserver.Stub {

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					cacheInfo.clear();
					adapter.notifyDataSetChanged();
				}
			});
		}
		
		

	}

	public class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return cacheInfo.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup root) {
			View view = null;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.cleancache_item_view, null);
			} else {
				view = convertView;
			}
			TextView name = (TextView) view.findViewById(R.id.app_name);
			TextView phone = (TextView) view.findViewById(R.id.cache_size);
			ImageView img = (ImageView) view.findViewById(R.id.iv_appicon);
			img.setImageDrawable(appLogos.get(position));
			name.setText(cacheInfo.get(position).get("name"));
			phone.setText(cacheInfo.get(position).get("size"));

			return view;
		}

	}

}
