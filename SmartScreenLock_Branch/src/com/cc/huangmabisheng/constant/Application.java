package com.cc.huangmabisheng.constant;

import com.cc.huangmabisheng.utils.MySQLiteOpenHelper;

import android.database.sqlite.SQLiteOpenHelper;


public class Application extends android.app.Application{
	public boolean isServiceRunning = true;
	public boolean isSpecialServiceRunning = false;
	public SQLiteOpenHelper myHelper;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		app = this;
		myHelper = new MySQLiteOpenHelper(this, "cc_screen_lock.db", null, 5);
	}
	public static Application app;
	public void setServiceOffStatus() {
		isServiceRunning = false;
	}
	public void setServiceOnStatus() {
		isServiceRunning = true;
	}
	public void setSpecialServiceOffStatus() {
		isSpecialServiceRunning = false;
	}
	public void setSpecialServiceOnStatus() {
		isSpecialServiceRunning = true;
	}
}
