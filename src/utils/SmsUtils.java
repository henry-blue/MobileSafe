package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Xml;

/**
 * 短信处理的工具类
 * @author Administrator
 *
 */
public class SmsUtils {
	
	/**
	 * 备份短信回调接口
	 * @author Administrator
	 *
	 */
	public interface BackupCallBack {
		/**
		 * 开始备份, 设置进度最大值
		 * @param max 总进度
		 */
		public void beforeBackup(int max);
		/**
		 * 备份过程中, 增加进度
		 * @param progress 当前进度
		 */
		public void onSmsBackup(int progress);
	}
	
	/**
	 * 还原短信回调接口
	 * @author Administrator
	 *
	 */
	public interface RestoreCallBack {
		/**
		 * 开始备份, 设置进度最大值
		 * @param max 总进度
		 */
		public void beforeRestore(int max);
		/**
		 * 备份过程中, 增加进度
		 * @param progress 当前进度
		 */
		public void onSmsRestore(int progress);
	}

	/**
	 * 备份用户短信
	 * @param context
	 * @param listener 接口回调
	 */
	public static void backupSms(Context context, BackupCallBack listener) throws Exception {
		ContentResolver resolver = context.getContentResolver();
		
		File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
		FileOutputStream fos = new FileOutputStream(file);
		//获取xml文件生成器
		XmlSerializer serializer = Xml.newSerializer();
		//初始化
		serializer.setOutput(fos, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[]{"body", "address", "type", "date"}, null, null, null);
		//设置进度条最大值
		int max = cursor.getCount();
		if (listener != null) {
			listener.beforeBackup(max);
		}
		//将保存的数目存储到文件中
		serializer.attribute(null, "max", max + "");
		
		int progress = 0;
		while (cursor.moveToNext()) {
			Thread.sleep(500);
			String body = cursor.getString(0);
			String address = cursor.getString(1);
			String type = cursor.getString(2);
			String date = cursor.getString(3);
			
			serializer.startTag(null, "sms");
			serializer.startTag(null, "body");
			serializer.text(body);
			serializer.endTag(null, "body");
			
			serializer.startTag(null, "address");
			serializer.text(address);
			serializer.endTag(null, "address");
			
			serializer.startTag(null, "type");
			serializer.text(type);
			serializer.endTag(null, "type");
			
			serializer.startTag(null, "date");
			serializer.text(date);
			serializer.endTag(null, "date");
			
			serializer.endTag(null, "sms");
			//备份过程增加进度
			progress++;
			if (listener != null) {
				listener.onSmsBackup(progress);
			}
			
		}
		cursor.close();
		serializer.endTag(null, "smss");
		serializer.endDocument();
		fos.close();
	}
	
	/**
	 * 还原短信
	 * 
	 * @param context
	 * @param flag
	 *            是否清理原来短信
	 * @param listener
	 */
	public static void restoreSms(Context context, boolean flag,
			RestoreCallBack listener) throws Exception {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://sms/");
		if (flag) {
			resolver.delete(uri, null, null);
		}

		XmlPullParser parser = Xml.newPullParser();
		File file = new File(Environment.getExternalStorageDirectory(),
				"backup.xml");
		FileInputStream fis = new FileInputStream(file);
		parser.setInput(fis, "utf-8");
		parser.next();
		int event = parser.getEventType();
		//获取恢复的短信数目
		String max = parser.getAttributeValue(null, "max");
		if (!TextUtils.isEmpty(max)) {
			if (listener != null) {
				listener.beforeRestore(Integer.valueOf(max));
			}
		}
		String body = null;
		String address = null;
		String type = null;
		String date = null;

		int progress = 0;
		while (event != XmlPullParser.END_DOCUMENT) {
			String tagNume = parser.getName();
			switch (event) {
			case XmlPullParser.START_TAG:
				if ("body".equals(tagNume)) {
					body = parser.nextText();
				} else if ("address".equals(tagNume)) {
					address = parser.nextText();
				} else if ("type".equals(tagNume)) {
					type = parser.nextText();
				} else if ("date".equals(tagNume)) {
					date = parser.nextText();
				}
				break;
			case XmlPullParser.END_TAG:
				if ("sms".equals(tagNume)) {
					// 将一条数据添加到短信库中
					ContentValues values = new ContentValues();
					values.put("body", body);
					values.put("address", address);
					values.put("type", type);
					values.put("date", date);
					resolver.insert(uri, values);
				}
				break;
			default:
				break;
			}
			progress++;
			if (listener != null) {
				listener.onSmsRestore(progress);
			}
			event = parser.next();
		}
	}

}
