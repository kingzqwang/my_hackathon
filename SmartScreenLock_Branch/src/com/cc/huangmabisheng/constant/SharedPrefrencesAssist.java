package com.cc.huangmabisheng.constant;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPrefrencesAssist {
	private Context appContext;
	private SharedPreferences sharedPreferences;
	public HttpClient hc;

	public SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}

	private static SharedPrefrencesAssist sharedPreferencesAssist;
	private void createThreadSafeHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		HttpConnectionParams.setSoTimeout(params, 3000); 
		//ConnManagerParams.setTimeout(params, 1);
		//params.setIntParameter("http.socket.timeout", 3000);
		ConnManagerParams.setMaxTotalConnections(params, 40);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, true);
		SchemeRegistry _schReg = new SchemeRegistry();
		_schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
//		_schReg.register(new Scheme("https", SSLSocketFactory
//				.getSocketFactory(), 443));
		ClientConnectionManager _connMgr = new ThreadSafeClientConnManager(
				params, _schReg);
		
		hc = new DefaultHttpClient(_connMgr, params);
		if (hc.getConnectionManager() == null) {
			Log.d("SharedPrefrencesAssist","create ,ClientConnectionManager is null");
		}
	}
	private SharedPrefrencesAssist(Context appContext) {

		this.appContext = appContext;
		sharedPreferences = appContext.getSharedPreferences(Constant.APP_NAME,
				android.content.Context.MODE_PRIVATE);
		createThreadSafeHttpClient();
	}

	public static SharedPrefrencesAssist instance(Context context) {
		if (null != sharedPreferencesAssist) {
			return sharedPreferencesAssist;
		} else {
			Context appContext = context.getApplicationContext();
			sharedPreferencesAssist = new SharedPrefrencesAssist(appContext);
			return sharedPreferencesAssist;
		}
	}

	public Context getAppContext() {
		return appContext;
	}

	public boolean write(String key, String value) {
		return sharedPreferences.edit().putString(key, value).commit();
	}

	public boolean write(String key, int value) {
		return sharedPreferences.edit().putInt(key, value).commit();
	}

	public String read(String key) {
		return sharedPreferences.getString(key, null);
	}
	public Boolean readBoolean(String key) {
		return sharedPreferences.getBoolean(key, false);
	}
	public Boolean readBoolean(String key,boolean d) {
		return sharedPreferences.getBoolean(key, d);
	}
	public int readInt(String key) {
		return sharedPreferences.getInt(key, 0);
	}
	public boolean writeInt(String key,int value) {
		return sharedPreferences.edit().putInt(key, value).commit();
	}
	public boolean writeBoolean(String key,boolean value) {
		return sharedPreferences.edit().putBoolean(key, value).commit();
	}
}
