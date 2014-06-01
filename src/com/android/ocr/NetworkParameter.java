package com.android.ocr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class NetworkParameter {
	
	public int nPE_hidden;
	public int nPE_output;
	public int nTrainingData;
	public double errorTolerance;
	public double learningRate;
	public long maxEpochs;
	
	private File configFile;
	
	public NetworkParameter(File dir) {
		configFile = new File(dir, "netcfg.json");
		if(!dir.exists()) {
			dir.mkdir();
		}
		if(!configFile.exists()) {
			nPE_hidden = 200;
			nPE_output = 26;
			nTrainingData = 5;
			errorTolerance = 0.09;
			learningRate = 0.03;
			maxEpochs = 1000;
			try {
				configFile.createNewFile();
				writeJSON();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.v("AndroidOCR", "Init : " + e.getMessage());
			}
		}
		readJSON();
	}
	
	private void writeJSON() {
		if(configFile.canWrite()) {
			try {
				JSONObject json = new JSONObject();
				json.put("nPE_hidden", nPE_hidden)
					.put("nPE_output", nPE_output)
					.put("nTrainingData", nTrainingData)
					.put("learningRate", learningRate)
					.put("errorTolerance", errorTolerance)
					.put("maxEpochs", maxEpochs);
				//if(file.createNewFile()) {
					FileWriter writer = new FileWriter(configFile);
					writer.write(json.toString());
					writer.flush();
					writer.close();
				//}
					Log.v("AndroidOCR","Writing netcfg.json : hPE:" + nPE_hidden + " oPE:" + nPE_output + " Samples:" + nTrainingData + 
							" rate:" + learningRate + " tolerance:" + errorTolerance + " epochs:" + maxEpochs);
				// reload parameters value
					readJSON();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.v("AndroidOCR","Writing netcfg.json : " + e.getMessage());
			} catch (IOException e) {
				// TODO: handle exception
				Log.v("AndroidOCR","Writing netcfg.json : " + e.getMessage());
			}
		} else {
			Log.v("AndroidOCR","Writing netcfg.json : " + configFile.toString() + " not writable.");
		}
		readJSON();
	}
	
	private void readJSON() {
		if(configFile.canRead()) {
			try {
				String jsonString = "";
				FileReader fr = new FileReader(configFile);
				char[] c = new char[(int)configFile.length()];
				fr.read(c);
				for(int i = 0;i < c.length;i++) {
					jsonString += c[i];
				}
				fr.close();
				JSONObject json = new JSONObject(jsonString);
				nPE_hidden = json.getInt("nPE_hidden");
				nPE_output = json.getInt("nPE_output");
				learningRate = json.getDouble("learningRate");
				nTrainingData = json.getInt("nTrainingData");
				errorTolerance = json.getDouble("errorTolerance");
				maxEpochs = json.getLong("maxEpochs");
				Log.v("AndroidOCR","Reading netcfg.json : hPE:" + nPE_hidden + " oPE:" + nPE_output + " Samples:" + nTrainingData + 
						" rate:" + learningRate + " tolerance:" + errorTolerance + " epochs:" + maxEpochs);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				Log.v("AndroidOCR","Reading netcfg.json : " + e.getMessage());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.v("AndroidOCR","Reading netcfg.json : " + e.getMessage());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				Log.v("AndroidOCR","Reading netcfg.json : " + e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.v("AndroidOCR","Reading netcfg.json : " + e.getMessage());
			}
		}
	}
	
	public void saveSettings(int hidden, 
			int output,
			int samples, 
			double 
			tolerance, 
			double 
			rate, 
			long epochs) {
		nPE_hidden = hidden;
		nPE_output = output;
		nTrainingData = samples;
		errorTolerance = tolerance;
		learningRate = rate;
		maxEpochs = epochs;
		Log.v("AndroidOCR","Updating netcfg.json : hPE:" + nPE_hidden + " oPE:" + nPE_output + " Samples:" + nTrainingData + 
				" rate:" + learningRate + " tolerance:" + errorTolerance + " epochs:" + maxEpochs);
		writeJSON();
	}
	
}
