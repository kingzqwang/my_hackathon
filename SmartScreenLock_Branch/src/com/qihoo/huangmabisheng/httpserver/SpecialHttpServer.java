package com.qihoo.huangmabisheng.httpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import com.qihoo.huangmabisheng.constant.Constant;
import com.qihoo.huangmabisheng.utils.Log;
import android.annotation.SuppressLint;
import android.os.Handler;

/**
 * HttpdServer
 * 
 * 一次性服务于一个客户端
 */
public class SpecialHttpServer extends NanoHTTPD {
	final String TAG = "HttpServer";
	private Handler handler;
	/**
	 * 服务的客户端id，即为lock，lock总是大于0的。
	 */
	private int lock = 0;
	private Map<String, BlockingDeque<File>> filesQueueMap = new HashMap<String, BlockingDeque<File>>();
	private static SpecialHttpServer httpdServer;

	public static SpecialHttpServer instance(Handler handler)
			throws IOException {
		if (null == handler)
			httpdServer = new SpecialHttpServer(handler);
		return httpdServer;
	}

	/**
	 * Constructs an HTTP server on given port.
	 */
	private SpecialHttpServer(Handler handler) throws IOException {
		super(Constant.PORT);
		this.handler = handler;
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
		if (0 == lock) {
			// TODO 其他客户端的请求显示该终端已被控制，请释放后重试。
		}
		Log.d(TAG, "OK+");
		Log.d(TAG, "parmas" + parms.get("type"));
		Response res = null;
		if ("connect".equals(parms.get("type"))) {
			Log.d(TAG, "connect+blocking");
			if (null == filesQueueMap.get(parms.get("tid"))) {
				//
			}

			BlockingDeque<File> blockingDeque = instance(parms.get("tid"));
			File file = null;
			try {
				// Log.d(TAG, "blockingDeque take前有 "+blockingDeque.size());
				file = blockingDeque.take();// (180, TimeUnit.SECONDS);
				// Log.d(TAG, "blockingDeque take后 "+blockingDeque.size());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
			if (null != file) {

				// Log.d(TAG, "blockingDeque push前 "+blockingDeque.size());
				blockingDeque.push(file);
			}
			// Log.d(TAG, "blockingDeque push后 "+blockingDeque.size());
			res = new Response(Response.Status.OK, MIME_PLAINTEXT, ""
					+ blockingDeque.size());
			// Log.d(TAG, "connect+"+blockingDeque.size());
		} else if ("download".equals(parms.get("type"))) {

			try {
				BlockingDeque<File> blockingDeque = instance(parms.get("tid"));
				File f = blockingDeque.poll();
				String mime = "application/octet-stream";
				long fileLen = f.length();
				res = new Response(Response.Status.OK, mime,
						new FileInputStream(f));
				res.addHeader("Content-Length", "" + fileLen);
				res.addHeader("Content-Disposition",
						"attachment;filename=" + f.getName());
				Log.d(TAG, "download+" + f.getPath());
			} catch (IOException ioe) {
				res = new Response(Response.Status.FORBIDDEN, MIME_PLAINTEXT,
						"FORBIDDEN: Reading file failed.");
			}
		} else if ("exist".equals(parms.get("type"))) {
			Log.d(TAG, "rcv exist");
			filesQueueMap.remove(parms.get("tid"));
			for (int i = 0; i < filesQueueMap.size(); i++) {
				// handler.obtainMessage(Constant.ADD_ONE_SERVICE).sendToTarget();
			}
		}
		// try {
		// File f = new File(path.get(0));
		// String mime = "application/octet-stream";
		// long fileLen = f.length();
		// res = new Response(Response.Status.OK, mime, new FileInputStream(f));
		// res.addHeader("Content-Length", "" + fileLen);
		// res.addHeader("Content-Disposition",
		// "attachment;filename=" + f.getName());
		// Log.d(TAG, f.getName());
		// } catch (IOException ioe) {
		// res = new Response(Response.Status.FORBIDDEN, MIME_PLAINTEXT,
		// "FORBIDDEN: Reading file failed.");
		// }
		return res;
	}

	private BlockingDeque<File> instance(String string) {
		if (null == filesQueueMap.get(string)) {
			Log.d(TAG, "该队列不存在");
			filesQueueMap.put(string, new LinkedBlockingDeque<File>());
		}
		Log.d(TAG, "该队列存在");
		return filesQueueMap.get(string);
	}

	// public static void main(String[] args) {
	// System.out.print("xxx");
	// try {
	// HttpServer s = new HttpServer(path);
	// s.start();
	// } catch (IOException ioe) {
	// System.out.println("Couldn't start server:\n" + ioe);
	// System.exit(-1);
	// }
	// System.out.println("Listening on port 8080. Hit Enter to stop.\n");
	// try {
	// System.in.read();
	// } catch (Throwable t) {
	// System.out.println("read error");
	// }
	// ;
	// }

}