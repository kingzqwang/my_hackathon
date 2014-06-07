package com.qihoo.huangmabisheng.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.R.integer;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.qihoo.huangmabisheng.utils.ProcessUtil;
import com.qihoo.huangmabisheng.R;
import com.qihoo.huangmabisheng.activity.TransparentActivity;
import com.qihoo.huangmabisheng.constant.Constant;
import com.qihoo.huangmabisheng.constant.SharedPrefrencesAssist;
import com.qihoo.huangmabisheng.interfaces.IUpdateDescription;
import com.qihoo.huangmabisheng.interfaces.IUpdatePackageIcon;
import com.qihoo.huangmabisheng.interfaces.IUpdateTime;
import com.qihoo.huangmabisheng.model.AppDataForList;
import com.qihoo.huangmabisheng.service.FloatWindowService;
import com.qihoo.huangmabisheng.service.SmartLockService;
import com.qihoo.huangmabisheng.utils.Log;
import com.qihoo.huangmabisheng.utils.MyWindowManager;
import com.qihoo.huangmabisheng.utils.TipHelper;
import com.qihoo.huangmabisheng.view.IconImageView.Orientation;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FloatWindowBigView extends LinearLayout implements
		IUpdatePackageIcon, IUpdateTime, IUpdateDescription {
	public enum TouchType {
		UP_DOWN, OPEN_SCREEN, NONE
	}

	boolean animating = false;
	PackageManager packageManager;
	FloatWindowService service;
	/**
	 * 记录大悬浮窗的宽度
	 */
	public static int viewWidth;
	String TAG = "FloatWindowBigView";
	/**
	 * 记录大悬浮窗的高度
	 */
	public static int viewHeight;
	public ViewGroup rootView;
	// ImageView testImageView;
	// ImageSwitcher image0View;
	// ImageSwitcher image1View;
	// ImageSwitcher image2View;
	// ImageSwitcher image3View;
	// ImageSwitcher image4View;
	// ImageSwitcher image5View;

	IconImageView image0View;
	IconImageView image1View;
	IconImageView image2View;
	IconImageView image3View;
	IconImageView image4View;
	IconImageView image5View;
	IconImageView[] imageViews = new IconImageView[5];
	Orientation orientation = Orientation.DOWN;
	// ImageView specialImageView;
	// ImageView screenPhotoImageView;
	// ImageView imageViewCanvas;
	// ImageView imageViewClose;
	// ImageView imageViewHand;
	// ImageView imageViewAdd;
	// ViewGroup layoutGuess;
	TextView hourTextView;
	TextView minuteTextView;
	TextView monthDateTextView;
	TextView dayTextView;
	TextView descriptionTextView;
	ViewGroup canvasLayout;
	int openL, openR, openT, openB;
	public ImageSwitcher switcher;
	Canvas canvas;
	private Bitmap baseBitmap;
	private Paint paint;
	private VelocityTracker mVelocityTracker;
	private int mMinimumFlingVelocity;
	private int mMaximumFlingVelocity;
	ViewConfiguration configuration;
	public TouchType flag = TouchType.NONE;// 1,2
	final float scale = 0.1f;

	private void pressIcon(View except) {
		for (IconImageView v : imageViews) {
			if (v != except)
				v.scaleTo(1 - scale);
			else
				v.scaleTo(1 + scale);
		}
	}

	private void pressUpIcon(View except, int from) {
		for (IconImageView v : imageViews) {
			if (v != except)
				v.scaleFrom(1 - scale);
			else
				v.scaleFrom(1 + scale);
		}
		IconImageView e = (IconImageView) except;
		e.resetTrans(from, iconLeft);
	}

	private void pressOpenIcon(final View except) {
		Animation b = new ScaleAnimation(1f, 5f, 1f, 5f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		b.setDuration(400);
		b.setFillAfter(false);
		b.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				except.clearAnimation();
				pressUpIcon(except, 0);
			}
		});
		except.startAnimation(b);
		b.startNow();
		ValueAnimator animationAlpha = ValueAnimator.ofFloat(1f, 0f);
		animationAlpha.setDuration(400);
		animationAlpha.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final float alpha = (Float) animation.getAnimatedValue();
				rootView.setAlpha(alpha);
			}
		});
		animationAlpha.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				FloatWindowBigView.this.setVisibility(View.GONE);
				rootView.setAlpha(1f);
				
				super.onAnimationEnd(animation);
			}

		});
		animationAlpha.start();
	}

	private void layoutRootView(int l, int t, int r, int b) {
		// setAllIconAlpha(l);
		// this.scrollTo(-l, 0);
		rootView.layout(l, t, r, b);
	}

	int iconWH, iconLeft;

	private void findAllViews(Context context) {
		packageManager = context.getPackageManager();
		configuration = ViewConfiguration.get(context);
		mMinimumFlingVelocity = 20 * configuration
				.getScaledMinimumFlingVelocity();
		mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
		LayoutInflater.from(context).inflate(R.layout.floating_view, this);
		rootView = (ViewGroup) findViewById(R.id.floating_layout);

		viewWidth = MyWindowManager.getWindowManager(context)
				.getDefaultDisplay().getWidth();
		viewHeight = MyWindowManager.getWindowManager(context)
				.getDefaultDisplay().getHeight();
		Log.d(TAG, viewHeight + "");
		// image0View = (ImageSwitcher) rootView.findViewById(R.id.imageview_0);
		// image1View = (ImageSwitcher) rootView.findViewById(R.id.imageview_1);
		// image2View = (ImageSwitcher) rootView.findViewById(R.id.imageview_2);
		// image3View = (ImageSwitcher) rootView.findViewById(R.id.imageview_3);
		// image4View = (ImageSwitcher) rootView.findViewById(R.id.imageview_4);
		// image5View = (ImageSwitcher) rootView.findViewById(R.id.imageview_5);

		iconWH = viewHeight / 12;
		// com.qihoo.huangmabisheng.utils.DensityUtil.dip2px(getContext(), 56f);
		final int marginTop = viewHeight / 7;
		final int marginLeft = com.qihoo.huangmabisheng.utils.DensityUtil
				.dip2px(getContext(), 25f);
		image0View = new IconImageView(service, iconWH, marginTop, marginLeft,
				1);
		image1View = new IconImageView(service, iconWH, marginTop, marginLeft,
				2);
		image2View = new IconImageView(service, iconWH, marginTop, marginLeft,
				3);
		image3View = new IconImageView(service, iconWH, marginTop, marginLeft,
				4);
		image4View = new IconImageView(service, iconWH, marginTop, marginLeft,
				5);
		// image5View = new IconImageView(service, iconWH, marginTop, 6);
		rootView.addView(image0View);
		imageViews[0] = image0View;
		rootView.addView(image1View);
		imageViews[1] = image1View;
		rootView.addView(image2View);
		imageViews[2] = image2View;
		rootView.addView(image3View);
		imageViews[3] = image3View;
		rootView.addView(image4View);
		imageViews[4] = image4View;
		iconLeft = marginLeft;
		// rootView.addView(image5View);

		// specialImageView = (ImageView) rootView
		// .findViewById(R.id.imageview_photo);
		hourTextView = (TextView) rootView.findViewById(R.id.hour_textview);
		minuteTextView = (TextView) rootView.findViewById(R.id.minute_textview);
		monthDateTextView = (TextView) rootView
				.findViewById(R.id.month_date_textview);
		dayTextView = (TextView) rootView.findViewById(R.id.day_textview);

		// layoutGuess = (ViewGroup) rootView.findViewById(R.id.layout_guess);
		// imageViewCanvas = (ImageView) rootView
		// .findViewById(R.id.imageview_canvas);
		// imageViewCanvas.setOnTouchListener(touchListener);
		paint = new Paint();
		paint.setStrokeWidth(25);
		paint.setColor(Color.GREEN);
		// imageViewClose = (ImageView)
		// rootView.findViewById(R.id.close_canvas);
		// imageViewAdd = (ImageView) rootView.findViewById(R.id.add_canvas);
		// canvasLayout = (ViewGroup) rootView.findViewById(R.id.canvas_layout);
		// imageViewHand = (ImageView) rootView.findViewById(R.id.image_hand);

		switcher = (ImageSwitcher) findViewById(R.id.switcher);
		descriptionTextView = (TextView) findViewById(R.id.description_textview);
		// screenPhotoImageView =
		// (ImageView)findViewById(R.id.screen_photo_imageview);

	}

	// private View.OnTouchListener touchListener = new OnTouchListener() {
	// float startX;
	// float startY;
	//
	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// switch (event.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	// if (baseBitmap == null) {
	// baseBitmap = Bitmap.createBitmap(
	// imageViewCanvas.getWidth(),
	// imageViewCanvas.getHeight(),
	// Bitmap.Config.ARGB_8888);
	// canvas = new Canvas(baseBitmap);
	// }
	// startX = event.getX();
	// startY = event.getY();
	// break;
	// case MotionEvent.ACTION_MOVE:
	// float stopX = event.getX();
	// float stopY = event.getY();
	// // 根据两点坐标，绘制连线
	// canvas.drawLine(startX, startY, stopX, stopY, paint);
	// // 更新开始点的位置
	// startX = event.getX();
	// startY = event.getY();
	// // 把图片展示到ImageView中
	// imageViewCanvas.setImageBitmap(baseBitmap);
	// break;
	// case MotionEvent.ACTION_UP:
	// baseBitmap = Bitmap.createBitmap(imageViewCanvas.getWidth(),
	// imageViewCanvas.getHeight(), Bitmap.Config.ARGB_8888);
	// canvas = new Canvas(baseBitmap);
	// imageViewCanvas.setImageBitmap(baseBitmap);
	// Intent intent = packageManager
	// .getLaunchIntentForPackage("com.qihoo.appstore");
	// service.startActivity(intent);
	// // service.sendBroadcast(new Intent(
	// // "com.qihoo.huangmabisheng.finish"));
	// FloatWindowBigView.this.setVisibility(View.GONE);
	// break;
	// default:
	// break;
	// }
	// return false;
	// }
	// };

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	private OnTouchListener iconOnTouchListener = new OnTouchListener() {
		/**
		 * 初次按下的坐标
		 */
		int tempX = 0;
		long now;

		@Override
		public boolean onTouch(final View v, MotionEvent event) {
			if (animating) {
				return true;
			}
			int x = (int) event.getRawX();
			int y = (int) event.getRawY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// if(screenPhotoImageView == v)
				// service.sendBroadcast(new Intent(
				// "com.qihoo.huangmabisheng.opencamera"));
				now = SystemClock.elapsedRealtime();
				flag = TouchType.OPEN_SCREEN;
				pressIcon(v);
				tempX = (int) event.getRawX();
				return true;
			case MotionEvent.ACTION_MOVE:
				int l = iconLeft + (x - tempX > 0 ? x - tempX : 0);
				int t = v.getTop();
				int r = l + iconWH;
				int b = v.getBottom();
				v.layout(l, t, r, b);
				invalidate();
				return true;
			case MotionEvent.ACTION_UP:

				final int gl = v.getLeft();
				if (gl > viewWidth * 1 / 2) {
					finishTransparentActivity();
					// try {
					// Intent intent = new Intent();
					// intent.setComponent(((IconImageView) except).currentCpm);
					// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// service.startActivity(intent);
					// } catch (Exception e) {
					Intent intent = packageManager
							.getLaunchIntentForPackage(((IconImageView) v).currentPck);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					service.startActivity(intent);
					// }
					SharedPrefrencesAssist.instance(service).writeInt(
							Constant.SCREEN_OPEN_COUNT,
							SharedPrefrencesAssist.instance(service).readInt(
									Constant.SCREEN_OPEN_COUNT) + 1);
					pressOpenIcon(v);
				} else
					pressUpIcon(v, v.getLeft());
				flag = TouchType.NONE;
			}
			return true;
		}
	};

	private OnTouchListener rootViewOnTouchListener = new OnTouchListener() {
		/**
		 * 初次按下的坐标
		 */
		int tempX = 0;
		int startTouchX = 0;
		int startTouchY = 0;
		long now;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (animating) {
				return true;
			}
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
			}
			mVelocityTracker.addMovement(event);
			int x = (int) event.getRawX();
			int y = (int) event.getRawY();
			// int p = (int) event.getX();
			// int q = (int) event.getY();
			Log.i(TAG, "OnTouchListener" + " X is " + x + " Y is " + y);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				now = SystemClock.elapsedRealtime();
				startTouchX = x;
				startTouchY = y;
				tempX = (int) event.getRawX();

				if (v.getTag() == Constant.TAG_SCREEN_PHOTO) {
					flag = TouchType.OPEN_SCREEN;
					service.sendBroadcast(new Intent(
							"com.qihoo.huangmabisheng.opencamera"));
				}
				return true;
			case MotionEvent.ACTION_MOVE:
				int l = x - tempX > 0 ? x - tempX : 0;
				// int t = y - temp[1];
				int t = 0;
				// int r = v.getRight();
				int r = l + viewWidth;
				// int b = y + v.getHeight() - temp[1];
				int b = viewHeight;
				// Log.e(TAG, flag.toString());
				switch (flag) {
				case OPEN_SCREEN:
					if (l >= 0)
						layoutRootView(l, t, r, b);
					break;
				case UP_DOWN:
					break;
				default:// NONE
					Log.d(TAG, "" + flag);
					if (startTouchX == x && startTouchY == y) {
						break;
					}
					int xD,
					yD;
					if (0 > (xD = startTouchX - x))
						xD = x - startTouchX;
					if (0 > (yD = startTouchY - y))
						yD = y - startTouchY;
					if (xD < yD) {// yD/xD>2 要刷新
						flag = TouchType.UP_DOWN;
					} else {
						flag = TouchType.OPEN_SCREEN;
						if (l >= 0)
							layoutRootView(l, t, r, b);
					}
				}
				return true;
			case MotionEvent.ACTION_UP:
				
				final int gl = rootView.getLeft();
				if (gl <= 0) {
					service.startActivity(service.mainActivityIntent);
				}
				mVelocityTracker.computeCurrentVelocity(1000,
						mMaximumFlingVelocity);
				final float velocityY = mVelocityTracker.getYVelocity();
				final float velocityX = mVelocityTracker.getXVelocity();
				if ((Math.abs(velocityY) > mMinimumFlingVelocity)
						|| (Math.abs(velocityX) > mMinimumFlingVelocity)) {
					switch (flag) {
					/**
					 * case UP_DOWN: if (0 > velocityY) orientation =
					 * Orientation.UP; else orientation = Orientation.DOWN;
					 * service.sendBroadcast(new Intent(
					 * "com.qihoo.huangmabisheng.UPDATE_ICON")); flag =
					 * TouchType.NONE; mVelocityTracker.recycle();
					 * mVelocityTracker = null; return true;
					 */
					case OPEN_SCREEN:
						if (0 < velocityX) {
							if (gl < viewWidth * 1 / 4)
								break;
							// long duration = (long) ((viewWidth - gl)*1000 /
							// velocityX);
							// duration = duration > 400 ? 400 : duration;
							// duration = duration < 200 ? 200 : duration;
							openScreenLockAnim(gl, Constant.OPENSCREEN_TIME, v);
						} else {
							// long duration = (long) (gl * 1000 / -velocityX);
							// duration = duration > 400 ? 400 : duration;
							// duration = duration < 150 ? 150 : duration;
							closeScreenLockAnim(gl, 2 * gl
									* Constant.CLOSESCREEN_TIME / viewWidth, v);
						}
						flag = TouchType.NONE;
						mVelocityTracker.recycle();
						mVelocityTracker = null;
						return true;
					default:
						flag = TouchType.NONE;
						mVelocityTracker.recycle();
						mVelocityTracker = null;
						return true;
					}
				}
				// long len = SystemClock.elapsedRealtime() - now;
				Log.d(TAG + " Action_Up", gl + "");
				if (gl > viewWidth * 1 / 2) {// 开屏
					// long duration = len * (viewWidth - gl) / gl;
					// duration = duration > 400 ? 400 : duration;
					// duration = duration < 200 ? 200 : duration;
					openScreenLockAnim(gl, Constant.OPENSCREEN_TIME, v);
				} else {// 关屏
					// long duration = 800 * gl / viewWidth;
					// duration = duration > 400 ? 400 : duration;
					// duration = duration < 150 ? 150 : duration;
					closeScreenLockAnim(gl, 2 * gl * Constant.CLOSESCREEN_TIME
							/ viewWidth, v);
				}
				break;
			}
			flag = TouchType.NONE;
			mVelocityTracker.recycle();
			mVelocityTracker = null;
			return true;
		}
	};

	public void openScreenLockAnim(final int gl, long duration, final View v) {
		// if (old == 0) {
		Animation pre = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
		pre.setDuration(10000);
		rootView.startAnimation(pre);
		old = 1;
		// }
		final int left = rootView.getLeft();
		final int top = rootView.getTop();
		final int right = rootView.getRight();
		final int bottom = rootView.getBottom();
		ValueAnimator a = ValueAnimator.ofInt(0, viewWidth - left);
		a.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final int g = (Integer) animation.getAnimatedValue();
				// Log.e(TAG,
				// "g="+g+",left="+rootView.getLeft()+",top="+top+",right="+rootView.getRight()+",bottom+"+bottom);
				layoutRootView(left + g, top, right + g, bottom);
			}
		});
		a.setDuration(duration);
		a.setInterpolator(AnimationUtils.loadInterpolator(service,
				android.R.anim.decelerate_interpolator));

		a.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				animating = true;
				TipHelper.Vibrate(service, Constant.VIBRATE_TIME);
				super.onAnimationStart(animation);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				FloatWindowBigView.this.setVisibility(View.GONE);
				animating = false;
				flag = TouchType.NONE;
				finishTransparentActivity();
				rootView.clearAnimation();
				SharedPrefrencesAssist.instance(service).writeInt(
						Constant.SCREEN_OPEN_COUNT,
						SharedPrefrencesAssist.instance(service).readInt(
								Constant.SCREEN_OPEN_COUNT) + 1);
				super.onAnimationEnd(animation);
			}
		});
		a.start();
	}

	int old = 0;

	private void closeScreenLockAnim(final int lg, long duration, final View v) {
		// if (old == 0) {
		Animation pre = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
		pre.setDuration(10000);
		rootView.startAnimation(pre);
		old = 1;
		// }
		// // TODO 动画回弹
		final int left = rootView.getLeft();
		final int top = rootView.getTop();
		final int right = rootView.getRight();
		final int bottom = rootView.getBottom();
		ValueAnimator a = ValueAnimator.ofInt(0, left);
		service.startActivity(service.mainActivityIntent);
		a.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final int g = (Integer) animation.getAnimatedValue();
				layoutRootView(left - g, top, right - g, bottom);
			}
		});
		a.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				animating = true;
				super.onAnimationStart(animation);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				animating = false;
				flag = TouchType.NONE;
				rootView.clearAnimation();
				if (v.getTag() == Constant.TAG_SCREEN_PHOTO) {
					service.sendBroadcast(new Intent(
							"com.qihoo.huangmabisheng.closecamera"));
				}
				super.onAnimationEnd(animation);
			}
		});
		a.setDuration(duration);
		a.setInterpolator(AnimationUtils.loadInterpolator(service,
				android.R.anim.decelerate_interpolator));
		a.start();

	}

	private void setAllListeners() {
		this.setOnTouchListener(rootViewOnTouchListener);
		for (View v : imageViews) {
			v.setOnTouchListener(iconOnTouchListener);
		}
		switcher.setFactory(new ViewFactory() {

			@Override
			public View makeView() {
				ImageView i = new ImageView(service);
				i.setLayoutParams(new ImageSwitcher.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

				i.setScaleType(ScaleType.CENTER_CROP);
				return i;
			}
		});
		switcher.setInAnimation(AnimationUtils.loadAnimation(service,
				android.R.anim.fade_in));
		switcher.setOutAnimation(AnimationUtils.loadAnimation(service,
				android.R.anim.fade_out));
	}

	// public void dismissCanvas() {
	// canvasLayout.setVisibility(View.GONE);
	// imageViewHand.setImageResource(R.drawable.hand);
	// }

	// public void dismissHand(boolean b) {
	// if (b)
	// imageViewHand.setVisibility(View.VISIBLE);
	// else
	// imageViewHand.setVisibility(View.GONE);
	// }

	public FloatWindowBigView(final Context context) {
		super(context);
		service = (FloatWindowService) context;
		findAllViews(context);
		setAllListeners();
	}

	@Override
	public void updatePackageIcon(List<AppDataForList> list) {
		int i = 0;
		int count = 5;
		while (i < count && i < list.size()) {
			AppDataForList appDataForList = list.get(i);
			try {
				if (null == packageManager
						.getLaunchIntentForPackage(appDataForList.packageName)) {
					list.remove(i);
					continue;
				}
				imageViews[i].switchApp(appDataForList.packageName,
						appDataForList.currentCompoment, orientation);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				list.remove(i);
				continue;
			}
			i++;
		}
		for (; i < count; i++) {
			imageViews[i].switchAppToBlank(orientation);
		}
		if (SharedPrefrencesAssist.instance(service).readBoolean(
				Constant.SCREEN_PHOTO_IMAGEVIEW)) {
			Log.e(TAG, "SCREEN_PHOTO_IMAGEVIEW");
			imageViews[2].switchSpecial(orientation, Constant.TAG_SCREEN_PHOTO,
					R.drawable.screen_photo);
			imageViews[2].setOnTouchListener(rootViewOnTouchListener);
		} else {
			imageViews[2].setOnTouchListener(iconOnTouchListener);
		}
	}

	@Override
	public void updateTime(int hour, int minute, int month, int date, int day) {
		Log.d(TAG, rootView.getVisibility() + "," + rootView.getLeft());
		// if (flag != TouchType.NONE)
		// return;
		String h = hour / 10 + "" + hour % 10;
		if (!hourTextView.getText().equals(h)) {
			hourTextView.setText(h);
			Log.d(TAG, "hourTextView");
		}
		String m = minute / 10 + "" + minute % 10;
		if (!minuteTextView.getText().equals(m)) {
			minuteTextView.setText(m);
			Log.d(TAG, "minuteTextView");
		}
		String md = (month + 1) + "月" + date;
		if (!monthDateTextView.getText().equals(md)) {
			monthDateTextView.setText(md);
		}
		String d;
		switch (day) {
		case 0:
			d = "天";
			break;
		case 1:
			d = "一";
			break;
		case 2:
			d = "二";
			break;
		case 3:
			d = "三";
			break;
		case 4:
			d = "四";
			break;
		case 5:
			d = "五";
			break;
		default:
			d = "六";
			break;
		}
		if (!dayTextView.getText().equals(d)) {
			dayTextView.setText(d);
		}
	}

	@Override
	public void updatePackageGuess(String pck) throws NameNotFoundException {
	}

	private void finishTransparentActivity() {
		service.sendBroadcast(new Intent("com.qihoo.huangmabisheng.finish"));
	}

	@Override
	public void updateDescription(String description) {
		descriptionTextView.setText(description);
	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		Log.e(TAG, visibility + "");
		if (visibility == View.GONE) {
			synchronized (SmartLockService.class) {
				if (visibility == View.GONE)
					SmartLockService.class.notify();
			}
		} else {
			synchronized (FloatWindowBigView.class) {
				if (visibility != View.GONE)
					FloatWindowBigView.class.notify();
			}
		}
	}

}