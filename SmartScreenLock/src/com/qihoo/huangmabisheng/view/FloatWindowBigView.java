package com.qihoo.huangmabisheng.view;

import java.security.acl.Group;
import java.util.List;
import java.util.Map.Entry;

import android.R.integer;
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
import android.text.Html.ImageGetter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qihoo.huangmabisheng.R;
import com.qihoo.huangmabisheng.activity.SettingActivity;
import com.qihoo.huangmabisheng.constant.SharedPrefrencesAssist;
import com.qihoo.huangmabisheng.interfaces.IUpdatePackageIcon;
import com.qihoo.huangmabisheng.interfaces.IUpdateTime;
import com.qihoo.huangmabisheng.model.AppDataForList;
import com.qihoo.huangmabisheng.service.FloatWindowService;
import com.qihoo.huangmabisheng.utils.Log;
import com.qihoo.huangmabisheng.utils.MyWindowManager;
import com.qihoo.huangmabisheng.utils.fb;

public class FloatWindowBigView extends LinearLayout implements
		IUpdatePackageIcon, IUpdateTime {

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
	ViewGroup rootView;
	ViewGroup openLayout;
	// ImageView testImageView;
	ImageView image0View;
	ImageView image1View;
	ImageView image2View;
	ImageView image3View;
	ImageView image4View;
	ImageView image5View;
	ImageView image6View;
	ImageView image7View;
	ImageView imageViewGuess;
	ImageView imageViewCanvas;
	ImageView imageViewClose;
	ImageView imageViewHand;
	ImageView imageViewAdd;
	ViewGroup layoutGuess;
	TextView hourTextView;
	TextView minuteTextView;
	ViewGroup canvasLayout;
	int openL, openR, openT, openB;
	int icon1T, icon2T, icon1B, icon2B;

	Canvas canvas;
	private Bitmap baseBitmap;
	private Paint paint;

	private void findAllViews(Context context) {
		packageManager = context.getPackageManager();
		LayoutInflater.from(context).inflate(R.layout.floating_view, this);
		rootView = (ViewGroup) findViewById(R.id.floating_layout);

		viewWidth = MyWindowManager.getWindowManager(context)
				.getDefaultDisplay().getWidth();
		viewHeight = MyWindowManager.getWindowManager(context)
				.getDefaultDisplay().getHeight();
		Log.d(TAG, viewHeight + "");
		image0View = (ImageView) rootView.findViewById(R.id.imageview_0);
		image1View = (ImageView) rootView.findViewById(R.id.imageview_1);
		image2View = (ImageView) rootView.findViewById(R.id.imageview_2);
		image3View = (ImageView) rootView.findViewById(R.id.imageview_3);
		icon1T = image0View.getTop();
		icon1B = image0View.getBottom();
		image4View = (ImageView) rootView.findViewById(R.id.imageview_4);
		image5View = (ImageView) rootView.findViewById(R.id.imageview_5);
		image6View = (ImageView) rootView.findViewById(R.id.imageview_6);
		image7View = (ImageView) rootView.findViewById(R.id.imageview_7);
		icon2T = image4View.getTop();
		openLayout = (ViewGroup) rootView.findViewById(R.id.open_layout);
		imageViewGuess = (ImageView) rootView
				.findViewById(R.id.imageview_guess);
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
	private OnClickListener iconOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
	};
	/**
	 * 画图板touch
	 **/
	private OnTouchListener iconOnTouchListener = new OnTouchListener() {
		int[] temp = new int[] { 0, 0 };
		int detY = 0, ty = 0;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int x = (int) event.getRawX();
			int y = (int) event.getRawY();
			int p = (int) event.getX();
			int q = (int) event.getY();
			Log.i(TAG, "OnTouchListener" + " X is " + x + " Y is " + y);
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				temp[0] = (int) event.getX();
				temp[1] = y - v.getTop();
				ty = y;
				return false;
			case MotionEvent.ACTION_MOVE:
				int l = v.getLeft();
				// int l = x - temp[0];
				int t = y - temp[1] > 0 ? y - temp[1] : v.getTop();
				int r = v.getRight();
				// int r = x + v.getWidth() - temp[0];
				int b = y - temp[1] > 0 ? y + v.getHeight() - temp[1] : v
						.getBottom();
				v.layout(l, t, r, b);
				v.invalidate();
				return false;
			case MotionEvent.ACTION_UP:
				if (y < temp[1]) {
					return false;
				}
				android.util.Log.d(TAG, "ACTION_UP");
				handleActionUp(v, event, ty - y);
				return false;
			}
			return false;
		}

	};

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

	private void setAllListeners() {
		openL = rootView.getLeft();
		openB = rootView.getBottom();
		openR = rootView.getRight();
		openT = rootView.getTop();
		image0View.setOnTouchListener(iconOnTouchListener);
		image0View.setOnClickListener(iconOnClickListener);
		image1View.setOnTouchListener(iconOnTouchListener);
		image1View.setOnClickListener(iconOnClickListener);
		image2View.setOnTouchListener(iconOnTouchListener);
		image2View.setOnClickListener(iconOnClickListener);
		image3View.setOnTouchListener(iconOnTouchListener);
		image3View.setOnClickListener(iconOnClickListener);
		image4View.setOnTouchListener(iconOnTouchListener);
		image4View.setOnClickListener(iconOnClickListener);
		image5View.setOnTouchListener(iconOnTouchListener);
		image5View.setOnClickListener(iconOnClickListener);
		image6View.setOnTouchListener(iconOnTouchListener);
		image6View.setOnClickListener(iconOnClickListener);
		image7View.setOnTouchListener(iconOnTouchListener);
		image7View.setOnClickListener(iconOnClickListener);
		imageViewGuess.setOnTouchListener(iconOnTouchListener);
		imageViewGuess.setOnClickListener(iconOnClickListener);
		openLayout.setOnTouchListener(new OnTouchListener() {
			int[] temp = new int[] { 0, 0 };

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int x = (int) event.getRawX();
				int y = (int) event.getRawY();
				int p = (int) event.getX();
				int q = (int) event.getY();
				Log.i(TAG, "OnTouchListener" + " X is " + x + " Y is " + y);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					temp[0] = (int) event.getX();
					temp[1] = y - v.getTop();
					return false;
				case MotionEvent.ACTION_MOVE:
					// int l = v.getLeft();
					int l = x - temp[0] > 0 ? x - temp[0] : v.getLeft();
					// int t = y - temp[1];
					int t = rootView.getTop();
					// int r = v.getRight();
					int r = x - temp[0] > 0 ? x + v.getWidth() - temp[0] : v
							.getRight();
					// int b = y + v.getHeight() - temp[1];
					int b = rootView.getBottom();
					rootView.layout(l, t, r, b);
					rootView.invalidate();
					return false;
				case MotionEvent.ACTION_UP:
					final int gl = rootView.getLeft();
					final int gr = rootView.getRight();
					android.util.Log.d(TAG + " Action_Up", gl + "");
					if (rootView.getLeft() > viewWidth / 2) {
						FloatWindowBigView.this.setVisibility(View.GONE);
						service.sendBroadcast(new Intent(
								"com.qihoo.huangmabisheng.finish"));
					} else {
						// TODO 动画回弹
						Animation a = new TranslateAnimation(0.0f, 0.0f - gl,
								0.0f, 0.0f);
						a.setDuration(1000);
						a.setFillEnabled(true);
						a.setInterpolator(AnimationUtils.loadInterpolator(
								service, android.R.anim.bounce_interpolator));
						rootView.startAnimation(a);
						a.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								rootView.setVisibility(View.GONE);
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								rootView.setVisibility(View.VISIBLE);
								int left = rootView.getLeft() - gl;
								int top = rootView.getTop();
								int right = rootView.getRight() - gl;
								int bottom = rootView.getBottom();
								rootView.clearAnimation();

								rootView.layout(left, top, right, bottom);

							}
						});
						a.startNow();
						// rootView.invalidate();
					}
					return false;
				}
				return false;
			}
		});
		openLayout.setOnClickListener(iconOnClickListener);
		imageViewAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
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
	}
	public void dismissCanvas() {
		canvasLayout.setVisibility(View.GONE);
		imageViewHand.setImageResource(R.drawable.hand);
	}
	public void dismissHand(boolean b) {
		if(b)
		imageViewHand.setVisibility(View.VISIBLE);
		else
			imageViewHand.setVisibility(View.GONE);
	}
	private void handleActionUp(final View v, MotionEvent event, final int detY) {
		android.util.Log.d(TAG, detY+"");
		if (detY == 0) {
			Animation a = new TranslateAnimation(0.0f, 0.0f, 60f, 0.0f);
			a.setDuration(500);
			 a.setFillEnabled(true);
			a.setInterpolator(AnimationUtils.loadInterpolator(service,
					android.R.anim.bounce_interpolator));
			v.setAnimation(a);
			a.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					v.setVisibility(View.GONE);
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					v.setVisibility(View.VISIBLE);
					v.layout(v.getLeft(), v.getTop() + 60, v.getRight(),
							v.getBottom() + 60);
					android.util.Log.d(TAG, "animation end");
				}
			});
			a.start();
			v.invalidate();
			
			
		}else if (event.getRawY() >= openLayout.getTop()) {
			android.util.Log.d(TAG, "event.getRawY() >= openLayout.getTop()");
			this.setVisibility(View.GONE);
			fb.d(service);
//			Intent intent = packageManager.getLaunchIntentForPackage((String) v
//					.getTag());
			Intent intent = new Intent();
			intent.setComponent((ComponentName)v.getTag());
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			service.startActivity(intent);
			service.sendBroadcast(new Intent("com.qihoo.huangmabisheng.finish"));

		} else {
			android.util.Log.d(TAG, "do animation");
			Animation a = new TranslateAnimation(0.0f, 0.0f, 0.0f, detY);
			a.setDuration(800);
			 a.setFillEnabled(true);
			a.setInterpolator(AnimationUtils.loadInterpolator(service,
					android.R.anim.bounce_interpolator));
			v.setAnimation(a);
			a.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					v.setVisibility(View.GONE);
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					v.setVisibility(View.VISIBLE);
					v.layout(v.getLeft(), v.getTop() + detY, v.getRight(),
							v.getBottom() + detY);
					android.util.Log.d(TAG, "animation end");
				}
			});
			a.start();
			v.invalidate();
		}
	}

	public FloatWindowBigView(final Context context) {
		super(context);
		service = (FloatWindowService) context;
		findAllViews(context);
		setAllListeners();
	}

	@Override
	public void updatePackageIcon(AppDataForList[] list,
			String lastPackage) throws NameNotFoundException {
		int i=0;
		for (;i<list.length;i++) {
			if (list[i]==null) {
				break;
			}
			String pck = list[i].packageName;
			ComponentName cpm = list[i].currentCompoment;
			PackageInfo packageInfo = packageManager.getPackageInfo(list[i].packageName, 0);
			Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
			switch (i) {
			case 0:
				image0View.setImageDrawable(drawable);
				image0View.setTag(cpm);
				break;
			case 1:
				image1View.setImageDrawable(drawable);
				image1View.setTag(cpm);
				break;
			case 2:
				image2View.setImageDrawable(drawable);
				image2View.setTag(cpm);
				break;
			case 3:
				image3View.setImageDrawable(drawable);
				image3View.setTag(cpm);
				break;
			case 4:
				image4View.setImageDrawable(drawable);
				image4View.setTag(cpm);
				break;
			case 5:
				image5View.setImageDrawable(drawable);
				image5View.setTag(cpm);
				break;
			case 6:
				image6View.setImageDrawable(drawable);
				image6View.setTag(cpm);
				break;
			default:
				break;
			}
		}
		
		/*
		int size = 7;
		if (list.size() < size) {
			size = list.size();
		}
		for (int i = 0; i < size;) {
			String pck = null;
			if (list.get(i) == null || (pck = list.get(i).getKey()) == null) {
				switch (i) {
				case 0:
					image0View.setImageDrawable(null);
					image0View.setTag(pck);
					break;
				case 1:
					image1View.setImageDrawable(null);
					image1View.setTag(pck);
					break;
				case 2:
					image2View.setImageDrawable(null);
					image2View.setTag(pck);
					break;
				case 3:
					image4View.setImageDrawable(null);
					image4View.setTag(pck);
					break;
				case 4:
					image5View.setImageDrawable(null);
					image5View.setTag(pck);
					break;
				case 5:
					image6View.setImageDrawable(null);
					image6View.setTag(pck);
					break;
				case 6:
					image7View.setImageDrawable(null);
					image7View.setTag(pck);
					break;
				default:
					break;
				}
				i++;
				continue;
			}
			PackageInfo packageInfo = packageManager.getPackageInfo(pck, 0);
			if (pck.equals(lastPackage)) {
				list.remove(i);
				size--;
				continue;
				// android.util.Log.d(TAG, i+","+pck+","+lastPackage);
			}
			switch (i) {

			case 0:
				image0View.setImageDrawable(packageInfo.applicationInfo
						.loadIcon(packageManager));
				image0View.setTag(pck);
				break;
			case 1:
				image1View.setImageDrawable(packageInfo.applicationInfo
						.loadIcon(packageManager));
				image1View.setTag(pck);
				break;
			case 2:
				image2View.setImageDrawable(packageInfo.applicationInfo
						.loadIcon(packageManager));
				image2View.setTag(pck);
				break;
			case 3:
				image4View.setImageDrawable(packageInfo.applicationInfo
						.loadIcon(packageManager));
				image4View.setTag(pck);
				break;
			case 4:
				image5View.setImageDrawable(packageInfo.applicationInfo
						.loadIcon(packageManager));
				image5View.setTag(pck);
				break;
			case 5:
				image6View.setImageDrawable(packageInfo.applicationInfo
						.loadIcon(packageManager));
				image6View.setTag(pck);
				break;
			case 6:
				image7View.setImageDrawable(packageInfo.applicationInfo
						.loadIcon(packageManager));
				image7View.setTag(pck);
				break;
			default:
				break;
			}
			i++;
		}
		PackageInfo lastPackageInfo = packageManager.getPackageInfo(
				lastPackage, 0);
		if (lastPackage == null || lastPackage.equals("")) {
			image3View.setImageDrawable(null);
			image3View.setTag(lastPackage);
		} else {
			image3View.setImageDrawable(lastPackageInfo.applicationInfo
					.loadIcon(packageManager));
			image3View.setTag(lastPackage);
		}
		*/
	}

	@Override
	public void updateTime(int hour, int minute) {
//		android.util.Log.d(TAG, "update-" + hour + ":" + minute);
		hourTextView.setText(hour / 10 + "" + hour % 10);
		minuteTextView.setText(minute / 10 + "" + minute % 10);
		layoutGuess.setVisibility(View.VISIBLE);
		imageViewGuess.setVisibility(View.VISIBLE);
		if (hour <= 21) {
			if (hour < 20) {
				layoutGuess.setVisibility(View.INVISIBLE);
				imageViewGuess.setVisibility(View.GONE);
				imageViewGuess.setTag(null);
				return;
			}
			if (imageViewGuess.getTag() != null
					&& imageViewGuess.getTag().equals("com.tencent.mm"))
				return;
			PackageInfo lastPackageInfo;
			try {
				lastPackageInfo = packageManager.getPackageInfo(
						"com.tencent.mm", 0);
				imageViewGuess.setImageDrawable(lastPackageInfo.applicationInfo
						.loadIcon(packageManager));
				imageViewGuess.setTag("com.tencent.mm");
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (hour <= 22) {
			if (imageViewGuess.getTag() != null
					&& imageViewGuess.getTag().equals("com.sdu.didi.psnger"))
				return;
			PackageInfo lastPackageInfo;
			try {
				lastPackageInfo = packageManager.getPackageInfo(
						"com.sdu.didi.psnger", 0);
				imageViewGuess.setImageDrawable(lastPackageInfo.applicationInfo
						.loadIcon(packageManager));
				imageViewGuess.setTag("com.sdu.didi.psnger");
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (hour <= 23) {
			if (imageViewGuess.getTag() != null
					&& imageViewGuess.getTag().equals("com.immomo.momo"))
				return;
			PackageInfo lastPackageInfo;
			try {
				lastPackageInfo = packageManager.getPackageInfo(
						"com.immomo.momo", 0);
				imageViewGuess.setImageDrawable(lastPackageInfo.applicationInfo
						.loadIcon(packageManager));
				imageViewGuess.setTag("com.immomo.momo");
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void updatePackageGuess(String pck) throws NameNotFoundException {
		PackageInfo lastPackageInfo = packageManager.getPackageInfo(pck, 0);
		imageViewGuess.setImageDrawable(lastPackageInfo.applicationInfo
				.loadIcon(packageManager));
		imageViewGuess.setTag(pck);
	}

}