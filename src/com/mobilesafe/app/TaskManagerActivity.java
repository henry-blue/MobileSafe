package com.mobilesafe.app;

import java.util.ArrayList;
import java.util.List;

import utils.SystemInfoUtils;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import domain.TaskInfo;
import engine.TaskInfoPrvider;

public class TaskManagerActivity extends BaseAcitivity {

	private TextView tv_process_count;
	private TextView tv_mem_info;
	
	private LinearLayout ll_loading;
	private ListView lv_task_manager;
	private List<TaskInfo> taskInfos;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> sysTaskInfos;
	private MyAapater adapter;
	private TextView tv_task_status;
	
	private Button btn_menu;
	
	private int processCount;
	private long availMem;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taskmanager);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
		tv_task_status = (TextView) findViewById(R.id.tv_show_task_count);
		btn_menu = (Button) findViewById(R.id.btn_menu);
		
		btn_menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showPopMenu(btn_menu);
			}
		});
		
		setTitle();
		
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_task_manager = (ListView) findViewById(R.id.lv_tasks_manager);
		adapter = new MyAapater();
		
		fillData();
		
		lv_task_manager.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (userTaskInfos != null && sysTaskInfos != null) {
					if (firstVisibleItem >= userTaskInfos.size() + 1) {
						tv_task_status.setText("系统进程( " + sysTaskInfos.size() + " )");
					} else {
						tv_task_status.setText("用户进程( " + userTaskInfos.size() + " )");
					}
				}
			}
		});
		
		lv_task_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				TaskInfo taskInfo;
				if (position == 0) {
	                return;
	            } else if (position == (userTaskInfos.size() + 1)) {
	                return;
	            } else if (position <= userTaskInfos.size()) { // 用户程序
	                int newPositon = position - 1;
	                taskInfo = userTaskInfos.get(newPositon);
	            } else { // 系统程序
	                int newPosition = position - userTaskInfos.size() - 2;
	                taskInfo = sysTaskInfos.get(newPosition);
	            }
				//屏蔽自身进程
				if (taskInfo.getPackName().equals(getPackageName())) {
					return;
				}
				ViewHolder holder = (ViewHolder) view.getTag();
				if (taskInfo.isChecked()) {
					taskInfo.setChecked(false);
					holder.cb_status.setChecked(false);
				} else {
					taskInfo.setChecked(true);
					holder.cb_status.setChecked(true);
				}
			}
		});
	}

	protected void showPopMenu(View view) {
		 PopupMenu popupMenu = new PopupMenu(this, view);
		 // menu布局
		 popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
		 // menu的item点击事件
		 popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
		  @Override
		  public boolean onMenuItemClick(MenuItem item) {
			  
		  switch (item.getItemId()) {
		case R.id.action_select:
			selectAllTask();
			break;
		case R.id.action_inselect:
			unSelectAllTask();
			break;
		case R.id.action_clean:
			killAllTask();
			break;
		case R.id.action_setting:
			enterSetting();
			break;
		default:
			break;
		}
		  return false;
		  }
		 });
		 // PopupMenu关闭事件
		 popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
		  @Override
		  public void onDismiss(PopupMenu menu) {
			  menu.dismiss();
		  }
		 });
		 popupMenu.show();
	}

	private void setTitle() {
		processCount = SystemInfoUtils.getRunningProcessCount(TaskManagerActivity.this);
		tv_process_count.setText("运行中的进程( " + processCount + " )");
		availMem = SystemInfoUtils.getAvailMem(TaskManagerActivity.this);
		long totalMem = SystemInfoUtils.getTotalMem(TaskManagerActivity.this);
		tv_mem_info.setText("剩余/总内存( " + Formatter.formatFileSize(TaskManagerActivity.this, availMem) +
				"/" + Formatter.formatFileSize(TaskManagerActivity.this, totalMem) + " )");
	}

	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(){

			public void run() {
				taskInfos = TaskInfoPrvider.getTaskInfos(TaskManagerActivity.this);
				userTaskInfos = new ArrayList<TaskInfo>();
				sysTaskInfos = new ArrayList<TaskInfo>();
				
				for (TaskInfo info : taskInfos) {
					if (info.isUserTask()) {
						userTaskInfos.add(info);
					} else {
						sysTaskInfos.add(info);
					}
				}
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						ll_loading.setVisibility(View.GONE);
						lv_task_manager.setAdapter(adapter);
						setTitle();
					}
				});
			};
		}.start();
	}
	
	private class MyAapater extends BaseAdapter {

        @Override
        public int getCount() {
        	if (sp.getBoolean("showsystem", false)) {
        		return taskInfos.size() + 2;
        	} else {
        		return userTaskInfos.size() + 1;
        	}
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TaskInfo taskInfo;

            if (position == 0) {
                TextView tv = new TextView(TaskManagerActivity.this);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.parseColor("#77212121"));
                tv.setText("用户程序 ( " + userTaskInfos.size() + " )");
                return tv;
            } else if (position == (userTaskInfos.size() + 1)) {
                TextView tv = new TextView(TaskManagerActivity.this);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.parseColor("#77212121"));
                tv.setText("系统程序 ( " + sysTaskInfos.size() + " )");
                return tv;
            } else if (position <= userTaskInfos.size()) { // 用户程序
                int newPositon = position - 1;
                taskInfo = userTaskInfos.get(newPositon);
            } else { // 系统程序
                int newPosition = position - userTaskInfos.size() - 2;
                taskInfo = sysTaskInfos.get(newPosition);
            }
            
            View view;
            final ViewHolder holder;

            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(TaskManagerActivity.this,
                        R.layout.list_item_taskinfo, null);
                holder = new ViewHolder();
                holder.tv_name = (TextView) view.findViewById(R.id.tv_task_name);
                holder.tv_size = (TextView) view
                        .findViewById(R.id.tv_task_size);
                holder.iv_icon = (ImageView) view
                        .findViewById(R.id.iv_task_icon);
                holder.cb_status = (CheckBox) view.findViewById(R.id.cb_task_isdelete);
                view.setTag(holder);
            }
            holder.iv_icon.setImageDrawable(taskInfo.getIcon());
            holder.tv_name.setText(taskInfo.getName());
            holder.tv_size.setText("内存占用:" + Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemSize()));
            holder.cb_status.setChecked(taskInfo.isChecked());
            if (getPackageName().equals(taskInfo.getPackName())) {
            	holder.cb_status.setVisibility(View.INVISIBLE);
			} else {
				holder.cb_status.setVisibility(View.VISIBLE);
			}
			
            return view;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }
	
    static class ViewHolder {
        TextView tv_name;
        TextView tv_size;
        ImageView iv_icon;
        CheckBox cb_status;
    }

	/**
	 * 选中全部
	 */
	private void selectAllTask() {
		List<TaskInfo> tmpInfos;
		if (sp.getBoolean("showsystem", false)) {
			tmpInfos = taskInfos;
		} else {
			tmpInfos = userTaskInfos;
		}
		for (TaskInfo info : tmpInfos) {
			if (getPackageName().equals(info.getPackName())) {
				continue;
			}
			info.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 反选
	 */
	private void unSelectAllTask() {
		List<TaskInfo> tmpInfos;
		if (sp.getBoolean("showsystem", false)) {
			tmpInfos = taskInfos;
		} else {
			tmpInfos = userTaskInfos;
		}
		
		for (TaskInfo info : tmpInfos) {
			if (getPackageName().equals(info.getPackName())) {
				continue;
			}
			info.setChecked(!info.isChecked());
		}
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 一键清理
	 */
	private void killAllTask() {
		List<TaskInfo> tmpInfos;
		if (sp.getBoolean("showsystem", false)) {
			tmpInfos = taskInfos;
		} else {
			tmpInfos = userTaskInfos;
		}
		
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int count = 0;
		int releaseMem = 0;
		List<TaskInfo> killedTaskInfos = new ArrayList<TaskInfo>();
		for (TaskInfo info : tmpInfos) {
			if (info.isChecked()) {
				am.killBackgroundProcesses(info.getPackName());
				if (info.isUserTask()) {
					userTaskInfos.remove(info);
				} else {
					sysTaskInfos.remove(info);
				}
				killedTaskInfos.add(info);
				count++;
				releaseMem += info.getMemSize();
			}
		}
		tmpInfos.removeAll(killedTaskInfos);
		
		adapter.notifyDataSetChanged();
		Toast.makeText(TaskManagerActivity.this,
				"结束" + count + "个进程,释放" + Formatter.formatFileSize(TaskManagerActivity.this, releaseMem) + "内存",
				Toast.LENGTH_SHORT).show();
		//更新title
		processCount -= count;
		availMem += releaseMem;
		tv_process_count.setText("运行中的进程( " + processCount + " )");
		long totalMem = SystemInfoUtils.getTotalMem(TaskManagerActivity.this);
		tv_mem_info.setText("剩余/总内存( " + Formatter.formatFileSize(TaskManagerActivity.this, availMem) +
				"/" + Formatter.formatFileSize(TaskManagerActivity.this, totalMem) + " )");
	
	}

	/**
	 * 进入设置
	 */
	private void enterSetting() {
		Intent intent = new Intent(TaskManagerActivity.this, TaskSettingAcitivity.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		adapter.notifyDataSetChanged();
		super.onActivityResult(requestCode, resultCode, data);
	}
}
