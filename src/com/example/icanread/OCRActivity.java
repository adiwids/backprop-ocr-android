package com.example.icanread;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.android.ocr.AndroidOCR;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
//import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OCRActivity extends Activity {
	
	private ImageView view;
	private Button btnCapture;
	private Button btnRecognize;
	//private final int ACTION_CAPTURE = 1;
	private final int ACTION_PICK = 2;
	private static AndroidOCR ocr;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ocr_layout);
		
		view = (ImageView)findViewById(R.id.imageTest);
		btnCapture = (Button)findViewById(R.id.captureImage);
		btnRecognize = (Button)findViewById(R.id.recognizeImage);
		
		ocr = TrainingActivity.ocr;//new AndroidOCR();
		
		btnRecognize.setEnabled(false);
		
		btnCapture.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Intent pick = new Intent(Intent.ACTION_PICK);
				pick.setType("image/jpeg");
				startActivityForResult(pick, ACTION_PICK);
			}
		});
		
		btnRecognize.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView textCharacter = (TextView)findViewById(R.id.textCharacter);
				//textCharacter.setText(ocr.recognize());
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode == RESULT_OK) {
			if(requestCode == ACTION_PICK) {
				try {
					InputStream is = getContentResolver().openInputStream(data.getData());
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					Log.v("AndroidOCR", "Image picked from " + data.getData().toString() + 
							" -> " + bitmap.getWidth() + "x" + bitmap.getHeight());
					ocr.setImage(bitmap);
					view.setImageBitmap(ocr.image);
					
					uiUpdate();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void uiUpdate() {
		Bitmap image = ((BitmapDrawable)view.getDrawable()).getBitmap();
		btnRecognize.setEnabled(!(image == null));
	}
}
