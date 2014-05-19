package com.qihoo.huangmabisheng.constant;

public class Constant {
	public static final int DECIDE_REFRESH = 2;
	public static String APP_NAME;
	public static boolean SAVE = true;
	public static boolean UNSAVE = false;
	/**
	 * 时间段 
	 */
	public enum TimeQuantum {
		BEFORE_SLEEP,WORKING_MORNING,WORKING_AFTERNOON,WORKING_NIGHT,SLEEPING,REST,DEFAULT
	}
	/**
	 * 场景，EARPHONE优先级最高
	 */
	public enum Scene {
		WIFI,DIS_WIFI,EARPHONE,DEFAULT
	}
	/**
	 * 三屏 
	 */
	public enum SizeType {
		TOTAL,QUANTUM,SCENE
	}
	public enum Screen {
		ON,OFF
	}
	public static int NUM_ON_SCREEN = 7;
}
