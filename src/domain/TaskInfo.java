package domain;

import android.graphics.drawable.Drawable;

/**
 * 进程信息
 * 
 * @author Administrator
 * 
 */
public class TaskInfo {
	private Drawable icon;
	private String name;
	private String packName;
	private long memSize;
	private boolean isUserTask;
	private boolean isChecked;
	
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackName() {
		return packName;
	}
	public void setPackName(String packName) {
		this.packName = packName;
	}
	public long getMemSize() {
		return memSize;
	}
	public void setMemSize(long memSize) {
		this.memSize = memSize;
	}
	public boolean isUserTask() {
		return isUserTask;
	}
	public void setUserTask(boolean isUserTask) {
		this.isUserTask = isUserTask;
	}
	
	@Override
	public String toString() {
		return "TaskInfo [icon=" + icon + ", name=" + name + ", packName="
				+ packName + ", memSize=" + memSize + ", isUserTask="
				+ isUserTask + "]";
	}

	
}
