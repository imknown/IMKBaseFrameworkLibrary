package com.soft2t.imk2tbaseframework.util.device.soft;

import java.util.List;
import java.util.Map;

import com.soft2t.imk2tbaseframework.base.BaseApplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;

public class KeyboardUtil {

	private final static int show_flags = 0;
	private final static int hide_flags = 0;

	private static InputMethodManager imm;

	public static InputMethodManager getIMM() {
		if (imm == null) {
			imm = (InputMethodManager) (BaseApplication.mApplicationContext.getSystemService(Context.INPUT_METHOD_SERVICE));
		}

		return imm;
	}

	/** 打开输入软键盘 */
	public static void showSystemKeyBoard(View v) {
		getIMM().showSoftInput(v, show_flags);
	}

	/** 隐藏输入软键盘 */
	public static void hideSystemKeyBoard(View v) {
		getIMM().hideSoftInputFromWindow(v.getWindowToken(), show_flags);
	}

	/** 显示 或者 关闭 软键盘 */
	public static void toggleSoftInput() {
		getIMM().toggleSoftInput(show_flags, hide_flags);
	}

	/** 显示 或者 关闭 软键盘 */
	public static void toggleSoftInputFromWindow(View v) {
		getIMM().toggleSoftInputFromWindow(v.getWindowToken(), show_flags, hide_flags);
	}

	/** 显示 输入法选择器 */
	public static void showInputMethodPicker() {
		getIMM().showInputMethodPicker();
	}

	/** 获取 所有 输入法 列表 */
	public static List<InputMethodInfo> getInputMethodList() {
		return getIMM().getInputMethodList();
	}

	/** 获取 已启用 输入法 列表 */
	public static List<InputMethodInfo> getEnabledInputMethodList() {
		return getIMM().getEnabledInputMethodList();
	}

	/** 获取 所有输入法 子状态 */
	public static Map<InputMethodInfo, List<InputMethodSubtype>> getShortcutInputMethodsAndSubtypes() {
		return getIMM().getShortcutInputMethodsAndSubtypes();
	}

	/** 根据 输入法Info 获取 子状态 */
	public static List<InputMethodSubtype> getEnabledInputMethodSubtypeList(InputMethodInfo imi, boolean allowsImplicitlySelectedSubtypes) {
		return getIMM().getEnabledInputMethodSubtypeList(imi, allowsImplicitlySelectedSubtypes);
	}

	/**
	 * 获取虚拟键盘的 高度<br>
	 * 可能需要在 &lt;Activity> 写 android:windowSoftInputMode="adjustResize"
	 */
	public static void getVirtualKeyboardSize(Activity a, final OnVirtualKeyboardGetCallback onVirtualKeyboardGetCallback) {
		final Window mRootWindow = a.getWindow();

		final View parentView = mRootWindow.getDecorView();

		final View mContentView = parentView.findViewById(android.R.id.content);
		final View mRootView = parentView.getRootView();

		ViewTreeObserver vto = mContentView.getViewTreeObserver();

		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				Rect r = new Rect();
				parentView.getWindowVisibleDisplayFrame(r);

				int[] rectResult = { r.left, r.top, r.right, r.bottom };

				int screenWidth = mRootView.getWidth();
				int keyboardWidth = screenWidth - (r.right - r.left);// Keyboard Width

				int screenHeight = mRootView.getHeight();
				int keyboardHeight = screenHeight - (r.bottom - r.top);// Keyboard Height

				onVirtualKeyboardGetCallback.onVirtualKeyboardGet(rectResult, keyboardWidth, keyboardHeight);
			}
		});
	}

	public interface OnVirtualKeyboardGetCallback {
		/**
		 * 当 获取到 输入法键盘 宽高等参数
		 * 
		 * @param rectResult
		 *            输入法键盘的 左上右下 的 坐标??
		 * @param keyboardWidth
		 *            输入法键盘的宽
		 * @param keyboardHeight
		 *            输入法键盘高度
		 */
		void onVirtualKeyboardGet(int[] rectResult, int keyboardWidth, int keyboardHeight);
	}
}
