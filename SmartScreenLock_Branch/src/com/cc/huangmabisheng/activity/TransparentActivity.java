package com.cc.huangmabisheng.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import com.cc.huangmabisheng.R;
import com.cc.huangmabisheng.constant.Constant;
import com.cc.huangmabisheng.plugin.seven.InstagramActivity;
import com.cc.huangmabisheng.utils.FileUtil;
import com.cc.huangmabisheng.utils.Log;
import com.cc.huangmabisheng.utils.MyWindowManager;
import com.cc.huangmabisheng.utils.TipHelper;
import com.cc.huangmabisheng.utils.fb;
import com.cc.huangmabisheng.view.FloatWindowBigView;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.Toast;

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
//		if (android.os.Build.VERSION.SDK_INT >= 16) 
//		getWindow().getDecorView().setSystemUiVisibility(
//				View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
//				View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//				View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//				View.SYSTEM_UI_FLAG_FULLSCREEN |
//				View.SYSTEM_UI_FLAG_IMMERSIVE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
		// WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);
		// startService(new Intent(MainActivity.this,
		// FloatWindowService.class));

	}

	@Override
	protected void onDestroy() {
		Log.e(TAG, "onDestroy");
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
		Log.e(TAG, "onPause");
		super.onPause();
//		fb.d(this);
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
		Log.e(TAG, "onResume");
//		fb.d(this);
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

	private void openCamera() {
		if (camera != null)
			return;
		Log.e(TAG, "openCamera");
		surfaceView = new SurfaceView(TransparentActivity.this);
		surfaceView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		holder = surfaceView.getHolder();
		holder.addCallback(TransparentActivity.this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		transLayout.addView(surfaceView);
	}

	private void closeCamera() {
		Log.e(TAG, "closeCamera");
		if (camera == null)
			return;
		try {
			camera.setPreviewCallback(null);
			camera.takePicture(null, null, this);
		} catch (Exception e) {
		}
	}

	@Override
	protected void setAllListeners() {
		registerReceiver(finishReceiver, new IntentFilter(
				"com.cc.huangmabisheng.finish"));
		registerReceiver(openCameraReceiver, new IntentFilter(
				"com.cc.huangmabisheng.opencamera"));
		registerReceiver(closeCameraReceiver, new IntentFilter(
				"com.cc.huangmabisheng.closecamera"));
	}

	@Override
	protected void findAllViews() {
		transLayout = (ViewGroup) findViewById(R.id.trans_layout);
	}

	private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("com.cc.huangmabisheng.finish")) {
				Log.d(TAG, "finish recieved");
				TransparentActivity.this.finish();
				overridePendingTransition(0, 0);
				// this.abortBroadcast();
			}
		}
	};
	private BroadcastReceiver openCameraReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction()
					.equals("com.cc.huangmabisheng.opencamera")) {
				openCamera();
			}
		}
	};
	private BroadcastReceiver closeCameraReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					"com.cc.huangmabisheng.closecamera")) {
				Log.e(TAG, "closeCameraReceiver");
				closeCamera();
			}
		}
	};

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		Log.e(TAG, "surfaceCreated");
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (TransparentActivity.class) {
					try {
						camera = Camera.open();
						Log.e(TAG, "Camera.opened");
						camera.setPreviewDisplay(holder);
						camera.setPreviewCallback(TransparentActivity.this);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								surfaceView.layout(surfaceView.getLeft(),
										surfaceView.getTop(),
										surfaceView.getRight() - 1,
										surfaceView.getBottom());
							}
						});
						camera.startPreview();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
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
		if (camera == null) {
			return;
		}
		camera.stopPreview();
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

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub

	}

	boolean status = false;

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
	}

	@Override
	public void onPictureTaken(final byte[] data, Camera camera) {
		try {
			// Log.e(TAG, "onPictureTaken"+data.length);
			// ContentValues values = new ContentValues();
			// Uri imageUri = this.getContentResolver().insert(
			// MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			// values);// 原来是这么写照出来的图片
			// try {
			// OutputStream os = this.getContentResolver().openOutputStream(
			// imageUri);
			// os.write(data);
			// os.flush();
			// os.close();
			// } catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			new Thread(new Runnable() {

				@Override
				public void run() {
					
					synchronized (TransparentActivity.class) {
						
						Log.e(TAG, "start");
						Bitmap bMap = BitmapFactory.decodeByteArray(data, 0,
								data.length);
						Matrix matrix = new Matrix();
						matrix.reset();
						matrix.postRotate(90);
						bMap = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(),
								bMap.getHeight(), matrix, true);

						File sd = FileUtil.getSDFileDir();
						if (sd == null) {
							return;
						}
						File dir = new File(sd, "CC锁屏");
						if (!dir.isDirectory()) {
							dir.mkdir();
						}
						File file = new File(dir, new Date().getTime() + ".jpg");
						file.delete();
						Log.e(TAG, "delete");
						try {
							file.createNewFile();
							BufferedOutputStream bos = new BufferedOutputStream(
									new FileOutputStream(file));
							bMap.compress(Bitmap.CompressFormat.JPEG, 100, bos);// 将图片压缩到流中
							bos.flush();// 输出
							bos.close();// 关闭
							bMap.recycle();
							bMap = null;
							Log.e(TAG, "OutputStream");
							// Toast.makeText(this,
							// "照片已保存在"+file.getAbsolutePath(),
							// Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(
									Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
							Uri uri = Uri.fromFile(file);
							intent.setData(uri);
							sendBroadcast(intent);
							Log.e(TAG, "sendBroadcast");
							TipHelper.Vibrate(TransparentActivity.this,
									Constant.VIBRATE_TIME_PHOTO);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}).start();

		} finally {
			Log.e(TAG, "finnaly");
			transLayout.removeAllViews();
			surfaceView = null;
		}
	}

}
