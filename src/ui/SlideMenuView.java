package ui;

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

    public void restorePosition() {
        this.scrollTo(0, 0);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once) {
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            ViewGroup content = (ViewGroup) wrapper.getChildAt(0);
            ViewGroup menu = (ViewGroup) wrapper.getChildAt(1);
            content.getLayoutParams().width = mScreenWidth;
            mMenuWidth = menu.getLayoutParams().width;
        }
        mHalfMenuWidth = mMenuWidth / 2;
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
                    scrollTo(mScreenWidth + mMenuWidth, 0);
                } else {
                    scrollTo(0, 0);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

}
