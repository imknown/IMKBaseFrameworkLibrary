package com.soft2t.imk2tbaseframework.util.image;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

/** 分割图片 */
public class ImageSplitter {

	/**
	 * 分割图片
	 * 
	 * @param bitmap
	 *            需要分割的图片
	 * @param xPiece
	 *            行数
	 * @param yPiece
	 *            列数
	 * @param pieceWidth
	 *            每个图片的宽度
	 * @param pieceHeight
	 *            每个图片的高度
	 * @return 小图片集合
	 */
	public static List<ImagePiece> split(Bitmap bitmap, int xPiece, int yPiece, int pieceWidth, int pieceHeight) {

		List<ImagePiece> pieces = new ArrayList<ImagePiece>(xPiece * yPiece);

		ImagePiece piece;
		int xValue;
		int yValue;

		for (int i = 0; i < yPiece; i++) {
			for (int j = 0; j < xPiece; j++) {
				piece = new ImagePiece();

				xValue = j * pieceWidth;
				yValue = i * pieceHeight;

				piece.index = j + i * xPiece;
				piece.bitmap = Bitmap.createBitmap(bitmap, xValue, yValue, pieceWidth, pieceHeight);

				pieces.add(piece);
			}
		}

		return pieces;
	}

	public static class ImagePiece {
		public int index = 0;
		public Bitmap bitmap = null;
	}
}
