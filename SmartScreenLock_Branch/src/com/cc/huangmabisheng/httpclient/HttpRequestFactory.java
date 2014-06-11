package com.cc.huangmabisheng.httpclient;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;

import com.cc.huangmabisheng.constant.Constant;
import com.cc.huangmabisheng.constant.SharedPrefrencesAssist;
import com.cc.huangmabisheng.utils.Log;


public class HttpRequestFactory {
	final String TAG = "HttpRequestFactory";
	private static HttpRequestFactory hrf;
	
	public HttpEntity get(String path,String...params) throws URISyntaxException, ClientProtocolException, IOException {
		return SharedPrefrencesAssist.instance(null).hc.execute(getRequest(path,createParameters(params))).getEntity();
	}
	public HttpEntity post(String path,String...params) throws URISyntaxException, ClientProtocolException, IOException {
		return SharedPrefrencesAssist.instance(null).hc.execute(postRequest(path,createParameters(params))).getEntity();
	}
	public HttpEntity post(int file ,String path,String fileName,String filePath,String...params) throws URISyntaxException, ClientProtocolException, IOException {
		HttpPost httpPost = postRequest(path,createParameters(params));
		httpPost.setEntity(documentParameter(fileName, filePath));
		HttpResponse httpResponse = SharedPrefrencesAssist.instance(null).hc.execute(httpPost);
		return httpResponse.getEntity();
	}
	
	private HttpRequestFactory() {
	}

	private MultipartEntity documentParameter(String name,String filePath) {
		MultipartEntity entity = new MultipartEntity();
		File file = new File(filePath);
		if (file != null && file.exists()) {
			entity.addPart(name,new FileBody(file));
		}
		return entity;
	}

	private List<NameValuePair> createParameters(String... params) {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		if (null != params) {
			for (String param : params) {
				String[] _params = param.split("=");
				if (_params.length == 1) {
					qparams.add(new BasicNameValuePair(_params[0], ""));
				}else {
					qparams.add(new BasicNameValuePair(_params[0], _params[1]));
				}
				
			}
		}
		return qparams;
	}

	private HttpPost postRequest(String path, List<NameValuePair> qparams)
			throws URISyntaxException {
		String ip = SharedPrefrencesAssist.instance(null).read("ip");
		
		URI uri = URIUtils.createURI("http", ip,Constant.PORT, path,
				URLEncodedUtils.format(qparams, "UTF-8"), null);
		HttpPost httpPost = new HttpPost(uri);
		Log.d(TAG, ip);
		return httpPost;
	}
	private HttpGet getRequest(String path, List<NameValuePair> qparams)
			throws URISyntaxException {
		String ip = SharedPrefrencesAssist.instance(null).read("ip");
		URI uri = URIUtils.createURI("http", ip,Constant.PORT, path,
				URLEncodedUtils.format(qparams, "UTF-8"), null);
		HttpGet httpGet = new HttpGet(uri);
		return httpGet;
	}
	public static HttpRequestFactory instance() {
		if (null == hrf) {
			hrf = new HttpRequestFactory();
		}
		return hrf;
	}
}
