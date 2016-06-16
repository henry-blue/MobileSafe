package service;

import java.io.InputStream;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GPSService extends Service {

	private LocationManager lm;
	private MyLocationListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);

		listener = new MyLocationListener();
		// 给位置提供者设置条件
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(criteria, true);
		lm.requestLocationUpdates(provider, 60000, 50, listener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(listener);
		listener = null;
	}

	class MyLocationListener implements LocationListener {

		// 当位置改变回调
		@Override
		public void onLocationChanged(Location location) {
			String longitude = "j:" + location.getLongitude() + "\n";
			String latitude = "w:" + location.getLatitude() + "\n";
			// 把标准坐标转换成火星坐标
			try {
				InputStream is = getAssets().open("");
				ModifyOffset offset = ModifyOffset.getInstance(is);
				offset.s2c(new PointDouble(location.getLongitude(), location
						.getLatitude()));
				longitude = "j:" + offset.X + "\n";
				latitude = "w:" + offset.Y + "\n";
			} catch (Exception e) {
				e.printStackTrace();
			}
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			Editor edit = sp.edit();
			edit.putString("lastlocation", longitude + latitude);
			edit.commit();
		}

		// 当状态改变回调
		@Override
		public void onProviderDisabled(String provider) {

		}

		// 某一位置提供者可以使用
		@Override
		public void onProviderEnabled(String provider) {

		}

		// 某一位置提供者可以使用
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	}
}
