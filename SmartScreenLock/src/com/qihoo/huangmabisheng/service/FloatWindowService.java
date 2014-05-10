package com.qihoo.huangmabisheng.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import com.qihoo.huangmabisheng.activity.TransparentActivity;
import com.qihoo.huangmabisheng.utils.MyWindowManager;
import com.qihoo.huangmabisheng.utils.fb;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class FloatWindowService extends Service {
	String TAG = "FloatWindowService";
	
	
	/**
	 * 用于在线程中创建或移除悬浮窗
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}

	};
	private Intent mainActivityIntent = null;
	/**
	 * 定时器，定时进行检测当前应该创建还是移除悬浮窗。
	 */
	private Timer timer;

	public void publish(int what) {
		handler.obtainMessage(what).sendToTarget();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 开启定时器，每隔0.5秒刷新一次
		Log.d(TAG, "onStartCommand");
		// if (timer == null) {
		// timer = new Timer();
		// // timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);
		// }
		
		if (!MyWindowManager.isWindowShowing()) {
			MyWindowManager.createBigWindow(FloatWindowService.this);
			MyWindowManager.setWindowGone(); 
		} else if (View.GONE == MyWindowManager.isWindowGone()) {
			MyWindowManager.setWindowVisible();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		// Service被终止的同时也停止定时器继续运行
		if(timer!=null)
		timer.cancel();
		timer = null;
		this.unregisterReceiver(screenOffReceiver);
		this.unregisterReceiver(screenOnReceiver);

		startService(new Intent(FloatWindowService.this, FloatWindowService.class));
	}

	// public void cancelTask() {
	// timer.cancel();
	// }
	// public void startTask() {
	// timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);
	// }
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
		mainActivityIntent = new Intent(FloatWindowService.this,
				TransparentActivity.class);
		mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		this.registerReceiver(screenOffReceiver, new IntentFilter(
				"android.intent.action.SCREEN_OFF"));
		this.registerReceiver(screenOnReceiver, new IntentFilter(
				"android.intent.action.SCREEN_ON"));
		
	}

	// class RefreshTask extends TimerTask {
	//
	// @Override
	// public void run() {
	// // 当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗。
	// Log.d(TAG, "RefreshTask run");
	// if (/*isHome()&& */!MyWindowManager.isWindowShowing()) {
	// handler.post(new Runnable() {
	// @Override
	// public void run() {
	// Log.d(TAG, "Runnable run");
	// MyWindowManager.createBigWindow(FloatWindowService.this);
	// }
	// });
	// }
	// // // 当前界面不是桌面，且有悬浮窗显示，则移除悬浮窗。
	// // else if (!isHome() && MyWindowManager.isWindowShowing()) {
	// // handler.post(new Runnable() {
	// // @Override
	// // public void run() {
	// // MyWindowManager.removeSmallWindow(getApplicationContext());
	// // MyWindowManager.removeBigWindow(getApplicationContext());
	// // }
	// // });
	// // }
	// // // 当前界面是桌面，且有悬浮窗显示，则更新内存数据。
	// // else if (isHome() && MyWindowManager.isWindowShowing()) {
	// // handler.post(new Runnable() {
	// // @Override
	// // public void run() {
	// // MyWindowManager.updateUsedPercent(getApplicationContext());
	// // }
	// // });
	// // }
	// }
	//
	// }

	// /**
	// * 判断当前界面是否是桌面
	// */
	// private boolean isHome() {
	// ActivityManager mActivityManager = (ActivityManager)
	// getSystemService(Context.ACTIVITY_SERVICE);
	// List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
	// return getHomes().contains(rti.get(0).topActivity.getPackageName());
	// }
	//
	// /**
	// * 获得属于桌面的应用的应用包名称
	// *
	// * @return 返回包含所有包名的字符串列表
	// */
	// private List<String> getHomes() {
	// List<String> names = new ArrayList<String>();
	// PackageManager packageManager = this.getPackageManager();
	// Intent intent = new Intent(Intent.ACTION_MAIN);
	// intent.addCategory(Intent.CATEGORY_HOME);
	// List<ResolveInfo> resolveInfo =
	// packageManager.queryIntentActivities(intent,
	// PackageManager.MATCH_DEFAULT_ONLY);
	// for (ResolveInfo ri : resolveInfo) {
	// names.add(ri.activityInfo.packageName);
	// }
	// return names;
	// }

	private BroadcastReceiver screenOnReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals("android.intent.action.SCREEN_ON")) {
				Log.i(TAG,
						"-----------ON------ android.intent.action.SCREEN_ON------");
				startActivity(mainActivityIntent);
			}
		}

	};
	private BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals("android.intent.action.SCREEN_OFF")) {
				Log.i(TAG,
						"-----------OFF------ android.intent.action.SCREEN_OFF------");
				MyWindowManager.setWindowVisible();// 放在前面比较快
				startActivity(mainActivityIntent);
			}
		}

	};
}