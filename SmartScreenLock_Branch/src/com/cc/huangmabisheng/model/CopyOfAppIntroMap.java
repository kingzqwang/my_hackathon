package com.cc.huangmabisheng.model;
//package com.cc.huangmabisheng.model;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.content.pm.ResolveInfo;
//import android.net.Uri;
//import android.provider.MediaStore;
//import android.util.Log;
//
//import com.cc.huangmabisheng.constant.Constant;
//import com.cc.huangmabisheng.constant.Constant.Scene;
//import com.cc.huangmabisheng.constant.Constant.SizeType;
//import com.cc.huangmabisheng.constant.Constant.TimeQuantum;
//import com.cc.huangmabisheng.utils.TimeUtil;
//
//
///**
// * 本身就囊括了所有最近打开的app 
// */
//public class CopyOfAppIntroMap extends HashMap<String, AppDataForList> {
//	Context context;
//	public AppDataForList[] appDatas = new AppDataForList[Constant.NUM_ON_SCREEN];//最常开启的6个应用
//	public Map<TimeQuantum, AppDataForList[]> appDatasNowMap = new HashMap<Constant.TimeQuantum, AppDataForList[]>();//本时间段最常开启6个开启的应用
//	public AppDataForList[] appDatasScene = new AppDataForList[Constant.NUM_ON_SCREEN];//场景推荐
//	public CopyOfAppIntroMap(Context context) {
//		this.context = context;
//		appDatasNowMap.put(TimeQuantum.BEFORE_SLEEP, new AppDataForList[Constant.NUM_ON_SCREEN]);
//		appDatasNowMap.put(TimeQuantum.REST, new AppDataForList[Constant.NUM_ON_SCREEN]);
//		appDatasNowMap.put(TimeQuantum.SLEEPING, new AppDataForList[Constant.NUM_ON_SCREEN]);
//		appDatasNowMap.put(TimeQuantum.WORKING_MORNING, new AppDataForList[Constant.NUM_ON_SCREEN]);
//		appDatasNowMap.put(TimeQuantum.WORKING_AFTERNOON, new AppDataForList[Constant.NUM_ON_SCREEN]);
//		appDatasNowMap.put(TimeQuantum.WORKING_NIGHT, new AppDataForList[Constant.NUM_ON_SCREEN]);
//	}
//	
//	public void updateAppDatasScene(Scene scene) {
//		appDatasScene = new AppDataForList[Constant.NUM_ON_SCREEN];
//		switch (scene) {
//		case EARPHONE:
//			PackageManager packageManager = context.getPackageManager();
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//			Uri u = Uri.parse("file:///test.mp3");
//            intent.setDataAndType(u, "audio/*");
//			List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
//					intent, 0);
//			Log.d("updateAppDatasScene", resolveInfo.size()+"");
//			for (int i=0;i<resolveInfo.size();i++) {
//				if (i==Constant.NUM_ON_SCREEN) {
//					break;
//				}
//				appDatasScene[i] = new AppDataForList(resolveInfo.get(i).activityInfo.packageName, null);
//			}
//			break;
//
//		default:
//			
//			break;
//		}
//	}
//	
//	public void updateData(String packageName,TimeQuantum timeQuantum) {
//		Log.d("updateData","共统计app"+this.size());
//		try {
//			AppDataForList appData = this.get(packageName);
//			int i = 0;
//			for (; i < Constant.NUM_ON_SCREEN; i++) {
//				if (appDatas[i] == null) {
//					break;
//				}
//				if (appDatas[i].packageName.equals(appData.packageName)) {
//					shiftDown(appDatas, i);
//					return;
//				}
//			}
//			if (i < Constant.NUM_ON_SCREEN) {
//				appDatas[i] = appData;
//				shiftUp(appDatas, i);
//			}else if (appDatas[0].size() < appData.size()) {
//				appDatas[0] = appData;
//				shiftDown(appDatas, 0);
//			}
//		} finally {
//			updateDataNow(packageName,SizeType.QUANTUM,timeQuantum);
//		}
//		
//	}
//
//	private void updateDataNow(String packageName,Constant.SizeType sizeType,TimeQuantum timeQuantum) {
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
//	
//
//	/**
//	 * 做最小堆
//	 */
//	private void topFreqList() {
//		if (0 == this.size()) {
//			return;
//		}
//		int len = Constant.NUM_ON_SCREEN;
//		Iterator<Entry<String, AppDataForList>> iterator = this.entrySet()
//				.iterator();
//		int i = 0;
//		appDatas[i++] = iterator.next().getValue();
//		for (; iterator.hasNext();) {
//			Entry<String, AppDataForList> entry = iterator.next();
//			if (i >= len) {// 若堆已满，则将判断小顶和目标哪个大
//				if (entry.getValue().size() > appDatas[0].size()) {// 比小顶大，相同不要换
//					appDatas[0] = entry.getValue();
//					shiftDown(appDatas, 0);
//				}
//			} else {
//				appDatas[i] = entry.getValue();
//				shiftUp(appDatas, i);
//			}
//			i++;
//		}
//	}
//
//	private void shiftDown(AppDataForList[] appDatas, int top) {
//		int i = top;
//		AppDataForList curr = null;
//		while (true) {
//			if (i >= Constant.NUM_ON_SCREEN/2) {
//				break;
//			}
//			int t;
//			AppDataForList left = appDatas[2 * i + 1];
//			AppDataForList right = appDatas[2 * i + 2];
//			if (left == null) {
//				break;
//			} else if (right == null) {
//				t = 2 * i + 1;
//			} else if (left.size() < right.size()) {
//				t = 2 * i + 1;
//			} else {
//				t = 2 * i + 2;
//			}
//			curr = appDatas[t];
//			if (curr.size() < appDatas[i].size()) {
//				appDatas[t] = appDatas[i];
//				appDatas[i] = curr;
//				i = t;
//			} else {
//				break;
//			}
//		}
//	}
//
//	private void shiftUp(AppDataForList[] appDatas, int tail) {
//		AppDataForList curr = null;
//		while (0 != tail) {
//			int parent = (tail - 1) / 2;
//			curr = appDatas[tail];
//			if (appDatas[parent].size() > curr.size()) {// 如果父节点比当前节点大，则换
//				appDatas[tail] = appDatas[parent];
//				appDatas[parent] = curr;
//				tail = parent;
//			} else {
//				break;
//			}
//		}
//	}
//	private void shiftDown(AppDataForList[] appDatas, int top,TimeQuantum timeQuantum) {
//		int i = top;
//		AppDataForList curr = null;
//		while (true) {
//			if (i >= Constant.NUM_ON_SCREEN/2) {
//				break;
//			}
//			int t;
//			AppDataForList left = appDatas[2 * i + 1];
//			AppDataForList right = appDatas[2 * i + 2];
//			if (left == null) {
//				break;
//			} else if (right == null) {
//				t = 2 * i + 1;
//			} else if (left.size(timeQuantum) < right.size(timeQuantum)) {
//				t = 2 * i + 1;
//			} else {
//				t = 2 * i + 2;
//			}
//			curr = appDatas[t];
//			if (curr.size(timeQuantum) < appDatas[i].size(timeQuantum)) {
//				appDatas[t] = appDatas[i];
//				appDatas[i] = curr;
//				i = t;
//			} else {
//				break;
//			}
//		}
//	}
//
//	private void shiftUp(AppDataForList[] appDatas, int tail,TimeQuantum timeQuantum) {
//		AppDataForList curr = null;
//		while (0 != tail) {
//			int parent = (tail - 1) / 2;
//			curr = appDatas[tail];
//			if (appDatas[parent].size(timeQuantum) > curr.size(timeQuantum)) {// 如果父节点比当前节点大，则换
//				appDatas[tail] = appDatas[parent];
//				appDatas[parent] = curr;
//				tail = parent;
//			} else {
//				break;
//			}
//		}
//	}
//}
