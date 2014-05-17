package com.qihoo.huangmabisheng.utils;

import java.util.Date;


import com.qihoo.huangmabisheng.constant.Constant.TimeQuantum;

public class TimeUtil {
	public static TimeQuantum decideTimeQuantumForNow(Date date) {
		String TAG = "TimeQuantum";
		// TODO 判断时间段
		int hour = date.getSeconds();
		if (hour%20>=10) {
			Log.d(TAG, "WORKING");
			return TimeQuantum.WORKING;
		}else {
			Log.d(TAG, "SLEEPING");
			return TimeQuantum.SLEEPING;
		}
	}
}
