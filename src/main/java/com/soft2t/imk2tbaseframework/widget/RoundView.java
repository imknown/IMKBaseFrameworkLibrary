package com.soft2t.imk2tbaseframework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.soft2t.imk2tbaseframework.R;
import com.soft2t.imk2tbaseframework.util.device.hard.DisplayUtil;

public class RoundView extends View {

	private Paint paint;
	private Context context;
	private DisplayUtil util;
	private int roundColor;// 圆的颜色
	private int roundRadius;// 圆的颜色

	public RoundView(Context context) {
		super(context);
	}

	public RoundView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundView, defStyle, 0);

		roundColor = a.getColor(R.styleable.RoundView_round_color, 0);
		roundRadius = a.getDimensionPixelSize(R.styleable.RoundView_round_radius, 0);
		util = new DisplayUtil(context);
		this.context = context;
		this.paint = new Paint();
		this.paint.setAntiAlias(true); // 消除锯齿
		this.paint.setStyle(Paint.Style.FILL); // 绘制实心圆

		a.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int center = getWidth() / 2;
		// int innerCircle = (int) util.dip2px_M2(83); //设置内圆半径

		// 绘制圆
		this.paint.setColor(roundColor);
		canvas.drawCircle(center, center, roundRadius, paint);
		// canvas.drawCircle(center,center, innerCircle, paint);
	}

}
