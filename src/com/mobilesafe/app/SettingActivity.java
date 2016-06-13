package com.mobilesafe.app;

import ui.SettingItemView;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 设置中心界面
 * 
 * @author Administrator
 * 
 */
public class SettingActivity extends BaseAcitivity {
    private SettingItemView siv_update;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();
    }

    private void init() {
        siv_update = (SettingItemView) findViewById(R.id.siv_update);
        sp = getSharedPreferences("config", MODE_PRIVATE);

        boolean isUpdate = sp.getBoolean("update", false);
        siv_update.setChecked(isUpdate);

        siv_update.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Editor edit = sp.edit();
                // 判断是否选中
                if (siv_update.isChecked()) {
                    siv_update.setChecked(false);
                    edit.putBoolean("update", false);
                } else {
                    siv_update.setChecked(true);
                    edit.putBoolean("update", true);
                }
                edit.commit();
            }
        });
    }

}
