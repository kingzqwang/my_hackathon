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

import com.qihoo.huangmabisheng.R;
import com.qihoo.huangmabisheng.activity.SettingActivity;
import com.qihoo.huangmabisheng.constant.Application;
import com.qihoo.huangmabisheng.constant.Constant;
import com.qihoo.huangmabisheng.constant.Constant.Scene;
import com.qihoo.huangmabisheng.constant.Constant.Screen;
import com.qihoo.huangmabisheng.constant.Constant.SizeType;
import com.qihoo.huangmabisheng.constant.Constant.TimeQuantum;
import com.qihoo.huangmabisheng.constant.SharedPrefrencesAssist;
import com.qihoo.huangmabisheng.model.AppDataForList;
import com.qihoo.huangmabisheng.model.AppIntroMap;
import com.qihoo.huangmabisheng.utils.FileUtil;
import com.qihoo.huangmabisheng.utils.Log;
import com.qihoo.huangmabisheng.utils.MyWindowManager;
import com.qihoo.huangmabisheng.utils.TimeUtil;
import com.qihoo.huangmabisheng.utils.TopApp;
import com.qihoo.huangmabisheng.utils.fb;
import com.qihoo.huangmabisheng.view.FloatWindowBigView;
import com.qihoo.huangmabisheng.view.FloatWindowBigView.TouchType;
import com.qihoo.huangmabisheng.wifi.WifiAdmin;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.PendingIntent;
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
import android.view.View;

public class SmartLockService extends Service {
	public static Map<String, Integer> filterMap = new HashMap<String, Integer>();// 二级过滤
	/**
	 * 不能公有，且不能有getter和setter
	 */
	private TimeQuantum timeQuantum = TimeQuantum.DEFAULT;
	private Constant.Scene scene = Scene.DEFAULT;
	private Constant.SizeType sizeType = SizeType.TOTAL;
	public static Constant.Screen screen = Screen.ON;
	private static String TAG = "SmartLockService";
	AppIntroMap app_fre = new AppIntroMap(this);

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
			switch (msg.what) {
			case Constant.UPDATE_IPADDRESS:
				if (null != MyWindowManager.getView()) {
					MyWindowManager.getView().updateDescription(msg.obj+"");
				}
				break;
			case Constant.UPDATE_TIME:
				Date date = new Date();
				changeState(date);
				if (null != MyWindowManager.getView()) {
					MyWindowManager.getView().updateTime(date.getHours(),
							date.getMinutes(), date.getMonth(), date.getDate(),
							date.getDay());
				}
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}
	};

	/**
	 * 先判断耳机，后判断时间段
	 */
	private void changeState(Scene scene) {
		FloatWindowBigView view = MyWindowManager.getView();
		if (view != null) {
			this.scene = scene;
			app_fre.updateAppDatasScene(scene);
			updatePcksIcon(SizeType.SCENE);
			switch (scene) {
			case EARPHONE:
				view.switcher.setImageResource(R.drawable.bg_cr_abs_driving);
				break;
			default:
				timeQuantum = TimeQuantum.DEFAULT;// 以备changeState(Date
													// date)时判断是否时序变化
				break;
			}
		}
	}

	private TimeQuantum changeTimeQuantumToNow() {
		return timeQuantum == TimeQuantum.DEFAULT ? TimeUtil
				.decideTimeQuantumForNow(new Date()) : timeQuantum;
	}

	/**
	 * 先判断耳机，后判断时间段
	 */
	private void changeState(Date date) {
		FloatWindowBigView view = MyWindowManager.getView();
		if (view == null || Scene.EARPHONE == this.scene) {
			return;
		}
		TimeQuantum now = TimeUtil.decideTimeQuantumForNow(date);
		switch (now) {
		case SLEEPING:
			if (timeQuantum != now) {
				Log.d(TAG, "change to SLEEPING");
				if (view != null) {
					view.switcher.setImageResource(R.drawable.black_bg);
				}
			}
			break;
		case WORKING_AFTERNOON:
			if (timeQuantum != now) {
				Log.d(TAG, "change to WORKING_AFTERNOON");
				if (view != null) {
					view.switcher
							.setImageResource(R.drawable.bg_outdoors_driving);
				}
			}
			break;
		case WORKING_MORNING:
			if (timeQuantum != now) {
				Log.d(TAG, "change to WORKING_MORNING");
				if (view != null) {
					view.switcher.setImageResource(R.drawable.bg_jordi_work);
				}
			}
			break;
		case WORKING_NIGHT:
			if (timeQuantum != now) {
				Log.d(TAG, "change to WORKING_NIGHT");
				if (view != null) {
					view.switcher
							.setImageResource(R.drawable.bg_cr_lit_home_night);
				}
			}
			break;
		case REST:
			if (timeQuantum != now) {
				Log.d(TAG, "change to REST");
				if (view != null) {
					view.switcher.setImageResource(R.drawable.bg_cr_abs_out);
				}
			}
			break;
		case BEFORE_SLEEP:
			if (timeQuantum != now) {
				Log.d(TAG, "change to BEFORE_SLEEP");
				if (view != null) {
					view.switcher
							.setImageResource(R.drawable.bg_jordi_home_night);
				}
			}
			break;
		default:
			// if (timeQuantum != now) {
			// Log.d(TAG, "change to DEFAULT");
			// if (view != null) {
			// view.switcher
			// .setImageResource(R.drawable.black_bg);
			// }
			// }
			break;
		}
		timeQuantum = now;
	}

	public void onCreate() {
		setFrontService();
		Log.d(TAG, "onCreate");
		super.onCreate();
		manager = MyWindowManager.getActivityManager(this);
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
		this.registerReceiver(changeIconReceiver, new IntentFilter(
				"com.qihoo.huangmabisheng.UPDATE_ICON"));
		this.registerReceiver(earListenerReceiver, new IntentFilter(
				"android.intent.action.HEADSET_PLUG"));
		if (timer == null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new RefreshTask(), 0, 1000);
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
		TimeQuantum timeQuantum = changeTimeQuantumToNow();
		AppDataForList appData;
		if (app_fre.containsKey(currentPackageName)) {
			appData = app_fre.get(currentPackageName);
			appData.push(timeQuantum);
		} else {
			appData = new AppDataForList(currentPackageName, topActivity,
					timeQuantum);
			app_fre.put(currentPackageName, appData);
		}
		app_fre.updateData(currentPackageName, timeQuantum);
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
		if (!isHome(packageName) && !Constant.alertFilter.contains(packageName))
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
			synchronized (SmartLockService.class) {
				if (screen == Screen.OFF)
					try {
						Log.e(TAG, "wait off");
						SmartLockService.class.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
			}
			updateCurrentPackageInfo();
			// android.util.Log.d(TAG, "update time");
			if (MyWindowManager.isWindowShowing()
					&& MyWindowManager.isWindowLocked()
					&& MyWindowManager.getView().flag == TouchType.NONE) {
				handler.obtainMessage(Constant.UPDATE_TIME).sendToTarget();// 更新时间
				String ipaddress = "slide anywhere to unlock";
				if(Application.app.isSpecialServiceRunning)
				ipaddress = WifiAdmin.getInstance(SmartLockService.this)
						.getIPAddressStr();
				handler.obtainMessage(
						Constant.UPDATE_IPADDRESS,ipaddress).sendToTarget();// 更新IP
			}
			// TODO
			// 先判断是不是可统计的app，即非系统app
			if (currentPackageName.equals(lastPackageName))
				return;
			try {
				if (!filterApplications(currentPackageName)) {
					return;
				}
				pushInAppFre(topActivity, currentPackageName, Constant.SAVE);
				Log.e(TAG, topActivity + "," + currentPackageName);

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
		stopForeground(true);
		Log.d(TAG, "Service destroy");
		super.onDestroy();
		this.unregisterReceiver(screenOffReceiver);
		this.unregisterReceiver(changeIconReceiver);
		this.unregisterReceiver(earListenerReceiver);
		// startService(new Intent(SmartLockService.this,
		// SmartLockService.class));
	}

	/**
	 * 更新锁屏上的icon，更新前进行二级过滤 ，此操作在关屏时启动
	 */
	private void updatePcksIcon() {
		TimeQuantum timeQuantum = changeTimeQuantumToNow();
		try {
			if (app_fre == null || filterMap == null) {
				Log.d(TAG, app_fre + "," + filterMap);
			}
			// List<Entry<String, Integer>> topApp = new TopApp(app_fre)
			// .toApp(filterMap);
			FloatWindowBigView view = MyWindowManager.getView();
			if (null != view)
				switch (sizeType) {
				case SCENE:
					view.updatePackageIcon(app_fre.appDatasScene);
					break;
				case QUANTUM:
					view.updatePackageIcon(app_fre.appDatasNowMap
							.get(timeQuantum));
					break;
				default:
					view.updatePackageIcon(app_fre.appDatas);
					break;
				}
			else {
				Log.d(TAG, "view null");
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 更新锁屏上的icon，更新前进行二级过滤 ，此操作在关屏时启动
	 */
	private void updatePcksIcon(SizeType sizeType) {
		TimeQuantum timeQuantum = changeTimeQuantumToNow();
		try {
			if (app_fre == null || filterMap == null) {
				Log.d(TAG, app_fre + "," + filterMap);
			}
			// List<Entry<String, Integer>> topApp = new TopApp(app_fre)
			// .toApp(filterMap);
			FloatWindowBigView view = MyWindowManager.getView();
			if (null != view)
				switch (sizeType) {
				case SCENE:
					view.updatePackageIcon(app_fre.appDatasScene);
					break;
				case QUANTUM:
					view.updatePackageIcon(app_fre.appDatasNowMap
							.get(timeQuantum));
					break;
				default:
					view.updatePackageIcon(app_fre.appDatas);
					break;
				}
			else {
				Log.d(TAG, "view null");
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
		final String TAG = "screenOffReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			// if (action.equals("android.intent.action.SCREEN_OFF")) {
			Log.i(TAG,
					"-----------OFF------ android.intent.action.SCREEN_OFF------");
			updatePcksIcon();
			// }
			fb.d(context);
		}

	};
	private BroadcastReceiver changeIconReceiver = new BroadcastReceiver() {
		final String TAG = "changeIconReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i(TAG, action);
			switch (sizeType) {
			case SCENE:
				sizeType = SizeType.TOTAL;
				break;
			case QUANTUM:
				sizeType = SizeType.SCENE;
				break;
			default:
				sizeType = SizeType.QUANTUM;
				break;
			}
			updatePcksIcon();
		}

	};
	/**
	 * 接收wifi连接的广播
	 **/
	private BroadcastReceiver wifiListenerReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

		}
	};
	/**
	 * 接收耳机连接的广播
	 **/
	private BroadcastReceiver earListenerReceiver = new BroadcastReceiver() {
		final String TAG = "earListenerReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i(TAG, action);
			if (intent.getIntExtra("state", 0) == 0) {
				changeState(Scene.DEFAULT);
			} else if (intent.getIntExtra("state", 0) == 1) {
				changeState(Scene.EARPHONE);
			}
		}
	};

	private void setFrontService() {
		Notification notification = new Notification(R.drawable.ic_launcher,
				"SmartScreenLock is protecting your phone.",
				System.currentTimeMillis());
		Intent intent = new Intent(this, SettingActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		notification.setLatestEventInfo(this, "Don't worry. Be happy.",
				"Touch here to set your SmartScreenLock", pendingIntent);
		startForeground(9527, notification);
	}
}
