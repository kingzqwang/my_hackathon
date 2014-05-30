package com.qihoo.huangmabisheng.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class RootView extends FrameLayout{

	public RootView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public RootView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public RootView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		
		super.onScrollChanged(l, t, oldl, oldt);
	}
	
}
