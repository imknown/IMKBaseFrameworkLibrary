package com.soft2t.imk2tbaseframework.util.view;

//import it.sephiroth.android.library.widget.HListView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;

public class ScrollUtil {

	public static void getGridViewUpScroll(GridView gv) {
		int first = gv.getFirstVisiblePosition();
		int last = gv.getLastVisiblePosition();

		int targetPosition = first - (last - first + 1 - gv.getNumColumns());

		gv.smoothScrollToPositionFromTop(targetPosition, 0);
	}

	public static void getGridViewDownScroll(GridView gv) {
		// int first = gv.getFirstVisiblePosition();
		int last = gv.getLastVisiblePosition();

		int targetPosition = last + 1 - gv.getNumColumns();

		gv.smoothScrollToPositionFromTop(targetPosition, 0);
	}

	public static void getListViewUpScroll(ListView listview) {
		int first = listview.getFirstVisiblePosition();
		int last = listview.getLastVisiblePosition();

		if (last - first >= 3) {// 当前页 至少可以显示三个
			int toggle = first - ((last - first + 1) - 2);
			// int toggle = 2 * first - last;

			listview.smoothScrollToPositionFromTop(toggle, 0);
		} else {// 当前页 只有一个
			listview.smoothScrollBy(-(listview.getHeight() - listview.getHeight() / 10), 500);
		}
	}

	public static void getListViewDownScroll(ListView listview) {
		int last = listview.getLastVisiblePosition();
		int toggle = last;
		listview.smoothScrollToPositionFromTop(toggle, 0);
	}

	// public static void getH_ListViewLeftScroll(HListView hListView) {
	// int first = hListView.getFirstVisiblePosition();
	// int last = hListView.getLastVisiblePosition();
	//
	// if (last - first >= 3) {// 当前页 至少可以显示三个
	// int toggle = first - ((last - first + 1) - 2);
	// // int toggle = 2 * first - last;
	//
	// hListView.smoothScrollToPositionFromLeft(toggle, 0);
	// } else {// 当前页 只有一个
	// hListView.smoothScrollBy(-(hListView.getHeight() - hListView.getWidth() / 10), 500);
	// }
	// }

	// public static void getH_ListViewRightScroll(HListView hListView) {
	// int last = hListView.getLastVisiblePosition();
	// int toggle = last;
	// hListView.smoothScrollToPositionFromLeft(toggle, 0);
	// }

	/**
	 * ScrollView 滚动
	 * 
	 * @param scrollView
	 * @param offsetByY
	 *            偏移量, 单位 像素(px), 负数 表示向上
	 */
	public static void getH_ScrollViewScroll(ScrollView scrollView, int offsetByY) {
		scrollView.smoothScrollBy(offsetByY, 0);
	}

	/**
	 * HorizontalScrollView 滚动
	 * 
	 * @param horizontalScrollView
	 * @param offsetByX
	 *            偏移量, 单位 像素(px), 负数 表示向左
	 */
	public static void getH_ScrollViewScroll(HorizontalScrollView horizontalScrollView, int offsetByX) {
		horizontalScrollView.smoothScrollBy(offsetByX, 0);
	}
}
