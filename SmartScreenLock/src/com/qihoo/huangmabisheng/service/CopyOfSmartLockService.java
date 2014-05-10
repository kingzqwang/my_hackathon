package com.qihoo.huangmabisheng.service;


import com.qihoo.huangmabisheng.activity.TransparentActivity;
import com.qihoo.huangmabisheng.utils.fb;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class CopyOfSmartLockService extends Service {

	private static String TAG = "SmartLockService";
	private Intent startActivityIntent = null ;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate(){
		Log.d(TAG, "Service oncreate");
		super.onCreate();
		startActivityIntent = new Intent(CopyOfSmartLockService.this , TransparentActivity.class);
		startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		/*注册广播*/
		IntentFilter mScreenOnFilter = new IntentFilter("android.intent.action.SCREEN_ON");
		CopyOfSmartLockService.this.registerReceiver(mScreenOnReceiver, mScreenOnFilter);
		
		/*注册广播*/
		IntentFilter mScreenOffFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
		CopyOfSmartLockService.this.registerReceiver(mScreenOffReceiver, mScreenOffFilter);
	}

	public int onStartCommand(Intent intent , int flags , int startId){
		
		return Service.START_STICKY;
		
	}
	
	public void onDestroy(){
		Log.d(TAG, "Service destroy");
		super.onDestroy();
		CopyOfSmartLockService.this.unregisterReceiver(mScreenOnReceiver);
		CopyOfSmartLockService.this.unregisterReceiver(mScreenOffReceiver);
		//在此重新启动
		startService(new Intent(CopyOfSmartLockService.this, CopyOfSmartLockService.class));
	}
	
	//屏幕变亮的广播,我们要隐藏默认的锁屏界面
	private BroadcastReceiver mScreenOnReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(final Context context , Intent intent) {
			
            Log.i(TAG, intent.getAction());

			if(intent.getAction().equals("android.intent.action.SCREEN_ON")){
				Log.i(TAG, "ON----------------- android.intent.action.SCREEN_ON------");
				startActivity(startActivityIntent);
				new  Thread(new Runnable() {
					
					@Override
					public void run() {
						while (fb.c(context)) {
							fb.d(context);
						}
					}
				}).start();
				
			}
		}
		
	};
	
	//屏幕变暗/变亮的广播 ， 我们要调用KeyguardManager类相应方法去解除屏幕锁定
	private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context , Intent intent) {
			String action = intent.getAction() ;
			if(action.equals("android.intent.action.SCREEN_OFF")){
				Log.i(TAG, "OFF----------------- android.intent.action.SCREEN_OFF------");
				startActivity(startActivityIntent);
				
			}
		}
		
	};
}
