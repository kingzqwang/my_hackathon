package com.qihoo.huangmabisheng.interfaces;

import java.util.List;
import java.util.Map.Entry;

import com.qihoo.huangmabisheng.model.AppDataForList;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public interface IUpdatePackageIcon {
	public void updatePackageGuess(String pck) throws NameNotFoundException;
	void updatePackageIcon(List<AppDataForList> list)
			throws NameNotFoundException;
}
