package com.qihoo.huangmabisheng.activity;

import java.util.List;

import com.qihoo.huangmabisheng.R;
import com.qihoo.huangmabisheng.constant.SharedPrefrencesAssist;
import com.qihoo.huangmabisheng.service.FloatWindowService;
import com.qihoo.huangmabisheng.service.SmartLockService;
import com.qihoo.huangmabisheng.utils.MyWindowManager;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingActivity extends BaseActivity {
	CheckBox startCheckBox;
	ViewGroup filterApplicationLayout;
	CheckBox handCheckBox;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.setting);
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void handleMsg(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setAllListeners() {
		if (isServiceRunning(this,
				"com.qihoo.huangmabisheng.service.FloatWindowService")) {
		}else {
			Intent intent = new Intent(SettingActivity.this,
					FloatWindowService.class);
			SettingActivity.this.startService(intent);
			Intent intent1 = new Intent(SettingActivity.this,
					SmartLockService.class);
			SettingActivity.this.startService(intent1);
			startCheckBox.setChecked(true);
		}
		startCheckBox.setChecked(true);
		startCheckBox.setButtonDrawable(R.drawable.rect_bth_checkbox_normal);
		startCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					Log.d(TAG, true + "");
					buttonView
							.setButtonDrawable(R.drawable.rect_bth_checkbox_normal);
					// TODO 开启服务，控制系统锁屏开锁
					// Intent intent = new Intent(SettingActivity.this,
					// MainActivity.class);
					// SettingActivity.this.startActivity(intent);
					Intent intent = new Intent(SettingActivity.this,
							FloatWindowService.class);
					SettingActivity.this.startService(intent);
					Intent intent1 = new Intent(SettingActivity.this,
							SmartLockService.class);
					SettingActivity.this.startService(intent1);
				} else {
					Log.d(TAG, false + "");
					buttonView
							.setButtonDrawable(R.drawable.rect_btn_checkbox_normal);
					// TODO 停止服务
					Intent intent = new Intent(SettingActivity.this,
							FloatWindowService.class);
					SettingActivity.this.stopService(intent);
					Intent intent1 = new Intent(SettingActivity.this,
							SmartLockService.class);
					SettingActivity.this.stopService(intent1);
				}
			}
		});
		filterApplicationLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, FilterApplicationActivity.class);
				startActivity(intent);
			}
		});
		SharedPrefrencesAssist.instance(SettingActivity.this).write("hand", "true");
		handCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if(null!=MyWindowManager.getView())MyWindowManager.getView().dismissHand(true);
					SharedPrefrencesAssist.instance(SettingActivity.this).write("hand", "true");
					buttonView.setButtonDrawable(R.drawable.rect_bth_checkbox_normal);
				}else {
					if(null!=MyWindowManager.getView())MyWindowManager.getView().dismissHand(false);
					SharedPrefrencesAssist.instance(SettingActivity.this).write("hand", "false");
					buttonView.setButtonDrawable(R.drawable.rect_btn_checkbox_normal);
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Intent intent = new Intent(SettingActivity.this,
		// FloatWindowService.class);
		// SettingActivity.this.startService(intent);
	}

	@Override
	protected void findAllViews() {
		startCheckBox = (CheckBox) findViewById(R.id.start_checkbox);
		filterApplicationLayout = (ViewGroup)findViewById(R.id.filter_application_select);
		handCheckBox = (CheckBox)findViewById(R.id.hand_checkbox);
		
	}

	private boolean isServiceRunning(Context mContext, String className) {

		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);

		if (!(serviceList.size() > 0)) {
			return false;
		}

		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
}
