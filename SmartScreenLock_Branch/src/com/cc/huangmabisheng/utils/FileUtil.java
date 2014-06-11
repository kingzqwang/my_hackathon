package com.cc.huangmabisheng.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cc.huangmabisheng.constant.Application;
import com.cc.huangmabisheng.constant.Constant;
import com.cc.huangmabisheng.constant.SharedPrefrencesAssist;
import com.cc.huangmabisheng.constant.Constant.TimeQuantum;
import com.cc.huangmabisheng.model.AppDataForList;
import com.cc.huangmabisheng.model.AppIntroMap;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class FileUtil {
	private Context context;

	/**
	 * Construct
	 * 
	 * @param Context
	 * @author wangzhiqing-xy
	 * 
	 * */
	public FileUtil(Context context) {
		super();
		this.context = context;

	}

	/**
	 * save file
	 * 
	 * @param filename
	 * @param content
	 * */
	public void save(String filename, String content) {
		FileOutputStream outstream;
		try {
			outstream = context.openFileOutput(filename, Context.MODE_APPEND);
			outstream.write(content.getBytes());
			outstream.write("\r\n".getBytes());
			outstream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param filename
	 * @return content
	 * @throws IOException
	 * @throws Exception
	 * */
	public String read(String filename) throws IOException {
		FileInputStream instream = context.openFileInput(filename);
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = instream.read(buffer)) != -1) {
			outstream.write(buffer, 0, len);
		}
		byte[] data = outstream.toByteArray();
		return new String(data);

	}

	public void read(AppIntroMap app_pre) {
		SQLiteDatabase db = Application.app.myHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM appdatas", null);
		TimeQuantum timeQuantum = TimeUtil.decideTimeUtilForNow(new Date()).timeQuantum;
		while (cursor.moveToNext()) {
			String packageName = cursor.getString(cursor
					.getColumnIndex(Constant.DB_COLUMN_PACKAGENAME));
			String componentNameStr = cursor.getString(cursor
					.getColumnIndex(Constant.DB_COLUMN_COMPONENTNAME));
			int count = cursor.getInt(cursor
					.getColumnIndex(Constant.DB_COLUMN_COUNTS));
			ComponentName componentName;
			if (componentNameStr.equals("")) {
				componentName = null;
			} else {
				componentName = ComponentName
						.unflattenFromString(componentNameStr);
			}
			AppDataForList appDataForList = new AppDataForList(packageName,
					componentName);
			appDataForList.pushReplace(timeQuantum, count);
			app_pre.put(packageName, appDataForList);
			app_pre.updateData(packageName, timeQuantum);
		}
		db.close();

	}

	public void save(AppDataForList appData) {
		SQLiteDatabase db = Application.app.myHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Constant.DB_COLUMN_PACKAGENAME, appData.packageName);
		if (appData.currentCompoment != null)
			values.put(Constant.DB_COLUMN_COMPONENTNAME,
					appData.currentCompoment.flattenToString());
		else
			values.put(Constant.DB_COLUMN_COMPONENTNAME, "");
		values.put(Constant.DB_COLUMN_COUNTS, appData.size());
		db.insertWithOnConflict("appdatas", null, values,
				SQLiteDatabase.CONFLICT_REPLACE);
		db.close();
	}

	static public File getSDFileDir() {
		// File sdDir = null;
		// boolean sdCardExist = Environment.getExternalStorageState().equals(
		// android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		// if (sdCardExist){ // 如果SD卡存在，则获取跟目录
		// sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		// }
		return Environment.getExternalStorageDirectory();

	}
}
