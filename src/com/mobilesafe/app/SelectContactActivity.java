package com.mobilesafe.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * 选择联系人界面
 * 
 * @author Administrator
 * 
 */
public class SelectContactActivity extends Activity {

	private ListView mContactListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_contact);
		mContactListView = (ListView) findViewById(R.id.lv_select_contact);
		List<Map<String, String>> contactInfo = getContactInfo();
		mContactListView.setAdapter(new SimpleAdapter(this, contactInfo, R.layout.contact_item,
				new String[]{"name", "number"}, new int[]{R.id.tv_name, R.id.tv_phone}));
	}

	private List<Map<String, String>> getContactInfo() {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		
		Cursor cursor = null;
		try {
			cursor = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null, null, null, null);
			while (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				
				String contactName = cursor.getString(cursor.getColumnIndex(
						ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String contactNumber = cursor.getString(cursor.getColumnIndex(
						ContactsContract.CommonDataKinds.Phone.NUMBER));
				map.put("name", contactName);
				map.put("number", contactNumber);
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
}
