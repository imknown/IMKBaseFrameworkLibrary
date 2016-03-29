package com.soft2t.imk2tbaseframework.util.device.hard;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

public class ScreenBrightnessController {
	private Activity activity;

	public ScreenBrightnessController(Activity activity) {
		this.activity = activity;
	}

	/**
	 * 获得当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度 SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
	 */
	public int getScreenMode() {
		int screenMode = 0;
		try {
			screenMode = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return screenMode;
	}

	/**
	 * 获得当前屏幕亮度值 0--255
	 */
	public int getScreenBrightness() {
		int screenBrightness = 255;
		try {
			screenBrightness = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return screenBrightness;
	}

	/**
	 * 设置当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度 SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
	 */
	public void setScreenMode(int paramInt) {
		try {
			Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	/**
	 * 设置当前屏幕亮度值 0--255
	 */
	public void saveScreenBrightness(int paramInt) {
		try {
			Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	/**
	 * 保存当前的屏幕亮度值，并使之生效
	 */
	public void setScreenBrightness(int paramInt) {
		Window localWindow = activity.getWindow();
		WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
		float f = paramInt / 255.0F;
		localLayoutParams.screenBrightness = f;
		localWindow.setAttributes(localLayoutParams);
	}

	/** 停止自动亮度调节 */
	public void stopAutoBrightness() {

		Settings.System.putInt(activity.getContentResolver(),

		Settings.System.SCREEN_BRIGHTNESS_MODE,

		Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
	}

	/**
	 * 开启亮度自动调节
	 * 
	 * @param activity
	 */
	public void startAutoBrightness() {

		Settings.System.putInt(activity.getContentResolver(),

		Settings.System.SCREEN_BRIGHTNESS_MODE,

		Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);

	}

	/** 保存亮度设置状态 */
	public void saveBrightness(ContentResolver resolver, int brightness) {

		Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");

		android.provider.Settings.System.putInt(resolver, "screen_brightness", brightness);

		// resolver.registerContentObserver(uri, true, myContentObserver);

		resolver.notifyChange(uri, null);
	}
}
