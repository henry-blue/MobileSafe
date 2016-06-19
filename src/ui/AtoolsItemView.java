package ui;

import com.mobilesafe.app.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 高级工具页面自定义子控件
 * @author Administrator
 *
 */
public class AtoolsItemView extends RelativeLayout {

	private TextView tv_index;
	private ImageView iv_goon;
	
	public AtoolsItemView(Context context) {
		super(context);
		initView(context);
	}

	public AtoolsItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		setAttributeSet(context, attrs);
	}

	private void initView(Context context) {
		View.inflate(context, R.layout.atools_item_view, this);
		tv_index = (TextView) this.findViewById(R.id.tv_index);
		iv_goon = (ImageView) this.findViewById(R.id.iv_goon);
	}
	
	private void setAttributeSet(Context context, AttributeSet attrs) {
		TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.AtoolsItemView);
		final String title = typeArray.getString(R.styleable.AtoolsItemView_content); 
		if (!TextUtils.isEmpty(title)) {
			setTitle(title);
		}
		final Drawable left = typeArray.getDrawable(R.styleable.AtoolsItemView_drawable_left);
		if (left != null) {
			setDrawable(left);
		}
		final boolean isShowArrow = typeArray.getBoolean(R.styleable.AtoolsItemView_show_arrow, true);
		if (!isShowArrow) {
			hideArrowRight();
		}
		
		typeArray.recycle();
	}

	private void hideArrowRight() {
		iv_goon.setVisibility(View.GONE);
	}

	private void setTitle(String title) {
		tv_index.setText(title);
	}
	
	private void setDrawable(Drawable left) {
		left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight()); 
		tv_index.setCompoundDrawables(left, null, null, null);
		tv_index.setCompoundDrawablePadding(8);
	}
	
	
}
