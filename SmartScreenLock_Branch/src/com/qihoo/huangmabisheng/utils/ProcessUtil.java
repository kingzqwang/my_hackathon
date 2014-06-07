package com.qihoo.huangmabisheng.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class ProcessUtil {
	final static String TAG = "ProcessUtil";
	public static void clearBackgroundProcess(String pck,Context context) throws NameNotFoundException {
				PackageInfo p = context.getPackageManager().getPackageInfo(pck, 0);
				ActivityManager a = (ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE);
				a.killBackgroundProcesses(p.applicationInfo.processName);
				//Log.e(TAG, p.applicationInfo.processName);
	}

}
