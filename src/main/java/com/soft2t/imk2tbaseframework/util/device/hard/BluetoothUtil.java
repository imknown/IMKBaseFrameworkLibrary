package com.soft2t.imk2tbaseframework.util.device.hard;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

public class BluetoothUtil {

	/** 请求打开 蓝牙 */
	public static final int REQUEST_ENABLE_BT = 3;

	/** 获取 蓝牙地址 */
	public static String getBluetoothAddress(boolean openOrNot, Activity activity) {
		String btMac = "";

		BluetoothAdapter bAdapt = BluetoothAdapter.getDefaultAdapter();

		if (bAdapt != null) {
			if (openOrNot && !bAdapt.isEnabled()) {
				Intent enBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				activity.startActivityForResult(enBT, REQUEST_ENABLE_BT);
			}

			btMac = bAdapt.getAddress();
		}

		return btMac;
	}

	// public void onActivityResult(int requestCode, int resultCode, Intent data) {
	// switch (requestCode) {
	//
	// case REQUEST_ENABLE_BT:
	// // When the request to enable Bluetooth returns
	// if (resultCode == Activity.RESULT_OK) {
	// // Bluetooth is now enabled, so set up a chat session
	// } else {
	// a.finish();
	// }
	// }
	// }
}
