package com.cc.huangmabisheng.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageSwitcher extends ImageView{

	public ImageSwitcher(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ImageSwitcher(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ImageSwitcher(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void switchImageResource(final int resId) {
		final ValueAnimator out = ValueAnimator.ofFloat(1f, 0f).setDuration(200);
		out.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float alpha = (Float) animation.getAnimatedValue();
				setAlpha(alpha);
			}
		});
		final ValueAnimator in = ValueAnimator.ofFloat(0f, 1f).setDuration(200);
		in.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float alpha = (Float) animation.getAnimatedValue();
				setAlpha(alpha);
			}
		});
		in.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				clearAnimation();
				super.onAnimationEnd(animation);
			}
		});
		out.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				setImageResource(resId);
				in.start();
				super.onAnimationEnd(animation);
			}
		});
		out.start();
		
	}
	
}
