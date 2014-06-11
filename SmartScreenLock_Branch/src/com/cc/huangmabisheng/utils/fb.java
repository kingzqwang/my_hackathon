package com.cc.huangmabisheng.utils;

import android.app.KeyguardManager;
import android.content.Context;

public class fb {
	public static boolean a(Context paramContext) {
		try {
			KeyguardManager paramKeyguardManager = (KeyguardManager) paramContext
					.getSystemService("keyguard");
			boolean bool = ((Boolean) KeyguardManager.class.getDeclaredMethod(
					"isKeyguardLocked", new Class[0]).invoke(
					paramKeyguardManager, new Object[0])).booleanValue();
			return bool;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return false;
	}

	public static boolean c(Context paramContext) {
		return ((KeyguardManager) paramContext.getSystemService("keyguard"))
				.inKeyguardRestrictedInputMode();
	}
	public static void u(Context paramContext) {
//		Log.e("fd.d", "上锁");
		KeyguardManager localKeyguardManager = (KeyguardManager) paramContext
				.getSystemService("keyguard");
		KeyguardManager.KeyguardLock localKeyguardLock = localKeyguardManager
				.newKeyguardLock("" + System.currentTimeMillis());
		localKeyguardLock.reenableKeyguard();
		// m.a("KeyguardManager", "isKeyGuardLocked : " +
		// a(localKeyguardManager));
		// new Handler().postDelayed(new fc(localKeyguardLock), 5000L);
	}
	public static void d(Context paramContext) {
//		Log.e("fd.d", "解锁");
		KeyguardManager localKeyguardManager = (KeyguardManager) paramContext
				.getSystemService("keyguard");
		KeyguardManager.KeyguardLock localKeyguardLock = localKeyguardManager
				.newKeyguardLock("" + System.currentTimeMillis());
		localKeyguardLock.disableKeyguard();
		// m.a("KeyguardManager", "isKeyGuardLocked : " +
		// a(localKeyguardManager));
		// new Handler().postDelayed(new fc(localKeyguardLock), 5000L);
	}
}
