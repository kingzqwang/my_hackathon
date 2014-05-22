package com.qihoo.huangmabisheng.httpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;

import com.qihoo.huangmabisheng.R;
import com.qihoo.huangmabisheng.constant.Constant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

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
			Log.d(TAG, "FORBIDDEN");
		} else {
			InputStream is = context.getResources().openRawResource(R.raw.test);
			res = new Response(Response.Status.OK, MIME_HTML, is);
			Log.d(TAG, "html");
			Log.d(TAG, "Test");
		}
		
		String p = parms.get("p");
		if(p!=null)
		if ( p.equals("0")) {
			handler.obtainMessage(Constant.WAKE_LOCK_CHANGESTATUS)
					.sendToTarget();
			Log.d(TAG, "WAKE_LOCK_CHANGESTATUS");
		} else if (p.equals("1")) {
			handler.obtainMessage(Constant.OPEN_SCREENLOCK).sendToTarget();
			Log.d(TAG, "OPEN_SCREENLOCK");
		}
		

		return res;
	}
}