package com.qihoo.huangmabisheng.view;

import java.util.Date;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.qihoo.huangmabisheng.utils.ProcessUtil;
import com.qihoo.huangmabisheng.R;
import com.qihoo.huangmabisheng.activity.TransparentActivity;
import com.qihoo.huangmabisheng.constant.Constant;
import com.qihoo.huangmabisheng.constant.SharedPrefrencesAssist;
import com.qihoo.huangmabisheng.interfaces.IUpdatePackageIcon;
import com.qihoo.huangmabisheng.interfaces.IUpdateTime;
import com.qihoo.huangmabisheng.model.AppDataForList;
import com.qihoo.huangmabisheng.service.FloatWindowService;
import com.qihoo.huangmabisheng.utils.Log;
import com.qihoo.huangmabisheng.utils.MyWindowManager;

public class FloatWindowBigView extends LinearLayout implements
		IUpdatePackageIcon, IUpdateTime {
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
	ImageSwitcher image0View;
	ImageSwitcher image1View;
	ImageSwitcher image2View;
	ImageSwitcher image3View;
	ImageSwitcher image4View;
	ImageSwitcher image5View;
	ImageView imageViewCanvas;
	ImageView imageViewClose;
	ImageView imageViewHand;
	ImageView imageViewAdd;
	ViewGroup layoutGuess;
	TextView hourTextView;
	TextView minuteTextView;
	ViewGroup canvasLayout;
	int openL, openR, openT, openB;
	public ImageSwitcher switcher;
	Canvas canvas;
	private Bitmap baseBitmap;
	private Paint paint;
	public TouchType flag = TouchType.NONE;// 1,2

	private void findAllViews(Context context) {
		packageManager = context.getPackageManager();
		LayoutInflater.from(context).inflate(R.layout.floating_view, this);
		rootView = (ViewGroup) findViewById(R.id.floating_layout);

		viewWidth = MyWindowManager.getWindowManager(context)
				.getDefaultDisplay().getWidth();
		viewHeight = MyWindowManager.getWindowManager(context)
				.getDefaultDisplay().getHeight();
		Log.d(TAG, viewHeight + "");
		image0View = (ImageSwitcher) rootView.findViewById(R.id.imageview_0);
		image1View = (ImageSwitcher) rootView.findViewById(R.id.imageview_1);
		image2View = (ImageSwitcher) rootView.findViewById(R.id.imageview_2);
		image3View = (ImageSwitcher) rootView.findViewById(R.id.imageview_3);
		image4View = (ImageSwitcher) rootView.findViewById(R.id.imageview_4);
		image5View = (ImageSwitcher) rootView.findViewById(R.id.imageview_5);
		hourTextView = (TextView) rootView.findViewById(R.id.hour_textview);
		minuteTextView = (TextView) rootView.findViewById(R.id.minute_textview);
		layoutGuess = (ViewGroup) rootView.findViewById(R.id.layout_guess);
		imageViewCanvas = (ImageView) rootView
				.findViewById(R.id.imageview_canvas);
		imageViewCanvas.setOnTouchListener(touchListener);
		paint = new Paint();
		paint.setStrokeWidth(25);
		paint.setColor(Color.GREEN);
		imageViewClose = (ImageView) rootView.findViewById(R.id.close_canvas);
		imageViewAdd = (ImageView) rootView.findViewById(R.id.add_canvas);
		canvasLayout = (ViewGroup) rootView.findViewById(R.id.canvas_layout);
		imageViewHand = (ImageView) rootView.findViewById(R.id.image_hand);

		switcher = (ImageSwitcher) findViewById(R.id.switcher);

	}

	private View.OnTouchListener touchListener = new OnTouchListener() {
		float startX;
		float startY;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (baseBitmap == null) {
					baseBitmap = Bitmap.createBitmap(
							imageViewCanvas.getWidth(),
							imageViewCanvas.getHeight(),
							Bitmap.Config.ARGB_8888);
					canvas = new Canvas(baseBitmap);
				}
				startX = event.getX();
				startY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				float stopX = event.getX();
				float stopY = event.getY();
				// 根据两点坐标，绘制连线
				canvas.drawLine(startX, startY, stopX, stopY, paint);
				// 更新开始点的位置
				startX = event.getX();
				startY = event.getY();
				// 把图片展示到ImageView中
				imageViewCanvas.setImageBitmap(baseBitmap);
				break;
			case MotionEvent.ACTION_UP:
				baseBitmap = Bitmap.createBitmap(imageViewCanvas.getWidth(),
						imageViewCanvas.getHeight(), Bitmap.Config.ARGB_8888);
				canvas = new Canvas(baseBitmap);
				imageViewCanvas.setImageBitmap(baseBitmap);
				Intent intent = packageManager
						.getLaunchIntentForPackage("com.qihoo.appstore");
				service.startActivity(intent);
				service.sendBroadcast(new Intent(
						"com.qihoo.huangmabisheng.finish"));
				FloatWindowBigView.this.setVisibility(View.GONE);
				break;
			default:
				break;
			}
			return false;
		}
	};

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	private OnTouchListener iconOnTouchListener = new OnTouchListener() {
		int[] temp = new int[] { 0, 0 };
		int[] startTouch = new int[] { 0, 0 };
		Date now;

		@Override
		public boolean onTouch(final View view, MotionEvent event) {
			synchronized (FloatWindowBigView.this) {

				if (animating) {
					return true;
				}
				int x = (int) event.getRawX();
				int y = (int) event.getRawY();
				// int p = (int) event.getX();
				// int q = (int) event.getY();
				Log.i(TAG, "OnTouchListener" + " X is " + x + " Y is " + y);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (view instanceof ImageView) {

						Animation a = new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
								Animation.RELATIVE_TO_SELF, 0.5f,
								Animation.RELATIVE_TO_SELF, 0.5f);
						a.setDuration(300);
						a.setFillAfter(true);
						view.startAnimation(a);
						a.startNow();
						if (TransparentActivity.context != null)
							TransparentActivity.context.finish();
						// Intent intent = new Intent();
						// intent.setComponent((ComponentName)view.getTag());
						// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						// service.startActivity(intent);
						// intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
						Intent intent = packageManager
								.getLaunchIntentForPackage(((String) view
										.getTag()));
						android.util.Log.d(TAG, (String) view.getTag());
						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						service.startActivity(intent);
					}
					now = new Date();
					startTouch[0] = x;
					startTouch[1] = y;
					temp[0] = (int) event.getRawX();
					// temp[1] = y - rootView.getTop();
					return true;
				case MotionEvent.ACTION_MOVE:
					if (TouchType.NONE == flag) {
						Log.i(TAG, "" + flag);
						if (startTouch[0] == x && startTouch[1] == y) {
							return true;
						} else if ((startTouch[0] == x && startTouch[1] != y)
								|| Math.abs(startTouch[1] - y)
										/ Math.abs(startTouch[0] - x) >= Constant.DECIDE_REFRESH) {
							if (view instanceof ImageView)
								flag = TouchType.OPEN_SCREEN;
							else
								flag = TouchType.UP_DOWN;// 上下刷新
						} else {
							flag = TouchType.OPEN_SCREEN;// 左右开屏
						}

						return true;
					}

					if (TouchType.UP_DOWN == flag) {
						Log.i(TAG, "" + flag);
						return true;
					}
					// int l = v.getLeft();
					int l = x - temp[0] > 0 ? x - temp[0] : rootView.getLeft();
					// int t = y - temp[1];
					int t = rootView.getTop();
					// int r = v.getRight();
					int r = x - temp[0] > 0 ? x + rootView.getWidth() - temp[0]
							: rootView.getRight();
					// int b = y + v.getHeight() - temp[1];
					int b = rootView.getBottom();
					if (l < 0)
						return true;
					rootView.layout(l, t, r, b);
					// rootView.invalidate();
					return true;
				case MotionEvent.ACTION_UP:
//					ProcessUtil.clearBackgroundProcess("com.qihoo.browser",service);
					
					if (TouchType.UP_DOWN == flag) {
						long len = new Date().getTime() - now.getTime();
						if (len < 200 && 100 < Math.abs(startTouch[1] - y)) {
							if (y < startTouch[1])
								flypUpAnimation();
							else
								flypDownAnimation();
							service.sendBroadcast(new Intent(
									"com.qihoo.huangmabisheng.UPDATE_ICON"));
						}
					}
					flag = TouchType.NONE;
					final int gl = rootView.getLeft();
					if (gl <= 0) {
						service.startActivity(service.mainActivityIntent);
						if (view instanceof ImageView) {
							Animation a = new ScaleAnimation(1.2f, 1f, 1.2f,
									1f, Animation.RELATIVE_TO_SELF, 0.5f,
									Animation.RELATIVE_TO_SELF, 0.5f);
							a.setDuration(300);
							a.setFillAfter(true);
							view.startAnimation(a);
							a.startNow();
						}
						return true;
					}
					Log.d(TAG + " Action_Up", gl + "");
					if (rootView.getLeft() > viewWidth * 2 / 5) {// 开屏
						Animation a = new TranslateAnimation(0.0f, viewWidth
								- gl, 0.0f, 0.0f);
						long len = new Date().getTime() - now.getTime();
						long duration = 2 * len / gl * (viewWidth - gl);
						duration = duration > 500 ? 500 : duration;
						duration = duration < 50 ? 50 : duration;
						android.util.Log.d(TAG, "开屏时间说设定："+len);
						a.setDuration(duration);
						a.setFillEnabled(true);
						a.setInterpolator(AnimationUtils
								.loadInterpolator(service,
										android.R.anim.decelerate_interpolator));
						rootView.startAnimation(a);
						if (TransparentActivity.context != null)
							TransparentActivity.context.finish();
						a.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								animating = true;
								android.util.Log.d(TAG, "开屏GONE");
								android.util.Log.d(TAG, "viewWidth="+viewWidth+",gl="+gl);
								rootView.setVisibility(View.GONE);
								if (view instanceof ImageView) {
									Animation a = new ScaleAnimation(1.2f, 1f,
											1.2f, 1f,
											Animation.RELATIVE_TO_SELF, 0.5f,
											Animation.RELATIVE_TO_SELF, 0.5f);
									a.setDuration(300);
									a.setFillAfter(true);
									view.startAnimation(a);
									a.startNow();
								}
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								android.util.Log.d(TAG, "开屏GO");
								FloatWindowBigView.this
										.setVisibility(View.GONE);
								// service.sendBroadcast(new Intent(
								// "com.qihoo.huangmabisheng.finish"));

								rootView.setVisibility(View.VISIBLE);
								int left = rootView.getLeft() - gl;
								int top = rootView.getTop();
								int right = rootView.getRight() - gl;
								int bottom = rootView.getBottom();
								rootView.clearAnimation();
								rootView.layout(left, top, right, bottom);
								animating = false;

							}
						});
						a.startNow();

					} else {
						// TODO 动画回弹
						service.startActivity(service.mainActivityIntent);

						Animation a = new TranslateAnimation(0.0f, 0.0f - gl,
								0.0f, 0.0f);
						long duration = 800 * gl / viewWidth;
						duration = duration < 50 ? 50: duration;
						a.setDuration(duration);
						a.setFillEnabled(true);
						a.setInterpolator(AnimationUtils
								.loadInterpolator(service,
										android.R.anim.decelerate_interpolator));
						rootView.startAnimation(a);
						a.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								android.util.Log.d(TAG, "回弹GONE");
								android.util.Log.d(TAG, "viewWidth="+viewWidth+",gl="+gl);
								animating = true;
								rootView.setVisibility(View.GONE);
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								android.util.Log.d(TAG, "回弹COME");
								rootView.setVisibility(View.VISIBLE);
								int left = rootView.getLeft() - gl;
								int top = rootView.getTop();
								int right = rootView.getRight() - gl;
								int bottom = rootView.getBottom();
								rootView.clearAnimation();
								rootView.layout(left, top, right, bottom);
								animating = false;
								
								if (view instanceof ImageView) {
									
									
									Animation a = new ScaleAnimation(1.2f, 1f,
											1.2f, 1f,
											Animation.RELATIVE_TO_SELF, 0.5f,
											Animation.RELATIVE_TO_SELF, 0.5f);
									a.setDuration(300);
									a.setFillAfter(true);
									view.startAnimation(a);
									a.startNow();
								}
							}
						});
						a.startNow();
					}
					return true;
				}
				return true;
			}
		}
	};

	private void setAllListeners() {
		openL = rootView.getLeft();
		openB = rootView.getBottom();
		openR = rootView.getRight();
		openT = rootView.getTop();

		rootView.setOnTouchListener(iconOnTouchListener);
		imageViewClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				canvasLayout.setVisibility(View.GONE);
				imageViewHand.setImageResource(R.drawable.hand);
			}
		});
		imageViewHand.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (canvasLayout.getVisibility() == View.VISIBLE) {
					canvasLayout.setVisibility(View.GONE);
					imageViewHand.setImageResource(R.drawable.hand);
				} else {
					canvasLayout.setVisibility(View.VISIBLE);
					imageViewHand.setImageResource(R.drawable.hand_open);
				}

			}
		});

		String str = SharedPrefrencesAssist.instance(service).read("hand");
		if (str == null || !str.equals("true")) {
			imageViewHand.setVisibility(View.GONE);
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
		final int w = com.qihoo.huangmabisheng.utils.DensityUtil.dip2px(
				service, 56f);
		ViewFactory viewFactory = new ViewFactory() {

			@Override
			public View makeView() {
				ImageView i = new ImageView(service);
				ImageSwitcher.LayoutParams layout = new ImageSwitcher.LayoutParams(
						w, w, Gravity.CENTER);
				i.setLayoutParams(layout);
				i.setOnTouchListener(iconOnTouchListener);
				return i;
			}
		};
		image0View.setFactory(viewFactory);
		image1View.setFactory(viewFactory);
		image2View.setFactory(viewFactory);
		image3View.setFactory(viewFactory);
		image4View.setFactory(viewFactory);
		image5View.setFactory(viewFactory);
		flypUpAnimation();
	}

	private void flypUpAnimation() {
		image0View.setInAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_in_up));
		image0View.setOutAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_out_up));
		image1View.setInAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_in_up));
		image1View.setOutAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_out_up));
		image2View.setInAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_in_up));
		image2View.setOutAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_out_up));
		image3View.setInAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_in_up));
		image3View.setOutAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_out_up));
		image4View.setInAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_in_up));
		image4View.setOutAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_out_up));
		image5View.setInAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_in_up));
		image5View.setOutAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_out_up));
	}

	private void flypDownAnimation() {
		image0View.setInAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_in_down));
		image0View.setOutAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_out_down));
		image1View.setInAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_in_down));
		image1View.setOutAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_out_down));
		image2View.setInAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_in_down));
		image2View.setOutAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_out_down));
		image3View.setInAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_in_down));
		image3View.setOutAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_out_down));
		image4View.setInAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_in_down));
		image4View.setOutAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_out_down));
		image5View.setInAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_in_down));
		image5View.setOutAnimation(AnimationUtils.loadAnimation(service,
				R.anim.icon_out_down));
	}

	public void dismissCanvas() {
		canvasLayout.setVisibility(View.GONE);
		imageViewHand.setImageResource(R.drawable.hand);
	}

	public void dismissHand(boolean b) {
		if (b)
			imageViewHand.setVisibility(View.VISIBLE);
		else
			imageViewHand.setVisibility(View.GONE);
	}

	public FloatWindowBigView(final Context context) {
		super(context);
		Log.d(TAG, "oncreate");
		service = (FloatWindowService) context;
		findAllViews(context);
		setAllListeners();
	}

	@Override
	public void updatePackageIcon(AppDataForList[] list)
			throws NameNotFoundException {
		int start = 0;
		for (int i = list.length; i > 0; i--) {
			if (list[i - 1] == null) {
				continue;
			} else {
				start = i;
				break;
			}
		}
		for (int i = start - 1; i >= 0; i--) {
			String pck = list[i].packageName;
			ComponentName cpm = list[i].currentCompoment;
			PackageInfo packageInfo = packageManager.getPackageInfo(
					list[i].packageName, 0);
			Drawable drawable = packageInfo.applicationInfo
					.loadIcon(packageManager);
			switch (start - i - 1) {
			case 0:
				image0View.setImageDrawable(drawable);
				image0View.getCurrentView().setEnabled(true);
				image0View.getCurrentView().setTag(pck);
				break;
			case 1:
				image1View.setImageDrawable(drawable);
				image1View.getCurrentView().setEnabled(true);
				image1View.getCurrentView().setTag(pck);
				break;
			case 2:
				image2View.setImageDrawable(drawable);
				image2View.getCurrentView().setEnabled(true);
				image2View.getCurrentView().setTag(pck);
				break;
			case 3:
				image3View.setImageDrawable(drawable);
				image3View.getCurrentView().setEnabled(true);
				image3View.getCurrentView().setTag(pck);
				break;
			case 4:
				image4View.setImageDrawable(drawable);
				image4View.getCurrentView().setEnabled(true);
				image4View.getCurrentView().setTag(pck);
				break;
			case 5:

				image5View.setImageDrawable(drawable);
				image5View.getCurrentView().setEnabled(true);
				image5View.getCurrentView().setTag(pck);
				break;
			}
		}
		for (int i = start; i < list.length; i++) {
			switch (i) {
			case 0:
				image0View.setImageResource(R.drawable.transp);
				image0View.getCurrentView().setEnabled(false);
				break;
			case 1:
				image1View.setImageResource(R.drawable.transp);
				image1View.getCurrentView().setEnabled(false);
				break;
			case 2:
				image2View.setImageResource(R.drawable.transp);
				image2View.getCurrentView().setEnabled(false);
				break;
			case 3:
				image3View.setImageResource(R.drawable.transp);
				image3View.getCurrentView().setEnabled(false);
				break;
			case 4:
				image4View.setImageResource(R.drawable.transp);
				image4View.getCurrentView().setEnabled(false);
				break;
			case 5:
				image5View.setImageResource(R.drawable.transp);
				image5View.getCurrentView().setEnabled(false);
				break;
			}
		}
	}

	@Override
	synchronized public void updateTime(int hour, int minute) {
		android.util.Log.d(TAG,
				rootView.getVisibility() + "," + rootView.getLeft());
		if (flag != TouchType.NONE)
			return;
		// android.util.Log.d(TAG, "update-" + hour + ":" + minute);
		String h = hour / 10 + "" + hour % 10;
		if(!hourTextView.getText().equals(h)){
		hourTextView.setText(h);
		Log.d(TAG, "hourTextView");
		}
		String m = minute / 10 + "" + minute % 10;
		if(!minuteTextView.getText().equals(m)){
		minuteTextView.setText(m);
		Log.d(TAG, "minuteTextView");
		}
		
	}

	@Override
	public void updatePackageGuess(String pck) throws NameNotFoundException {
	}

}