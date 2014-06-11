package com.cc.huangmabisheng.httpserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;

import com.cc.huangmabisheng.R;
import com.cc.huangmabisheng.constant.Constant;
import com.cc.huangmabisheng.plugin.seven.InstagramActivity;
import com.cc.huangmabisheng.utils.Log;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Handler;

/**
 * HttpdServer
 * 
 * 一次性服务于一个客户端
 */
public class SpecialHttpServer extends NanoHTTPD {
	final String TAG = "HttpServer";
	private Handler handler;
	private Context context;
	/**
	 * 服务的客户端id，即为lock，lock总是大于0的。
	 */
	private int lock = 0;
	private Map<String, BlockingDeque<File>> filesQueueMap = new HashMap<String, BlockingDeque<File>>();
	private static SpecialHttpServer httpdServer;
	static public byte[] data = null;

	public static SpecialHttpServer instance(Context context, Handler handler)
			throws IOException {
		if (null == httpdServer)
			httpdServer = new SpecialHttpServer(context, handler);
		return httpdServer;
	}

	/**
	 * Constructs an HTTP server on given port.
	 */
	private SpecialHttpServer(Context context, Handler handler)
			throws IOException {
		super(Constant.PORT);
		this.handler = handler;
		this.context = context;
	}

	@Override
	public void stop() {
		data = null;
		super.stop();
	}

	public boolean existFile(File file) {
		if (null == filesQueueMap.get(file.getPath())) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Response serve(String uri, Method method,
			Map<String, String> header, Map<String, String> parms,
			Map<String, String> files) {
		Log.d(TAG, "OK+");
		Log.d(TAG, "parmas" + parms.get("p"));
		Response res = null;

		if (0 != lock) {
			// TODO 其他客户端的请求显示该终端已被控制，请释放后重试。
			res = new Response(Response.Status.FORBIDDEN, MIME_PLAINTEXT,
					"FORBIDDEN: Reading file failed.");
			Log.e(TAG, "FORBIDDEN");
		} else {
			InputStream is = context.getResources().openRawResource(R.raw.test);
			res = new Response(Response.Status.OK, MIME_HTML, is);
			Log.d(TAG, "html");
			Log.d(TAG, "Test");
		}

		String p = parms.get("p");
		do {
			if (p != null)
				if (p.equals(Constant.PARAM_WAKE_LOCK)) {
					handler.obtainMessage(Constant.WAKE_LOCK_CHANGESTATUS)
							.sendToTarget();
					Log.d(TAG, "WAKE_LOCK_CHANGESTATUS");
					break;
				}
			if (p.equals(Constant.PARAM_CHANGE_SCREENLOCK)) {
				handler.obtainMessage(Constant.OPEN_SCREENLOCK).sendToTarget();
				Log.d(TAG, "OPEN_SCREENLOCK");
				break;
			}
			if (InstagramActivity.instance == null) {
				break;
			}
			if (p.equals(Constant.PARAM_AUTO_FOCUS)) {
				InstagramActivity.instance.handler.obtainMessage(
						Constant.SERVER_CAMERA_FOCUS).sendToTarget();
				break;
			}
			if (p.equals(Constant.PARAM_TAKEPHOTO)) {
				InstagramActivity.instance.handler.obtainMessage(
						Constant.SERVER_TAKE_PHOTO).sendToTarget();
				break;
			}
			if (p.equals(Constant.PARAM_CATCH_PIC)) {
				if (data == null)
					res = new Response(Response.Status.FORBIDDEN,
							MIME_PLAINTEXT, "0");
				else {
					int width = InstagramActivity.instance.parameters
							.getPreviewSize().width;
					int height = InstagramActivity.instance.parameters
							.getPreviewSize().height;

					YuvImage yuv = new YuvImage(data,
							InstagramActivity.instance.parameters
									.getPreviewFormat(), width, height, null);

					ByteArrayOutputStream out = new ByteArrayOutputStream();
					yuv.compressToJpeg(new Rect(0, 0, width, height), 20, out);
					byte[] bytes = out.toByteArray();
					res = new Response(Response.Status.OK,
							MIME_PLAINTEXT, "");
					res.setData(new ByteArrayInputStream(bytes));
				}
				break;
			}
		} while (false);
		return res;
	}

}