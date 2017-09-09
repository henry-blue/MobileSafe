package com.app.mobilesafe.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 病毒数据库查询业务类
 * 
 * @author Administrator
 * 
 */
public class AntivirusDao {
	/**
	 * 查询md5是否存在病毒数据库中
	 * @param md5
	 * @return
	 */
	public static boolean isVirus(String md5) {
		String path = "/data/data/com.app.mobilesafe/files/antivirus.db";
		boolean result = false;
		//打开数据库
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from datable where md5=?", new String[]{md5});
		if (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
}
