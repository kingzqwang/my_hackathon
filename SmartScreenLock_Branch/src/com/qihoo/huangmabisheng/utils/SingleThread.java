package com.qihoo.huangmabisheng.utils;

public class SingleThread implements Runnable {
	Thread thisThread;
	boolean stoped = false;
	Runnable runnable;
	public SingleThread(Runnable runnable) {
		thisThread = new Thread(this);
		this.runnable = runnable;
	}
	
	public void start() {
		thisThread.start();
	}

	public void stop() {
		stoped = true;
		thisThread.interrupt();
	}

	@Override
	public void run() {
		while (!stoped && !Thread.interrupted()) {
			runnable.run();
		}
	}

}