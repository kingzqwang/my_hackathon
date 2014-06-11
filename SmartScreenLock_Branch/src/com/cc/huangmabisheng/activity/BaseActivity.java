package com.cc.huangmabisheng.activity;

import java.util.ArrayList;
import java.util.Date;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BaseActivity extends Activity {
	String TAG;
//	TextView backTextView;
//	protected ArrayList<AsyncTask> httpTasks = new ArrayList<AsyncTask>();
	public Handler handler;
//	@Override
//	protected void onDestroy() {
//		for (AsyncTask task : httpTasks) {
//			task.cancel(true);
//		}
//		super.onDestroy();
//	}
//	@Override
//	protected void onResume() {
//		if (new Date().after(new Date(WjApplication.sharedPreferences.getLong("cdate", new Date(2013-1900,4,1).getTime()) + 30*24*3600000l))) {
//			finish();
//			System.exit(0);
//		}
//		super.onResume();
//	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		TAG = this.getClass().getSimpleName();
		handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				
				handleMsg(msg);
				super.handleMessage(msg);
			}
			
		};
//		backTextView = (TextView) findViewById(R.id.return_back_btn);
//		backTextView.setOnClickListener(new OnClickListener() {
//
//			public void onClick(View v) {
//				BaseActivity.this.finish();
//			}
//		});
		findAllViews();
		
		
		setAllListeners();
	}
	protected abstract void handleMsg(Message msg);
	protected abstract void setAllListeners();
	protected abstract void findAllViews();

}
