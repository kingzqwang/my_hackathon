package com.cc.huangmabisheng.view;

import com.cc.huangmabisheng.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class OptionCheckBox extends CheckBox{

	int srcOn = R.drawable.rect_on_checkbox_normal;
	int srcOff = R.drawable.rect_off_checkbox_normal;
	
	public OptionCheckBox(Context context) {
		super(context);
	}
	public void setSrc(int on,int off) {
		srcOff = off;
		srcOn = on;
	}
	public OptionCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public OptionCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setChecked(boolean checked) {
		setSelected(checked);
		if (checked) {
			setButtonDrawable(srcOn);
		}else {
			setButtonDrawable(srcOff);
		}
		super.setChecked(checked);
	}
	
}
