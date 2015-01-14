package com.dpkm95.maze.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapTransformer {
	public static Bitmap getResizedBitmap(Bitmap image, int bitmapWidth,
			int bitmapHeight) {
		return Bitmap
				.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
	}

	public static Bitmap RotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
	}
}
