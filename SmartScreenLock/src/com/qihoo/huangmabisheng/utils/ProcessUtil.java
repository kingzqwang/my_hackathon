package com.qihoo.huangmabisheng.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class ProcessUtil {
	public static void clearBackgroundProcess(String pck,Context context) {
			try {
				PackageInfo p = context.getPackageManager().getPackageInfo(pck, 0);
				ActivityManager a = MyWindowManager.getActivityManager(context);
				a.killBackgroundProcesses(p.applicationInfo.processName);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
