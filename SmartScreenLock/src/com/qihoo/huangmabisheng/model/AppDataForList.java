package com.qihoo.huangmabisheng.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.ComponentName;

import com.qihoo.huangmabisheng.constant.Constant;
import com.qihoo.huangmabisheng.constant.Constant.TimeQuantum;
import com.qihoo.huangmabisheng.utils.TimeUtil;

public class AppDataForList implements Serializable {

	public AppDataForList(String packageName, ComponentName currentCompoment , TimeQuantum timeQuantum) {
		super();
		this.packageName = packageName;
		this.currentCompoment = currentCompoment;
		push(timeQuantum);
	}

	/**
	 * 该应用的包名
	 */
	public String packageName;
	/**
	 * 该应用若为打开状态，该字段不为null，显示当前该应用最顶activity。
	 */
	public ComponentName currentCompoment;
	/**
	 * 每个时间段应用打开的次数
	 */
	private Map<TimeQuantum, Integer> countsForEveryTime = new HashMap<Constant.TimeQuantum, Integer>();
	/**
	 * 打开总次数，禁止直接获取来 + -
	 */
	private int size = 0;

	/**
	 * 以当前时间段为基本push进countsForEveryTime，或将使用次数+1，此方法为this同步方法
	 */
	synchronized public void push(TimeQuantum timeQuantum) {
		int count = 1;
		if (countsForEveryTime.containsKey(timeQuantum)) {
			count = countsForEveryTime.get(timeQuantum);
			count++;
		}
		countsForEveryTime.put(timeQuantum, count);
		size++;
	}

	/**
	 * 获取总数，此方法为this同步方法
	 */
	synchronized public int size(Constant.TimeQuantum timeQuantum) {
			Integer c = countsForEveryTime.get(timeQuantum);
			return c == null ? 0 : c;
	}

	synchronized public int size() {
		return size;
	}
}
