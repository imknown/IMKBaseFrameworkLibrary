package com.soft2t.imk2tbaseframework.base;

import android.app.Activity;

import com.lidroid.xutils.ViewUtils;

public class BaseHolder {

	public BaseHolder(Activity activity) {
		ViewUtils.inject(this, activity);
	}
}
