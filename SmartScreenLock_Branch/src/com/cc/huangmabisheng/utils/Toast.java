package com.cc.huangmabisheng.utils;

import android.content.Context;

public class Toast {
	public static void showShort(Context context,String msg) {
		android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show();
	}
	public static void showLong(Context context,String msg) {
		android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show();
	}
}
