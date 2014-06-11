package com.cc.huangmabisheng.view;

import com.cc.huangmabisheng.R;
import com.cc.huangmabisheng.interfaces.EventListener;
import com.cc.huangmabisheng.utils.Log;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CloudDialog extends Dialog {
	final String TAG="CloudDialog"; 
	String yes, no, title,info;
	TextView titleTextView, noTextView, yesTextView;
	EditText infoEditText;
	EventListener noListener, yesListener;

	private void construct() {
		infoEditText = (EditText) findViewById(R.id.dialog_info);
		titleTextView = (TextView) findViewById(R.id.dialog_title);
		noTextView = (TextView) findViewById(R.id.no_textview);
		yesTextView = (TextView) findViewById(R.id.yes_textview);
		infoEditText.setText(info);
		titleTextView.setText(title);
		yesTextView.setText(yes);
		noTextView.setText(no);
		noTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(null!=noListener)noListener.onEventExecute(null);
				CloudDialog.this.cancel();
			}
		});
		yesTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(null!=yesListener)yesListener.onEventExecute(infoEditText.getText().toString());
				CloudDialog.this.cancel();
			}
		});
	}

	// public CloudDialog(Context context, int theme) {
	// super(context, theme);
	// // TODO Auto-generated constructor stub
	// }
	//
	// public CloudDialog(Context context) {
	// super(context);
	// // TODO Auto-generated constructor stub
	// }

	public CloudDialog(Context context, String title,String info,
			String yes, String no, EventListener yesListener,
			EventListener noListener) {
		super(context,R.style.MyDialog);
		this.yesListener = yesListener;
		this.info = info==null?"192.168.0.1":info;
		this.noListener = noListener;
		this.title = title;
		this.yes = yes;
		this.no = no;
		Log.d(TAG,"构造函数");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate");
		setContentView(R.layout.cloud_dialog);
		construct();
	}

}
