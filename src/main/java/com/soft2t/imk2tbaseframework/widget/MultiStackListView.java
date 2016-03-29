package com.soft2t.imk2tbaseframework.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MultiStackListView extends ListView {
	public MultiStackListView(Context context) {
		super(context);
	}

	public MultiStackListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
