package com.qihoo.huangmabisheng.wifi;

import com.qihoo.huangmabisheng.constant.Constant;
import com.qihoo.huangmabisheng.utils.Log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Parcelable;

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
		} else if (intent.getAction().endsWith(
				WifiManager.WIFI_STATE_CHANGED_ACTION)) {
		} else if (intent.getAction().endsWith(
				WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			Log.d("WTScanResults----->网络状态变化---->",
					"android.net.wifi.STATE_CHANGE");
			Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);    
            if (null != parcelableExtra) {    
				handler.obtainMessage(Constant.WIFI_CONNECTED).sendToTarget();
            }   
			
		}
	}

}
