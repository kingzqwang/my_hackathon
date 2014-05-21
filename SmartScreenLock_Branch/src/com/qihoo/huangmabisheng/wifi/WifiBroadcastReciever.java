package com.qihoo.huangmabisheng.wifi;

import com.qihoo.huangmabisheng.constant.Constant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;

public class WifiBroadcastReciever extends BroadcastReceiver {

	Handler handler;
	
	public WifiBroadcastReciever(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().endsWith(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
			Log.d("WTScanResults---->扫描到了可用网络---->",
					"android.net.wifi.SCAN_RESULTS");
//			handler.obtainMessage(Constant.NOTIFY_DATA_CHANGED)
//			.sendToTarget();
			
		} else if (intent.getAction().endsWith(
				WifiManager.WIFI_STATE_CHANGED_ACTION)) {
//			Log.d("WTScanResults----->wifi状态变化--->",
//					"android.net.wifi.WIFI_STATE_CHANGED");
		} else if (intent.getAction().endsWith(
				WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			Log.d("WTScanResults----->网络状态变化---->",
					"android.net.wifi.STATE_CHANGE");
			Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);    
            if (null != parcelableExtra) {    
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;    
                State state = networkInfo.getState();  
                boolean isConnected = state==State.CONNECTED;//当然，这边可以更精确的确定状态  
                if(isConnected){  
//                	handler.obtainMessage(Constant.WIFI_CONNECTED)
//        			.sendToTarget();
                }else{  
                      
                }  
            }   
			
		}
	}

}
