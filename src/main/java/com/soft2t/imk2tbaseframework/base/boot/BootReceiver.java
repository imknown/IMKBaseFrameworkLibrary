package com.soft2t.imk2tbaseframework.base.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			// MyToastManager.showToast(context, "接收到了开机完成的广播");
			Log.e("■", "接收到了开机完成的广播");

			Intent intentService = new Intent(context, LocationService.class);
			// intent.setAction("com.getlocation.service");
			context.startService(intentService);
		}
	}
}