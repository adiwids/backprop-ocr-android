package com.example.icanread;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.android.ocr.AndroidOCR;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class TrainingActivity extends Activity {
	
	private ImageView view;
	private Button btnPick;
	private Button btnAddData;
	private Button btnTrain;
	private final int ACTION_PICK = 2;
	public static AndroidOCR ocr;
	private ProgressDialog progDialog;
	public Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training_layout);
		context = this;
		
		ocr = new AndroidOCR();
		
		view = (ImageView)findViewById(R.id.imageSample);
		btnPick = (Button)findViewById(R.id.pickSample);
		btnAddData = (Button)findViewById(R.id.addTrainingSet);
		btnTrain = (Button)findViewById(R.id.doTraining);
		
		btnAddData.setEnabled(false);
		btnTrain.setEnabled(false);
		
		//uiUpdate();
		
		btnPick.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent pick = new Intent(Intent.ACTION_PICK);
				pick.setType("image/jpeg");
				startActivityForResult(pick, ACTION_PICK);
			}
		});
		
		btnAddData.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String label = (((EditText)findViewById(R.id.inputCharacter)).getText()).toString();
				/*Bitmap bmp = ((BitmapDrawable)view.getDrawable()).getBitmap();
				ocr.setImage(OCRImage.resize(96, bmp));
				view.setImageBitmap(ocr.image);
				ocr.setImage(PreprocessImage.binarize(ocr.image));
				Log.v("AndroidOCR", "Binary: " + ocr.image.getWidth() + "x" + ocr.image.getHeight());
				ocr.setImage(SegmentationChar.doSegment(ocr.image));
				Log.v("AndroidOCR", "Segmented: " + ocr.image.getWidth() + "x" + ocr.image.getHeight());
				//ocr.setImage(OCRImage.resize(24, 24, ocr.image));
				//Log.v("AndroidOCR", "Normalized: " + ocr.image.getWidth() + "x" + ocr.image.getHeight());
				view.setImageBitmap(ocr.image);*/
				
				AsyncTask<Void, Void, Void> addDataTask = new AsyncTask<Void, Void, Void>() {
					
					@Override
					protected void onPreExecute() {
						btnTrain.setEnabled(false);
						progDialog = new ProgressDialog(context);
						progDialog.setTitle("Adding sample");
						progDialog.setMessage("Sample " + label + " " + ocr.DATASET_COUNTER);
						progDialog.setCancelable(false);
						progDialog.setIndeterminate(true);
						progDialog.show();
					}

					@Override
					protected Void doInBackground(Void... params) {
						ocr.addTrainingSet(label);
						for (int i = 0; i <= 10; i += 5) {
							try {
								Thread.sleep(1000);
							}
							catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						return null;
					}
					
					@Override
					protected void onPostExecute(Void result) {
						uiUpdate();
						if(progDialog != null) {
							progDialog.dismiss();	
						}
					}
				};
				addDataTask.execute((Void[])null);
			}
		});
		
		btnTrain.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AsyncTask<Void, Void, Void> trainTask = new AsyncTask<Void, Void, Void>() {
					
					@Override
					protected void onPreExecute() {
						progDialog = new ProgressDialog(context);
						progDialog.setTitle("Training network...");
						progDialog.setCancelable(false);
						progDialog.setIndeterminate(true);
						progDialog.show();
					}

					@Override
					protected Void doInBackground(Void... params) {
						ocr.trainNetwork();
						for (int i = 0; i <= 10; i += 5) {
							try {
								Thread.sleep(1000);
							}
							catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						return null;
					}
					
					@Override
					protected void onPostExecute(Void result) {
						uiUpdate();
						if(progDialog != null) {
							progDialog.dismiss();	
						}
					}
				};
				trainTask.execute((Void[])null);
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.training_menu, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
			case R.id.menuNetParams:
				Intent settings = new Intent(this, SettingsActivity.class);
				startActivity(settings);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/*@Override
	protected void onDestroy() {
		if(progDialog != null) {
			progDialog.dismiss();
		}
	}*/
	
	private void uiUpdate() {
		Bitmap image = ((BitmapDrawable)view.getDrawable()).getBitmap();
		btnAddData.setEnabled(!(image == null));
		
		btnAddData.setEnabled((ocr.DATASET_COUNTER < ocr.parameters.nTrainingData));
		btnTrain.setEnabled((ocr.DATASET_COUNTER >= ocr.parameters.nTrainingData));
	} 
}
