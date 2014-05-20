package com.qihoo.huangmabisheng.constant;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefrencesAssist {
	private Context appContext;
	private SharedPreferences sharedPreferences;
	
	public SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}
	private static SharedPrefrencesAssist sharedPreferencesAssist;
	private SharedPrefrencesAssist(Context appContext) {
		
		this.appContext = appContext;
		sharedPreferences = appContext.getSharedPreferences(Constant.APP_NAME, android.content.Context.MODE_PRIVATE);
	}
	public static SharedPrefrencesAssist instance(Context context){
		if (null != sharedPreferencesAssist) {
			return sharedPreferencesAssist;
		}else {
			Context appContext = context.getApplicationContext();
			sharedPreferencesAssist = new SharedPrefrencesAssist(appContext);
			return sharedPreferencesAssist;
		}
	}
	public Context getAppContext() {
		return appContext;
	}
	public boolean write(String key,String value) {
		return sharedPreferences.edit()
		.putString(key, value)
		.commit();
	}
	public boolean write(String key,int value) {
		return sharedPreferences.edit()
		.putInt(key, value)
		.commit();
	}
	public String read(String key) {
		return sharedPreferences.getString(key, null);
	}
}
