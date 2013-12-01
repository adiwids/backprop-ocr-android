package com.android.ocr;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

public abstract class SegmentationChar {
	
	private static Bitmap bitmap;
	private static int width, height;
	public static int x1, y1, x2, y2;
	
	public SegmentationChar() {}
	
	public static Bitmap doSegment(Bitmap bmp) {
		bitmap = bmp.copy(bmp.getConfig(), true);
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		
		//cariRegion();
		getBoundary();
		crop();
		
		return bitmap;
	}
	
	private static void getBoundary() {
		x1 = width;
		y1 = height;
		x2 = 0;
		y2 = 0;
		int pixel;

		for(int y = 0;y < height;y++) {
			for(int x = 0;x < width;x++) {
				pixel = Color.red(bitmap.getPixel(x, y));
				if(pixel == 0) {
					if(x < x1) {
						x1 = x;
					}
					
					if(x > x2) {
						x2 = x;
					}
					
					if(y < y1) {
						y1 = y;
					}
					
					if(y > y2) {
						y2 = y;
					}
				} 
			}
		}
	}
	
	private static void crop() {
		Bitmap cropped;
		Matrix m = new Matrix();
		
		m.postScale(1, 1);
		cropped = Bitmap.createBitmap(bitmap, x1, y1, x2 - x1, y2 - y1, m, false);
		bitmap = cropped;
	}
}
