package com.android.ocr;

import android.graphics.Bitmap;
import android.graphics.Color;

public abstract class OCRImage {
	
	public OCRImage() {}
	
	public static double[][] convertToMatrix(Bitmap bmp) {
		double[][] matrix = new double[bmp.getWidth()][bmp.getHeight()];
		int pixel;
		
		for(int y = 0;y < bmp.getHeight();y++) {
			for(int x = 0;x < bmp.getWidth();x++) {
				pixel = Color.red(bmp.getPixel(x, y));
				if(pixel == 0) {
					matrix[x][y] = 1;
				} else {
					matrix[x][y] = 0;
				}
			}
		}
		return matrix;
	}
	
	public static Bitmap resize(int maxSize, Bitmap bmp) {
		Bitmap resizedBitmap = null;
		float hRatio = (float)maxSize / (float)bmp.getHeight();
		float wRatio = (float)maxSize / (float)bmp.getWidth();
		float viewSize = hRatio < wRatio ? hRatio : wRatio;
		
		resizedBitmap = Bitmap.createScaledBitmap(bmp, Math.round(viewSize * bmp.getWidth()), Math.round(viewSize * bmp.getHeight()), false);

		return resizedBitmap;
	}
	
	public static Bitmap resize(int newWidth, int newHeight, Bitmap bmp) {
		Bitmap resizedBitmap = null;
		
		resizedBitmap = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);
		
		return resizedBitmap;
	}
}
