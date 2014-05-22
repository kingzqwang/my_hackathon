package com.qihoo.huangmabisheng.utils;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class ScreenUtil {
	/**
	 * 获取和保存当前屏幕的截图
	 */
	public static void GetandSaveCurrentImage(Activity activity) {
		// 构建Bitmap
		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int w = display.getWidth();
		int h = display.getHeight();
		Bitmap Bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		// 获取屏幕
		View decorview = activity.getWindow().getDecorView();
		decorview.setDrawingCacheEnabled(true);
		Bmp = decorview.getDrawingCache();
		// 图片存储路径
		String SavePath = getSDCardPath() + "/Demo/ScreenImages";
		Log.d("GetandSaveCurrentImage", SavePath);
		// 保存Bitmap
		try {
			File path = new File(SavePath);
			// 文件
			String filepath = SavePath + "/Screen_1.png";
			File file = new File(filepath);
			if (!path.exists()) {
				path.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = null;
			fos = new FileOutputStream(file);
			if (null != fos) {
				Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
//				Toast.makeText(mContext, "截屏文件已保存至SDCard/ScreenImages/目录下",
//						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取SDCard的目录路径功能
	 * 
	 * @return
	 */
	private static String getSDCardPath() {
		File sdcardDir = null;
		// 判断SDCard是否存在
		boolean sdcardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdcardExist) {
			sdcardDir = Environment.getExternalStorageDirectory();
		}
		return sdcardDir.toString();
	}
}
