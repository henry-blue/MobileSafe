package com.mobilesafe.app;

import utils.CommonUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 主界面
 * 
 * @author Administrator
 * 
 */
public class MainActivity extends Activity {

    public interface function {
        int MOBILE_SAFE = 0;
        int CALL_MSG_SAFE = 1;
        int APP_MANAGER = 2;
        int TASK_MANAGER = 3;
        int NETWORK_MANAGER = 4;
        int MOBILE_TROJAN = 5;
        int SYS_OPTIMIZE = 6;
        int ATOOLS = 7;
        int MOBILE_SETTING = 8;
    }

    private static String[] names = { "手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计",
            "手机杀毒", "缓存清理", "高级工具", "设置中心" };

    private static int[] ids = { R.drawable.safe, R.drawable.callmsgsafe,
            R.drawable.app, R.drawable.taskmanager, R.drawable.netmanager,
            R.drawable.trojan, R.drawable.sysoptimize, R.drawable.atools,
            R.drawable.settings };

    private static String[] bgColors = { "#65b3cb", "#b3d030", "#8000ff",
            "#ffaec9", "#42bbb4", "#f99877", "#e9b438", "#b500b5", "#177ab1" };

    private GridView mGridView;
    private SharedPreferences sp;

    // 密码输入对话框
    private EditText et_setup_pwd;
    private EditText et_confirm_pwd;
    private Button bt_cancel;
    private Button bt_ok;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        sp = getSharedPreferences("config", MODE_PRIVATE);

        mGridView = (GridView) findViewById(R.id.gv_list);
        MyAdapter adapter = new MyAdapter();
        mGridView.setAdapter(adapter);

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                switch (position) {
                case function.MOBILE_SETTING: // 进入设置中心
                    enterIndexPage(SettingActivity.class);
                    break;
                case function.MOBILE_SAFE:   //进入手机防盗
                     showLoastFindDialog();
                    break;
                case function.SYS_OPTIMIZE: //进入清理缓存
                    enterIndexPage(CleanCacheActivity.class);
                    break;
                case function.ATOOLS: //进入高级工具
                	enterIndexPage(AToolsActivity.class);
                	break;
                case function.CALL_MSG_SAFE: //进入通讯卫士
                	enterIndexPage(CallSmsSafeActivity.class);
                	break;
                case function.APP_MANAGER: //进入软件管理
                	enterIndexPage(AppManagerActivity.class);
                	break;
                case function.TASK_MANAGER:
                	enterIndexPage(TaskManagerActivity.class);
                	break;
                default:
                    break;
                }
            }
        });
    }

    protected void showLoastFindDialog() {
        // 判断是否设置密码
        if (isSetupPwd()) {
            // 显示输入对话框
            showEnterDialog();
        } else {
            // 显示设置密码对话框
            showSetupPwdDialog();
        }
    }

    private boolean isSetupPwd() {
        String pwd = sp.getString("password", null);
        return !TextUtils.isEmpty(pwd);
    }

    /**
     * 输入密码对话框
     */
    private void showEnterDialog() {
        AlertDialog.Builder builder = new Builder(MainActivity.this);
        View view = View.inflate(MainActivity.this, R.layout.dialog_enter_pwd,
                null);
        builder.setView(view);

        et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
        bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        bt_ok = (Button) view.findViewById(R.id.bt_ok);

        bt_cancel.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        bt_ok.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                String pwd = et_setup_pwd.getText().toString().trim();
                pwd = CommonUtil.md5EncodePassword(pwd);

                String savepwd = sp.getString("password", null);
                Log.i("MainActivity", pwd + " : " + savepwd);
                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(MainActivity.this, "密码不能为空,请重新输入",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pwd.equals(savepwd)) {
                    mDialog.dismiss();
                    enterIndexPage(MobileSafeActivity.class);
                } else {
                    Toast.makeText(MainActivity.this, "密码错误,请重新输入",
                            Toast.LENGTH_SHORT).show();
                    et_setup_pwd.setText("");
                    return;
                }
            }
        });

        mDialog = builder.create();
        mDialog.setView(view, 0, 0, 0, 0);
        mDialog.show();
    }

    /**
     * 设置密码对话框
     */
    private void showSetupPwdDialog() {
        AlertDialog.Builder builder = new Builder(MainActivity.this);
        View view = View.inflate(MainActivity.this, R.layout.dialog_setup_pwd,
                null);
        builder.setView(view);

        et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
        et_confirm_pwd = (EditText) view.findViewById(R.id.et_confirm_pwd);
        bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        bt_ok = (Button) view.findViewById(R.id.bt_ok);

        bt_cancel.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        bt_ok.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                String pwd = et_setup_pwd.getText().toString().trim();
                String confirm_pwd = et_confirm_pwd.getText().toString().trim();

                if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(confirm_pwd)) {
                    Toast.makeText(MainActivity.this, "密码不能为空,请重新输入",
                            Toast.LENGTH_SHORT).show();
                    et_setup_pwd.setText("");
                    et_confirm_pwd.setText("");
                    return;
                }

                if (pwd.length() < 6) {
                    Toast.makeText(MainActivity.this, "密码长度需要大于5位",
                            Toast.LENGTH_SHORT).show();
                    et_setup_pwd.setText("");
                    et_confirm_pwd.setText("");
                    return;
                }

                if (pwd.equals(confirm_pwd)) {
                    Editor edit = sp.edit();
                    edit.putString("password",
                            CommonUtil.md5EncodePassword(pwd));
                    edit.commit();
                    mDialog.dismiss();
                    enterIndexPage(MobileSafeActivity.class);

                } else {
                    Toast.makeText(MainActivity.this, "密码不一致,请重新输入",
                            Toast.LENGTH_SHORT).show();
                    et_confirm_pwd.setText("");
                    return;
                }
            }
        });

        mDialog = builder.create();
        mDialog.setView(view, 0, 0, 0, 0);
        mDialog.show();
    }

    /**
     * 根据传入的class，进入不同的Activity
     * 
     * @param class1
     */
    protected void enterIndexPage(Class<?> class1) {
        Intent intent = new Intent(MainActivity.this, class1);
        startActivity(intent);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if (convertView == null) {
                view = View.inflate(MainActivity.this, R.layout.list_item_main,
                        null);
                view.setBackgroundColor(Color.parseColor(bgColors[position]));
                holder = new ViewHolder();
                holder.view = (ImageView) view.findViewById(R.id.iv_itemimage);
                holder.text = (TextView) view.findViewById(R.id.tv_itemname);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            holder.text.setText(names[position]);
            holder.view.setImageResource(ids[position]);
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        class ViewHolder {
            ImageView view;
            TextView text;
        }

    }
}
