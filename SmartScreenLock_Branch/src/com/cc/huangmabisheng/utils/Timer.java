package com.cc.huangmabisheng.utils;

import java.util.TimerTask;

public class Timer {
	Thread thread = null;
	boolean stoped = false;
	public void schedule(final TimerTask task,int delay,final long sleep) {
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (!Thread.interrupted() && !stoped) {
					task.run();
					try {
						Thread.sleep(sleep);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
				}
				
			}
		});
		thread.start();
	}
	public void cancel() {
		stoped = true;
		thread.interrupt();
		thread = null;
	}
}
