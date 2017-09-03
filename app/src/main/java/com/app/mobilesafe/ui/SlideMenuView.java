package com.app.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SlideMenuView extends HorizontalScrollView {

    private int mScreenWidth;
    private int mHalfMenuWidth;
    private int mMenuWidth;
    private boolean once;
    private boolean isOpened;
    private OnMenuViewListener menuViewListener; 
    
    public SlideMenuView(Context context) {
       this(context, null);
    }
    
    public SlideMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.setHorizontalScrollBarEnabled(false);

    }
    
    /**
     * 还原位置
     */
    public void restorePosition() {
        this.scrollTo(0, 0);
    }
    
    /**
     * 判断菜单项是否打开
     * @return 菜单项是否打开
     */
    public boolean isOpenMenued() {
        return isOpened;
    }
    
    /**
     * 打开菜单项
     */
    public void openMenuView() {
        if (isOpened) {
            return;
        }
        this.smoothScrollTo(mScreenWidth + mMenuWidth, 0);
        isOpened = true;
    }
    
    /**
     * 关闭菜单项
     */
    public void closeMenuView() {
        if (isOpened) {
            this.smoothScrollTo(0, 0);
            isOpened = false;
        }
    }
    
    /**
     * 切换菜单状态
     */
    public void toggle() {
        if (isOpened) {
            closeMenuView();
        } else {
            openMenuView();
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once) {
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            ViewGroup content = (ViewGroup) wrapper.getChildAt(0);
            ViewGroup menu = (ViewGroup) wrapper.getChildAt(1);
            content.getLayoutParams().width = mScreenWidth;
            mMenuWidth = menu.getLayoutParams().width;
            mHalfMenuWidth = mMenuWidth / (menu.getChildCount());
        }
        
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.scrollTo(0, 0);
            once = true;
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                if (scrollX >= mHalfMenuWidth) {
                    //模拟器上可能没效果
                    this.smoothScrollTo(mScreenWidth + mMenuWidth, 0);
                    isOpened = true;
                    menuViewListener.onMenuOpened(isOpened);
                    return false;
                } else {
                    this.smoothScrollTo(0, 0);
                    isOpened = false;
                    menuViewListener.onMenuOpened(isOpened);
                    return false;
                }
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }
 
    public void setOnMenuViewScrollListener(OnMenuViewListener listener) {
        this.menuViewListener = listener;
    }
    
    public interface OnMenuViewListener {
        void onMenuOpened(boolean isOpened);
    }
}
