package com.soft2t.imk2tbaseframework.util;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentUtil {

	private FragmentManager fm;
	private int containerId;

	public FragmentUtil(FragmentManager fm, int containerId) {
		this.fm = fm;
		this.containerId = containerId;
	}

	/** Fragment 跳转 */
	public void turnToFragment(Class<? extends Fragment> fromFragmentClass, Class<? extends Fragment> toFragmentClass, Bundle args) {

		// FragmentManager fm = getFragmentManager();

		// 被切换的Fragment标签
		String fromTag = fromFragmentClass.getSimpleName();

		// 切换到的Fragment标签
		String toTag = toFragmentClass.getSimpleName();

		// 查找切换的Fragment
		Fragment fromFragment = fm.findFragmentByTag(fromTag);
		Fragment toFragment = fm.findFragmentByTag(toTag);

		// 如果要切换到的Fragment不存在，则创建
		if (toFragment == null) {
			try {
				toFragment = toFragmentClass.newInstance();
				toFragment.setArguments(args);
			} catch (java.lang.InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		// 如果有参数传递
		if (args != null && !args.isEmpty()) {
			toFragment.getArguments().putAll(args);
		}

		// Fragment事务
		FragmentTransaction ft = fm.beginTransaction();

		// 设置Fragment切换效果
		// ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

		// 如果要切换到的Fragment没有被Fragment事务添加，则隐藏被切换的Fragment，
		// 添加要切换的Fragment 否则，则隐藏被切换的Fragment，显示要切换的Fragment
		if (!toFragment.isAdded()) {
			ft.hide(fromFragment).add(containerId, toFragment, toTag);
		} else {
			ft.hide(fromFragment).show(toFragment);
		}

		// 添加到返回堆栈
		// ft.addToBackStack(tag);

		// 不保留状态提交事务
		ft.commitAllowingStateLoss();
	}

	/** 将所有的Fragment都置为隐藏状态 */
	public void hideFragments(FragmentTransaction transaction, List<Fragment> fragmentList) {
		for (Fragment fragment : fragmentList) {
			if (fragment != null && fragment.isAdded()) {
				transaction.hide(fragment);
			}
		}
	}
}
