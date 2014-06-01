package com.example.icanread;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity{
	
	private int APP_MODE = 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		TabHost maintabhost = getTabHost();
		// OCR Tab
		TabSpec tabOCR = maintabhost.newTabSpec("OCR").setIndicator("OCR");
		Intent OCRIntent = new Intent(this, OCRActivity.class);
		tabOCR.setContent(OCRIntent);
		maintabhost.addTab(tabOCR);
		// Training Tab
		TabSpec tabTraining = maintabhost.newTabSpec("Training").setIndicator("Training");
		Intent TrainIntent = new Intent(this, TrainingActivity.class);
		tabTraining.setContent(TrainIntent);
		maintabhost.addTab(tabTraining);
		
		if(APP_MODE == 1) {
			maintabhost.setCurrentTab(0);
		} else {
			maintabhost.setCurrentTab(1);
		}
	}
}
