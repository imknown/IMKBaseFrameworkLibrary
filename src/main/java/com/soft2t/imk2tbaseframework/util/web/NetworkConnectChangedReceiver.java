package com.soft2t.imk2tbaseframework.util.web;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

public class NetworkConnectChangedReceiver extends BroadcastReceiver {

	public interface INetworkConnectChangedReceiver {

		public void onApSsidConnected();

		public void onWifiDisEnabled();
	}

	private INetworkConnectChangedReceiver iNetworkConnectChangedReceiver;

	public void setiNetworkConnectChangedReceiver(INetworkConnectChangedReceiver iNetworkConnectChangedReceiver) {
		this.iNetworkConnectChangedReceiver = iNetworkConnectChangedReceiver;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// 这个监听wifi的打开与关闭，与wifi的连接无关
		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
			switch (wifiState) {
			case WifiManager.WIFI_STATE_ENABLED:
				Log.e("wifi是否打开", "WIFI_STATE_ENABLED");
				break;
			case WifiManager.WIFI_STATE_ENABLING:
				Log.e("wifi是否打开", "WIFI_STATE_ENABLING");
				break;
			case WifiManager.WIFI_STATE_DISABLED:
				Log.e("wifi是否打开", "WIFI_STATE_DISABLED");
				if (iNetworkConnectChangedReceiver != null) {
					iNetworkConnectChangedReceiver.onWifiDisEnabled();
				}
				break;
			case WifiManager.WIFI_STATE_DISABLING:
				Log.e("wifi是否打开", "WIFI_STATE_DISABLING");
				break;
			}
		}
		// 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
		// 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，当然刚打开wifi肯定还没有连接到有效的无线
		if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
			Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (null != parcelableExtra) {
				NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
				switch (networkInfo.getState()) {
				case CONNECTED:

					Log.e("wifi是否连上了一个有效路由器", "CONNECTED");
					if (iNetworkConnectChangedReceiver != null) {
						iNetworkConnectChangedReceiver.onApSsidConnected();
					}
					break;
				case CONNECTING:
					Log.e("wifi是否连上了一个有效路由器", "CONNECTING");
					break;
				case DISCONNECTED:
					Log.e("wifi是否连上了一个有效路由器", "DISCONNECTED");
					break;
				case DISCONNECTING:
					Log.e("wifi是否连上了一个有效路由器", "DISCONNECTING");
					break;
				case SUSPENDED:
					Log.e("wifi是否连上了一个有效路由器", "SUSPENDED");
					break;
				case UNKNOWN:
					Log.e("wifi是否连上了一个有效路由器", "UNKNOWN");
					break;
				default:
					break;
				}
			}
		}
		// 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。
		// 最好用的还是这个监听。wifi如果打开，关闭，以及连接上可用的连接都会接到监听。见log
		// 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
			NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if (info != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("info.getTypeName() : " + info.getTypeName() + "\n");
				sb.append("getSubtypeName() : " + info.getSubtypeName() + "\n");
				sb.append("getState() : " + info.getState() + "\n");
				sb.append("getDetailedState() : " + info.getDetailedState().name() + "\n");
				sb.append("getDetailedState() : " + info.getExtraInfo() + "\n");
				sb.append("getType() : " + info.getType());
				Log.e("wifi", sb.toString());
			}
		}
	}
}