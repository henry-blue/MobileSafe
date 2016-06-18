package dao;

import java.util.ArrayList;
import java.util.List;

import domain.BlackNumberInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 对blacknumber.db的操作
 * @author Administrator
 *
 */
public class BlackNumberDao {
	
	private BlackNumberDBOpenHelper helper;
	
	public BlackNumberDao(Context context) {
		helper = new BlackNumberDBOpenHelper(context);
	}
	
	//查询黑名单号码是否存在
	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacknumber where number=?", new String[]{number});
		if (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
 	}
	
	//查询所有黑名单号码
	public List<BlackNumberInfo> findAll() {
		List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber", null);
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
		    String mode = cursor.getString(1);
		    info.setNumber(number);
		    info.setMode(mode);
		    result.add(info);
		}
		cursor.close();
		db.close();
		
		return result;
 	}
	
	//添加黑名单号码, mode: 1-电话拦截 2-短信拦截 3-全部拦截
	public void insert(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		db.close();
	}
	
	//修改黑名单的拦截模式
	public void update(String number, String newMode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newMode);
		db.update("blacknumber", values, "number=?", new String[]{number});
		db.close();
	}
	
	//删除黑名单号码
	public void delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("blacknumber", "number=?", new String[]{number});
		db.close();
	}

}
