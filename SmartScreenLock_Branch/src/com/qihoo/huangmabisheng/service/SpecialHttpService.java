package com.qihoo.huangmabisheng.service;

import java.io.IOException;

import com.qihoo.huangmabisheng.httpserver.SpecialHttpServer;
import com.qihoo.huangmabisheng.wifi.WifiAdmin;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class SpecialHttpService extends Service{
	final String TAG = "SpecialHttpService";
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		//开启httpserver
		try {
			SpecialHttpServer httpdServer = SpecialHttpServer.instance(handler);
			WifiAdmin wifiAdmin = WifiAdmin.getInstance(this);
			Log.d(TAG, wifiAdmin.getIPAddressStr());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
