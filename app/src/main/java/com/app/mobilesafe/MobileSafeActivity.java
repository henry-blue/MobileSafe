package com.app.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * 手机防盗界面
 * 
 * @author Administrator
 * 
 */
public class MobileSafeActivity extends BaseActivity {

    private SharedPreferences sp;
    private TextView safe_number;
    private CheckBox isOpenProtect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);

        boolean isconfiged = sp.getBoolean("configed", false);
        //已经配置了信息，进入主界面
        if (isconfiged) {
            setContentView(R.layout.activity_mobile_safe);
            safe_number = (TextView) findViewById(R.id.tv_safe_number);
            safe_number.setText(sp.getString("safenumber", ""));
            
            isOpenProtect = (CheckBox) findViewById(R.id.cb_isopen_protect);
            isOpenProtect.setChecked(sp.getBoolean("protecting", false));
            
        } else {
            // 进入设置向导界面
            Intent intent = new Intent(MobileSafeActivity.this,
                    Setup1Activity.class);
            startActivity(intent);
            finish();
        }

    }

    /**
     * 重新进入设置向导界面textView点击事件
     * 
     * @param view
     */
    public void resetSetup(View view) {
        // 进入设置向导界面
        Intent intent = new Intent(MobileSafeActivity.this,
                Setup1Activity.class);
        startActivity(intent);
        finish();
    }
}
