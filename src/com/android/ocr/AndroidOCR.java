package com.android.ocr;

import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Environment;

public class AndroidOCR{
	public Bitmap image;
	private File baseDir;
	private File netcfg;
	
	private boolean cfgIsReady;
	public int DATASET_COUNTER;
	
	public AndroidOCR() {
		baseDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidOCR");
		netcfg = new File(baseDir.toString() + "/netcfg.xml");
		if(!baseDir.exists() || !baseDir.isDirectory()) {
			if(baseDir.mkdirs()) {
				if(!netcfg.exists()) {
					try {
						cfgIsReady = netcfg.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		DATASET_COUNTER = 0;
	}
	
	public void setImage(Bitmap bmp) {
		if(image != null) {
			image = null;
		}
		image = bmp.copy(bmp.getConfig(), true);
		image = OCRImage.resize(96, image);
	}
}
