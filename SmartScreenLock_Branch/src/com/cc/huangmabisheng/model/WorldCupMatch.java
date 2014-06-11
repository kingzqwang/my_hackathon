package com.cc.huangmabisheng.model;

import java.io.Serializable;
import java.util.Date;

public class WorldCupMatch{
	public int lv;
	public int rv;
	public Date start;//21:00
	public boolean clockable = false;
	public WorldCupMatch(int lv, int rv,  Date start) {
		super();
		this.lv = lv;
		this.rv = rv;
		this.start = start;
	}
	
}
