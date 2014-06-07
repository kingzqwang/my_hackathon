package com.qihoo.huangmabisheng.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.qihoo.huangmabisheng.constant.Constant;
import com.qihoo.huangmabisheng.constant.Constant.Scene;
import com.qihoo.huangmabisheng.constant.Constant.SizeType;
import com.qihoo.huangmabisheng.constant.Constant.TimeQuantum;
import com.qihoo.huangmabisheng.utils.TimeUtil;


/**
 * 本身就囊括了所有最近打开的app 
 */
public class AppIntroMap extends HashMap<String, AppDataForList> {
	Context context;
	public ArrayList<AppDataForList> appDatas = new ArrayList<AppDataForList>(Constant.NUM_ON_SCREEN);//最常开启的应用
	//public Map<TimeQuantum, ArrayList<AppDataForList>> appDatasNowMap = new HashMap<Constant.TimeQuantum, ArrayList<AppDataForList>>();//本时间段最常开启6个开启的应用
	public ArrayList<AppDataForList> appDatasScene = new ArrayList<AppDataForList>(Constant.NUM_ON_SCREEN);//场景推荐
	ValueComparator vc = new ValueComparator();
	public AppIntroMap(Context context) {
		this.context = context;
//		appDatasNowMap.put(TimeQuantum.BEFORE_SLEEP, new ArrayList<AppDataForList>(Constant.NUM_ON_SCREEN));
//		appDatasNowMap.put(TimeQuantum.REST, new ArrayList<AppDataForList>(Constant.NUM_ON_SCREEN));
//		appDatasNowMap.put(TimeQuantum.SLEEPING, new ArrayList<AppDataForList>(Constant.NUM_ON_SCREEN));
//		appDatasNowMap.put(TimeQuantum.WORKING_MORNING, new ArrayList<AppDataForList>(Constant.NUM_ON_SCREEN));
//		appDatasNowMap.put(TimeQuantum.WORKING_AFTERNOON, new ArrayList<AppDataForList>(Constant.NUM_ON_SCREEN));
//		appDatasNowMap.put(TimeQuantum.WORKING_NIGHT, new ArrayList<AppDataForList>(Constant.NUM_ON_SCREEN));
	}
	
	private void updateAppDatasScene(Scene scene) {
		appDatasScene = new ArrayList<AppDataForList>(Constant.NUM_ON_SCREEN);
		switch (scene) {
		case EARPHONE:
			PackageManager packageManager = context.getPackageManager();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri u = Uri.parse("file:///test.mp3");
            intent.setDataAndType(u, "audio/*");
			List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
					intent, 0);
			Log.d("updateAppDatasScene", resolveInfo.size()+"");
			for (int i=0;i<resolveInfo.size();i++) {
				if (i==Constant.NUM_ON_SCREEN) {
					break;
				}
				appDatasScene.add(new AppDataForList(resolveInfo.get(i).activityInfo.packageName, null));
			}
			break; 

		default:
			
			break;
		}
	}
	
	public void updateData(String packageName,TimeQuantum timeQuantum) {
		Log.e("updateData","共统计app"+this.size());
		AppDataForList app = this.get(packageName);
		if (appDatas.contains(app)) {
			Log.e("updateData","appDatas.contains");
			int no = appDatas.indexOf(app);
			appDatas.remove(no);
			insertAndSort(no-1,app);
//			Collections.sort(appDatas,vc);
		}else if (appDatas.size() == Constant.NUM_ON_SCREEN) {//已满
			Log.e("updateData","已满");
			if (appDatas.get(appDatas.size()-1).size() <= app.size()) {
				appDatas.remove(appDatas.size()-1);
				insertAndSort(appDatas.size()-1,app);
//				Collections.sort(appDatas,vc);
			}
		}else {//未满
			insertAndSort(appDatas.size()-1, app);
//			appDatas.add(app);
//			Collections.sort(appDatas,vc);
		}
		//updateDataNow(packageName,timeQuantum);
	}
	private void insertAndSort(int i,AppDataForList app) {
		for (;i >= 0; i--) {
			if (appDatas.get(i).size()>app.size()) {
				appDatas.add(i+1, app);
				break;
			}
		}
		if (-1 == i) {
			appDatas.add(0,app);
		}
	}
//	private void updateDataNow(String packageName,TimeQuantum timeQuantum) {
//		AppDataForList appData = this.get(packageName);
//		int i = 0;
//		AppDataForList[] appDatasNow = appDatasNowMap.get(timeQuantum);
//		for (; i < Constant.NUM_ON_SCREEN; i++) {
//			if (appDatasNow[i] == null) {
//				break;
//			}
//			if (appDatasNow[i].packageName.equals(appData.packageName)) {
//				shiftDown(appDatasNow, i,timeQuantum);
//				return;
//			}
//		}
//		if (i < Constant.NUM_ON_SCREEN) {
//			appDatasNow[i] = appData;
//			shiftUp(appDatasNow, i,timeQuantum);
//		}else if (appDatasNow[0].size(timeQuantum) < appData.size(timeQuantum)) {
//			appDatasNow[0] = appData;
//			shiftDown(appDatasNow, 0,timeQuantum);
//		}
//	}
	

	
	class ValueComparator implements Comparator<AppDataForList> {

		@Override
		public int compare(AppDataForList lhs, AppDataForList rhs) {
			// TODO Auto-generated method stub
			return rhs.size()-lhs.size();
		}
	}
	
}
