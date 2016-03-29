package com.soft2t.imk2tbaseframework.util.device.soft;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.soft2t.imk2tbaseframework.R;

public class ShortcutUtil {
	public static void createShortcutIcon(Context context, String name, Class<? extends Activity> mainClass) {
		Intent shortcutIntent = new Intent(context, mainClass);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		Intent addIntent = new Intent();
		// addIntent.putExtra("duplicate", false);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.mipmap.eclipse_adt_ic_launcher));
		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		context.sendBroadcast(addIntent);
	}
}
