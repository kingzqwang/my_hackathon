package com.qihoo.huangmabisheng.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import com.qihoo.huangmabisheng.constant.Constant;
import com.qihoo.huangmabisheng.constant.SharedPrefrencesAssist;
import com.qihoo.huangmabisheng.utils.FileUtil;
import com.qihoo.huangmabisheng.utils.MyWindowManager;
import com.qihoo.huangmabisheng.utils.TopApp;
import com.qihoo.huangmabisheng.utils.fb;
import com.qihoo.huangmabisheng.view.FloatWindowBigView;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class SmartLockService extends Service {
	public static Map<String, Integer> filterMap = new HashMap<String, Integer>();
	
	private static String TAG = "SmartLockService";
	Map<String, Integer> app_fre = new HashMap<String, Integer>();

	ActivityManager manager;// = (ActivityManager)
							// getSystemService(ACTIVITY_SERVICE);

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	List<RunningTaskInfo> runningTasks;// = manager.getRunningTasks(1);
	RunningTaskInfo runningTaskInfo;// = runningTasks.get(0);
	ComponentName topActivity;// = runningTaskInfo.topActivity;
	String currentPackageName, lastPackageName;
	FileUtil fs;// = new FileService(getApplicationContext());
	private Timer timer;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Date date = new Date();
			if (null != MyWindowManager.getView())
				MyWindowManager.getView().updateTime(date.getHours(),
						date.getMinutes());
			super.handleMessage(msg);
		}

	};

	public void onCreate() {
		super.onCreate();
		manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		app_fre = (Map<String, Integer>) SharedPrefrencesAssist.instance(this)
				.getSharedPreferences().getAll();
		app_fre.remove("hand");
		Log.d(TAG, app_fre.size() + "");
		fs = new FileUtil(getApplicationContext());
		updateCurrentPackageInfo();
		// TODO 记录当前
		PackageInfo packageInfo = null;
		try {
			packageInfo = getPackageManager().getPackageInfo(
					currentPackageName, 0);
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
					&& !currentPackageName.equals("com.qihoo.huangmabisheng")) {
				pushInAppFre(currentPackageName,Constant.UNSAVE);
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lastPackageName = currentPackageName;// 记录上次的Top，当前赋值
		this.registerReceiver(screenOffReceiver, new IntentFilter(
				"android.intent.action.SCREEN_OFF"));
		if (timer == null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new RefreshTask(), 0, 2000);
		}
	}
	private void pushInAppFre(String currentPackageName,boolean save) {
		int count = 1;
		if (app_fre.containsKey(currentPackageName)) {
			count = app_fre.get(currentPackageName).intValue() + 1;
		}
		app_fre.put(currentPackageName, count);
		if(save)fs.save(currentPackageName, count);//耗时操作
	}
	private void updateCurrentPackageInfo() {
		runningTasks = manager.getRunningTasks(1);// Return a list of the tasks
													// that are currently
													// running, 1 max
		runningTaskInfo = runningTasks.get(0);
		topActivity = runningTaskInfo.topActivity;// The activity component at
													// the top of the history
													// stack of the task. This
													// is what the user is
													// currently doing.
		currentPackageName = topActivity.getPackageName();// 每次循环都取top activity
	}

	/**
	 * @return 返回若为true则表明该应用是可以统计的，反之则该应用是在统计范围外的。
	 * @throws NameNotFoundException
	 */
	private boolean filterApplications(String packageName)
			throws NameNotFoundException {
		PackageInfo packageInfo = null;
		packageInfo = getPackageManager().getPackageInfo(packageName, 0);
		return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
				&& !packageName.equals("com.qihoo.huangmabisheng");
	}

	class RefreshTask extends TimerTask {
		@Override
		public void run() {
			updateCurrentPackageInfo();
			// android.util.Log.d(TAG, "update time");
			handler.obtainMessage().sendToTarget();// 更新时间

			// TODO 先判断是不是可统计的app，即非系统appXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX======================>>>>>TODO
			if (currentPackageName.equals(lastPackageName))
				return;
			try {
				if (!filterApplications(currentPackageName)) {
					return;
				}
				pushInAppFre(currentPackageName,Constant.SAVE);
			} catch (NameNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			lastPackageName = currentPackageName;//更新last top
		}
	}

	public int onStartCommand(Intent intent, int flags, int startId) {

		return Service.START_STICKY;

	}

	public void onDestroy() {
		Log.d(TAG, "Service destroy");
		super.onDestroy();
		this.unregisterReceiver(screenOffReceiver);
		startService(new Intent(SmartLockService.this, SmartLockService.class));
	}

	private void updatePcksIcon() {

		try {
			if (app_fre == null || filterMap == null) {
				Log.d(TAG, app_fre + "," + filterMap);
			}
			List<Entry<String, Integer>> topApp = new TopApp(app_fre)
					.toApp(filterMap);
			FloatWindowBigView view = MyWindowManager.getView();
			if (null != view)
				view.updatePackageIcon(topApp, currentPackageName);
			else {
				Log.d(TAG, "view null");
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals("android.intent.action.SCREEN_OFF")) {
				Log.i(TAG,
						"-----------OFF------ android.intent.action.SCREEN_OFF------");
				updatePcksIcon();
			}
			fb.d(context);
		}

	};
}
