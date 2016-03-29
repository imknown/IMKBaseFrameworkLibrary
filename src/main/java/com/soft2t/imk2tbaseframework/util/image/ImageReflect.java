package com.soft2t.imk2tbaseframework.util.image;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.util.Log;
import android.view.View;

public class ImageReflect {

	/** view界面转换成bitmap */
	public static Bitmap getViewBitmap(View v) {
		v.clearFocus();
		v.setPressed(false);

		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);

		// Reset the drawing cache background color to fully transparent
		// for the duration of this operation
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);

		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap cacheBitmap = v.getDrawingCache();
		if (cacheBitmap == null) {
			Log.e("", "failed getViewBitmap(" + v + ")", new RuntimeException());
			return null;
		}

		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

		// Restore the view
		v.destroyDrawingCache();
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);

		return bitmap;
	}

	/**
	 * 同时创建 图片及其自身倒影
	 * 
	 * @param originalBitmap
	 *            需要倒过来的 图片
	 * @param reflectionGap
	 *            图片与倒影间隔距离
	 * @return 图片及其自身倒影
	 */
	public static Bitmap createReflectedImageWithOriginalBitmap(Bitmap originalBitmap, final int reflectionGap) {
		// 图片的宽度
		int width = originalBitmap.getWidth();
		// 图片的高度
		int height = originalBitmap.getHeight();
		if (height <= reflectionGap) {
			return null;
		}

		Matrix matrix = new Matrix();
		// 图片缩放，x轴变为原来的1倍，y轴为-1倍,实现图片的反转
		matrix.preScale(1, -1);
		// 创建反转后的图片Bitmap对象，图片高是原图的一半。
		Bitmap reflectionBitmap = Bitmap.createBitmap(originalBitmap, 0, height / 2, width, height / 2, matrix, false);
		// 创建标准的Bitmap对象，宽和原图一致，高是原图的1.5倍。
		Bitmap withReflectionBitmap = Bitmap.createBitmap(width, (height + height / 2 + reflectionGap), Config.ARGB_8888);

		// 构造函数传入Bitmap对象，为了在图片上画图
		Canvas canvas = new Canvas(withReflectionBitmap);
		// 画原始图片
		canvas.drawBitmap(originalBitmap, 0, 0, null);

		// 画间隔矩形
		Paint defaultPaint = new Paint();

		int itsColor = android.R.color.transparent;
		defaultPaint.setColor(itsColor);
		canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);

		// 画倒影图片
		canvas.drawBitmap(reflectionBitmap, 0, height + reflectionGap, null);

		// 实现倒影效果
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, originalBitmap.getHeight(), 0, withReflectionBitmap.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.MIRROR);
		// LinearGradient shader = new LinearGradient(0, originalBitmap.getHeight(), 0, withReflectionBitmap.getHeight(), 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

		// 覆盖效果
		canvas.drawRect(0, height, width, withReflectionBitmap.getHeight(), paint);

		return withReflectionBitmap;
	}

	/**
	 * 只创建 图片自身倒影
	 * 
	 * @param originalBitmap
	 *            需要倒过来的 图片
	 * @param reflectHeight
	 *            倒影高度
	 * @return 自身倒影
	 */
	public static Bitmap createReflectedImage(Bitmap bitmap, int reflectHeight) {
		int width = bitmap.getWidth();

		int height = bitmap.getHeight();
		int totleHeight = reflectHeight;

		if (height <= totleHeight) {
			return null;
		}

		Matrix matrix = new Matrix();

		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height - reflectHeight, width, reflectHeight, matrix, true);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, reflectHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(reflectionImage, 0, 0, null);
		LinearGradient shader = new LinearGradient(0, 0, 0, bitmapWithReflection.getHeight(), 0x80ffffff, 0x00ffffff, TileMode.CLAMP);

		Paint paint = new Paint();
		paint.setShader(shader);

		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, 0, width, bitmapWithReflection.getHeight(), paint);
		if (!reflectionImage.isRecycled()) {
			reflectionImage.recycle();
		}
		// if (!bitmap.isRecycled()) {
		// bitmap.recycle();
		// }
		System.gc();
		return bitmapWithReflection;
	}
}
