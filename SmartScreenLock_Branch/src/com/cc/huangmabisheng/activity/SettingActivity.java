package com.cc.huangmabisheng.activity;

import java.util.List;

import com.cc.huangmabisheng.R;
import com.cc.huangmabisheng.constant.Application;
import com.cc.huangmabisheng.constant.Constant;
import com.cc.huangmabisheng.constant.SharedPrefrencesAssist;
import com.cc.huangmabisheng.interfaces.EventListener;
import com.cc.huangmabisheng.plugin.seven.InstagramActivity;
import com.cc.huangmabisheng.reciever.AlarmReceiver;
import com.cc.huangmabisheng.service.FloatWindowService;
import com.cc.huangmabisheng.service.SmartLockService;
import com.cc.huangmabisheng.service.SpecialHttpService;
import com.cc.huangmabisheng.utils.JavaBasicUtil;
import com.cc.huangmabisheng.utils.MyWindowManager;
import com.cc.huangmabisheng.utils.Toast;
import com.cc.huangmabisheng.view.CloudDialog;
import com.cc.huangmabisheng.view.OptionCheckBox;
import com.cc.huangmabisheng.wifi.WifiAdmin;
import com.cc.huangmabisheng.wifi.WifiBroadcastReciever;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Camera;
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
import android.widget.ImageSwitcher;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

public class SettingActivity extends BaseActivity {
	OptionCheckBox startCheckBox;
	OptionCheckBox screenCleanCheckBox;
	OptionCheckBox screenPhotoCheckBox;
	OptionCheckBox worldCupCheckBox;
	ViewGroup numViewGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.setting);
//		if (android.os.Build.VERSION.SDK_INT >= 16) 
//			getWindow().getDecorView().setSystemUiVisibility(
//					View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
//					View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//					View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//					View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//					View.SYSTEM_UI_FLAG_FULLSCREEN |
//					View.SYSTEM_UI_FLAG_IMMERSIVE);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void handleMsg(Message msg) {
		switch (msg.what) {

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
				"com.cc.huangmabisheng.service.FloatWindowService",
				"com.cc.huangmabisheng.service.SmartLockService")) {
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
		startCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					startAllServices();
				} else {
					stopAllServices();
				}
			}
		});
		screenPhotoCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						SharedPrefrencesAssist.instance(SettingActivity.this)
								.writeBoolean(Constant.SCREEN_PHOTO_IMAGEVIEW,
										isChecked);
						if (isChecked)

							new Thread(new Runnable() {
								public void run() {
									try {
										android.hardware.Camera camera = android.hardware.Camera
												.open();
										camera.release();
									} catch (Exception e) {
										SharedPrefrencesAssist
												.instance(SettingActivity.this)
												.writeBoolean(
														Constant.SCREEN_PHOTO_IMAGEVIEW,
														false);
										runOnUiThread(new Runnable() {

											@Override
											public void run() {
												screenPhotoCheckBox
														.setChecked(false);
												Toast.showShort(
														SettingActivity.this,
														"您的手机不支持此功能");
											}
										});
									}
								}
							}).start();

					}
				});
		if (SharedPrefrencesAssist.instance(this).readBoolean(
				Constant.SCREEN_PHOTO_IMAGEVIEW)) {
			screenPhotoCheckBox.setChecked(true);
		}
		screenCleanCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						SharedPrefrencesAssist.instance(SettingActivity.this)
								.writeBoolean(Constant.SCREEN_CLEAN_IMAGEVIEW,
										isChecked);
					}
				});
		if (SharedPrefrencesAssist.instance(this).readBoolean(
				Constant.SCREEN_CLEAN_IMAGEVIEW,true)) {
			screenCleanCheckBox.setChecked(true);
		}

		worldCupCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						SharedPrefrencesAssist.instance(SettingActivity.this)
								.writeBoolean(Constant.WORLD_CUP_IMAGEVIEW,
										isChecked);
//						if (isChecked) {
//							AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//							Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
//							
//							PendingIntent pendIntent = PendingIntent.getBroadcast(getApplicationContext(),
//									0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//							// 5秒后发送广播，只发送一次
////							int triggerAtTime = SystemClock.elapsedRealtime() + 5 * 1000;
//							alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000*15, pendIntent);
//
//						}else {
//							AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//							Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
//							PendingIntent pendIntent = PendingIntent.getBroadcast(getApplicationContext(),
//									0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//							alarmMgr.cancel(pendIntent);
//						}
					}
				});
		if (SharedPrefrencesAssist.instance(this).readBoolean(
				Constant.WORLD_CUP_IMAGEVIEW,true)) {
			worldCupCheckBox.setChecked(true);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void updateCount() {
		numViewGroup.removeAllViews();
		final int screenOpenCount = SharedPrefrencesAssist.instance(this)
				.readInt(Constant.SCREEN_OPEN_COUNT);
		for (int i = JavaBasicUtil.sizeOfInt(screenOpenCount) - 1; i >= 0; i--) {
			ImageView imageView = new ImageView(SettingActivity.this);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			imageView.setLayoutParams(layoutParams);
			imageView.setImageResource(R.drawable.num_3d_0);
			if (i > 0)
				imageView.setVisibility(View.GONE);
			numViewGroup.addView(imageView);
		}
		int uu = screenOpenCount;
		int cc = numViewGroup.getChildCount() - 1;
		while (uu > 0) {
			final int u = uu;
			final int c = cc;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					int t = u % 10;
					ImageView i = (ImageView) numViewGroup.getChildAt(c);
					i.setVisibility(View.VISIBLE);
					switch (t) {
					case 1:
						i.setImageResource(R.drawable.num_3d_1);
						break;
					case 2:
						i.setImageResource(R.drawable.num_3d_2);
						break;
					case 3:
						i.setImageResource(R.drawable.num_3d_3);
						break;
					case 4:
						i.setImageResource(R.drawable.num_3d_4);
						break;
					case 5:
						i.setImageResource(R.drawable.num_3d_5);
						break;
					case 6:
						i.setImageResource(R.drawable.num_3d_6);
						break;
					case 7:
						i.setImageResource(R.drawable.num_3d_7);
						break;
					case 8:
						i.setImageResource(R.drawable.num_3d_8);
						break;
					case 9:
						i.setImageResource(R.drawable.num_3d_9);
						break;
					default:
						i.setImageResource(R.drawable.num_3d_0);
						break;
					}
				}
			});
			uu = uu / 10;
			cc--;
		}
	}

	@Override
	protected void findAllViews() {
		numViewGroup = (ViewGroup) findViewById(R.id.num_linearLayout);
		startCheckBox = (OptionCheckBox) findViewById(R.id.start_checkbox);
		startCheckBox.setSrc(R.drawable.rect_on_checkbox_normal_edge,
				R.drawable.rect_off_checkbox_normal_edge);
		screenPhotoCheckBox = (OptionCheckBox) findViewById(R.id.screen_photo_checkbox);
		screenCleanCheckBox = (OptionCheckBox) findViewById(R.id.screen_clean_checkbox);
		worldCupCheckBox = (OptionCheckBox) findViewById(R.id.world_cup_checkbox);
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

	@Override
	protected void onResume() {
		updateCount();
		super.onResume();
	}

}
