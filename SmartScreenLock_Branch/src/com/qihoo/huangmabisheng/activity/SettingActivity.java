package com.qihoo.huangmabisheng.activity;

import java.util.List;

import com.qihoo.huangmabisheng.R;
import com.qihoo.huangmabisheng.constant.Application;
import com.qihoo.huangmabisheng.constant.Constant;
import com.qihoo.huangmabisheng.constant.SharedPrefrencesAssist;
import com.qihoo.huangmabisheng.interfaces.EventListener;
import com.qihoo.huangmabisheng.service.FloatWindowService;
import com.qihoo.huangmabisheng.service.SmartLockService;
import com.qihoo.huangmabisheng.service.SpecialHttpService;
import com.qihoo.huangmabisheng.special.instagram.InstagramActivity;
import com.qihoo.huangmabisheng.utils.MyWindowManager;
import com.qihoo.huangmabisheng.view.CloudDialog;
import com.qihoo.huangmabisheng.view.OptionCheckBox;
import com.qihoo.huangmabisheng.wifi.WifiAdmin;
import com.qihoo.huangmabisheng.wifi.WifiBroadcastReciever;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
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
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

public class SettingActivity extends BaseActivity {
	OptionCheckBox startCheckBox;
	ViewGroup filterApplicationLayout;
//	CheckBox handCheckBox;
	OptionCheckBox unbelievableCheckBox;
	
	ViewGroup specialModuleLayout;
	ViewGroup photoLayout;
	OptionCheckBox screenPhotoCheckBox;
	ImageView ipSettingImageView;
	WifiBroadcastReciever wifiBroadcastReciever;
	TextView specialServiceTitleTextView;
	TextView specialServiceDescriptionTextView;
	IntentFilter filter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.setting);
		
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void handleMsg(Message msg) {
		switch (msg.what) {
		case Constant.WIFI_CONNECTED:
			WifiAdmin wifiAdmin = WifiAdmin.getInstance(SettingActivity.this);
			String ip = wifiAdmin.getIPAddressStr();
			if (!ip.equals("0.0.0.0"))
				specialServiceTitleTextView.setText("已连接："
						+ WifiAdmin.getInstance(SettingActivity.this)
								.getWifiInfo().getSSID());
			else {
				specialServiceTitleTextView.setText("您尚未连接任何wifi");
			}
			specialServiceDescriptionTextView.setText(ip);
			break;

		default:
			break;
		}
	}

	@Override
	protected void setAllListeners() {

		ActivityManager activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(50);

		if (isServiceRunning(serviceList,
				"com.qihoo.huangmabisheng.service.FloatWindowService",
				"com.qihoo.huangmabisheng.service.SmartLockService")) {
			Log.d(TAG, "is running");
			// 已经运行了
		} else {
			// 当前服务没执行
			if (Application.app.isServiceRunning) {
				// 但服务标记已开启，说明服务是被杀死的
				startAllServices();
			} else {
				// 服务标记关闭
				startCheckBox.setChecked(false);
			}
		}
		if (isServiceRunning(serviceList,
				"com.qihoo.huangmabisheng.service.SpecialHttpService")) {
			unbelievableCheckBox.setChecked(true);
			specialModuleLayout.setVisibility(View.VISIBLE);
			ipSettingImageView.setVisibility(View.VISIBLE);
		} else {
			if (Application.app.isSpecialServiceRunning) {
				startSpecialService();
				unbelievableCheckBox.setChecked(true);
				specialModuleLayout.setVisibility(View.VISIBLE);
				ipSettingImageView.setVisibility(View.VISIBLE);
			}
		}
		// startCheckBox默认open
		startCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					Log.d(TAG, true + "");
					startAllServices();
				} else {
					Log.d(TAG, false + "");
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
//		handCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView,
//					boolean isChecked) {
//				if (isChecked) {
//					if (null != MyWindowManager.getView())
//						MyWindowManager.getView().dismissHand(true);
//					SharedPrefrencesAssist.instance(SettingActivity.this)
//							.write("hand", "true");
//					openCheckBox(buttonView);
//				} else {
//					if (null != MyWindowManager.getView())
//						MyWindowManager.getView().dismissHand(false);
//					SharedPrefrencesAssist.instance(SettingActivity.this)
//							.write("hand", "false");
//					closeCheckBox(buttonView);
//				}
//			}
//		});
		unbelievableCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							startSpecialService();
							registerWifiBroadcastReciever();
						} else {
							stopSpecialService();
							specialServiceTitleTextView.setText("你绝对想不到");
							specialServiceDescriptionTextView.setText("特殊服务");
							unregisterWifiBroadcastReciever();
						}
					}
				});
		photoLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingActivity.this,
						InstagramActivity.class);
				startActivity(intent);
			}
		});
		ipSettingImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final SharedPrefrencesAssist spa = SharedPrefrencesAssist
						.instance(SettingActivity.this);
				new CloudDialog(SettingActivity.this, "请设置ip", spa.read("ip"),
						"确定", "取消", new EventListener() {
							@Override
							public void onEventExecute(String info) {
								spa.write("ip", info);
							}
						}, null).show();
			}
		});
		if(SharedPrefrencesAssist.instance(this).readBoolean(Constant.SCREEN_PHOTO_IMAGEVIEW)){
			screenPhotoCheckBox.setChecked(true);
		}
		screenPhotoCheckBox
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SharedPrefrencesAssist.instance(SettingActivity.this).writeBoolean(Constant.SCREEN_PHOTO_IMAGEVIEW, isChecked);
				if (isChecked) {
					MyWindowManager.getView().openScreenPhoto();
				} else {
					MyWindowManager.getView().closeScreenPhoto();
				}
			}
		});
		
		filter = new IntentFilter();
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// finish();
	}

	private void registerWifiBroadcastReciever() {
		wifiBroadcastReciever = new WifiBroadcastReciever(handler);
		registerReceiver(wifiBroadcastReciever, filter);
	}

	private void unregisterWifiBroadcastReciever() {
		if (null != wifiBroadcastReciever) {
			unregisterReceiver(wifiBroadcastReciever);
			wifiBroadcastReciever = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterWifiBroadcastReciever();
		// Intent intent = new Intent(SettingActivity.this,
		// FloatWindowService.class);
		// SettingActivity.this.startService(intent);
	}

	@Override
	protected void findAllViews() {
		startCheckBox = (OptionCheckBox) findViewById(R.id.start_checkbox);
		filterApplicationLayout = (ViewGroup) findViewById(R.id.filter_application_select);
//		handCheckBox = (CheckBox) findViewById(R.id.hand_checkbox);
		unbelievableCheckBox = (OptionCheckBox) findViewById(R.id.service_control_checkbox);
		ipSettingImageView = (ImageView) findViewById(R.id.setting_ip_imageview);
		specialServiceTitleTextView = (TextView) findViewById(R.id.specialservice_title_textview);
		specialServiceDescriptionTextView = (TextView) findViewById(R.id.specialservice_description_textview);
		specialModuleLayout = (ViewGroup)findViewById(R.id.special_module_layout);
		screenPhotoCheckBox = (OptionCheckBox)findViewById(R.id.screen_photo_checkbox);
		photoLayout = (ViewGroup) findViewById(R.id.photo_layout);
	}

	private boolean isServiceRunning(
			List<ActivityManager.RunningServiceInfo> serviceList,
			String... classNames) {

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
		Application.app.setServiceOffStatus();
	}

	private void startAllServices() {
		Intent intent = new Intent(SettingActivity.this,
				FloatWindowService.class);
		SettingActivity.this.startService(intent);
		Intent intent1 = new Intent(SettingActivity.this,
				SmartLockService.class);
		SettingActivity.this.startService(intent1);
		Application.app.setServiceOnStatus();
	}

	private void startSpecialService() {
		Intent httpIntent = new Intent(SettingActivity.this,
				SpecialHttpService.class);
		SettingActivity.this.startService(httpIntent);
		specialModuleLayout.setVisibility(View.VISIBLE);
		ipSettingImageView.setVisibility(View.VISIBLE);
	}

	private void stopSpecialService() {
		Intent httpIntent = new Intent(SettingActivity.this,
				SpecialHttpService.class);
		SettingActivity.this.stopService(httpIntent);
		specialModuleLayout.setVisibility(View.GONE);
		ipSettingImageView.setVisibility(View.GONE);
	}

}
