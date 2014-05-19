package com.qihoo.huangmabisheng.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.qihoo.huangmabisheng.R;
import com.qihoo.huangmabisheng.model.AppInfoForFilter;
import com.qihoo.huangmabisheng.service.FloatWindowService;
import com.qihoo.huangmabisheng.service.SmartLockService;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class FilterApplicationActivity extends BaseActivity {
	ListView appListView;
	ImageView imageView;
	ListAdapter listAdapter;
	List<AppInfoForFilter> appInfos = new ArrayList<AppInfoForFilter>();
	LayoutInflater layoutInflater;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.filter_application);
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void handleMsg(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setAllListeners() {
		appListView.setAdapter(listAdapter);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FilterApplicationActivity.this.finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void findAllViews() {
		layoutInflater = LayoutInflater.from(this);
		appListView = (ListView)findViewById(R.id.application_list);
		imageView = (ImageView)findViewById(R.id.back_imageview);
		appInfoProvider();
		listAdapter = new BaseAdapter() {
			
			OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					AppInfoForFilter appInfo = (AppInfoForFilter)buttonView.getTag();
					if (isChecked) {
						Log.d(TAG,"remove");
						appInfo.isFiltered = false;
						SmartLockService.filterMap.remove(appInfo.packageName);
						buttonView.setButtonDrawable(R.drawable.rect_bth_checkbox_normal);
					}else {
						Log.d(TAG,"add");
						appInfo.isFiltered = true;
						SmartLockService.filterMap.put(appInfo.packageName,0);
						buttonView.setButtonDrawable(R.drawable.rect_btn_checkbox_normal);
					}
					appListView.invalidate();
				}
			};
			class Tag{
				TextView info;
				TextView desc;
				CheckBox checkBox;
				ImageView imageView;
				public Tag(TextView info, TextView desc, CheckBox checkBox,
						ImageView imageView) {
					super();
					this.info = info;
					this.desc = desc;
					this.checkBox = checkBox;
					this.imageView = imageView;
				}
				
			}
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				AppInfoForFilter appInfo = appInfos.get(position);
				TextView info;
				TextView desc;
				CheckBox checkBox;
				ImageView imageView;
				if (convertView == null) {
					convertView = layoutInflater.inflate(R.layout.application_list_item, null);
					info = (TextView) convertView.findViewById(R.id.app_name);
					desc = (TextView) convertView.findViewById(R.id.skinname);
					checkBox = (CheckBox)convertView.findViewById(R.id.filter_checkbox);
					imageView = (ImageView)convertView.findViewById(R.id.app_icon);
					
					Tag tag = new Tag(info, desc, checkBox,imageView);
					convertView.setTag(tag);
				}else {
					Tag tag = (Tag)convertView.getTag();
					info = (TextView) tag.info;
					desc = (TextView) tag.desc;
					checkBox = (CheckBox)tag.checkBox;
					imageView = (ImageView)tag.imageView;
				}
				checkBox.setTag(appInfo);
				info.setText(appInfo.appName);
				Log.d(TAG,SmartLockService.filterMap.size()+"");
				if (!SmartLockService.filterMap.containsKey(appInfo.packageName)) {
					checkBox.setButtonDrawable(R.drawable.rect_bth_checkbox_normal);
					desc.setText("允许在锁屏里显示");
					imageView.setImageDrawable(appInfo.appIcon);
				}else {
					checkBox.setButtonDrawable(R.drawable.rect_btn_checkbox_normal);
					desc.setText("禁止在锁屏里显示");
					imageView.setImageDrawable(appInfo.appIcon);
				}
				checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
				return convertView;
			}
			
			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return appInfos.size();
			}
		};
	}
	/**
	 * @param void
	 * @return ArrayList<AppInfo>
	 * 
	 * */
	private void appInfoProvider() {

		List<PackageInfo> packages = getPackageManager()
				.getInstalledPackages(0);

		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			// if (appNameList.contains(packageInfo.applicationInfo.loadLabel(
			// getPackageManager()).toString()))
			// continue;
			// else
			// appNameList.add(packageInfo.applicationInfo.loadLabel(
			// getPackageManager()).toString());
			AppInfoForFilter tmpInfo = new AppInfoForFilter();
			tmpInfo.appName = packageInfo.applicationInfo.loadLabel(
					getPackageManager()).toString();
			tmpInfo.packageName = packageInfo.packageName;
			tmpInfo.versionName = packageInfo.versionName;
			tmpInfo.versionCode = packageInfo.versionCode;
			tmpInfo.appIcon = packageInfo.applicationInfo
					.loadIcon(getPackageManager());
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && !tmpInfo.packageName.equals("com.qihoo.huangmabisheng")) {

				if (null == SmartLockService.filterMap.get(tmpInfo)){
					tmpInfo.isFiltered = false;
				}else {
					tmpInfo.isFiltered = true;
				}

				appInfos.add(tmpInfo);
				// if (myAppNames.contains(tmpInfo.packageName))
				// continue;
				// else {
				// myAppNames.add(tmpInfo.packageName);
				// Log.v("app", tmpInfo.packageName);
				// }
			}
		}
	}

}
