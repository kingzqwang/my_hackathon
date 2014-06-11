package com.cc.huangmabisheng.utils;

import java.util.Date;

import com.cc.huangmabisheng.R;
import com.cc.huangmabisheng.constant.Constant.TimeQuantum;

public class TimeUtil {
	/**
	 * 时间段对应的
	 */
	public int drawableResource;
	public TimeQuantum timeQuantum;
	public TimeUtil(int drawableResource, TimeQuantum timeQuantum) {
		this.drawableResource = drawableResource;
		this.timeQuantum = timeQuantum;
	}

	public static TimeUtil decideTimeUtilForNow(Date date) {
		// TODO 判断时间段
		int hour = date.getHours();
		if(hour<=2){
			return new TimeUtil(R.drawable.tq_0_1_2, TimeQuantum.TQ_0_1_2);
		} else if (hour<=5) {
			return new TimeUtil(R.drawable.tq_3_4_5, TimeQuantum.TQ_3_4_5);
		}else if (hour<=8) {
			return new TimeUtil(R.drawable.tq_6_7_8, TimeQuantum.TQ_6_7_8);
		}else if(hour<=11){
			if (hour == 10 && (date.getMinutes()==23 || date.getMinutes()==24)) {
				return new TimeUtil(R.drawable.cc_like_u, TimeQuantum.CC_LIKE_U);
			}
			return new TimeUtil(R.drawable.tq_9_10_11, TimeQuantum.TQ_9_10_11);
		}else if(hour<=15){
			return new TimeUtil(R.drawable.tq_12_13_14_15, TimeQuantum.TQ_12_13_14_15);
		}else if(hour<=19){
			return new TimeUtil(R.drawable.tq_16_17_18_19, TimeQuantum.TQ_16_17_18_19);
		}else{
			return new TimeUtil(R.drawable.tq_20_21_22_23, TimeQuantum.TQ_20_21_22_23);
		}
	}
	
}
