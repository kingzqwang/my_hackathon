package com.qihoo.huangmabisheng.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import com.qihoo.huangmabisheng.R;
import com.qihoo.huangmabisheng.special.instagram.InstagramActivity;
import com.qihoo.huangmabisheng.utils.Log;
import com.qihoo.huangmabisheng.utils.MyWindowManager;
import com.qihoo.huangmabisheng.utils.fb;
import com.qihoo.huangmabisheng.view.FloatWindowBigView;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;

public class TransparentActivity extends BaseActivity implements
		SurfaceHolder.Callback, AutoFocusCallback, PreviewCallback,
		PictureCallback {

	private ViewGroup transLayout;
	private Camera camera;
	private SurfaceView surfaceView;
	private SurfaceHolder holder;
	Parameters parameters;

	// public static TransparentActivity context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "TransparentActivity onCreate");
		// context = this;
		// WindowManager.LayoutParams lp = getWindow().getAttributes();
		// lp.flags |= FLAG_HOMEKEY_DISPATCHED;
		// getWindow().setAttributes(lp);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
		// WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);
		// startService(new Intent(MainActivity.this,
		// FloatWindowService.class));

	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		unregisterReceiver(finishReceiver);
		unregisterReceiver(openCameraReceiver);
		unregisterReceiver(closeCameraReceiver);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		fb.d(this);
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
	}

	synchronized private void openCamera() {
		if(camera != null) return;
		Log.e(TAG, "openCamera");
		surfaceView = new SurfaceView(TransparentActivity.this);
		surfaceView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		holder = surfaceView.getHolder();
		holder.addCallback(TransparentActivity.this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		transLayout.addView(surfaceView);
	}

	synchronized private void closeCamera() {
		if(camera==null) return;
		camera.takePicture(null, null, this);
		Log.e(TAG, "closeCamera");

	}

	@Override
	protected void setAllListeners() {
		registerReceiver(finishReceiver, new IntentFilter(
				"com.qihoo.huangmabisheng.finish"));
		registerReceiver(openCameraReceiver, new IntentFilter(
				"com.qihoo.huangmabisheng.opencamera"));
		registerReceiver(closeCameraReceiver, new IntentFilter(
				"com.qihoo.huangmabisheng.closecamera"));
	}

	@Override
	protected void findAllViews() {
		transLayout = (ViewGroup) findViewById(R.id.trans_layout);

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
	private BroadcastReceiver openCameraReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction()
					.equals("com.qihoo.huangmabisheng.opencamera")) {
				openCamera();
			}
		}
	};
	private BroadcastReceiver closeCameraReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					"com.qihoo.huangmabisheng.closecamera")) {
				closeCamera();
			}
		}
	};

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.e(TAG, "surfaceCreated");
		camera = Camera.open();
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		camera.setPreviewCallback(this);
		camera.startPreview();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (camera == null)
			return;
		WindowManager windowManager = this.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		Log.e(TAG, "" + display.getRotation() + ",Build.VERSION.SDK_INT="
				+ Build.VERSION.SDK_INT);
		parameters = camera.getParameters();
		switch (display.getRotation()) {
		case Surface.ROTATION_0:
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				camera.setDisplayOrientation(90);
			} else {
				parameters.setRotation(90);
			}
			break;
		case Surface.ROTATION_90:
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				camera.setDisplayOrientation(0);
			} else {
				parameters.setRotation(0);
			}
			break;
		case Surface.ROTATION_270:
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				camera.setDisplayOrientation(180);
			} else {
				parameters.setRotation(180);
			}
		}
		Size size = parameters.getPreviewSize();
		parameters.setPictureFormat(PixelFormat.JPEG); // 设置照片格式
		try {
			parameters.setPictureSize(display.getHeight(), display.getWidth());
			camera.setParameters(parameters);
		} catch (Exception e) {
			parameters.setPictureSize(size.width, size.height);
			camera.setParameters(parameters);
		}

		camera.autoFocus(this);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.e(TAG, "surfaceDestroyed");
		camera.stopPreview();
		if (camera != null) {
			try {
				camera.setPreviewDisplay(null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		Log.e(TAG, data.length + "");
	}
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		synchronized (this) {
			try {
				Log.e(TAG, "onPictureTaken"+data.length);
				Uri imageUri = this.getContentResolver().insert(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						new ContentValues());// 原来是这么写照出来的图片
				try {
					OutputStream os = this.getContentResolver().openOutputStream(
							imageUri);
					os.write(data);
					os.flush();
					os.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				camera.stopPreview();
				try {
					camera.reconnect();
					camera.setPreviewCallback(this);
					camera.startPreview();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} finally{
				transLayout.removeAllViews();
				surfaceView = null;
			}
		}
	}

}
