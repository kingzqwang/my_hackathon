package com.cc.huangmabisheng.view;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.SystemClock;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.cc.huangmabisheng.R;
import com.cc.huangmabisheng.constant.Constant;
import com.cc.huangmabisheng.constant.SharedPrefrencesAssist;
import com.cc.huangmabisheng.interfaces.IUpdateDescription;
import com.cc.huangmabisheng.interfaces.IUpdatePackageIcon;
import com.cc.huangmabisheng.interfaces.IUpdateTime;
import com.cc.huangmabisheng.model.AppDataForList;
import com.cc.huangmabisheng.model.WorldCupMatch;
import com.cc.huangmabisheng.service.FloatWindowService;
import com.cc.huangmabisheng.service.SmartLockService;
import com.cc.huangmabisheng.utils.Log;
import com.cc.huangmabisheng.utils.MyWindowManager;
import com.cc.huangmabisheng.view.IconImageView.Orientation;

import android.widget.ImageSwitcher;

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
	public ViewGroup worldCupViewGroup;
	ImageView rvImageViews[] = new ImageView[4];
	ImageView lvImageViews[] = new ImageView[4];
	TextView rvTextViews[] = new TextView[4];
	TextView lvTextViews[] = new TextView[4];
	public ImageView clockImageViews[] = new ImageView[4];
	TextView timeTextViews[] = new TextView[4];
	public TextView clockTextViews[] = new TextView[4];
	ViewGroup layouts[] = new ViewGroup[4];
	public ViewGroup clockLayouts[] = new ViewGroup[4];
	
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
		// com.cc.huangmabisheng.utils.DensityUtil.dip2px(getContext(), 56f);
		final int marginTop = viewHeight / 7;
		final int marginLeft = com.cc.huangmabisheng.utils.DensityUtil
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
		worldCupViewGroup = (ViewGroup)findViewById(R.id.world_cup_layout);
		lvImageViews[0] = (ImageView)worldCupViewGroup.findViewById(R.id.lv_imageview_0);
		lvImageViews[1] = (ImageView)worldCupViewGroup.findViewById(R.id.lv_imageview_1);
		lvImageViews[2] = (ImageView)worldCupViewGroup.findViewById(R.id.lv_imageview_2);
		lvImageViews[3] = (ImageView)worldCupViewGroup.findViewById(R.id.lv_imageview_3);
		lvTextViews[0] = (TextView)worldCupViewGroup.findViewById(R.id.lv_textview_0);
		lvTextViews[1] = (TextView)worldCupViewGroup.findViewById(R.id.lv_textview_1);
		lvTextViews[2] = (TextView)worldCupViewGroup.findViewById(R.id.lv_textview_2);
		lvTextViews[3] = (TextView)worldCupViewGroup.findViewById(R.id.lv_textview_3);
		rvImageViews[0] = (ImageView)worldCupViewGroup.findViewById(R.id.rv_imageview_0);
		rvImageViews[1] = (ImageView)worldCupViewGroup.findViewById(R.id.rv_imageview_1);
		rvImageViews[2] = (ImageView)worldCupViewGroup.findViewById(R.id.rv_imageview_2);
		rvImageViews[3] = (ImageView)worldCupViewGroup.findViewById(R.id.rv_imageview_3);
		rvTextViews[0] = (TextView)worldCupViewGroup.findViewById(R.id.rv_textview_0);
		rvTextViews[1] = (TextView)worldCupViewGroup.findViewById(R.id.rv_textview_1);
		rvTextViews[2] = (TextView)worldCupViewGroup.findViewById(R.id.rv_textview_2);
		rvTextViews[3] = (TextView)worldCupViewGroup.findViewById(R.id.rv_textview_3);
		timeTextViews[0] = (TextView)worldCupViewGroup.findViewById(R.id.time_textview_0);
		timeTextViews[1] = (TextView)worldCupViewGroup.findViewById(R.id.time_textview_1);
		timeTextViews[2] = (TextView)worldCupViewGroup.findViewById(R.id.time_textview_2);
		timeTextViews[3] = (TextView)worldCupViewGroup.findViewById(R.id.time_textview_3);
		clockTextViews[0] = (TextView)worldCupViewGroup.findViewById(R.id.clock_textview_0);
		clockTextViews[1] = (TextView)worldCupViewGroup.findViewById(R.id.clock_textview_1);
		clockTextViews[2] = (TextView)worldCupViewGroup.findViewById(R.id.clock_textview_2);
		clockTextViews[3] = (TextView)worldCupViewGroup.findViewById(R.id.clock_textview_3);
		clockImageViews[0] = (ImageView)worldCupViewGroup.findViewById(R.id.clock_imageview_0);
		clockImageViews[1] = (ImageView)worldCupViewGroup.findViewById(R.id.clock_imageview_1);
		clockImageViews[2] = (ImageView)worldCupViewGroup.findViewById(R.id.clock_imageview_2);
		clockImageViews[3] = (ImageView)worldCupViewGroup.findViewById(R.id.clock_imageview_3);
		layouts[0] = (ViewGroup)worldCupViewGroup.findViewById(R.id.layout_0);
		layouts[1] = (ViewGroup)worldCupViewGroup.findViewById(R.id.layout_1);
		layouts[2] = (ViewGroup)worldCupViewGroup.findViewById(R.id.layout_2);
		layouts[3] = (ViewGroup)worldCupViewGroup.findViewById(R.id.layout_3);
		clockLayouts[0] = (ViewGroup)worldCupViewGroup.findViewById(R.id.clock_layout_0);
		clockLayouts[1] = (ViewGroup)worldCupViewGroup.findViewById(R.id.clock_layout_1);
		clockLayouts[2] = (ViewGroup)worldCupViewGroup.findViewById(R.id.clock_layout_2);
		clockLayouts[3] = (ViewGroup)worldCupViewGroup.findViewById(R.id.clock_layout_3);
	}
	
	public void setWorldCup() {
		if (!SharedPrefrencesAssist.instance(service).readBoolean(
				Constant.WORLD_CUP_IMAGEVIEW)) {
			worldCupViewGroup.setVisibility(View.GONE);
		}else {
			worldCupViewGroup.setVisibility(View.VISIBLE);
		}
	}
	
	public void updateWorldCup(List<WorldCupMatch> matchsToday) {
		Log.e(TAG, "updateWorldCup");
		DateFormat dateFormat = new java.text.SimpleDateFormat("HH:mm");  
		int s = matchsToday.size();
		Date now = new Date();
		for (int i = 0; i < s; i++) {
			layouts[i].setVisibility(View.VISIBLE);
			WorldCupMatch worldCupMatch = matchsToday.get(i);
			lvImageViews[i].setImageResource(Constant.WORLD_CUP_DRAWABLE_SRC[worldCupMatch.lv]);
			rvImageViews[i].setImageResource(Constant.WORLD_CUP_DRAWABLE_SRC[worldCupMatch.rv]);
			lvTextViews[i].setText(Constant.WORLD_CUP_DRAWABLE_NAME[worldCupMatch.lv]);
			rvTextViews[i].setText(Constant.WORLD_CUP_DRAWABLE_NAME[worldCupMatch.rv]);
			timeTextViews[i].setText(dateFormat.format(worldCupMatch.start));
			if (now.after(worldCupMatch.start)) {
				layouts[i].setAlpha(0.4f);
			}else {
				layouts[i].setAlpha(0.8f);
			}
			if (worldCupMatch.clockable) {
				clockImageViews[i].setImageResource(R.drawable.eys_open);
				clockTextViews[i].setText("闹钟开");
			}else {
				clockImageViews[i].setImageResource(R.drawable.eys_close);
				clockTextViews[i].setText("闹钟关");
			}
		}
		for (int i = s; i < 4; i++) {
			layouts[i].setVisibility(View.GONE);
		}
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
	// // "com.cc.huangmabisheng.finish"));
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
				// "com.cc.huangmabisheng.opencamera"));
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
							"com.cc.huangmabisheng.opencamera"));
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
					 * "com.cc.huangmabisheng.UPDATE_ICON")); flag =
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
		pre.setDuration(duration);
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
		pre.setDuration(duration);
		rootView.startAnimation(pre);
		old = 1;
		// }
		// // TODO 动画回弹
		final int left = rootView.getLeft();
		final int top = rootView.getTop();
		final int right = rootView.getRight();
		final int bottom = rootView.getBottom();
		ValueAnimator a = ValueAnimator.ofInt(0,left);
		service.startActivity(service.mainActivityIntent);
		a.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				final int g = (Integer) animation.getAnimatedValue();
				layoutRootView(left-g, top, right-g, bottom);
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
				Log.e(TAG, "closeScreenLockAnim end");
				animating = false;
				flag = TouchType.NONE;
				rootView.clearAnimation();
				if (v.getTag() == Constant.TAG_SCREEN_PHOTO) {
					service.sendBroadcast(new Intent(
							"com.cc.huangmabisheng.closecamera"));
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
//		if (android.os.Build.VERSION.SDK_INT >= 16) {
//			Log.e(TAG, "android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT");
//			setSystemUiVisibility(
//					View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
//					View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//					View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//					View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//					View.SYSTEM_UI_FLAG_FULLSCREEN |
//					View.SYSTEM_UI_FLAG_IMMERSIVE);
//		}
		
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
				i.setBackgroundDrawable(null);
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
		service.sendBroadcast(new Intent("com.cc.huangmabisheng.finish"));
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