package com.cc.huangmabisheng.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cc.huangmabisheng.activity.TransparentActivity;
import com.cc.huangmabisheng.constant.Constant;
import com.cc.huangmabisheng.constant.SharedPrefrencesAssist;
import com.cc.huangmabisheng.constant.Constant.Screen;
import com.cc.huangmabisheng.utils.MyWindowManager;
import com.cc.huangmabisheng.utils.ProcessUtil;
import com.cc.huangmabisheng.utils.fb;
import com.cc.huangmabisheng.view.FloatWindowBigView;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
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
	public Intent mainActivityIntent = null;

	/**
	 * 定时器，定时进行检测当前应该创建还是移除悬浮窗。
	 */
	// private Timer timer;

	public void publish(int what) {
		handler.obtainMessage(what).sendToTarget();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");

		// if (!MyWindowManager.isWindowShowing()) {
		MyWindowManager.createBigWindow(FloatWindowService.this);
		MyWindowManager.setWindowGone();
		// } else if (View.GONE == MyWindowManager.getWindowVisibility()) {
		// // MyWindowManager.setWindowVisible();
		// }
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		this.unregisterReceiver(screenOffReceiver);
		this.unregisterReceiver(screenOnReceiver);
		MyWindowManager.setWindowGone();
		fb.u(this);
		super.onDestroy();
	}

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

	private BroadcastReceiver screenOnReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// String action = intent.getAction();
			// if (action.equals("android.intent.action.SCREEN_ON")) {
			synchronized (FloatWindowService.class) {
				SmartLockService.screen = Screen.ON;
				FloatWindowService.class.notify();
			}
			Date date = new Date();
			if (null != MyWindowManager.getView()) {
				MyWindowManager.getView().updateTime(date.getHours(),
						date.getMinutes(), date.getMonth(), date.getDate(),
						date.getDay());
			}
			// startActivity(mainActivityIntent);

			Log.e(TAG,
					"-----------ON------ android.intent.action.SCREEN_ON------");
			fb.d(FloatWindowService.this);
		}

	};

	private BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// String action = intent.getAction();
			// if (action.equals("android.intent.action.SCREEN_OFF")) {
			SmartLockService.screen = Screen.OFF;
			MyWindowManager.setWindowVisible();// 放在前面比较快
			startActivity(mainActivityIntent);
			// }

			Log.e(TAG,
					"-----------OFF------ android.intent.action.SCREEN_OFF------");
			fb.d(FloatWindowService.this);
		}
	};

	private Set<String> getHomes() {
		Set<String> names = new HashSet<String>();
		PackageManager packageManager = this.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
				intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo ri : resolveInfo) {
			names.add(ri.activityInfo.packageName);
		}
		return names;
	}

}