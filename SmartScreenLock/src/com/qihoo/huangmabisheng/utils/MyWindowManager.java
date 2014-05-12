package com.qihoo.huangmabisheng.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.qihoo.huangmabisheng.view.FloatWindowBigView;
import com.qihoo.huangmabisheng.view.FloatWindowSmallView;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class MyWindowManager {
	static String TAG = "MyWindowManager";
	/**
	 * С������View��ʵ��
	 */
	private static FloatWindowSmallView smallWindow;
	private final static int FLAG_APKTOOL_VALUE = 1280;
	/**
	 * ��������View��ʵ��
	 */
	private static FloatWindowBigView bigWindow;

	/**
	 * С������View�Ĳ���
	 */
	private static LayoutParams smallWindowParams;

	/**
	 * ��������View�Ĳ���
	 */
	private static android.view.WindowManager.LayoutParams bigWindowParams;

	/**
	 * ���ڿ�������Ļ�����ӻ��Ƴ�������
	 */
	private static WindowManager mWindowManager;

	/**
	 * ���ڻ�ȡ�ֻ������ڴ�
	 */
	private static ActivityManager mActivityManager;

	/**
	 * ��С����������Ļ���Ƴ���
	 * 
	 * @param context
	 *            ����ΪӦ�ó����Context.
	 */
	public static void removeSmallWindow(Context context) {
		if (smallWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(smallWindow);
			smallWindow = null;
		}
	}

	/**
	 * ����һ������������λ��Ϊ��Ļ���м䡣
	 * 
	 * @param context
	 *            ����ΪӦ�ó����Context.
	 */
	public static void createBigWindow(Context context) {
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
				bigWindowParams.type = LayoutParams.TYPE_SYSTEM_ALERT;
				bigWindowParams.format = PixelFormat.RGBA_8888;
				bigWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
				bigWindowParams.width = FloatWindowBigView.viewWidth;
				bigWindowParams.height = FloatWindowBigView.viewHeight;
				bigWindowParams.flags = FLAG_APKTOOL_VALUE;
				Log.d(TAG, bigWindowParams.x + "," + bigWindowParams.y);
			}
			windowManager.addView(bigWindow, bigWindowParams);
		}
	}

	public static FloatWindowBigView getView() {
		return bigWindow;
	}

	/**
	 * ��������������Ļ���Ƴ���
	 * 
	 * @param context
	 *            ����ΪӦ�ó����Context.
	 */
	public static void removeBigWindow(Context context) {
		if (bigWindow != null) {
			WindowManager windowManager = getWindowManager(context);
			windowManager.removeView(bigWindow);
			bigWindow = null;
		}
	}

	/**
	 * ����С��������TextView�ϵ����ݣ���ʾ�ڴ�ʹ�õİٷֱȡ�
	 * 
	 * @param context
	 *            �ɴ���Ӧ�ó��������ġ�
	 */
	public static void updateUsedPercent(Context context) {
		// if (smallWindow != null) {
		// TextView percentView = (TextView)
		// smallWindow.findViewById(R.id.percent);
		// percentView.setText(getUsedPercentValue(context));
		// }
	}

	/**
	 * �Ƿ���������(����С�������ʹ�������)��ʾ����Ļ�ϡ�
	 * 
	 * @return ����������ʾ�������Ϸ���true��û�еĻ�����false��
	 */
	public static boolean isWindowShowing() {
		return smallWindow != null || bigWindow != null;
	}

	public static int isWindowGone() {
		return bigWindow.getVisibility();
	}

	public static void setWindowVisible() {
		bigWindow.setVisibility(View.VISIBLE);
		bigWindow.dismissCanvas();
	}
	public static void setWindowGone() {
		bigWindow.setVisibility(View.GONE);
	}

	/**
	 * ���WindowManager��δ�������򴴽�һ���µ�WindowManager���ء����򷵻ص�ǰ�Ѵ�����WindowManager��
	 * 
	 * @param context
	 *            ����ΪӦ�ó����Context.
	 * @return WindowManager��ʵ�������ڿ�������Ļ�����ӻ��Ƴ���������
	 */
	public static WindowManager getWindowManager(Context context) {
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
		}
		return mWindowManager;
	}

	/**
	 * ���ActivityManager��δ�������򴴽�һ���µ�ActivityManager���ء����򷵻ص�ǰ�Ѵ�����ActivityManager��
	 * 
	 * @param context
	 *            �ɴ���Ӧ�ó��������ġ�
	 * @return ActivityManager��ʵ�������ڻ�ȡ�ֻ������ڴ档
	 */
	private static ActivityManager getActivityManager(Context context) {
		if (mActivityManager == null) {
			mActivityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
		}
		return mActivityManager;
	}

	/**
	 * ������ʹ���ڴ�İٷֱȣ������ء�
	 * 
	 * @param context
	 *            �ɴ���Ӧ�ó��������ġ�
	 * @return ��ʹ���ڴ�İٷֱȣ����ַ�����ʽ���ء�
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
		return "������";
	}

	/**
	 * ��ȡ��ǰ�����ڴ棬�����������ֽ�Ϊ��λ��
	 * 
	 * @param context
	 *            �ɴ���Ӧ�ó��������ġ�
	 * @return ��ǰ�����ڴ档
	 */
	private static long getAvailableMemory(Context context) {
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		getActivityManager(context).getMemoryInfo(mi);
		return mi.availMem;
	}

}