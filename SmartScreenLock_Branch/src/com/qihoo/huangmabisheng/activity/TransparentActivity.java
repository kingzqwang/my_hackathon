package com.qihoo.huangmabisheng.activity;

import com.qihoo.huangmabisheng.R;
import com.qihoo.huangmabisheng.utils.Log;
import com.qihoo.huangmabisheng.utils.MyWindowManager;
import com.qihoo.huangmabisheng.utils.fb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class TransparentActivity extends BaseActivity {
	// public static TransparentActivity context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "TransparentActivity onCreate");
		// context = this;
		// WindowManager.LayoutParams lp = getWindow().getAttributes();
		// lp.flags |= FLAG_HOMEKEY_DISPATCHED;
		// getWindow().setAttributes(lp);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);
		// startService(new Intent(MainActivity.this,
		// FloatWindowService.class));
		registerReceiver(finishReceiver, new IntentFilter(
				"com.qihoo.huangmabisheng.finish"));
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		unregisterReceiver(finishReceiver);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onPause() {
		fb.d(this);
		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (View.GONE == MyWindowManager.getWindowVisibility()) {
			finish();
			return;
		}
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		fb.d(this);
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, event.toString());
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_BACK:
			Log.d(TAG, "KEYCODE_BACK");
			return true;
		case KeyEvent.KEYCODE_HOME:
			Log.d(TAG, "KEYCODE_HOME");
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void handleMsg(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setAllListeners() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void findAllViews() {
		// TODO Auto-generated method stub

	}

	private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("com.qihoo.huangmabisheng.finish")) {
				Log.d(TAG, "finish recieved");
				TransparentActivity.this.finish();
				// this.abortBroadcast();
			}
		}
	};
}
