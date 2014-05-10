package com.qihoo.huangmabisheng.interfaces;

import java.util.List;
import java.util.Map.Entry;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public interface IUpdatePackageIcon {
	public void updatePackageIcon(List<Entry<String, Integer>> list,String lastPackage) throws NameNotFoundException;
	public void updatePackageGuess(String pck) throws NameNotFoundException;
}
