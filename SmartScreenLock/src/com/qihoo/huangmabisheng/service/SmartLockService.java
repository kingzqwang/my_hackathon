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
import com.qihoo.huangmabisheng.model.AppDataForList;
import com.qihoo.huangmabisheng.model.AppIntroMap;
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
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class SmartLockService extends Service {
	public static Map<String, Integer> filterMap = new HashMap<String, Integer>();// 二级过滤

	private static String TAG = "SmartLockService";
	AppIntroMap app_fre = new AppIntroMap();

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
		Log.d(TAG, "onCreate");
		super.onCreate();
		manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// app_fre.putAll((AppIntroMap) SharedPrefrencesAssist.instance(this)
		// .getSharedPreferences().getAll());
		// app_fre.remove("hand");
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
				pushInAppFre(topActivity, currentPackageName, Constant.UNSAVE);
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

	/**
	 * 将指定的app包名统计入库
	 * 
	 * @param topActivity
	 *            栈顶组件
	 * @param currentPackageName
	 *            应用包名
	 * @param save
	 *            是否本地化
	 */
	private void pushInAppFre(ComponentName topActivity,
			String currentPackageName, boolean save) {
		AppDataForList appData;
		if (app_fre.containsKey(currentPackageName)) {
			appData = app_fre.get(currentPackageName);
			appData.push();
		} else {
			appData = new AppDataForList(currentPackageName, topActivity);
			app_fre.put(currentPackageName, appData);
		}
		app_fre.updateData(currentPackageName);
		fs.save(currentPackageName, appData);// 耗时操作
	}

	/**
	 * 取当前top activity的包名
	 **/
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
	 * 判断当前界面是否是桌面
	 */
	private boolean isHome(String packageName) {
		ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		return getHomes().contains(packageName);
	}

	/**
	 * 获得属于桌面的应用的应用包名称
	 * 
	 * @return 返回包含所有包名的字符串列表
	 */
	private List<String> getHomes() {
		List<String> names = new ArrayList<String>();
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

	/**
	 * 一级过滤，判断是否为系统app
	 * 
	 * @return 返回若为true则表明该应用是可以统计的，反之则该应用是永远在统计范围外的。
	 * @throws NameNotFoundException
	 */
	private boolean filterApplications(String packageName)
			throws NameNotFoundException {
		// PackageInfo packageInfo = null;
		// packageInfo = getPackageManager().getPackageInfo(packageName, 0);
		// return (packageInfo.applicationInfo.flags &
		// ApplicationInfo.FLAG_SYSTEM) == 0
		// && !packageName.equals("com.qihoo.huangmabisheng");
		if (!isHome(packageName)
				&& !packageName.equals("com.qihoo.huangmabisheng")&& !packageName.equals("com.android.packageinstaller")&& !packageName.equals("android"))
			return true;
		else {
			app_fre.remove(packageName);
			return false;
		}
	}

	/**
	 * 该类是该服务的循环主体，用于不断读取top activity更新app_fre，同时也更新时间
	 */
	class RefreshTask extends TimerTask {
		@Override
		public void run() {
			updateCurrentPackageInfo();
			// android.util.Log.d(TAG, "update time");
			handler.obtainMessage().sendToTarget();// 更新时间

			// TODO
			// 先判断是不是可统计的app，即非系统app
			if (currentPackageName.equals(lastPackageName))
				return;
			try {
				if (!filterApplications(currentPackageName)) {
					return;
				}
				pushInAppFre(topActivity, currentPackageName, Constant.SAVE);
				Log.d(TAG, topActivity + "," + currentPackageName);

			} catch (NameNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				lastPackageName = currentPackageName;// 更新last top
			}
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

	/**
	 * 更新锁屏上的icon，更新前进行二级过滤 ，此操作在关屏时启动
	 */
	private void updatePcksIcon() {
		try {
			if (app_fre == null || filterMap == null) {
				Log.d(TAG, app_fre + "," + filterMap);
			}
			// List<Entry<String, Integer>> topApp = new TopApp(app_fre)
			// .toApp(filterMap);
			FloatWindowBigView view = MyWindowManager.getView();
			if (null != view)
				view.updatePackageIcon(app_fre.appDatas, lastPackageName);// 包括两部分，一部分是更新最常使用icon，一部分是更新最近使用????????????????????????????????????????????????????????????
			else {
				Log.d(TAG, "view null");
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
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
