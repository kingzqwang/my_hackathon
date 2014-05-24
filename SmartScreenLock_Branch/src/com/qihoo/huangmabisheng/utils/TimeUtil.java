package com.qihoo.huangmabisheng.utils;

import java.util.Date;


import com.qihoo.huangmabisheng.constant.Constant.TimeQuantum;

public class TimeUtil {
	public static TimeQuantum decideTimeQuantumForNow(Date date) {
		String TAG = "TimeQuantum";
		// TODO 判断时间段
		int hour = date.getHours();
		if(hour<2){
			Log.d(TAG, "BEFORE_SLEEP");
			return TimeQuantum.BEFORE_SLEEP;
		} else if (hour<7) {
			Log.d(TAG, "SLEEPING");
			return TimeQuantum.SLEEPING;
		}else if (hour == 12 || hour == 19) {
			Log.d(TAG, "REST");
			return TimeQuantum.REST;
		}else if(hour<12){
			Log.d(TAG, "WORKING_MORNING");
			return TimeQuantum.WORKING_MORNING;
		}else if(hour<19){
			Log.d(TAG, "WORKING_AFTERNOON");
			return TimeQuantum.WORKING_AFTERNOON;
		}else {
			Log.d(TAG, "WORKING_NIGHT");
			return TimeQuantum.WORKING_NIGHT;
		}
	}
}
