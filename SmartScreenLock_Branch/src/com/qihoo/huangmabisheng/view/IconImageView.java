package com.qihoo.huangmabisheng.view;

import com.qihoo.huangmabisheng.R;
import com.qihoo.huangmabisheng.constant.Constant;
import com.qihoo.huangmabisheng.utils.Log;
import com.qihoo.huangmabisheng.utils.MyWindowManager;

import android.R.integer;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class IconImageView extends ImageView {
	AnimatorSet animationUp;
	AnimatorSet animationDown;
	FrameLayout.LayoutParams layoutParams;
	int marginTop, iconWH, index;
	final String TAG = "IconImageView";
	String currentPck = null;
	ComponentName currentCpm = null;
	Drawable currentDrawable = null;
	PackageManager packageManager;

	public enum Orientation {
		UP, DOWN
	}

	public void switchApp(String pck, ComponentName cpm, Orientation orientation)
			throws NameNotFoundException {
		this.currentCpm = cpm;
		this.currentPck = pck;
		this.currentDrawable = packageManager.getPackageInfo(pck, 0).applicationInfo
				.loadIcon(packageManager);
		if (Orientation.UP == orientation) {
			flyOutUpForIndex();
		} else {
			flyOutDownForIndex();
		}
		setEnabled(true);
	}

	public void switchAppToBlank(Orientation orientation) {
		setEnabled(false);
		this.currentCpm = null;
		this.currentPck = null;
		this.currentDrawable = null;
		if (Orientation.UP == orientation) {
			flyOutUpForIndex();
		} else {
			flyOutDownForIndex();
		}
	}

	private void setAnimationDown() {
		ValueAnimator animationOutAlpha = ValueAnimator.ofFloat(1f, 0f);
		ValueAnimator animationOutTran = ValueAnimator.ofInt(0, (7-index )* iconWH/2);
		ValueAnimator animationInAlpha = ValueAnimator.ofFloat(0f, 1f);
		ValueAnimator animationInTran = ValueAnimator.ofInt(index * iconWH/2, 0);
		
		animationOutAlpha.setDuration(Constant.ANIMATION_TIME);
		animationOutAlpha.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float alpha = (Float) animation.getAnimatedValue();
				setAlpha(alpha);
			}
		});

		animationOutTran.setDuration(Constant.ANIMATION_TIME);
		animationOutTran.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final int margin = (Integer) animation.getAnimatedValue();
				layout(getLeft(), marginTop + margin, getRight(), iconWH
						+ marginTop + margin);
			}
		});

		animationInAlpha.setDuration(Constant.ANIMATION_TIME);
		animationInAlpha.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float alpha = (Float) animation.getAnimatedValue();
				setAlpha(alpha);
			}
		});
		animationInTran.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
					IconImageView.this.setImageDrawable(currentDrawable);
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
			}
			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
		animationInTran.setDuration(Constant.ANIMATION_TIME);
		animationInTran.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final int margin = (Integer) animation.getAnimatedValue();
				layout(getLeft(), marginTop - margin, getRight(), iconWH
						+ marginTop - margin);
			}
		});
		animationDown.play(animationOutAlpha).with(animationOutTran);
		animationDown.play(animationOutTran).before(animationInAlpha);
		animationDown.play(animationInAlpha).with(animationInTran);
		animationDown.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				MyWindowManager.getView().animating = true;
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				MyWindowManager.getView().animating = false;
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
	}

	private void setAnimationUp() {
		final ValueAnimator animationOutAlpha = ValueAnimator.ofFloat(1f, 0f);
		final ValueAnimator animationOutTran = ValueAnimator.ofInt(0, index * iconWH/2);
		final ValueAnimator animationInAlpha = ValueAnimator.ofFloat(0f, 1f);
		final ValueAnimator animationInTran = ValueAnimator.ofInt((7-index )* iconWH/2, 0);
		animationOutAlpha.setDuration(Constant.ANIMATION_TIME);
		animationOutAlpha.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float alpha = (Float) animation.getAnimatedValue();
				setAlpha(alpha);
			}
		});
		animationOutTran.setDuration(Constant.ANIMATION_TIME);
		animationOutTran.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final int margin = (Integer) animation.getAnimatedValue();
				layout(getLeft(), marginTop - margin, getRight(), iconWH
						+ marginTop - margin);
			}
		});
		animationInAlpha.setDuration(Constant.ANIMATION_TIME);
		animationInAlpha.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float alpha = (Float) animation.getAnimatedValue();
				setAlpha(alpha);
			}
		});
		animationInTran.setDuration(Constant.ANIMATION_TIME);
		animationInTran.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final int margin = (Integer) animation.getAnimatedValue();
				layout(getLeft(), marginTop + margin, getRight(), iconWH
						+ marginTop + margin);
			}
		});
		animationOutAlpha.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if(null!=currentDrawable)
					IconImageView.this.setImageDrawable(currentDrawable);
				else {
					IconImageView.this.setImageResource(R.drawable.transp);
				}
				super.onAnimationEnd(animation);
			}
		});
		animationUp.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				MyWindowManager.getView().animating = true;
				super.onAnimationStart(animation);
			}
			@Override
			public void onAnimationEnd(Animator animation) {
				MyWindowManager.getView().animating = false;
				super.onAnimationEnd(animation);
			}
		});
		animationUp.play(animationOutAlpha).with(animationOutTran);
		animationUp.play(animationOutTran).before(animationInAlpha);
		animationUp.play(animationInAlpha).with(animationInTran);
	}

	public IconImageView(Context context, final int iconWH, int marginTop,
			int index) {
		super(context);
		packageManager = getContext().getPackageManager();
		this.marginTop = index * marginTop;
		this.iconWH = iconWH;
		this.index = index;
		setImageResource(R.drawable.transp);
		final int marginLeft = com.qihoo.huangmabisheng.utils.DensityUtil
				.dip2px(getContext(), 20f);
		animationUp = new AnimatorSet();
		animationDown = new AnimatorSet();
		layoutParams = new FrameLayout.LayoutParams(iconWH, iconWH,
				Gravity.LEFT);
		layoutParams.setMargins(marginLeft, this.marginTop, 0, 0);
		setLayoutParams(layoutParams);
		setEnabled(false);
		setAnimationUp();
		setAnimationDown();
//		animationUp.setStartDelay((index - 1) * 50);
//		animationDown.setStartDelay((6 - index) * 50);
	}

	public void flyOutUpForIndex() {
		animationUp.setTarget(this);
		animationUp.start();
	}

	public void flyOutDownForIndex() {
		animationDown.setTarget(this);
		animationDown.start();
	}

	public void flyInUp() {

	}

	public void flyInDown() {

	}
}
