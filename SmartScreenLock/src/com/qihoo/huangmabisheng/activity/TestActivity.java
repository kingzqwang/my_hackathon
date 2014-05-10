package com.qihoo.huangmabisheng.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qihoo.huangmabisheng.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

/**
 * 
 * 
 */
public class TestActivity extends Activity {
	private ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
	private ArrayList<String> appNameList = new ArrayList<String>();
	private ArrayList<String> myAppNames = new ArrayList<String>();


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Map<String, Integer> app_fre = new HashMap<String, Integer>();
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		String up_package = new String("");
		List<RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
		RunningTaskInfo runningTaskInfo = runningTasks.get(0);
		ComponentName topActivity = runningTaskInfo.topActivity;
		String packageName = topActivity.getPackageName();
		up_package = packageName;
		appInfoProvider();
		while (true) {
			runningTasks = manager.getRunningTasks(1);
			runningTaskInfo = runningTasks.get(0);
			topActivity = runningTaskInfo.topActivity;
			packageName = topActivity.getPackageName();

			int count = 0;
			if (packageName.equals(up_package))
				continue;
			else if (app_fre.containsKey(packageName)) {
				
				if (myAppNames.contains(packageName)) {
					count = app_fre.get(packageName).intValue() + 1;
					app_fre.put(packageName, count);
					Log.i(packageName,
							Integer.toString(app_fre.get(packageName)));
				}
			} else {
				if (myAppNames.contains(packageName)) {
					count = 1;
					app_fre.put(packageName, count);
					Log.i(packageName,
							Integer.toString(app_fre.get(packageName)));
				}
			}

			SystemClock.sleep(3000);
			up_package = packageName;
		}
	}

	/**
	 * @param void
	 * @return ArrayList<AppInfo>
	 * 
	 * */
	public ArrayList<String> appInfoProvider() {

		// 获取手机已安装的所有应用package的信息(其中包括用户自己安装的，还有系统自带的)
		List<PackageInfo> packages = getPackageManager()
				.getInstalledPackages(0);

		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			// if (appNameList.contains(packageInfo.applicationInfo.loadLabel(
			// getPackageManager()).toString()))
			// continue;
			// else
			// appNameList.add(packageInfo.applicationInfo.loadLabel(
			// getPackageManager()).toString());
			AppInfo tmpInfo = new AppInfo();
			tmpInfo.appName = packageInfo.applicationInfo.loadLabel(
					getPackageManager()).toString();
			tmpInfo.packageName = packageInfo.packageName;
			tmpInfo.versionName = packageInfo.versionName;
			tmpInfo.versionCode = packageInfo.versionCode;
			tmpInfo.appIcon = packageInfo.applicationInfo
					.loadIcon(getPackageManager());
			// 如果属于非系统程序，则添加到列表显示
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				appList.add(tmpInfo);
				if (myAppNames.contains(tmpInfo.packageName))
					continue;
				else {
					myAppNames.add(tmpInfo.packageName);
					Log.v("app", tmpInfo.packageName);
				}
			}
		}
		return myAppNames;
	}

	/**
	 * @author wangzhiqing-xy
	 * @version 1.0 This is the struct of app's information
	 * */
	public class AppInfo {
		public String appName = "";
		public String packageName = "";
		public String versionName = "";
		public int versionCode = 0;
		public Drawable appIcon = null;

		public void print() {
			Log.v("app", "Name:" + appName + " Package:" + packageName);
			// Log.v("app", "Name:" + appName + " versionName:" + versionName);
			// Log.v("app", "Name:" + appName + " versionCode:" + versionCode);
		}

	}

}