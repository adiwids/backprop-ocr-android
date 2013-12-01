package com.android.ocr;

import android.graphics.Bitmap;
import android.graphics.Color;

public abstract class PreprocessImage {
	
	//private final static double GLOBAL_THRESHOLD = 127.5;
	
	public PreprocessImage(){}
	
	private static Bitmap convertToGrayscale(Bitmap rgbBitmap) {
		int pixel;
		int red, green, blue, grayscalePixel;
		Bitmap grayBitmap = rgbBitmap.copy(rgbBitmap.getConfig(), true);
		
		for(int y = 0;y < grayBitmap.getHeight(); y++) {
			for(int x = 0;x < grayBitmap.getWidth();x++) {
				pixel = grayBitmap.getPixel(x, y);
				red = Color.red(pixel);
				green = Color.green(pixel);
				blue = Color.blue(pixel);
				
				grayscalePixel = (int)((red + green + blue) / 3);
				red = green = blue = grayscalePixel;
				grayBitmap.setPixel(x, y, Color.rgb(red, green, blue));
			}
		}
		
		return grayBitmap;
	}
	
	public static Bitmap binarize(Bitmap rgbBitmap) {
		int pixel, red, green, blue, binaryPixel;
		Bitmap binBitmap = rgbBitmap.copy(rgbBitmap.getConfig(), true);
		binBitmap = convertToGrayscale(rgbBitmap);
		double otsuThreshold = otsuThreshold(binBitmap);
		
		for(int y = 0;y < binBitmap.getHeight(); y++) {
			for(int x = 0;x < binBitmap.getWidth();x++) {
				pixel = Color.red(binBitmap.getPixel(x, y));
				
				if(pixel > otsuThreshold) {
					binaryPixel = 255;
				} else {
					binaryPixel = 0;
				}
				red = green = blue = binaryPixel;
				
				binBitmap.setPixel(x, y, Color.rgb(red, green, blue));
			}
		}
		
		return binBitmap;
	}
	
	// Code by zerocool
	// Get binary treshold using Otsu's method
    private static int otsuThreshold(Bitmap original) {
 
        int[] histogram = imageHistogram(original);
        int total = original.getHeight() * original.getWidth();
 
        float sum = 0;
        for(int i=0; i<256; i++) sum += i * histogram[i];
 
        float sumB = 0;
        int wB = 0;
        int wF = 0;
 
        float varMax = 0;
        int threshold = 0;
 
        for(int i=0 ; i<256 ; i++) {
            wB += histogram[i];
            if(wB == 0) continue;
            wF = total - wB;
 
            if(wF == 0) break;
 
            sumB += (float) (i * histogram[i]);
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;
 
            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);
 
            if(varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }
 
        return threshold;
    }
    
    // Code by zerocool
 	private static int[] imageHistogram(Bitmap bmp) {
 		 
         int[] histogram = new int[256];
  
         for(int i=0; i<histogram.length; i++) histogram[i] = 0;
  
         for(int i=0; i<bmp.getWidth(); i++) {
             for(int j=0; j<bmp.getHeight(); j++) {
                 int red = Color.red(bmp.getPixel(i, j));
                 histogram[red]++;
             }
         }
  
         return histogram;
     }
}
