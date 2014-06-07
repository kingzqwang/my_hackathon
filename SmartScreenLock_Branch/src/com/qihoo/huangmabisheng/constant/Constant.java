package com.qihoo.huangmabisheng.constant;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Constant {
	public static final int DECIDE_REFRESH = 2;
	public static final int PORT = 8081;
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
		alertFilter.add("com.lbe.security.miui");
		alertFilter.add("com.google.android.gsf.login");
		alertFilter.add("com.android.phone");
		alertFilter.add("com.android.systemui");
		alertFilter.add("com.android.packageinstaller");
		alertFilter.add("com.qihoo.huangmabisheng");
		alertFilter.add("android");
		alertFilter.add("com.miui.networkassistant");
	}
	public static final int WAKE_LOCK_CHANGESTATUS = 5212314;
	public static final int OPEN_SCREENLOCK = 5212348;
	public static final int UPDATE_TIME = 5232042;
	public static final int UPDATE_IPADDRESS = 5232043;
	public static final String PARAM_WAKE_LOCK = "0";
	public static final String PARAM_AUTO_FOCUS = "2";
	public static final String PARAM_CHANGE_SCREENLOCK = "1";
	public static final String PARAM_CATCH_PIC = "3";
	public static final int SERVER_CAMERA_FOCUS = 5241907;
	public static final String PARAM_TAKEPHOTO = "4";
	public static final int SERVER_TAKE_PHOTO = 5242109;
	public static final int WIFI_CONNECTED = 5261235;
	public static final long REFRESH_TIME = 20000;
	public static final long SCHEDULE_TIME = 1000;
	public static final long ANIMATION_TIME = 200;
	public static final long OPENSCREEN_TIME = 200;
	public static final long CLOSESCREEN_TIME = 300;
	public static final long VIBRATE_TIME = 5;
	public static final String SCREEN_PHOTO_IMAGEVIEW = "SCREEN_PHOTO_IMAGEVIEW";
	public static final String SCREEN_CLEAN = "SCREEN_CLEAN";
	public static final Object TAG_SCREEN_PHOTO = 32966;
	public static final String SCREEN_OPEN_COUNT = "SCREEN_OPEN_COUNT";
	
	
	public static final String DB_COLUMN_PACKAGENAME= "DB_COLUMN_PACKAGENAME";
	public static final String DB_COLUMN_COMPONENTNAME= "DB_COLUMN_COMPONENTNAME";
	public static final String DB_COLUMN_COUNTS= "DB_COLUMN_COUNTS";
	public static final String DB_COMLUMN_ID = "DB_COMLUMN_ID";
	public static final String SCREEN_CLEAN_IMAGEVIEW = "SCREEN_CLEAN_IMAGEVIEW";
	public static final String WORLD_CUP_IMAGEVIEW = "WORLD_CUP_IMAGEVIEW";
}
