package com.qihoo.huangmabisheng.utils;

import android.content.Context;

public class Toast {
	public static void show(Context context,String msg) {
		android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show();
	}
}
