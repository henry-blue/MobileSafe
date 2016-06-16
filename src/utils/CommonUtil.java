package utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;


public class CommonUtil {

	private static final int CONNECT_SUCESS = 200;
    
	// 连接网络请求数据
	public static void sendHttpRequest(final String address,
			final HttpCallBackListener listener) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(3000);
					connection.setReadTimeout(3000);
					connection.setDoOutput(true);
					connection.setDoInput(true);
					StringBuilder response = new StringBuilder();
					int code = connection.getResponseCode();

					if (CONNECT_SUCESS == code) {
						InputStream in = connection.getInputStream();
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(in));
						String line;
						while ((line = reader.readLine()) != null) {
							response.append(line);
						}
					}

					if (listener != null) {
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if (listener != null) {
						listener.onError(e);
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}

	public interface HttpCallBackListener {
		void onFinish(String response);

		void onError(Exception e);
	}

	// 获取升级信息： 版本信息， 新版本的描述， 新版本下载地址
	public static HashMap<String, String> getUpdateInfos(String json) {
		String version = null;
		String description = null;
		String apkurl = null;
		HashMap<String, String> updateInfos = new HashMap<String, String>();

		try {
			JSONObject object = new JSONObject(json);
			version = (String) object.getString("version");
			description = (String) object.getString("description");
			apkurl = (String) object.getString("apkurl");

			updateInfos.put("version", version);
			updateInfos.put("description", description);
			updateInfos.put("apkurl", apkurl);
		} catch (JSONException e) {
			updateInfos.clear();
		}

		return updateInfos;
	}

	/**
	 * md5加密方法
	 * @param password
	 * @return
	 */
	public static String md5EncodePassword(String password) {

		try {
			// 得到一个信息摘要器
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte[] result = digest.digest(password.getBytes());
			StringBuffer buffer = new StringBuffer();
			// 把没一个byte 做一个与运算 0xff;
			for (byte b : result) {
				// 与运算
				int number = b & 0xff;// 加盐
				String str = Integer.toHexString(number);
				if (str.length() == 1) {
					buffer.append("0");
				}
				buffer.append(str);
			}

			// 标准的md5加密后的结果
			return buffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}

	}
}
