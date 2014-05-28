package com.qihoo.huangmabisheng.service;

import java.io.IOException;

import com.qihoo.huangmabisheng.activity.TransparentActivity;
import com.qihoo.huangmabisheng.constant.Application;
import com.qihoo.huangmabisheng.constant.Constant;
import com.qihoo.huangmabisheng.constant.Constant.Screen;
import com.qihoo.huangmabisheng.httpserver.SpecialHttpServer;
import com.qihoo.huangmabisheng.utils.MyWindowManager;
import com.qihoo.huangmabisheng.utils.Toast;
import com.qihoo.huangmabisheng.view.FloatWindowBigView;
import com.qihoo.huangmabisheng.wifi.WifiAdmin;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

public class SpecialHttpService extends Service {
	final String TAG = "SpecialHttpService";
	SpecialHttpServer httpdServer;
	private PowerManager pm;
	PowerManager.WakeLock wakeLock;
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.WAKE_LOCK_CHANGESTATUS:
				if (!wakeLock.isHeld())
					wakeLock.acquire();
				else
					wakeLock.release();
				break;
			case Constant.OPEN_SCREENLOCK:
//				if (MyWindowManager.isWindowLocked()) {
//					if (!wakeLock.isHeld())
//						wakeLock.acquire();
//					MyWindowManager.getView().openScreenLockAnim(0, 500, null);
//				} else {
//					MyWindowManager.setWindowVisible();// 放在前面比较快
//					Intent mainActivityIntent = new Intent(
//							SpecialHttpService.this, TransparentActivity.class);
//					mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					mainActivityIntent
//							.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//					startActivity(mainActivityIntent);
//					MyWindowManager.getView().closeScreenLockAnim(
//							-FloatWindowBigView.viewWidth, 500, null);
//					if (wakeLock.isHeld())
//						wakeLock.release();
//				}

				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		Toast.show(this, "特殊服务已开启");
		Application.app.setSpecialServiceOnStatus();
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		// 开启httpserver
		try {
			httpdServer = SpecialHttpServer.instance(this,handler);
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						httpdServer.start();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
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
		Toast.show(this, "特殊服务已关闭");
		Application.app.setSpecialServiceOffStatus();
		httpdServer.stop();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
