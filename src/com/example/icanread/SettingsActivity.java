package com.example.icanread;

import com.android.ocr.AndroidOCR;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class SettingsActivity extends ListActivity implements OnItemClickListener{
	
	private AndroidOCR ocr;
	private final String[] parameters = {"Hidden PE", "Output PE", "Samples", "Learning Rate", "Error Tolerance", 
			"Max Epochs" };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ListView list = getListView();
		list.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.parameter_layout, 
				R.id.parameterTitle, parameters));
		ocr = new AndroidOCR();
		list.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(this);
		View dialogView = inflater.inflate(R.layout.paramdialog_layout, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(dialogView);
		final EditText fieldValue = (EditText) dialogView.findViewById(R.id.paramDialogValue);
		builder
			.setTitle(parameters[arg2])
			.setCancelable(true)
			.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					double val = Double.parseDouble((fieldValue.getText()).toString());
					//ocr.setNetParameter(arg2, val);
				}
			})
			.setNegativeButton("Close", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
		AlertDialog prompt = builder.create();
		prompt.setOnShowListener(new DialogInterface.OnShowListener() {
			
			@Override
			public void onShow(DialogInterface dialog) {
				// TODO Auto-generated method stub
				//fillToField(fieldValue, arg2);
			}
		});
		prompt.show();
	}
	
	/**private void fillToField(EditText field, int idxParam) {
		
		switch(idxParam) {
		case 0:
			field.setText(Integer.toString(ocr.parameters.nPE_hidden));break;
		case 1:
			field.setText(Integer.toString(ocr.parameters.nPE_output));break;
		case 2:
			field.setText(Integer.toString(ocr.parameters.nTrainingData));break;
		case 3:
			field.setText(Double.toString(ocr.parameters.learningRate));break;
		case 4:
			field.setText(Double.toString(ocr.parameters.errorTolerance));break;
		case 5:
			field.setText(Long.toString(ocr.parameters.maxEpochs));break;
		default:
			break;
		}
	}*/
}
