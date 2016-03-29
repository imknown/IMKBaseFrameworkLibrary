package com.soft2t.imk2tbaseframework.util;

import android.util.Log;

import com.soft2t.imk2tbaseframework.base.BaseApplication;
import com.soft2t.imk2tbaseframework.base.Constant;

public class LogUtil {

	private final static String DEFAULT_TAG = BaseApplication.mApplicationContext != null ? BaseApplication.mApplicationContext.getPackageName() : "Imk2TFrameworkLog";

	public static void v(String msg) {
		v(DEFAULT_TAG, msg);
	}

	public static void v(String tag, String msg) {
		if (Constant.Debug.SHOW_DEVELOP_LOG) {
			Log.v(tag, msg);
		}
	}

	public static void d(String msg) {
		d(DEFAULT_TAG, msg);
	}

	public static void d(String tag, String msg) {
		if (Constant.Debug.SHOW_DEVELOP_LOG) {
			Log.d(tag, msg);
		}
	}

	public static void i(String msg) {
		i(DEFAULT_TAG, msg);
	}

	public static void i(String tag, String msg) {
		if (Constant.Debug.SHOW_DEVELOP_LOG) {
			Log.i(tag, msg);
		}
	}

	public static void w(String msg) {
		w(DEFAULT_TAG, msg);
	}

	public static void w(String tag, String msg) {
		if (Constant.Debug.SHOW_DEVELOP_LOG) {
			w(tag, msg, null);
		}
	}

	public static void w(String tag, String msg, Throwable t) {
		if (Constant.Debug.SHOW_DEVELOP_LOG) {
			if (t == null) {
				Log.w(tag, msg);
			} else {
				Log.w(tag, msg, t);
			}
		}
	}

	public static void e(String msg) {
		e(DEFAULT_TAG, msg);
	}

	public static void e(String tag, String msg) {
		if (Constant.Debug.SHOW_DEVELOP_LOG) {
			e(tag, msg, null);
		}
	}

	public static void e(String tag, String msg, Throwable t) {
		if (Constant.Debug.SHOW_DEVELOP_LOG) {
			if (t == null) {
				Log.e(tag, msg);
			} else {
				Log.e(tag, msg, t);
			}
		}
	}

	public static void wtf(String msg) {
		wtf(DEFAULT_TAG, msg);
	}

	public static void wtf(String tag, String msg) {
		if (Constant.Debug.SHOW_DEVELOP_LOG) {
			wtf(tag, msg, null);
		}
	}

	public static void wtf(String tag, String msg, Throwable t) {
		if (Constant.Debug.SHOW_DEVELOP_LOG) {
			if (t == null) {
				Log.wtf(tag, msg);
			} else {
				Log.wtf(tag, msg, t);
			}
		}
	}
}
