package com.qihoo.huangmabisheng.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.qihoo.huangmabisheng.constant.SharedPrefrencesAssist;

import android.content.Context;

public class FileUtil {
	private Context context;
	/**
	 * Construct
	 * @param Context
	 * @author wangzhiqing-xy
	 * 
	 * */
	public FileUtil(Context context){
		super();
		this.context = context;
		
	}
	
	public void save(String packageName,int count){
		SharedPrefrencesAssist.instance(context).write(packageName, count);
	}
	
	/**
	 * save file
	 * @param filename
	 * @param content
	 * */
	public void save(String filename, String content){
		FileOutputStream outstream;
		try {
			outstream = context.openFileOutput(filename, Context.MODE_APPEND);
			outstream.write(content.getBytes());
			outstream.write("\r\n".getBytes());
			outstream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param filename
	 * @return content
	 * @throws IOException 
	 * @throws Exception
	 * */
	public String read(String filename) throws IOException{
		FileInputStream instream = context.openFileInput(filename);
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while((len = instream.read(buffer)) != -1 ){
			outstream.write(buffer,0,len);
		}
		byte [] data = outstream.toByteArray();
		return new String(data);
		
	}

}
