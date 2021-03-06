package com.cc.huangmabisheng.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.cc.huangmabisheng.constant.Constant.Screen;
import com.cc.huangmabisheng.service.FloatWindowService;
import com.cc.huangmabisheng.service.SmartLockService;
import com.cc.huangmabisheng.view.FloatWindowBigView;
import com.cc.huangmabisheng.view.FloatWindowSmallView;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

public class MyWindowManager {
	static String TAG = "MyWindowManager";
	/**
	 * 小悬浮窗View的实例
	 */
	private static FloatWindowSmallView smallWindow;
	private final static int FLAG_APKTOOL_VALUE = 1280;
	/**
	 * 大悬浮窗View的实例
	 */
	private static FloatWindowBigView bigWindow;

	/**
	 * 小悬浮窗View的参数
	 */
	private static LayoutParams smallWindowParams;

	/**
	 * 大悬浮窗View的参数
	 */
	private static android.view.WindowManager.LayoutParams bigWindowParams;

	/**
	 * 用于控制在屏幕上添加或移除悬浮窗
	 */
	private static WindowManager mWindowManager;

	/**
	 * 用于获取手机可用内存
	 */
	private static ActivityManager mActivityManager;

	/**
	 * 将小悬浮窗从屏幕上移除。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void removeSmallWindow(Context context) {
		if (smallWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(smallWindow);
			smallWindow = null;
		}
	}

	public static void removeBigView(Context context) {
		WindowManager windowManager = getWindowManager(context);
		windowManager.removeView(bigWindow);
		bigWindow = null;
	}
	
	/**
	 * 创建一个大悬浮窗。位置为屏幕正中间。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void createBigWindow(Context context) {
		Log.d(TAG, "createBigWindow");
		WindowManager windowManager = getWindowManager(context);
		// int screenWidth = windowManager.getDefaultDisplay().getWidth();
		// int screenHeight = windowManager.getDefaultDisplay().getHeight();
		if (bigWindow == null) {
			bigWindow = new FloatWindowBigView(context);
			if (bigWindowParams == null) {
				bigWindowParams = new LayoutParams();
				bigWindowParams.x = LayoutParams.MATCH_PARENT;
				// screenWidth / 2
				// - FloatWindowBigView.viewWidth / 2;
				bigWindowParams.y = LayoutParams.MATCH_PARENT;
				// screenHeight / 2
				// - FloatWindowBigView.viewHeight / 2;
				bigWindowParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
				bigWindowParams.format = PixelFormat.RGBA_8888;
				bigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				bigWindowParams.width = FloatWindowBigView.viewWidth;
				bigWindowParams.height = FloatWindowBigView.viewHeight;
				bigWindowParams.flags = FLAG_APKTOOL_VALUE;
				Log.d(TAG, bigWindowParams.x + "," + bigWindowParams.y);
			}
			windowManager.addView(bigWindow, bigWindowParams);

		}
		bigWindow.setVisibility(View.VISIBLE);
	}

	public static FloatWindowBigView getView() {
		return bigWindow;
	}

	/**
	 * 将大悬浮窗从屏幕上移除。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 */
	public static void removeBigWindow(Context context) {
		if (bigWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(bigWindow);
			bigWindow = null;
		}
	}

	/**
	 * 更新小悬浮窗的TextView上的数据，显示内存使用的百分比。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 */
	public static void updateUsedPercent(Context context) {
		// if (smallWindow != null) {
		// TextView percentView = (TextView)
		// smallWindow.findViewById(R.id.percent);
		// percentView.setText(getUsedPercentValue(context));
		// }
	}

	/**
	 * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
	 * 
	 * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
	 */
	public static boolean isWindowShowing() {
		return bigWindow != null;
	}

	public static int getWindowVisibility() {
		return bigWindow.getVisibility();
	}

	public static boolean isWindowLocked() {
		if (!isWindowShowing()) {
			Log.e(TAG, "bigWindow == null but isWindowLocked");
			return true;
		}
		return getWindowVisibility() != View.GONE;
	}

	public static void setWindowVisible() {
		Log.d(TAG, "setWindowVisible");
		bigWindow.rootView.setVisibility(View.VISIBLE);
		bigWindow.setVisibility(View.VISIBLE);
//		bigWindow.dismissCanvas();
		bigWindow.invalidate();
	}

	public static void setWindowGone() {
		bigWindow.setVisibility(View.GONE);
	}

	/**
	 * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
	 * 
	 * @param context
	 *            必须为应用程序的Context.
	 * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
	 */
	public static WindowManager getWindowManager(Context context) {
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
		}
		return mWindowManager;
	}

	/**
	 * 如果ActivityManager还未创建，则创建一个新的ActivityManager返回。否则返回当前已创建的ActivityManager。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return ActivityManager的实例，用于获取手机可用内存。
	 */
	public static ActivityManager getActivityManager(Context context) {
		if (mActivityManager == null) {
			mActivityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
		}
		return mActivityManager;
	}

	/**
	 * 计算已使用内存的百分比，并返回。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return 已使用内存的百分比，以字符串形式返回。
	 */
	public static String getUsedPercentValue(Context context) {
		String dir = "/proc/meminfo";
		try {
			FileReader fr = new FileReader(dir);
			BufferedReader br = new BufferedReader(fr, 2048);
			String memoryLine = br.readLine();
			String subMemoryLine = memoryLine.substring(memoryLine
					.indexOf("MemTotal:"));
			br.close();
			long totalMemorySize = Integer.parseInt(subMemoryLine.replaceAll(
					"\\D+", ""));
			long availableSize = getAvailableMemory(context) / 1024;
			int percent = (int) ((totalMemorySize - availableSize)
					/ (float) totalMemorySize * 100);
			return percent + "%";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "悬浮窗";
	}

	/**
	 * 获取当前可用内存，返回数据以字节为单位。
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return 当前可用内存。
	 */
	private static long getAvailableMemory(Context context) {
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		getActivityManager(context).getMemoryInfo(mi);
		return mi.availMem;
	}

}