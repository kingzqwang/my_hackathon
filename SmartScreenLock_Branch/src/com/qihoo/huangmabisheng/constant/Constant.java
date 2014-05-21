package com.qihoo.huangmabisheng.constant;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Constant {
	public static final int DECIDE_REFRESH = 2;
	public static final int PORT = 80;
	public static String APP_NAME;
	public final static boolean SAVE = true;
	public final static boolean UNSAVE = false;

	/**
	 * 时间段
	 */
	public enum TimeQuantum {
		BEFORE_SLEEP, WORKING_MORNING, WORKING_AFTERNOON, WORKING_NIGHT, SLEEPING, REST, DEFAULT
	}

	/**
	 * 场景，EARPHONE优先级最高
	 */
	public enum Scene {
		WIFI, DIS_WIFI, EARPHONE, DEFAULT
	}

	/**
	 * 三屏
	 */
	public enum SizeType {
		TOTAL, QUANTUM, SCENE
	}

	public enum Screen {
		ON, OFF
	}

	public final static int NUM_ON_SCREEN = 7;

	public enum WIFI_STATUS {
		OPENED, CLOSED
	}

	public static final Set<String> alertFilter = new HashSet<String>();
	static{
		alertFilter.add("com.android.packageinstaller");
		alertFilter.add("com.qihoo.huangmabisheng");
		alertFilter.add("android");
	}
}
