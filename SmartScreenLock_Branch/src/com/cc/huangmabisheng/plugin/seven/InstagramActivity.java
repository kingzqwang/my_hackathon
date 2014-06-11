package com.cc.huangmabisheng.plugin.seven;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;

import com.cc.huangmabisheng.R;
import com.cc.huangmabisheng.constant.Constant;
import com.cc.huangmabisheng.constant.SharedPrefrencesAssist;
import com.cc.huangmabisheng.httpclient.HttpRequestFactory;
import com.cc.huangmabisheng.httpserver.SpecialHttpServer;
import com.cc.huangmabisheng.interfaces.IRecieveDataSpecialServiceListener;
import com.cc.huangmabisheng.utils.Log;
import com.cc.huangmabisheng.utils.SingleThread;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class InstagramActivity extends Activity implements AutoFocusCallback,
		SurfaceHolder.Callback, ShutterCallback, PictureCallback,
		PreviewCallback {
	public static InstagramActivity instance;
	boolean isControl = true;
	Bitmap bitmap;
	String TAG = "AppMain";
	public FrameLayout prepareLayout, takingPictureLayout;
	public Parameters parameters;
	public ImageButton startCameraButton, takePictureButton,
			startControlButton;
	public ImageView snapImageView;
	public StateListDrawable cameraStateListDrawable;

	public AssetManager assetManager;

	public SurfaceView surfaceView;
	public SurfaceHolder holder;
	private Camera camera;
	public SingleThread singleThread, singleThread_1;
	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.SERVER_TAKE_PHOTO:
				if (takePictureButton != null)
					takePictureButton.performClick();
				break;
			case Constant.SERVER_CAMERA_FOCUS:
				if (camera != null)
					camera.autoFocus(InstagramActivity.this);
				break;
			default:
				if (snapImageView == null)
					break;
				BitmapDrawable bitmapDrawable = (BitmapDrawable) snapImageView
						.getDrawable();
				Bitmap b = null;
				if (bitmapDrawable != null)
					b = bitmapDrawable.getBitmap();
				synchronized (InstagramActivity.this) {
					if (bitmap != null && !bitmap.isRecycled()) {
						Log.d(TAG, "bitmap.getWidth: " + bitmap.getWidth());
						Matrix matrix = new Matrix();
						matrix.reset();
						matrix.setRotate(90);
						bitmap = Bitmap.createBitmap(bitmap, 0, 0,
								bitmap.getWidth(), bitmap.getHeight(), matrix,
								true);
						snapImageView.setImageBitmap(bitmap);
					} else {
						snapImageView.setImageBitmap(null);
					}
				}
				if (b != null)
					b.recycle();
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.e(TAG, "onCreate");
		instance = this;
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		prepareLayout = new FrameLayout(InstagramActivity.this);
		prepareLayout.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		startControlButton = new ImageButton(InstagramActivity.this);
		startCameraButton = new ImageButton(InstagramActivity.this);
		FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(100, 100);
		layout.bottomMargin = 100;
		layout.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
		startCameraButton.setLayoutParams(layout);

		FrameLayout.LayoutParams layout1 = new FrameLayout.LayoutParams(100,
				100);
		layout1.topMargin = 100;
		layout1.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		startControlButton.setLayoutParams(layout1);

		FrameLayout.LayoutParams layout2 = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT);
		snapImageView = new ImageView(InstagramActivity.this);
		snapImageView.setLayoutParams(layout2);

		Drawable pressDrawable = new BitmapDrawable(getAssetBitMap(this,
				"btn_navi_reality_pressed.png"));
		Drawable normalDrawable = new BitmapDrawable(getAssetBitMap(this,
				"btn_navi_reality_normal.png"));
		cameraStateListDrawable = new StateListDrawable();
		cameraStateListDrawable.addState(
				new int[] { android.R.attr.state_pressed }, pressDrawable);
		cameraStateListDrawable.addState(
				new int[] { android.R.attr.state_enabled }, normalDrawable);

		startCameraButton.setBackgroundDrawable(cameraStateListDrawable);
		startControlButton.setBackgroundResource(R.drawable.refresh);

		startControlButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// prepareLayout.setBackgroundResource(R.drawable.bg_cr_abs_driving);
				Log.e(TAG, "startControlButton click");
				new Thread(new Runnable() {
					public void run() {
						try {
							URL url = new URL("http://"
									+ SharedPrefrencesAssist.instance(null)
											.read("ip") + ":" + Constant.PORT
									+ "/?" + "p=" + Constant.PARAM_AUTO_FOCUS);
							Log.e(TAG, url.getPath());
							HttpURLConnection conn = (HttpURLConnection) url
									.openConnection();
							conn.setConnectTimeout(15000);
							Log.e(TAG, conn.getResponseCode() + "");
							conn.disconnect();
							// HttpRequestFactory.instance().post("/",
							// "p=" + Constant.PARAM_AUTO_FOCUS);
							Log.e(TAG, "focus");
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
				if (singleThread != null)
					singleThread.stop();
				singleThread = new SingleThread(new Runnable() {
					public void run() {
						try {
							Log.d(TAG, "HttpRequestFactory start");
							URL url = new URL("http://"
									+ SharedPrefrencesAssist.instance(null)
											.read("ip") + ":" + Constant.PORT
									+ "/?" + "p=" + Constant.PARAM_CATCH_PIC);
							HttpURLConnection conn = (HttpURLConnection) url
									.openConnection();
							conn.setConnectTimeout(15000);
							// HttpEntity httpEntity = HttpRequestFactory
							// .instance().post("/",
							// "p=" + Constant.PARAM_CATCH_PIC);
							Log.e(TAG, conn.getResponseCode() + "");
							InputStream inputStream = conn.getInputStream();// httpEntity.getContent();
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							int next = inputStream.read();
							while (next > -1) {
								// Log.e(TAG, "next");
								bos.write(next);
								next = inputStream.read();
							}
							bos.flush();
							conn.disconnect();
							byte[] data = bos.toByteArray();
							synchronized (InstagramActivity.this) {
								bitmap = BitmapFactory.decodeByteArray(data, 0,
										data.length);
							}
							Log.d(TAG, data.length + "");
							handler.obtainMessage().sendToTarget();
							Log.d(TAG, "HttpRequestFactory end");

						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				singleThread.start();
				startCameraButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						new Thread(new Runnable() {
							public void run() {
								try {
									Log.e(TAG, "startCameraButton take");
									URL url = new URL("http://"
											+ SharedPrefrencesAssist.instance(
													null).read("ip") + ":"
											+ Constant.PORT + "/?" + "p="
											+ Constant.PARAM_TAKEPHOTO);
									HttpURLConnection conn = (HttpURLConnection) url
											.openConnection();
									conn.setConnectTimeout(15000);
									Log.e(TAG, conn.getResponseCode() + "");
									conn.disconnect();
									// HttpRequestFactory.instance().post("/",
									// "p=" + Constant.PARAM_TAKEPHOTO);
								} catch (ClientProtocolException e) {
									Log.e(TAG, e.toString());
								} catch (IOException e) {
									Log.e(TAG, e.toString());
								}
							}
						}).start();
						snapImageView.setImageBitmap(null);
					}

				});
			}
		});
		startCameraButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				takePictureButton = new ImageButton(InstagramActivity.this);
				FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(
						100, 100);
				layout.bottomMargin = 100;
				layout.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
				takePictureButton.setLayoutParams(layout);
				Drawable pressDrawable = new BitmapDrawable(getAssetBitMap(
						InstagramActivity.this, "btn_navi_reality_pressed.png"));
				Drawable normalDrawable = new BitmapDrawable(getAssetBitMap(
						InstagramActivity.this, "btn_navi_reality_normal.png"));
				cameraStateListDrawable = new StateListDrawable();
				cameraStateListDrawable.addState(
						new int[] { android.R.attr.state_pressed },
						pressDrawable);
				cameraStateListDrawable.addState(
						new int[] { android.R.attr.state_enabled },
						normalDrawable);
				takePictureButton
						.setBackgroundDrawable(cameraStateListDrawable);

				takePictureButton
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								if (camera == null)
									return;
								// m_caCamera.ta
								camera.setPreviewCallback(null);
								camera.takePicture(null,null,InstagramActivity.this);
							}
						});

				// TODO Auto-generated method stub
				takingPictureLayout = new FrameLayout(InstagramActivity.this);
				takingPictureLayout.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

				surfaceView = new SurfaceView(InstagramActivity.this);
				surfaceView.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
				holder = surfaceView.getHolder();
				holder.addCallback(InstagramActivity.this);
				holder.setKeepScreenOn(true);
				holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
				takingPictureLayout.addView(surfaceView, 0);
				takingPictureLayout.addView(takePictureButton);
				setContentView(takingPictureLayout);// 全部activity换掉
				getWindow().setFlags(
						WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);

			}
		});
		prepareLayout.addView(snapImageView);
		prepareLayout.addView(startControlButton);
		prepareLayout.addView(startCameraButton);
		super.onCreate(savedInstanceState);
		setContentView(prepareLayout);
	}

	public Bitmap getAssetBitMap(Context context, String picabsolutename) {
		if (context == null || picabsolutename == "")
			return null;
		AssetManager assetManager = context.getAssets();
		if (assetManager == null)
			return null;
		InputStream is = null;

		try {
			is = assetManager.open(picabsolutename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (is == null)
			return null;
		Bitmap bitmap = BitmapFactory.decodeStream(is);
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (bitmap == null)
			return null;
		return bitmap;

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		if (camera == null)
			return;
		parameters = camera.getParameters();
		WindowManager windowManager = this.getWindowManager();
		Display display = windowManager.getDefaultDisplay();

		switch (display.getRotation()) {
		case Surface.ROTATION_0:
			Log.e(TAG, "竖屏");
			// parameters.setPictureSize(display.getHeight(),
			// display.getWidth());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				camera.setDisplayOrientation(90);
			} else {
				parameters.setRotation(90);
			}
			break;
		case Surface.ROTATION_90:
			Log.e(TAG, "横屏-右侧在上");
			// parameters.setPictureSize(display.getWidth(),
			// display.getHeight());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				camera.setDisplayOrientation(0);
			} else {
				parameters.setRotation(0);
			}
			break;
		case Surface.ROTATION_270:
			Log.e(TAG, "横屏-左侧在上");
			// parameters.setPictureSize(display.getHeight(),
			// display.getWidth());
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
		camera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d(TAG, "surfaceCreated");
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
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d(TAG, "surfaceDestroyed");
		if (camera == null)
			return;
		parameters = null;
		camera = null;
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		// if(data == null)return;
		// Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
		// data.length-1);
		// BitmapFactory.
		// if(bitmap == null) return;
		// anothercameraButton.setBackgroundDrawable(new
		// BitmapDrawable(bitmap));
		Log.e(TAG, "onPictureTaken");
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

	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
		// data.length-1);
		// takePictureButton.setBackgroundDrawable(new
		// BitmapDrawable(bitmap));
		SpecialHttpServer.data = data;
	}

	@Override
	public void onShutter() {
		// TODO Auto-generated method stub
		Toast.makeText(InstagramActivity.this, "onShutter", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onResume() {
		Log.e(TAG, "onResume");
		instance = this;
		if (camera != null) {
			try {
				camera.reconnect();
				camera.setPreviewCallback(this);
				camera.startPreview();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Log.e(TAG, "onDestroy");
		if (singleThread != null)
			singleThread.stop();
		singleThread = null;
		if (camera != null) {
			camera.release();
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		instance = null;
		SpecialHttpServer.data = null;
		Log.e(TAG, "onPause");
		if (camera != null) {
			camera.stopPreview();
		}
		super.onPause();
	}
}
