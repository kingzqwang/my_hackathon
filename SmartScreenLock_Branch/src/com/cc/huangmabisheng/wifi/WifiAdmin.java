package com.cc.huangmabisheng.wifi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;




import com.cc.huangmabisheng.constant.Constant;
import com.cc.huangmabisheng.constant.Constant.WIFI_STATUS;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * wifi管理类
 * 来源：<a href="http://www.eoeandroid.com/thread-296427-1-1.html">Android之高仿飞鸽传书WIFI热点搜索与创建</a>
 * @author  way
 * 
 */
public class WifiAdmin {
	public enum AP_TYPE{
		WT,AP
	}
	private Context context;
	private static WifiAdmin wiFiAdmin = null;
	private List<WifiConfiguration> mWifiConfiguration;
	private WifiInfo mWifiInfo;
	private List<ScanResult> mWifiList;
	WifiManager.WifiLock mWifiLock;
	public WifiManager mWifiManager;

	private WifiAdmin(Context paramContext) {
		this.context = paramContext;
		this.mWifiManager = ((WifiManager) paramContext
				.getSystemService("wifi"));
		this.mWifiInfo = this.mWifiManager.getConnectionInfo();
	}

	public static WifiAdmin getInstance(Context paramContext) {
//		if (wiFiAdmin == null)
		wiFiAdmin = new WifiAdmin(paramContext);
		return wiFiAdmin;
	}

	public WifiConfiguration isExsits(String paramString) {
		List<WifiConfiguration> list = this.mWifiManager
				.getConfiguredNetworks();
		if (null == list) {
			return null;
		}
		Iterator localIterator = list.iterator();
		WifiConfiguration localWifiConfiguration;
		do {
			if (!localIterator.hasNext())
				return null;
			localWifiConfiguration = (WifiConfiguration) localIterator.next();
		} while (!localWifiConfiguration.SSID.equals("\"" + paramString + "\""));
		return localWifiConfiguration;
	}

	public void AcquireWifiLock() {
		this.mWifiLock.acquire();
	}

	public void CreatWifiLock() {
		this.mWifiLock = this.mWifiManager.createWifiLock("Test");
	}

	public Constant.WIFI_STATUS openWifi() {
		if (!this.mWifiManager.isWifiEnabled()){
			this.mWifiManager.setWifiEnabled(true);
			return Constant.WIFI_STATUS.CLOSED;
		}else {
			return Constant.WIFI_STATUS.OPENED;
		}
	}
	public void openWifi(WIFI_STATUS ws) {
		if (ws == WIFI_STATUS.OPENED)
			this.mWifiManager.setWifiEnabled(true);
		else {
			return;
		}
	}
	public void ReleaseWifiLock() {
		if (this.mWifiLock.isHeld())
			this.mWifiLock.acquire();
	}

	public boolean addNetwork(WifiConfiguration paramWifiConfiguration) {
		int i = this.mWifiManager.addNetwork(paramWifiConfiguration);
		if (-1 == i) {
			return false;
		}
		return this.mWifiManager.enableNetwork(i, true);
	}
	
	/**
	 *	@return 关闭前的wifi状态 
	 */
	public Constant.WIFI_STATUS closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			this.mWifiManager.setWifiEnabled(false);
			return Constant.WIFI_STATUS.OPENED;
		}
		return Constant.WIFI_STATUS.CLOSED;
	}

	public void connectConfiguration(int paramInt) {
		if (paramInt > this.mWifiConfiguration.size())
			return;
		this.mWifiManager
				.enableNetwork(((WifiConfiguration) this.mWifiConfiguration
						.get(paramInt)).networkId, true);
	}

	/**
	 * 根据wifi信息创建一个热点
	 * 
	 * @param paramWifiConfiguration
	 * @param paramBoolean
	 */
	public void createWiFiAP(WifiConfiguration paramWifiConfiguration) {
		try {
			Class localClass = this.mWifiManager.getClass();
			Class[] arrayOfClass = new Class[2];
			arrayOfClass[0] = WifiConfiguration.class;
			arrayOfClass[1] = Boolean.TYPE;
			Method localMethod = localClass.getMethod("setWifiApEnabled",
					arrayOfClass);
			WifiManager localWifiManager = this.mWifiManager;
			Object[] arrayOfObject = new Object[2];
			arrayOfObject[0] = paramWifiConfiguration;
			arrayOfObject[1] = Boolean.valueOf(true);
			localMethod.invoke(localWifiManager, arrayOfObject);
			return;
		} catch (Exception localException) {
		}
	}
	/**
	 * 根据wifi信息关闭热点
	 * 
	 * @param paramWifiConfiguration
	 * @param paramBoolean
	 */
	public void closeWiFiAP(WifiConfiguration paramWifiConfiguration) {
		try {
			Class localClass = this.mWifiManager.getClass();
			Class[] arrayOfClass = new Class[2];
			arrayOfClass[0] = WifiConfiguration.class;
			arrayOfClass[1] = Boolean.TYPE;
			Method localMethod = localClass.getMethod("setWifiApEnabled",
					arrayOfClass);
			WifiManager localWifiManager = this.mWifiManager;
			Object[] arrayOfObject = new Object[2];
			arrayOfObject[0] = paramWifiConfiguration;
			arrayOfObject[1] = Boolean.valueOf(false);
			localMethod.invoke(localWifiManager, arrayOfObject);
			return;
		} catch (Exception localException) {
		}
	}

	/**
	 * 
	 * 创建一个wifi信息
	 * 
	 * @param ssid
	 *            名称
	 * 
	 * @param paramString2
	 *            密码
	 * 
	 * @param paramInt
	 *            有3个参数，1是无密码，2是简单密码，3是wap加密
	 * 
	 * @param paramString3
	 *            是"ap"还是"wt"
	 * 
	 * @return
	 */
	public WifiConfiguration createWifiInfo(String ssid, String paramString2,
			int paramInt, AP_TYPE paramString3) {
		WifiConfiguration localWifiConfiguration1 = new WifiConfiguration();
		localWifiConfiguration1.allowedAuthAlgorithms.clear();
		localWifiConfiguration1.allowedGroupCiphers.clear();
		localWifiConfiguration1.allowedKeyManagement.clear();
		localWifiConfiguration1.allowedPairwiseCiphers.clear();
		localWifiConfiguration1.allowedProtocols.clear();
		switch (paramString3) {
		case WT:
			localWifiConfiguration1.SSID = (ssid);
			WifiConfiguration localWifiConfiguration2 = isExsits(ssid);
			if (localWifiConfiguration2 != null)
				mWifiManager.removeNetwork(localWifiConfiguration2.networkId);
			if (paramInt == 1) {
				localWifiConfiguration1.wepKeys[0] = "";
				localWifiConfiguration1.allowedKeyManagement.set(0);
				localWifiConfiguration1.wepTxKeyIndex = 0;
			} else if (paramInt == 2) {
				localWifiConfiguration1.hiddenSSID = true;
				localWifiConfiguration1.wepKeys[0] = (paramString2);
			} else {
				localWifiConfiguration1.preSharedKey = (paramString2);
				localWifiConfiguration1.hiddenSSID = true;
				localWifiConfiguration1.allowedAuthAlgorithms.set(0);
				localWifiConfiguration1.allowedGroupCiphers.set(2);
				localWifiConfiguration1.allowedKeyManagement.set(1);
				localWifiConfiguration1.allowedPairwiseCiphers.set(1);
				localWifiConfiguration1.allowedGroupCiphers.set(3);
				localWifiConfiguration1.allowedPairwiseCiphers.set(2);
			}
			
			break;
		case AP:
			localWifiConfiguration1.SSID = ssid;
			localWifiConfiguration1.allowedAuthAlgorithms.set(1);
			localWifiConfiguration1.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.CCMP);
			localWifiConfiguration1.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.TKIP);
			localWifiConfiguration1.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP40);
			localWifiConfiguration1.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			localWifiConfiguration1.allowedKeyManagement.set(0);
			localWifiConfiguration1.wepTxKeyIndex = 0;
			if (paramInt == 1) {
				localWifiConfiguration1.wepKeys[0] = "";
				localWifiConfiguration1.allowedKeyManagement.set(0);
				localWifiConfiguration1.wepTxKeyIndex = 0;
			} else if (paramInt == 2) {
				localWifiConfiguration1.hiddenSSID = true;
				localWifiConfiguration1.wepKeys[0] = paramString2;
			} else if (paramInt == 3) {
				localWifiConfiguration1.preSharedKey = paramString2;
				localWifiConfiguration1.allowedAuthAlgorithms.set(0);
				localWifiConfiguration1.allowedProtocols.set(1);
				localWifiConfiguration1.allowedProtocols.set(0);
				localWifiConfiguration1.allowedKeyManagement.set(1);
				localWifiConfiguration1.allowedPairwiseCiphers.set(2);
				localWifiConfiguration1.allowedPairwiseCiphers.set(1);
			}
			break;

		default:
			break;
		}
		return localWifiConfiguration1;
	}

	public WifiConfiguration createWifiInfo(String SSID, String Password,
			int Type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = this.isExsits(SSID);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}

		if (Type == 1) // WIFICIPHER_NOPASS
		{
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 2) // WIFICIPHER_WEP
		{
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + Password + "\"";
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers
					.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (Type == 3) // WIFICIPHER_WPA
		{
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		return config;
	}

	/**
	 * 端口指定id的wifi
	 * 
	 * @param paramInt
	 */
	public void disconnectWifi(int paramInt) {
		this.mWifiManager.disableNetwork(paramInt);
	}

	/**
	 * 获取热点名
	 * 
	 * @return
	 */
	public String getApSSID() {
		try {
			Method localMethod = this.mWifiManager.getClass()
					.getDeclaredMethod("getWifiApConfiguration", new Class[0]);
			if (localMethod == null)
				return null;
			Object localObject1 = localMethod.invoke(this.mWifiManager,
					new Object[0]);
			if (localObject1 == null)
				return null;
			WifiConfiguration localWifiConfiguration = (WifiConfiguration) localObject1;
			if (localWifiConfiguration.SSID != null)
				return localWifiConfiguration.SSID;
			Field localField1 = WifiConfiguration.class
					.getDeclaredField("mWifiApProfile");
			if (localField1 == null)
				return null;
			localField1.setAccessible(true);
			Object localObject2 = localField1.get(localWifiConfiguration);
			localField1.setAccessible(false);
			if (localObject2 == null)
				return null;
			Field localField2 = localObject2.getClass()
					.getDeclaredField("SSID");
			localField2.setAccessible(true);
			Object localObject3 = localField2.get(localObject2);
			if (localObject3 == null)
				return null;
			localField2.setAccessible(false);
			String str = (String) localObject3;
			return str;
		} catch (Exception localException) {
		}
		return null;
	}

	/**
	 * 获取wifi名
	 * 
	 * @return
	 */
	public String getBSSID() {
		if (this.mWifiInfo == null)
			return "NULL";
		return this.mWifiInfo.getBSSID();
	}

	public List<WifiConfiguration> getConfiguration() {
		return this.mWifiConfiguration;
	}

	/**
	 * 获取ip地址
	 * 
	 * @return
	 */
	public int getIPAddress() {
		if (this.mWifiInfo == null)
			return 0;
		return this.mWifiInfo.getIpAddress();
	}

	public String getIPAddressStr() {
		int ip = getIPAddress();
		String ipString = String.format("%d.%d.%d.%d", (ip & 0xff),
				(ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));

		return ipString;
	}

	/**
	 * 获取物理地址
	 * 
	 * @return
	 */
	public String getMacAddress() {
		if (this.mWifiInfo == null)
			return "NULL";
		return this.mWifiInfo.getMacAddress();
	}

	/**
	 * 获取网络id
	 * 
	 * @return
	 */
	public int getNetworkId() {
		if (this.mWifiInfo == null)
			return 0;
		return this.mWifiInfo.getNetworkId();
	}

	/**
	 * 获取热点创建状态
	 * 
	 * @return
	 */
	public int getWifiApState() {
		try {
			int i = ((Integer) this.mWifiManager.getClass()
					.getMethod("getWifiApState", new Class[0])
					.invoke(this.mWifiManager, new Object[0])).intValue();
			return i;
		} catch (Exception localException) {
		}
		return 4;
	}

	/**
	 * 获取wifi连接信息
	 * 
	 * @return
	 */
	public WifiInfo getWifiInfo() {
		return this.mWifiManager.getConnectionInfo();
	}

	public List<ScanResult> getWifiList() {
		return this.mWifiList;
	}

	public StringBuilder lookUpScan() {
		StringBuilder localStringBuilder = new StringBuilder();
		for (int i = 0;; i++) {
			if (i >= 2)
				return localStringBuilder;
			localStringBuilder.append("Index_" + new Integer(i + 1).toString()
					+ ":");
			localStringBuilder.append(((ScanResult) this.mWifiList.get(i))
					.toString());
			localStringBuilder.append("/n");
		}
	}

	/**
	 * 设置wifi搜索结果
	 */
	public void setWifiList() {
		this.mWifiList = this.mWifiManager.getScanResults();
	}

	/**
	 * 开始搜索wifi
	 */
	public void startScan() {
		this.mWifiManager.startScan();
	}

	public boolean isWifiConnect() {
		boolean isConnect = true;
		if (!((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected())
			isConnect = false;
		return isConnect;
	}
}