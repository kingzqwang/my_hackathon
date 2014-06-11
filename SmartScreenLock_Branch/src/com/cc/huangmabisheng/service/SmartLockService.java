package com.cc.huangmabisheng.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cc.huangmabisheng.R;
import com.cc.huangmabisheng.activity.SettingActivity;
import com.cc.huangmabisheng.constant.Application;
import com.cc.huangmabisheng.constant.Constant;
import com.cc.huangmabisheng.constant.Constant.Scene;
import com.cc.huangmabisheng.constant.Constant.Screen;
import com.cc.huangmabisheng.constant.Constant.SizeType;
import com.cc.huangmabisheng.constant.Constant.TimeQuantum;
import com.cc.huangmabisheng.constant.SharedPrefrencesAssist;
import com.cc.huangmabisheng.model.AppDataForList;
import com.cc.huangmabisheng.model.AppIntroMap;
import com.cc.huangmabisheng.model.WorldCupMatch;
import com.cc.huangmabisheng.utils.FileUtil;
import com.cc.huangmabisheng.utils.Log;
import com.cc.huangmabisheng.utils.MyWindowManager;
import com.cc.huangmabisheng.utils.ProcessUtil;
import com.cc.huangmabisheng.utils.TimeUtil;
import com.cc.huangmabisheng.utils.Timer;
import com.cc.huangmabisheng.utils.TipHelper;
import com.cc.huangmabisheng.utils.Toast;
import com.cc.huangmabisheng.utils.TopApp;
import com.cc.huangmabisheng.utils.fb;
import com.cc.huangmabisheng.view.FloatWindowBigView;
import com.cc.huangmabisheng.view.FloatWindowBigView.TouchType;
import com.cc.huangmabisheng.wifi.WifiAdmin;

import android.R.bool;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
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
import android.drm.DrmStore.Action;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Contacts;
import android.view.LayoutInflater.Filter;
import android.view.View;
import android.view.View.OnClickListener;

public class SmartLockService extends Service {
	class Matchs {
		Date today = null;
		List<WorldCupMatch> matchsToday = new ArrayList<WorldCupMatch>();
	}

	Matchs matchs = new Matchs();
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
		return null;
	}

	List<RunningTaskInfo> runningTasks;// = manager.getRunningTasks(1);
	RunningTaskInfo runningTaskInfo;// = runningTasks.get(0);
	ComponentName topActivity;// = runningTaskInfo.topActivity;
	String currentPackageName, lastPackageName;
	FileUtil fs;// = new FileService(getApplicationContext());
	private Timer scheduleTimer;
	private Timer timer;
	MediaPlayer mediaPlayer = null;
	PowerManager.WakeLock wakeLock;

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.UPDATE_IPADDRESS:
				if (null != MyWindowManager.getView()) {
					MyWindowManager.getView().updateDescription(msg.obj + "");
				}
				break;
			case Constant.UPDATE_TIME:
				Date date = new Date();
				changeState(date, false);
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

	// /**
	// * 先判断耳机，后判断时间段
	// */
	// private void changeState(Scene scene) {
	// FloatWindowBigView view = MyWindowManager.getView();
	// if (view != null) {
	// /**
	// * 暂时去掉场景icon this.scene = scene;
	// * app_fre.updateAppDatasScene(scene);
	// * updatePcksIcon(SizeType.SCENE);
	// */
	// switch (scene) {
	// case EARPHONE:
	// view.switcher.setImageResource(R.drawable.bg_cr_abs_driving);
	// break;
	// default:
	// // timeQuantum = TimeQuantum.DEFAULT;// 以备changeState(Date
	// // // date)时判断是否时序变化
	// changeState(null, true);
	// break;
	// }
	// }
	// }

	// private TimeQuantum changeTimeQuantumToNow() {
	// return timeQuantum == TimeQuantum.DEFAULT ? TimeUtil
	// .decideTimeQuantumForNow(new Date()) : timeQuantum;
	// }

	/**
	 * 先判断耳机，后判断时间段
	 * 
	 * @param date
	 *            传入时间，若为null则为现在系统时间
	 * @param force
	 *            强制换壁纸
	 */
	private void changeState(Date date, boolean force) {
		FloatWindowBigView view = MyWindowManager.getView();
		if (view == null || Scene.EARPHONE == this.scene) {
			return;
		}
		if (null == date) {
			date = new Date();
		}
		TimeUtil nowUtil = TimeUtil.decideTimeUtilForNow(date);
		if ((!force && timeQuantum != nowUtil.timeQuantum) || force) {
			Log.e(TAG, "change to " + nowUtil.timeQuantum);
			view.switcher.setImageResource(nowUtil.drawableResource);
		}

		// TimeQuantum now = TimeUtil.decideTimeQuantumForNow(date);
		// switch (now) {
		// case SLEEPING:
		// if (timeQuantum != now) {
		// Log.d(TAG, "change to SLEEPING");
		// if (view != null) {
		// view.switcher.setImageResource(R.drawable.black_bg);
		// }
		// }
		// break;
		// case WORKING_AFTERNOON:
		// if (timeQuantum != now) {
		// Log.d(TAG, "change to WORKING_AFTERNOON");
		// if (view != null) {
		// view.switcher
		// .setImageResource(R.drawable.bg_outdoors_driving);
		// }
		// }
		// break;
		// case WORKING_MORNING:
		// if (timeQuantum != now) {
		// Log.d(TAG, "change to WORKING_MORNING");
		// if (view != null) {
		// view.switcher.setImageResource(R.drawable.bg_jordi_work);
		// }
		// }
		// break;
		// case WORKING_NIGHT:
		// if (timeQuantum != now) {
		// Log.d(TAG, "change to WORKING_NIGHT");
		// if (view != null) {
		// view.switcher
		// .setImageResource(R.drawable.bg_cr_lit_home_night);
		// }
		// }
		// break;
		// case REST:
		// if (timeQuantum != now) {
		// Log.d(TAG, "change to REST");
		// if (view != null) {
		// view.switcher.setImageResource(R.drawable.bg_cr_abs_out);
		// }
		// }
		// break;
		// case BEFORE_SLEEP:
		// if (timeQuantum != now) {
		// Log.d(TAG, "change to BEFORE_SLEEP");
		// if (view != null) {
		// view.switcher
		// .setImageResource(R.drawable.bg_jordi_home_night);
		// }
		// }
		// break;
		// default:
		// // if (timeQuantum != now) {
		// // Log.d(TAG, "change to DEFAULT");
		// // if (view != null) {
		// // view.switcher
		// // .setImageResource(R.drawable.black_bg);
		// // }
		// // }
		// break;
		// }
		timeQuantum = nowUtil.timeQuantum;
	}

	public void onCreate() {
		setFrontService();
		Log.e(TAG, "Service onCreate");
		super.onCreate();
		manager = MyWindowManager.getActivityManager(this);
		// app_fre.putAll((AppIntroMap) SharedPrefrencesAssist.instance(this)
		// .getSharedPreferences().getAll());
		// app_fre.remove("hand");
		Log.d(TAG, app_fre.size() + "");
		fs = new FileUtil(getApplicationContext());
		fs.read(app_fre);
		init();
		updateCurrentPackageInfo();
		PackageInfo packageInfo = null;
		try {
			packageInfo = getPackageManager().getPackageInfo(
					currentPackageName, 0);
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
					&& !currentPackageName.equals("com.cc.huangmabisheng")) {
				pushInAppFre(topActivity, currentPackageName, Constant.UNSAVE);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		lastPackageName = currentPackageName;// 记录上次的Top，当前赋值
		this.registerReceiver(screenOffReceiver, new IntentFilter(
				"android.intent.action.SCREEN_OFF"));
		this.registerReceiver(changeIconReceiver, new IntentFilter(
				"com.cc.huangmabisheng.UPDATE_ICON"));
		this.registerReceiver(phoneListenerReceiver, new IntentFilter(
				"android.intent.action.PHONE_STATE"));
		// this.registerReceiver(earListenerReceiver, new IntentFilter(
		// "android.intent.action.HEADSET_PLUG"));
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new RefreshTimeTask(), 0, Constant.REFRESH_TIME);
		}
		if (scheduleTimer == null) {
			scheduleTimer = new Timer();
			scheduleTimer.schedule(new ScheduleTask(), 0,
					Constant.SCHEDULE_TIME);
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
		// TimeQuantum timeQuantum = changeTimeQuantumToNow();
		AppDataForList appData;
		if (app_fre.containsKey(currentPackageName)) {
			appData = app_fre.get(currentPackageName);
			appData.push(timeQuantum);// 次数+1
		} else {
			appData = new AppDataForList(currentPackageName, topActivity,
					timeQuantum);
			app_fre.put(currentPackageName, appData);
		}
		Log.e(TAG, currentPackageName + "次数:" + appData.size());
		app_fre.updateData(currentPackageName, timeQuantum);
		fs.save(appData);
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
		// ActivityManager mActivityManager = (ActivityManager)
		// getSystemService(Context.ACTIVITY_SERVICE);
		// List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
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

	private void init() {
		if (app_fre.size() == 0) {
			PackageManager packageManager = this.getPackageManager();

			Intent intent;
			List<ResolveInfo> resolveInfos;

			intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
			resolveInfos = packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
			if (resolveInfos.size() > 0) {
				String packageName = resolveInfos.get(0).activityInfo.packageName;
				AppDataForList appDataForList = new AppDataForList(packageName,
						null, timeQuantum);
				app_fre.put(packageName, appDataForList);
				app_fre.updateData(packageName, timeQuantum);
				fs.save(appDataForList);
			}

			intent = new Intent();
			intent.setType("image/*");
			resolveInfos = packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
			if (resolveInfos.size() > 0) {
				String packageName = resolveInfos.get(0).activityInfo.packageName;
				AppDataForList appDataForList = new AppDataForList(packageName,
						null, timeQuantum);
				app_fre.put(packageName, appDataForList);
				app_fre.updateData(packageName, timeQuantum);
				fs.save(appDataForList);
			}

			intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://www.baidu.com"));
			resolveInfos = packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
			if (resolveInfos.size() > 0) {
				String packageName = resolveInfos.get(0).activityInfo.packageName;
				AppDataForList appDataForList = new AppDataForList(packageName,
						null, timeQuantum);
				app_fre.put(packageName, appDataForList);
				app_fre.updateData(packageName, timeQuantum);
				fs.save(appDataForList);
			}

			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Contacts.People.CONTENT_URI);
			resolveInfos = packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
			if (resolveInfos.size() > 0) {
				String packageName = resolveInfos.get(0).activityInfo.packageName;
				AppDataForList appDataForList = new AppDataForList(packageName,
						null, timeQuantum);
				app_fre.put(packageName, appDataForList);
				app_fre.updateData(packageName, timeQuantum);
				fs.save(appDataForList);
			}

			intent = new Intent(Intent.ACTION_VIEW);
			intent.setType("vnd.android-dir/mms-sms");
			resolveInfos = packageManager.queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
			if (resolveInfos.size() > 0) {
				String packageName = resolveInfos.get(0).activityInfo.packageName;
				AppDataForList appDataForList = new AppDataForList(packageName,
						null, timeQuantum);
				app_fre.put(packageName, appDataForList);
				app_fre.updateData(packageName, timeQuantum);
				fs.save(appDataForList);
			}

			for (String init_pck : Constant.APP_INIT_STRINGS) {
				if (null == packageManager.getLaunchIntentForPackage(init_pck)) {
					continue;
				}
				AppDataForList appDataForList = new AppDataForList(init_pck,
						null, timeQuantum);
				app_fre.put(init_pck, appDataForList);
				app_fre.updateData(init_pck, timeQuantum);
				fs.save(appDataForList);
			}
		}
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
		// && !packageName.equals("com.cc.huangmabisheng");
		if (!isHome(packageName) && !Constant.alertFilter.contains(packageName))
			return true;
		else {
			app_fre.remove(packageName);
			return false;
		}
	}

	class RefreshTimeTask extends TimerTask {
		@Override
		public void run() {
			if (MyWindowManager.isWindowShowing()
					&& MyWindowManager.getView().flag == TouchType.NONE) {
				handler.obtainMessage(Constant.UPDATE_TIME).sendToTarget();// 更新时间
			}
			// if(fb.a(SmartLockService.this))
			fb.d(SmartLockService.this);
			// 如果开启闹钟，则不wait()

			if (MyWindowManager.isWindowShowing()
					&& !MyWindowManager.isWindowLocked())
				synchronized (FloatWindowBigView.class) {
					if (MyWindowManager.isWindowShowing()
							&& !MyWindowManager.isWindowLocked())
						try {
							Log.e(TAG, "FloatWindowBigView wait");
							FloatWindowBigView.class.wait();
							if (MyWindowManager.isWindowShowing()
									&& MyWindowManager.getView().flag == TouchType.NONE) {
								handler.obtainMessage(Constant.UPDATE_TIME)
										.sendToTarget();// 更新时间
							}
							Log.e(TAG, "FloatWindowBigView notify");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
			if (!SharedPrefrencesAssist.instance(SmartLockService.this)
					.readBoolean(Constant.WORLD_CUP_IMAGEVIEW)) {
				if (SmartLockService.screen == Screen.OFF)
					synchronized (FloatWindowService.class) {
						if (SmartLockService.screen == Screen.OFF)
							try {
								Log.e(TAG, "FloatWindowService wait");
								FloatWindowService.class.wait();
								if (MyWindowManager.isWindowShowing()
										&& MyWindowManager.getView().flag == TouchType.NONE) {
									handler.obtainMessage(Constant.UPDATE_TIME)
											.sendToTarget();// 更新时间
								}
								Log.e(TAG, "FloatWindowService notify");
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
					}
			} else {
				try {
					// TODO
					Date now = new Date();
					for (int i = 0; i < matchs.matchsToday.size(); i++) {
						WorldCupMatch match = matchs.matchsToday.get(i);
						Date start = new Date(match.start.getTime() - 600000);
						if (now.getMinutes() == start.getMinutes()
								&& now.getHours() == start.getHours()
								&& now.getDate() == start.getDate()
								&& now.getMonth() == start.getMonth()
								&& now.getYear() == start.getYear()) {
							if (!match.clockable) {
								break;
							} else {
								match.clockable = false;
								updateWorldCupViewClock(i, match.clockable);
								// TODO 播声音
								if (wakeLock == null)
									wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
											.newWakeLock(
													PowerManager.ACQUIRE_CAUSES_WAKEUP
															| PowerManager.SCREEN_DIM_WAKE_LOCK,
													"My Tag");
								wakeLock.acquire();
								AudioManager audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);  
							    // 获取最大音乐音量  
							    int maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC); 
							    audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume/2,  
						                AudioManager.FLAG_PLAY_SOUND);  
								if (mediaPlayer == null)
									mediaPlayer = MediaPlayer.create(
											SmartLockService.this,
											R.raw.we_are_one);
								mediaPlayer.start();

								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										if (mediaPlayer == null)
											return;
										mediaPlayer.stop();
										mediaPlayer.release();
										mediaPlayer = null;
										if (wakeLock == null)
											return;
										wakeLock.release();
										wakeLock = null;
									}
								}, 30000);
							}
						}
					}
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	private void updateWorldCupViewClock(final int i, final boolean clockable) {
		final FloatWindowBigView view = MyWindowManager.getView();
		if (view == null)
			return;
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (clockable) {
					view.clockImageViews[i]
							.setImageResource(R.drawable.eys_open);
					view.clockTextViews[i].setText("闹钟开");
				} else {
					view.clockImageViews[i]
							.setImageResource(R.drawable.eys_close);
					view.clockTextViews[i].setText("闹钟关");
				}

			}
		});
	}

	int tim = 0;

	/**
	 * 该类是该服务的循环主体，用于不断读取top activity更新app_fre，同时也更新时间
	 */
	class ScheduleTask extends TimerTask {
		@Override
		public void run() {
			Log.d(TAG, "运行次数" + (++tim));
			if (!SharedPrefrencesAssist.instance(SmartLockService.this)
					.readBoolean(Constant.WORLD_CUP_IMAGEVIEW)) {
				// TODO
			}
			// synchronized (SmartLockService.class) {
			// if (screen == Screen.OFF)
			// try {
			// Log.e(TAG, "wait off");
			// SmartLockService.class.wait();
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// return;
			// }
			// }
			fb.d(SmartLockService.this);
			if (MyWindowManager.isWindowLocked())
				synchronized (SmartLockService.class) {
					if (MyWindowManager.isWindowLocked())
						try {
							// Log.d(TAG,
							// "wait "
							// + MyWindowManager
							// .getWindowVisibility());
							// Log.d(TAG, "wait 运行次数" + tim);
							SmartLockService.class.wait();
							if (SharedPrefrencesAssist.instance(
									SmartLockService.this).readBoolean(
									Constant.SCREEN_CLEAN_IMAGEVIEW))
								clearProcess();
							Log.d(TAG, "notify");
						} catch (InterruptedException e) {
							e.printStackTrace();
							return;
						}
				}
			if (SharedPrefrencesAssist.instance(SmartLockService.this)
					.readBoolean(Constant.WORLD_CUP_IMAGEVIEW)) {
				Date now = new Date();
				for (int i = 0; i < matchs.matchsToday.size(); i++) {
					WorldCupMatch match = matchs.matchsToday.get(i);
					Date start = new Date(match.start.getTime() - 600000);
					if (now.getMinutes() == start.getMinutes()
							&& now.getHours() == start.getHours()
							&& now.getDate() == start.getDate()
							&& now.getMonth() == start.getMonth()
							&& now.getYear() == start.getYear()) {
						if (!match.clockable) {
							break;
						} else {
							match.clockable = false;
							updateWorldCupViewClock(i, match.clockable);
							// TODO 播声音
							if (wakeLock == null)
								wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE))
										.newWakeLock(
												PowerManager.ACQUIRE_CAUSES_WAKEUP
														| PowerManager.SCREEN_DIM_WAKE_LOCK,
												"My Tag");
							wakeLock.acquire();
							if (mediaPlayer == null)
								mediaPlayer = MediaPlayer
										.create(SmartLockService.this,
												R.raw.todo);
							AudioManager audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);  
						    // 获取最大音乐音量  
						    int maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC); 
						    audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume/2,  
					                AudioManager.FLAG_PLAY_SOUND);  
							mediaPlayer.start();
							handler.post(new Runnable() {
								@Override
								public void run() {
									Toast.showLong(SmartLockService.this, "CC 提醒您, 比赛快要开始了");
								}
							});
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									if (mediaPlayer == null)
										return;
									mediaPlayer.stop();
									mediaPlayer.release();
									mediaPlayer = null;
									if (wakeLock == null)
										return;
									wakeLock.release();
									wakeLock = null;
								}
							}, 4000);
						}
					}
				}
			}
			updateCurrentPackageInfo();
			if (currentPackageName.equals(lastPackageName))
				return;
			try {
				// 过滤应用
				if (!filterApplications(currentPackageName)) {
					return;
				}
				pushInAppFre(topActivity, currentPackageName, Constant.SAVE);
				Log.e(TAG, "pushInAppFre:" + currentPackageName);
			} catch (NameNotFoundException e1) {
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
		this.unregisterReceiver(screenOffReceiver);
		this.unregisterReceiver(changeIconReceiver);
		this.unregisterReceiver(phoneListenerReceiver);
		// this.unregisterReceiver(earListenerReceiver);
		timer.cancel();
		scheduleTimer.cancel();
		Log.e(TAG, "Service destroy");
		super.onDestroy();
		fb.u(this);
		// startService(new Intent(SmartLockService.this,
		// SmartLockService.class));
	}

	/**
	 * 此操作在关屏时启动
	 */
	private void updatePcksIcon() {
		if (app_fre == null || filterMap == null) {
			Log.d(TAG, app_fre + "," + filterMap);
		}
		// List<Entry<String, Integer>> topApp = new TopApp(app_fre)
		// .toApp(filterMap);
		FloatWindowBigView view = MyWindowManager.getView();
		if (null != view)
			view.updatePackageIcon(app_fre.appDatas);
		/*
		 * switch (sizeType) { case SCENE:
		 * view.updatePackageIcon(app_fre.appDatasScene); break; case QUANTUM:
		 * view.updatePackageIcon(app_fre.appDatasNowMap .get(timeQuantum));
		 * break; default: view.updatePackageIcon(app_fre.appDatas); break; }
		 */
		else
			Log.d(TAG, "view null");

	}

	private void setWorldCup() {
		FloatWindowBigView view = MyWindowManager.getView();
		if (null != view)
			view.setWorldCup();
		else
			Log.d(TAG, "view null");
	}

	// private void updatePcksIcon(SizeType sizeType) {
	// // TimeQuantum timeQuantum = changeTimeQuantumToNow();
	// try {
	// if (app_fre == null || filterMap == null) {
	// Log.d(TAG, app_fre + "," + filterMap);
	// }
	// // List<Entry<String, Integer>> topApp = new TopApp(app_fre)
	// // .toApp(filterMap);
	// FloatWindowBigView view = MyWindowManager.getView();
	// if (null != view)
	// switch (sizeType) {
	// case SCENE:
	// view.updatePackageIcon(app_fre.appDatasScene);
	// break;
	// case QUANTUM:
	// view.updatePackageIcon(app_fre.appDatasNowMap
	// .get(timeQuantum));
	// break;
	// default:
	// view.updatePackageIcon(app_fre.appDatas);
	// break;
	// }
	// else {
	// Log.d(TAG, "view null");
	// }
	// } catch (NameNotFoundException e) {
	// e.printStackTrace();
	// }
	// }

	private boolean judgeJsonForWoldCup(String json, Date now) {
		List<WorldCupMatch> worldCupMatchs = new ArrayList<WorldCupMatch>();
		try {

			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
				Log.e(TAG, "lv:" + jsonObject2.getInt("lv"));
				int lv = jsonObject2.getInt("lv");
				Log.e(TAG, "rv:" + jsonObject2.getInt("rv"));
				int rv = jsonObject2.getInt("rv");
				Log.e(TAG, "time:" + jsonObject2.getString("time"));
				Date clock = new SimpleDateFormat("yyyy-MM-dd HH:mm")
						.parse(jsonObject2.getString("time"));
				worldCupMatchs.add(new WorldCupMatch(lv, rv, clock));
			}
			int version;
			version = jsonObject.getInt("version");
			Log.e(TAG, "version:" + version);
			if (version < 1) {
				Log.e(TAG, "version不是最新");
				return false;
			} else {
				matchs.today = now;
				matchs.matchsToday = worldCupMatchs;
				Log.e(TAG, "matchs:" + worldCupMatchs.size() + "");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "json 解析有问题");
			return false;
		} catch (NullPointerException e) {
			Log.e(TAG, "请注意数组的逗号");
			return false;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private void updateWorldCupView() {
		final FloatWindowBigView view = MyWindowManager.getView();
		if (view == null)
			return;
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < matchs.matchsToday.size(); i++) {
					final WorldCupMatch worldCupMatch = matchs.matchsToday
							.get(i);
					view.clockLayouts[i]
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									if (worldCupMatch.clockable) {
										worldCupMatch.clockable = false;
										view.updateWorldCup(matchs.matchsToday);
									} else {
										worldCupMatch.clockable = true;
										view.updateWorldCup(matchs.matchsToday);
									}
								}
							});
				}
				view.updateWorldCup(matchs.matchsToday);
			}
		});
	}

	/**
	 * 判断现在是哪一天，DEADLINE前算今天，DEADLINE点后算明天，若DEADLINE为null则算10点
	 */
	private Date judgeToday() {
		Date date = new Date();
		if (10 <= date.getHours()) {
			return new Date(date.getTime() + 24 * 60 * 60 * 1000);
		} else { 
			return date;
		}
	}

	private void updateWorldCupFile() {
		Log.e(TAG, "updateWorldCupFile");
		Date now = judgeToday();
		if (matchs.today != null) {
			// TODO 拉过了，需要判断该已经拉取的时间是否是今天的，如果是，则return；如果不是，则重新拉
			if (matchs.today.getDate() == now.getDate()
					&& matchs.today.getMonth() == now.getMonth()
					&& matchs.today.getYear() == now.getYear()) {
				Log.e(TAG, "内存 existed");
				updateWorldCupView();
				return;
			} else {
				Log.e(TAG, "clear");
				matchs.matchsToday.clear();
			}
		}
		// 重新拉前，先确认本地有没有，若有就直接用
		DateFormat d = new SimpleDateFormat("yyyy_MM_dd");
		String dateKey = d.format(now);
		String json = SharedPrefrencesAssist.instance(this).read(dateKey);
		if (json != null) {
			Log.e(TAG, "json existed");
			if (judgeJsonForWoldCup(json, now)) {
				updateWorldCupView();
				return;
			}
		}
		// 真的要拉了
		URL url;
		try {
			url = new URL("http://screenlock.sinaapp.com/?t=" + dateKey);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(15000);
			Log.e(TAG, conn.getResponseCode() + "");
			if (conn.getResponseCode() != 200) {
				return;
			}
			StringBuffer stringBuffer = new StringBuffer();
			String lines;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			while ((lines = reader.readLine()) != null) {
				stringBuffer.append(lines);
			}
			Log.e(TAG, stringBuffer.toString());
			if (judgeJsonForWoldCup(stringBuffer.toString(), now)) {
				SharedPrefrencesAssist.instance(this).write(dateKey,
						stringBuffer.toString());
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			updateWorldCupView();
		}
	}

	private BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updatePcksIcon();
			setWorldCup();
			if (SharedPrefrencesAssist.instance(SmartLockService.this)
					.readBoolean(Constant.WORLD_CUP_IMAGEVIEW))
				new Thread(new Runnable() {
					@Override
					public void run() {
						updateWorldCupFile();
					}
				}).start();
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

	private BroadcastReceiver phoneListenerReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			View view = MyWindowManager.getView();
			if(view!=null)view.setVisibility(View.GONE);
		}
	};
	
	// /**
	// * 接收耳机连接的广播
	// **/
	// private BroadcastReceiver earListenerReceiver = new BroadcastReceiver() {
	// final String TAG = "earListenerReceiver";
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// String action = intent.getAction();
	// Log.i(TAG, action);
	// if (intent.getIntExtra("state", 0) == 0) {
	// changeState(Scene.DEFAULT);
	// } else if (intent.getIntExtra("state", 0) == 1) {
	// changeState(Scene.EARPHONE);
	// }
	// }
	// };

	private void setFrontService() {
		Notification notification = new Notification(R.drawable.ic_launcher,
				"SmartScreenLock is protecting your phone.",
				System.currentTimeMillis());
		Intent intent = new Intent(this, SettingActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		notification.setLatestEventInfo(this,
				getResources().getString(R.string.notification_title),
				getResources().getString(R.string.notification_desc),
				pendingIntent);
		startForeground(9527, notification);
	}

	private void clearProcess() {
		TipHelper.Vibrate(this, Constant.VIBRATE_TIME);
		Log.e(TAG, "clear");
		new Thread(new Runnable() {
			private boolean filter(String processName) {
				if (getHomes().contains(processName))
					return false;
				// if (processName.startsWith("com.android"))
				// return false;
				// if (processName.startsWith("com.qihoo"))
				// return false;
				// if (processName.startsWith("com.google"))
				// return false;
				// if (processName.startsWith("com.miui"))
				// return false;
				return true;
			}

			@Override
			public void run() {
				ActivityManager activityManager = (ActivityManager) SmartLockService.this
						.getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningAppProcessInfo> runningApps = activityManager
						.getRunningAppProcesses();
				final ActivityManager.MemoryInfo memoryBefore = new ActivityManager.MemoryInfo();
				activityManager.getMemoryInfo(memoryBefore);
				for (RunningAppProcessInfo info : runningApps) {
					if (!filter(info.processName)) {
						Log.d(TAG, "过滤" + info.processName);
						continue;
					}
					try {
						ProcessUtil.clearBackgroundProcess(info.processName,
								SmartLockService.this);
					} catch (NameNotFoundException e) {
					}
				}
				final ActivityManager.MemoryInfo memoryAfter = new ActivityManager.MemoryInfo();
				activityManager.getMemoryInfo(memoryAfter);
				handler.post(new Runnable() {

					@Override
					public void run() {
						long av = (memoryAfter.availMem - memoryBefore.availMem)
								* 100 / memoryBefore.availMem;
						if (av > 5) {
							Toast.showShort(SmartLockService.this, "CC 为您加速 " + av
									+ "%");
						} else {
							Toast.showShort(SmartLockService.this, "手机状态很好哦");
						}

					}
				});
			}
		}).start();
	}
}
