package com.cc.huangmabisheng.utils;

import com.cc.huangmabisheng.constant.Application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.http.SslCertificate.DName;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	String TAG  = "MySQLiteOpenHelper";
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	public MySQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		Log.e(TAG, "MySQLiteOpenHelper()");
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.execSQL("CREATE TABLE appdatas (DB_COLUMN_PACKAGENAME text PRIMARY KEY,DB_COLUMN_COMPONENTNAME text,DB_COLUMN_COUNTS integer)");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.close();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.e(TAG, "onUpgrade");
	}
}
