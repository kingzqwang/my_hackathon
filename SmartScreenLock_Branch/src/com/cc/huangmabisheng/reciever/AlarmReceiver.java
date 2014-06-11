package com.cc.huangmabisheng.reciever;

import com.cc.huangmabisheng.utils.Log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver{
	final String TAG = "AlarmReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		Log.e(TAG, "AlarmReceiver recieved");
	}
	
}
