package com.qihoo.huangmabisheng.view;

import com.qihoo.huangmabisheng.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class OptionCheckBox extends CheckBox{

	public OptionCheckBox(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
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
			setButtonDrawable(R.drawable.rect_bth_checkbox_normal);
		}else {
			setButtonDrawable(R.drawable.rect_btn_checkbox_normal);
		}
		super.setChecked(checked);
	}
	
}
