package com.app.mobilesafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 选择联系人界面
 * 
 * @author Administrator
 * 
 */
public class SelectContactActivity extends Activity {

    private ListView mContactListView;
    private List<Map<String, String>> contactInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select_contact);
        mContactListView = (ListView) findViewById(R.id.lv_select_contact);
        contactInfo = getContactInfo();
        MyAdapter adapter = new MyAdapter();
        mContactListView.setAdapter(adapter);
        mContactListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String phone = contactInfo.get(position).get("number");
				Intent data = new Intent();
				data.putExtra("phone", phone);
				setResult(0, data);
				//退出当前页面
				finish();
			}
		});
    }

    private List<Map<String, String>> getContactInfo() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    null, null, null);

            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<String, String>();

                String contactName = cursor
                        .getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String contactNumber = cursor
                        .getString(cursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                map.put("name", contactName);
                map.put("number", contactNumber);
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return contactInfo.size();
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
                        R.layout.contact_item, null);
            } else {
                view = convertView;
            }
            TextView name = (TextView) view.findViewById(R.id.tv_name);
            TextView phone = (TextView) view.findViewById(R.id.tv_phone);
            name.setText(contactInfo.get(position).get("name"));
            phone.setText(contactInfo.get(position).get("number"));

            return view;
        }

    }
}
