package com.qihoo.huangmabisheng.constant;


public class Application extends android.app.Application{
	public boolean isServiceRunning = true;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		app = this;
	}
	public static Application app;
	public void setServiceOffStatus() {
		isServiceRunning = false;
	}
	public void setServiceOnStatus() {
		isServiceRunning = true;
	}
}
