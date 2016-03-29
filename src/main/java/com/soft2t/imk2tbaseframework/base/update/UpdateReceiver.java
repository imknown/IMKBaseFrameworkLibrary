package com.soft2t.imk2tbaseframework.base.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateReceiver extends BroadcastReceiver {
//	public static String UPDATE＿RECEIVER = "UpdateReceiver";
//	public static String FULL_APP_SAVE_PATH = "fullAppSavePath";

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

//		if (action.equals(UPDATE＿RECEIVER)) {
//			// Bundle bundle = intent.getExtras();
//			// intent.getStringExtra("downloadUrl");
//			// intent.getStringExtra("fileNameOnly");
//			String fullAppSavePath = intent.getStringExtra(FULL_APP_SAVE_PATH);
//
//			if (!TextUtils.isEmpty(fullAppSavePath)) {
//				UpdateService.install(context, fullAppSavePath);
//			}
//
//			context.removeStickyBroadcast(intent);
//		}
	}
}
