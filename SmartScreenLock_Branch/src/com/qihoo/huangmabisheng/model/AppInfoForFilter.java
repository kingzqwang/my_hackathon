package com.qihoo.huangmabisheng.model;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
	 * @author wangzhiqing-xy
	 * @version 1.0 This is the struct of app's information
	 * */
	public class AppInfoForFilter {
		public String appName = "";
		public String packageName = "";
		public String versionName = "";
		public int versionCode = 0;
		public Drawable appIcon = null;
		public boolean isFiltered=false;
		public void print() {
			Log.v("app", "Name:" + appName + " Package:" + packageName);
			// Log.v("app", "Name:" + appName + " versionName:" + versionName);
			// Log.v("app", "Name:" + appName + " versionCode:" + versionCode);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof AppInfoForFilter) {
				if(packageName.equals(((AppInfoForFilter)o).packageName)) return true;
			}
			return false;
		}

	}