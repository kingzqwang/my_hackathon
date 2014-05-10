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

import com.qihoo.huangmabisheng.constant.SharedPrefrencesAssist;
import com.qihoo.huangmabisheng.utils.FileUtil;
import com.qihoo.huangmabisheng.utils.MyWindowManager;
import com.qihoo.huangmabisheng.utils.TopApp;
import com.qihoo.huangmabisheng.utils.fb;

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
	
	ActivityManager manager;// = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	List<RunningTaskInfo> runningTasks;// = manager.getRunningTasks(1);
	RunningTaskInfo runningTaskInfo;// = runningTasks.get(0);
	ComponentName topActivity;// = runningTaskInfo.topActivity;
	String packageName,up_package;
	FileUtil fs;// = new FileService(getApplicationContext());
	private Timer timer;
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			Date date = new Date();
			if(null!=MyWindowManager.getView())MyWindowManager.getView().updateTime(date.getHours(), date.getMinutes());
			super.handleMessage(msg);
		}
		
	};
	public void onCreate() {
		super.onCreate();
		manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		app_fre = (Map<String, Integer>) SharedPrefrencesAssist.instance(this)
				.getSharedPreferences().getAll();
		app_fre.remove("hand");
		Log.d(TAG, app_fre.size()+"");
		fs = new FileUtil(getApplicationContext());
		runningTasks = manager.getRunningTasks(1);
		runningTaskInfo = runningTasks.get(0);
		topActivity = runningTaskInfo.topActivity;
		packageName = topActivity.getPackageName();
		up_package = packageName;
		this.registerReceiver(screenOffReceiver, new IntentFilter(
				"android.intent.action.SCREEN_OFF"));
		if (timer == null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new RefreshTask(), 0, 2000);
		}
	}

	class RefreshTask extends TimerTask {
		@Override
		public void run() {
			runningTasks = manager.getRunningTasks(1);
			runningTaskInfo = runningTasks.get(0);
			topActivity = runningTaskInfo.topActivity;
			packageName = topActivity.getPackageName();
			

//			android.util.Log.d(TAG, "update time");
			handler.obtainMessage().sendToTarget();
			
			int count = 0;
//			android.util.Log.d(TAG, packageName+","+up_package);
			if (packageName.equals(up_package))
				return;
			else if (app_fre.containsKey(packageName)) {
				PackageInfo packageInfo = null;
				try {
					packageInfo = getPackageManager().getPackageInfo(
							packageName, 0);
					if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && !packageName.equals("com.qihoo.huangmabisheng")) {
						count = app_fre.get(packageName).intValue() + 1;
						app_fre.put(packageName, count);
						//TODO fs.save("uappstat", packageName + " " + count);
						fs.save(packageName,count);
						android.util.Log.d(packageName,
								Integer.toString(app_fre.get(packageName)));
					}
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				PackageInfo packageInfo = null;
				try {
					packageInfo = getPackageManager().getPackageInfo(
							packageName, 0);
					if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && !packageName.equals("com.qihoo.huangmabisheng")) {
						count = 1;
						app_fre.put(packageName, count);
						//TODO fs.save("uappstat", packageName + " " + count);
						fs.save(packageName,count);
						Log.i(packageName,
								Integer.toString(app_fre.get(packageName)));
					}
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			

			up_package = packageName;
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
			List<Entry<String, Integer>> topApp = new TopApp(app_fre).toApp(filterMap);
			MyWindowManager.getView().updatePackageIcon(topApp,packageName);
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
