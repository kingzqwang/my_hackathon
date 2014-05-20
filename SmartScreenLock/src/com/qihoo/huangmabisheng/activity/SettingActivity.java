package com.qihoo.huangmabisheng.activity;

import java.util.List;

import com.qihoo.huangmabisheng.R;
import com.qihoo.huangmabisheng.constant.Application;
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
	CheckBox unbelievableCheckBox;
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
				"com.qihoo.huangmabisheng.service.FloatWindowService",
				"com.qihoo.huangmabisheng.service.SmartLockService")) {
			Log.d(TAG, "is running");
			// 已经运行了

		} else {
			//当前服务没执行
			if (Application.app.isServiceRunning) {
				//但服务标记已开启，说明服务是被杀死的
				startAllServices();
			}else {
				//服务标记关闭
				startCheckBox.setChecked(false);
				closeCheckBox(startCheckBox);
			}
		}
		// startCheckBox默认open
		startCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					Log.d(TAG, true + "");
					openCheckBox(buttonView);
					startAllServices();
				} else {
					Log.d(TAG, false + "");
					closeCheckBox(buttonView);
					stopAllServices();
				}
			}
		});
		filterApplicationLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this,
						FilterApplicationActivity.class);
				startActivity(intent);
			}
		});
		SharedPrefrencesAssist.instance(SettingActivity.this).write("hand",
				"true");
		handCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					if (null != MyWindowManager.getView())
						MyWindowManager.getView().dismissHand(true);
					SharedPrefrencesAssist.instance(SettingActivity.this)
							.write("hand", "true");
					openCheckBox(buttonView);
				} else {
					if (null != MyWindowManager.getView())
						MyWindowManager.getView().dismissHand(false);
					SharedPrefrencesAssist.instance(SettingActivity.this)
							.write("hand", "false");
					closeCheckBox(buttonView);
				}
			}
		});
		unbelievableCheckBox .setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
				} else {
				}
			}
		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
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
		filterApplicationLayout = (ViewGroup) findViewById(R.id.filter_application_select);
		handCheckBox = (CheckBox) findViewById(R.id.hand_checkbox);
		unbelievableCheckBox = (CheckBox) findViewById(R.id.service_control_checkbox);
	}

	private boolean isServiceRunning(Context mContext, String... classNames) {

		// boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(50);

		if (serviceList.size() == 0) {
			return false;
		}
		for (String className : classNames) {

			for (int i = 0; i < serviceList.size(); i++) {
				Log.d(TAG, serviceList.get(i).service.getClassName());
				if (serviceList.get(i).service.getClassName().equals(className) == true) {
					return true;
				}
			}
		}
		return false;
	}

	private void stopAllServices() {
		Intent intent = new Intent(SettingActivity.this,
				FloatWindowService.class);
		SettingActivity.this.stopService(intent);
		Intent intent1 = new Intent(SettingActivity.this,
				SmartLockService.class);
		SettingActivity.this.stopService(intent1);
		Application.app.isServiceRunning = false;
	}

	private void startAllServices() {
		Intent intent = new Intent(SettingActivity.this,
				FloatWindowService.class);
		SettingActivity.this.startService(intent);
		Intent intent1 = new Intent(SettingActivity.this,
				SmartLockService.class);
		SettingActivity.this.startService(intent1);
		Application.app.isServiceRunning = true;
	}

	private void openCheckBox(CompoundButton buttonView) {
		buttonView.setButtonDrawable(R.drawable.rect_bth_checkbox_normal);
	}

	private void closeCheckBox(CompoundButton cb) {
		cb.setButtonDrawable(R.drawable.rect_btn_checkbox_normal);
	}
}
