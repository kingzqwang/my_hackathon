package com.cc.huangmabisheng.constant;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.cc.huangmabisheng.R;

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
		DEFAULT,TQ_0_1_2, TQ_3_4_5, TQ_6_7_8, TQ_9_10_11, TQ_12_13_14_15, TQ_16_17_18_19, TQ_20_21_22_23,CC_LIKE_U
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
		alertFilter.add("com.iooly.android.lockscreen");
		alertFilter.add("com.miui.home");
		alertFilter.add("com.qigame.lock");
		alertFilter.add("com.wandoujia.roshan");
		alertFilter.add("com.cleanmaster.locker");
		alertFilter.add("com.lbe.security.miui");
		alertFilter.add("com.google.android.gsf.login");
		alertFilter.add("com.android.phone");
		alertFilter.add("com.android.systemui");
		alertFilter.add("com.android.packageinstaller");
		alertFilter.add("com.cc.huangmabisheng");
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
	public static final long REFRESH_TIME = 1000;
	public static final long SCHEDULE_TIME = 1000;
	public static final long ANIMATION_TIME = 200;
	public static final long OPENSCREEN_TIME = 200;
	public static final long CLOSESCREEN_TIME = 200;
	public static final long VIBRATE_TIME = 5;
	public static final long VIBRATE_TIME_PHOTO = 200;
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
	
	public static final String[] APP_INIT_STRINGS = {
		"by.game.binumbers",
		"com.baidu.BaiduMap",
		"com.qihoo.appstore",
		"com.netease.newsreader.activity",
		"com.miantan.myoface",
		"com.sina.weibo",
		"com.taobao.taobao",
		"com.tencent.mobileqq",
		"com.tencent.mm",
		"com.qihoo360.mobilesafe",
		};
	public static final String[] WORLD_CUP_DRAWABLE_NAME ={
		"",
		"阿尔及利亚",
		"阿根廷",
		"澳大利亚",
		"巴    西",
		"比利时",
		"波    黑",
		"德    国",
		"厄瓜多尔",
		"俄罗斯",
		"法    国",
		"哥伦比亚",
		"哥斯达黎加",
		"韩    国",
		"荷    兰",
		"洪都拉斯",
		"加    纳",
		"喀麦隆",
		"克罗地亚",
		"科特迪瓦",
		"美    国",
		"墨西哥",
		"尼日利亚",
		"葡萄牙",
		"日    本",
		"瑞    士",
		"乌拉圭",
		"西班牙",
		"希    腊",
		"意大利",
		"伊    朗",
		"英格兰",
		"智    利"
	};
	public static final int[] WORLD_CUP_DRAWABLE_SRC = {
		-1,
		R.drawable.aerjiliya,
		R.drawable.agenting,
		R.drawable.aodaliya,
		R.drawable.baxi,
		R.drawable.bilishi,
		R.drawable.bohei,
		R.drawable.deguo,
		R.drawable.eguaduoer,
		R.drawable.eluosi,
		R.drawable.faguo,
		R.drawable.gelunbiya,
		R.drawable.gesidalijia,
		R.drawable.hanguo,
		R.drawable.helan,
		R.drawable.hongdulasi,
		R.drawable.jiana,
		R.drawable.kamailong,
		R.drawable.keluodiya,
		R.drawable.ketediwa,
		R.drawable.meiguo,
		R.drawable.moxige,
		R.drawable.niriliya,
		R.drawable.putaoya,
		R.drawable.riben,
		R.drawable.ruishi,
		R.drawable.wulagui,
		R.drawable.xibanya,
		R.drawable.xila,
		R.drawable.yidali,
		R.drawable.yilang,
		R.drawable.yinggelan,
		R.drawable.zhili
	};
}
