package com.soft2t.imk2tbaseframework.util.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;

/**
 * 图片工具类
 * 
 * @author imknown
 *
 */
public class BitmapAndDrawableUtil {

	/**
	 * 由资源id获取图片
	 * 
	 * @param context
	 * 
	 * @param resId
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Drawable getDrawableById(Context context, int resId) {
		if (context == null) {
			return null;
		}

		return context.getResources().getDrawable(resId);
	}

	/**
	 * 由资源id获取位图
	 * 
	 * @param context
	 * 
	 * @param resId
	 * 
	 * @return
	 */
	public static Bitmap getBitmapById(Context context, int resId) {
		if (context == null) {
			return null;
		}

		return BitmapFactory.decodeResource(context.getResources(), resId);
	}

	// ===========================================

	@SuppressWarnings("deprecation")
	public static Options getDefaultOriginalOptions() {
		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inSampleSize = 1;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;

		return options;
	}

	// ===========================================

	/**
	 * 按指定宽度和高度缩放图片,不保证宽高比例
	 * 
	 * @param bitmap
	 * 
	 * @param w
	 * 
	 * @param h
	 * 
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {

		if (bitmap == null) {
			return null;
		}

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();

		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);

		matrix.postScale(scaleWidht, scaleHeight);

		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

		return newbmp;
	}

	/**
	 * 将bitmap位图保存到path路径下，图片格式为Bitmap.CompressFormat.PNG，质量为100
	 * 
	 * @param bitmap
	 * 
	 * @param path
	 */
	public static boolean saveBitmap(Bitmap bitmap, String path) {

		try {
			File file = new File(path);

			File parent = file.getParentFile();

			if (!parent.exists()) {
				parent.mkdirs();
			}

			FileOutputStream fos = new FileOutputStream(file);

			boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

			fos.flush();
			fos.close();

			return b;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 将bitmap位图保存到path路径下
	 * 
	 * @param bitmap
	 * 
	 * @param path
	 *            保存路径-Bitmap.CompressFormat.PNG或Bitmap.CompressFormat.JPEG.PNG
	 * @param format
	 *            格式
	 * @param quality
	 *            Hint to the compressor, 0-100. 0 meaning compress for small<br>
	 *            size, 100 meaning compress for max quality. Some formats, like<br>
	 *            PNG which is lossless, will ignore the quality setting
	 * 
	 * @return
	 */
	public static boolean saveBitmap(Bitmap bitmap, String path, CompressFormat format, int quality) {

		try {
			File file = new File(path);
			File parent = file.getParentFile();

			if (!parent.exists()) {
				parent.mkdirs();
			}

			FileOutputStream fos = new FileOutputStream(file);

			boolean b = bitmap.compress(format, quality, fos);

			fos.flush();
			fos.close();

			return b;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 获得圆角图片
	 * 
	 * @param bitmap
	 * @param roundPx
	 * 
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		if (bitmap == null) {
			return null;
		}

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;

		final Paint paint = new Paint();

		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);

		paint.setColor(color);

		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 
	 * 获得带倒影的图片
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}

		final int reflectionGap = 4;

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();

		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);

		canvas.drawBitmap(bitmap, 0, 0, null);

		Paint deafalutPaint = new Paint();

		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();

		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);

		paint.setShader(shader);

		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * 取得指定区域的图形
	 * 
	 * @param source
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmap(Bitmap source, int x, int y, int width, int height) {
		Bitmap bitmap = Bitmap.createBitmap(source, x, y, width, height);
		return bitmap;
	}

	/**
	 * 从大图中截取小图
	 * 
	 * @param r
	 * @param resourseId
	 * @param row
	 * @param col
	 * @param rowTotal
	 * @param colTotal
	 * @return
	 */
	public static Bitmap getImage(Context context, Bitmap source, int row, int col, int rowTotal, int colTotal, float multiple, boolean isRecycle) {
		Bitmap temp = getBitmap(source, (col - 1) * source.getWidth() / colTotal, (row - 1) * source.getHeight() / rowTotal, source.getWidth() / colTotal, source.getHeight() / rowTotal);

		if (isRecycle) {
			recycleBitmap(source);
		}

		if (multiple != 1.0) {
			Matrix matrix = new Matrix();
			matrix.postScale(multiple, multiple);
			temp = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, true);
		}

		return temp;
	}

	/**
	 * 从大图中截取小图
	 * 
	 * @param r
	 * @param resourseId
	 * @param row
	 * @param col
	 * @param rowTotal
	 * @param colTotal
	 * @return
	 */
	public static Drawable getDrawableImage(Context context, Bitmap source, int row, int col, int rowTotal, int colTotal, float multiple) {

		Bitmap temp = getBitmap(source, (col - 1) * source.getWidth() / colTotal, (row - 1) * source.getHeight() / rowTotal, source.getWidth() / colTotal, source.getHeight() / rowTotal);

		if (multiple != 1.0) {
			Matrix matrix = new Matrix();
			matrix.postScale(multiple, multiple);
			temp = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, true);
		}

		Drawable d = new BitmapDrawable(context.getResources(), temp);

		return d;
	}

	public static Drawable[] getDrawables(Context context, int resourseId, int row, int col, float multiple) {
		Drawable drawables[] = new Drawable[row * col];

		Bitmap source = decodeResource(context, resourseId);

		int temp = 0;

		for (int i = 1; i <= row; i++) {
			for (int j = 1; j <= col; j++) {
				drawables[temp] = getDrawableImage(context, source, i, j, row, col, multiple);
				temp++;
			}
		}

		if (source != null && !source.isRecycled()) {
			source.recycle();
			source = null;
		}

		return drawables;
	}

	public static Drawable[] getDrawables(Context context, String resName, int row, int col, float multiple) {
		Drawable drawables[] = new Drawable[row * col];

		Bitmap source = decodeBitmapFromAssets(context, resName);

		int temp = 0;

		for (int i = 1; i <= row; i++) {
			for (int j = 1; j <= col; j++) {
				drawables[temp] = getDrawableImage(context, source, i, j, row, col, multiple);
				temp++;
			}
		}

		if (source != null && !source.isRecycled()) {
			source.recycle();
			source = null;
		}

		return drawables;
	}

	/**
	 * 根据一张大图，返回切割后的图元数组
	 * 
	 * @param resourseId
	 *            资源id
	 * @param row
	 *            总行数
	 * @param col
	 *            总列数 multiple:图片缩放的倍数1:表示不变，2表示放大为原来的2倍
	 * @return
	 */
	public static Bitmap[] getBitmaps(Context context, int resourseId, int row, int col, float multiple) {
		Bitmap bitmaps[] = new Bitmap[row * col];

		Bitmap source = decodeResource(context, resourseId);

		int temp = 0;

		for (int i = 1; i <= row; i++) {
			for (int j = 1; j <= col; j++) {
				bitmaps[temp] = getImage(context, source, i, j, row, col, multiple, false);
				temp++;
			}
		}

		if (source != null && !source.isRecycled()) {
			source.recycle();
			source = null;
		}

		return bitmaps;
	}

	public static Bitmap[] getBitmaps(Context context, String resName, int row, int col, float multiple) {
		Bitmap bitmaps[] = new Bitmap[row * col];

		Bitmap source = decodeBitmapFromAssets(context, resName);

		int temp = 0;

		for (int i = 1; i <= row; i++) {
			for (int j = 1; j <= col; j++) {
				bitmaps[temp] = getImage(context, source, i, j, row, col, multiple, false);
				temp++;
			}
		}

		if (source != null && !source.isRecycled()) {
			source.recycle();
			source = null;
		}

		return bitmaps;
	}

	public static Bitmap[] getBitmapsByBitmap(Context context, Bitmap source, int row, int col, float multiple) {
		Bitmap bitmaps[] = new Bitmap[row * col];

		int temp = 0;

		for (int i = 1; i <= row; i++) {
			for (int j = 1; j <= col; j++) {
				bitmaps[temp] = getImage(context, source, i, j, row, col, multiple, false);
				temp++;
			}
		}

		return bitmaps;
	}

	@SuppressWarnings("deprecation")
	public static Bitmap decodeResource(Context context, int resourseId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
		opt.inPurgeable = true;
		opt.inInputShareable = true; // 需把 inPurgeable设置为true，否则被忽略

		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resourseId);

		return BitmapFactory.decodeStream(is, null, opt); // decodeStream直接调用JNI>>nativeDecodeAsset()来完成decode，无需再使用java层的createBitmap，从而节省了java层的空间
	}

	/**
	 * 从assets文件下解析图片
	 * 
	 * @param resName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Bitmap decodeBitmapFromAssets(Context context, String resName) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inPurgeable = true;
		options.inInputShareable = true;

		InputStream in = null;

		try {
			// in = AssetsResourcesUtil.openResource(resName);
			in = context.getAssets().open(resName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return BitmapFactory.decodeStream(in, null, options);
	}

	/**
	 * 回收不用的bitmap
	 * 
	 * @param b
	 */
	public static void recycleBitmap(Bitmap b) {
		if (b != null && !b.isRecycled()) {
			b.recycle();
			b = null;
		}
	}

	/**
	 * 获取某些连在一起的图片的某一个画面（图片为横着排的情况）
	 * 
	 * @param source
	 * @param frameIndex
	 *            从1开始
	 * @param totalCount
	 * @return
	 */
	public static Bitmap getOneFrameImg(Bitmap source, int frameIndex, int totalCount) {
		int singleW = source.getWidth() / totalCount;
		return Bitmap.createBitmap(source, (frameIndex - 1) * singleW, 0, singleW, source.getHeight());
	}

	public static void recycleBitmaps(Bitmap bitmaps[]) {
		if (bitmaps != null) {
			for (Bitmap b : bitmaps) {
				recycleBitmap(b);
			}

			bitmaps = null;
		}
	}

	// ===========================================
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int TOP = 3;
	public static final int BOTTOM = 4;

	/**
	 * 图片去色,返回灰度图片
	 * 
	 * @param bmpOriginal
	 *            传入的图片
	 * @return 去色后的图片
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	/**
	 * 去色同时加圆角
	 * 
	 * @param bmpOriginal
	 *            原图
	 * @param pixels
	 *            圆角弧度
	 * @return 修改后的图片
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal, int pixels) {
		return toRoundCorner(toGrayscale(bmpOriginal), pixels);
	}

	/**
	 * 把图片变成圆角
	 * 
	 * @param bitmap
	 *            需要修改的图片
	 * @param pixels
	 *            圆角的弧度
	 * @return 圆角图片
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 使圆角功能支持BitampDrawable
	 * 
	 * @param bitmapDrawable
	 * @param pixels
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable, int pixels) {
		Bitmap bitmap = bitmapDrawable.getBitmap();
		bitmapDrawable = new BitmapDrawable(toRoundCorner(bitmap, pixels));
		return bitmapDrawable;
	}

	/**
	 * 读取路径中的图片，然后将其转化为缩放后的bitmap
	 * 
	 * @param path
	 */
	public static void saveBefore(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高
		Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回bm为空
		options.inJustDecodeBounds = false;
		// 计算缩放比
		int be = (int) (options.outHeight / (float) 200);
		if (be <= 0)
			be = 1;
		options.inSampleSize = 2; // 图片长宽各缩小二分之一
		// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
		bitmap = BitmapFactory.decodeFile(path, options);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		System.out.println(w + " " + h);
		// savePNG_After(bitmap,path);
		saveJPGE_After(bitmap, path);
	}

	/**
	 * 保存图片为PNG
	 * 
	 * @param bitmap
	 * @param name
	 */
	public static void savePNG_After(Bitmap bitmap, String name) {
		File file = new File(name);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存图片为JPEG
	 * 
	 * @param bitmap
	 * @param path
	 */
	public static void saveJPGE_After(Bitmap bitmap, String path) {
		File file = new File(path);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 水印
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createBitmapForWatermark(Bitmap src, Bitmap watermark) {
		if (src == null) {
			return null;
		}
		int w = src.getWidth();
		int h = src.getHeight();
		int ww = watermark.getWidth();
		int wh = watermark.getHeight();
		// create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		// draw src into
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		// draw watermark into
		cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);// 在src的右下角画入水印
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		return newb;
	}

	/**
	 * 图片合成
	 * 
	 * @return
	 */
	public static Bitmap potoMix(int direction, Bitmap... bitmaps) {
		if (bitmaps.length <= 0) {
			return null;
		}
		if (bitmaps.length == 1) {
			return bitmaps[0];
		}
		Bitmap newBitmap = bitmaps[0];
		// newBitmap = createBitmapForFotoMix(bitmaps[0],bitmaps[1],direction);
		for (int i = 1; i < bitmaps.length; i++) {
			newBitmap = createBitmapForFotoMix(newBitmap, bitmaps[i], direction);
		}
		return newBitmap;
	}

	private static Bitmap createBitmapForFotoMix(Bitmap first, Bitmap second, int direction) {
		if (first == null) {
			return null;
		}
		if (second == null) {
			return first;
		}
		int fw = first.getWidth();
		int fh = first.getHeight();
		int sw = second.getWidth();
		int sh = second.getHeight();
		Bitmap newBitmap = null;
		if (direction == LEFT) {
			newBitmap = Bitmap.createBitmap(fw + sw, fh > sh ? fh : sh, Config.ARGB_8888);
			Canvas canvas = new Canvas(newBitmap);
			canvas.drawBitmap(first, sw, 0, null);
			canvas.drawBitmap(second, 0, 0, null);
		} else if (direction == RIGHT) {
			newBitmap = Bitmap.createBitmap(fw + sw, fh > sh ? fh : sh, Config.ARGB_8888);
			Canvas canvas = new Canvas(newBitmap);
			canvas.drawBitmap(first, 0, 0, null);
			canvas.drawBitmap(second, fw, 0, null);
		} else if (direction == TOP) {
			newBitmap = Bitmap.createBitmap(sw > fw ? sw : fw, fh + sh, Config.ARGB_8888);
			Canvas canvas = new Canvas(newBitmap);
			canvas.drawBitmap(first, 0, sh, null);
			canvas.drawBitmap(second, 0, 0, null);
		} else if (direction == BOTTOM) {
			newBitmap = Bitmap.createBitmap(sw > fw ? sw : fw, fh + sh, Config.ARGB_8888);
			Canvas canvas = new Canvas(newBitmap);
			canvas.drawBitmap(first, 0, 0, null);
			canvas.drawBitmap(second, 0, fh, null);
		}
		return newBitmap;
	}

	/**
	 * 将Bitmap转换成指定大小
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap createBitmapBySize(Bitmap bitmap, int width, int height) {
		return Bitmap.createScaledBitmap(bitmap, width, height, true);
	}

	// ===========================================

	/**
	 * drawable转换成bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		} else if (drawable instanceof NinePatchDrawable) {
			Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

			drawable.draw(canvas);
			return bitmap;
		} else {
			throw new IllegalArgumentException("can not support this drawable to bitmap now!!!");
		}
	}

	/** 将byte[]转换成InputStream */
	public static InputStream byte2InputStream(byte[] b) {
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		return bais;
	}

	/** 将InputStream转换成byte[] */
	public static byte[] inputStream2Bytes(InputStream is) {
		String str = "";

		byte[] readByte = new byte[1024];

		@SuppressWarnings("unused")
		int readCount = -1;

		try {
			while ((readCount = is.read(readByte, 0, 1024)) != -1) {
				str += new String(readByte).trim();
			}

			return str.getBytes();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/** 将Bitmap转换成InputStream */
	public static InputStream bitmap2InputStream(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	/** 将Bitmap转换成InputStream */
	public static InputStream bitmap2InputStream(Bitmap bm, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	/** 将InputStream转换成Bitmap */
	public static Bitmap inputStream2Bitmap(InputStream is) {
		return BitmapFactory.decodeStream(is);
	}

	/** Drawable转换成InputStream */
	public static InputStream drawable2InputStream(Drawable d) {
		Bitmap bitmap = drawable2Bitmap(d);
		return bitmap2InputStream(bitmap);
	}

	/** InputStream转换成Drawable */
	public static Drawable inputStream2Drawable(InputStream is) {
		Bitmap bitmap = inputStream2Bitmap(is);
		return bitmap2Drawable(bitmap);
	}

	/** Drawable转换成byte[] */
	public static byte[] drawable2Bytes(Drawable d) {
		Bitmap bitmap = drawable2Bitmap(d);
		return bitmap2Bytes(bitmap);
	}

	/** byte[]转换成Drawable */
	public static Drawable bytes2Drawable(byte[] b) {
		Bitmap bitmap = bytes2Bitmap(b);
		return bitmap2Drawable(bitmap);
	}

	/** Bitmap转换成byte[] */
	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/** byte[]转换成Bitmap */
	public static Bitmap bytes2Bitmap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		}

		return null;
	}

	/** Drawable转换成Bitmap */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/** Bitmap转换成Drawable */
	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		@SuppressWarnings("deprecation")
		BitmapDrawable bd = new BitmapDrawable(bitmap);
		Drawable d = (Drawable) bd;
		return d;
	}
}