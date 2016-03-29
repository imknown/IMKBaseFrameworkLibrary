package com.soft2t.imk2tbaseframework.base;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.HttpHandler;

@SuppressLint({ "InlinedApi", "NewApi" })
public class BaseActivity extends FragmentActivity {
	/** 等待对话框 */
	protected ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ViewUtils.inject(this);

		BaseApplication.MyActivityManager.addActivity(this);

		// if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
		// Window window = getWindow();
		// window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		// window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
		// window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		// window.setStatusBarColor(Color.TRANSPARENT);
		// window.setNavigationBarColor(Color.TRANSPARENT);
		// }
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Deprecated
	protected void showWait(Context context, String waitMsg) {
		showWait(context, waitMsg, true, null);
	}

	protected void showWait(Context context, int resId) {
		showWait(context, context.getResources().getString(resId), true, null);
	}

	protected <T> void showWait(Context context, int resId, boolean cancelable, final HttpHandler<T> handler) {
		showWait(context, context.getResources().getString(resId), cancelable, handler);
	}

	/**
	 * 显示等待对话框, 等待对应网络连接任务<br />
	 * 当等待对话框被用户手动关闭后，网络连接任务终止
	 * 
	 * @param waitMsg
	 *            等待提示信息
	 */
	protected <T> void showWait(Context context, String waitMsg, boolean cancelable, final HttpHandler<T> handler) {
		dismissDialog();

		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(cancelable);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage(waitMsg);
		progressDialog.show();

		progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (handler != null) {
					handler.cancel();
				}
			}
		});
	}

	protected void dismissDialog() {
		try {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
