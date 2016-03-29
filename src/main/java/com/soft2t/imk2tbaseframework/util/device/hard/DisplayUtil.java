package com.soft2t.imk2tbaseframework.util.device.hard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * densityDpi 表示, 每英寸多少个像素点,<br/>
 * density = densityDpi / 160 (谷歌定义的标准为160),<br/>
 * density = resolution (分辨率) / Screen size (屏幕尺寸)
 * 
 * @author imknown
 */
public class DisplayUtil {
	/** 屏幕测量 */
	private DisplayMetrics dm;

	private Context context;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 */
	public DisplayUtil(Context context) {
		this.context = context;
		this.dm = context.getResources().getDisplayMetrics();
	}

	// 换算 =========================

	/**
	 * 
	 * @param dipValue
	 * @param fixFloat
	 *            是否微偏移, 建议不要偏移, 会有误差
	 * @return
	 */
	@Deprecated
	public float dip2px_M1(float dipValue, boolean fixFloat) {
		final float density = context.getResources().getDisplayMetrics().density;

		float fixOffext = 0.5f * (dipValue >= 0 ? 1 : -1);

		return (dipValue * density + (fixFloat ? fixOffext : 0));
	}

	/**
	 * 
	 * @param pxValue
	 * @param fixFloat
	 *            是否微偏移, 建议不要偏移, 会有误差
	 * @return
	 */
	@Deprecated
	public float px2dip_M1(float pxValue, boolean fixFloat) {
		final float density = context.getResources().getDisplayMetrics().density;

		float fixOffext = 0.5f * (pxValue >= 0 ? 1 : -1);

		return (pxValue / density + (fixFloat ? (fixOffext) : 0));
	}

	public float dip2px_M2(float dipValue) {
		return dipValue * getDensity();
	}

	public float px2dip_M2(float pxValue) {
		return pxValue / getDensity();
	}

	public float dip2px_M3(float dipValue) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, dm);
	}

	public float px2dip_M3(float pxValue) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pxValue, dm);
	}

	// 常量 =========================

	/** 160, 240 等 */
	public float getDpi() {
		return dm.densityDpi;
	}

	/** 除以 160 之后的结果 */
	public float getDensity() {
		return dm.density;
	}

	// 硬件分辨率 =========================

	/**
	 * 获得屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	@Deprecated
	public int getScreenWidth_M1() {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 获得屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	@Deprecated
	public int getScreenHeight_M1() {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	/**
	 * 获得屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public int getScreenWidth_M2() {
		return dm.widthPixels;
	}

	/**
	 * 获得屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public int getScreenHeight_M2() {
		return dm.heightPixels;
	}

	// 状态栏 =========================

	/**
	 * 获取 状态栏高度
	 * 
	 * @param a
	 *            当前Activity对象
	 * @return 状态栏高度(px)
	 */
	public static int getStatusBarHeight(Activity a) {
		Rect frame = new Rect();
		a.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;// 状态栏高度

		return statusBarHeight;
	}

	/**
	 * 获得 状态栏高度
	 * 
	 * @param context
	 * @return 状态栏高度(px)
	 */
	public static int getStatusBarHeight_M2(Context context) {
		int statusHeight = -1;

		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();

			int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());

			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return statusHeight;
	}

	/**
	 * 获取 标题栏高度
	 * 
	 * @param a
	 *            当前Activity对象
	 * @return 标题栏高度(px)
	 */
	public static int getTitleBarHeight(Activity a) {
		View v = a.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
		int contentTop = v.getTop();
		int titleBarHeight = contentTop - getStatusBarHeight(a);// 标题栏高度

		if (titleBarHeight < 0) {
			titleBarHeight = 0;
		}

		return titleBarHeight;
	}

	/**
	 * 获取当前屏幕截图，包含状态栏
	 * 
	 * @param activity
	 * @return
	 */
	public Bitmap snapShotWithStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = getScreenWidth_M2();
		int height = getScreenHeight_M2();
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;
	}

	/**
	 * 获取当前屏幕截图，不包含状态栏
	 * 
	 * @param activity
	 * @return
	 */
	public Bitmap snapShotWithoutStatusBar(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		int width = getScreenWidth_M2();
		int height = getScreenHeight_M2();
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
		view.destroyDrawingCache();
		return bp;
	}
}
