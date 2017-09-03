package com.app.mobilesafe.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NumberQureyUtils {
	
	private static String path = "data/data/com.mobilesafe.app/files/address.db";
	/**
	 * 查找指定电话号码的归属地
	 * @param number
	 * @return
	 */
	public static String qureyNumber(String number) {
		String address = number;
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		if (number.matches("^1[34568]\\d{9}$")) {
			//手机号码
			Cursor cursor = database.rawQuery("select location from data2 where id = (" +
					"select outkey from data1 where id =?)", new String[]{number.substring(0, 7)});
			
			while (cursor.moveToNext()) {
				String location = cursor.getString(0);
				address = location;
			}
			cursor.close();
		} else {
			//其他号码
			switch (number.length()) {
			case 3:
				address = "特殊号码"; 
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "客服电话";
			case 7:
			case 8:
				address = "本地号码";
				break;
			default:
				//长途电话
				if (number.length() > 10 && number.startsWith("0")) {
					Cursor cursor = database.rawQuery("select location from data2 where area=?", new String[]{number.substring(1, 3)});
					while (cursor.moveToNext()) {
						String area = cursor.getString(0);
						address = area.substring(0, area.length() - 2);
					}
					cursor.close();
					
					cursor = database.rawQuery("select location from data2 where area=?", new String[]{number.substring(1, 4)});
					while (cursor.moveToNext()) {
						String area = cursor.getString(0);
						address = area.substring(0, area.length() - 2);
					}
					cursor.close();
				}
				break;
			}
		}
		
		
		return address;
	}

}
