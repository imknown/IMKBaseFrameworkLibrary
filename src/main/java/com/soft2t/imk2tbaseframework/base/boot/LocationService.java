package com.soft2t.imk2tbaseframework.base.boot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service {
	@Override
	public void onCreate() {
		// MyToastManager.showToast(this, "开始执行 LocationService 服务了");
		Log.e("■", "开始执行 LocationService 服务了");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
