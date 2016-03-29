package com.soft2t.imk2tbaseframework.util.web;

import com.soft2t.imk2tbaseframework.base.BaseApplication;
import com.soft2t.imk2tbaseframework.util.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiReceiver extends BroadcastReceiver {

	// <!-- wifi Receiver -->
	// <receiver android:name="com.soft2t.yanglaomanager.util.WifiReceiver" >
	// <intent-filter>
	// <action android:name="android.net.wifi.RSSI_CHANGED" />
	// <action android:name="android.net.wifi.STATE_CHANGE" />
	// <action android:name="android.net.wifi.WIFI_STATE_CHANGED" />
	// </intent-filter>
	// </receiver>

	public interface IWifiReceiverCallback {

		/** WIFI 硬件已准备好 */
		public void onWifiEnabled();

		/** AP RSSi 信号强度改变 */
		public void onRSSIChange();

		/** 已经连上了 AP 的 SSID */
		public void onApSsidConnected();

		/** 已经断开了 AP 的 SSID */
		public void onApSsidDisConnected();
	}

	private IWifiReceiverCallback iWifiReceiverCallback;

	public void setiWifiReceiverCallback(IWifiReceiverCallback iWifiReceiverCallback) {
		this.iWifiReceiverCallback = iWifiReceiverCallback;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// 使应用程序监控当前Wi-Fi连接的信号强度
		if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
			if (iWifiReceiverCallback != null) {
				iWifiReceiverCallback.onRSSIChange();

				int nrssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0);
				LogUtil.d("当前新的 WIFI RSSI = " + nrssi);
			}
			// signal strength changed
		} else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {// wifi连接上与否
			LogUtil.d("网络状态已改变");

			NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

			if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
				LogUtil.d("网络连接已断开");

				if (iWifiReceiverCallback != null) {
					iWifiReceiverCallback.onApSsidDisConnected();
				}
			} else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {

				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();

				// 获取当前wifi名称
				LogUtil.d("网络已连接到 " + wifiInfo.getSSID());

				if (iWifiReceiverCallback != null) {
					iWifiReceiverCallback.onApSsidConnected();
				}
			}

		} else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {// wifi打开与否
			int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);

			if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
				LogUtil.d("WIFI 硬件已被关闭");

				BaseApplication.dismissDialog();
			} else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
				LogUtil.d("WIFI 硬件已被开启");

				if (iWifiReceiverCallback != null) {
					iWifiReceiverCallback.onWifiEnabled();
				}
			}
		}
	}
}
