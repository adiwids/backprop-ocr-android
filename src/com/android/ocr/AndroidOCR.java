package com.android.ocr;

import java.io.File;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

public class AndroidOCR{
	
	public Bitmap image;
	public NetworkParameter parameters;
	public File baseDir;
	public File trainingDir;
	public double[][] trainingSets;
	public double[][] targets;
	private final String[] characters = {"?", "A", "B", "C", "D", "E", "F", "G", "H", "I", 
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	private int imageMaxSize = 96; // in pixels 
	public int DATASET_COUNTER = 0;
	//private int[] PELayers;
	
	private BackpropagationNet network;
	
	public AndroidOCR() {
		baseDir = new File(Environment.getExternalStorageDirectory() + "/AndroidOCR/");
		trainingDir = new File(baseDir.toString() + "/data-training/");
		parameters = new NetworkParameter(baseDir);
		trainingSets = new double[parameters.nTrainingData][(24*24)];
		targets = new double[parameters.nTrainingData][parameters.nPE_output];
		
		Log.v("AndroidOCR","Init lib -> baseDir:" + baseDir.toString() + " trainingDir:" + trainingDir.toString());
	}
	
	public void setImage(Bitmap bmp) {
		if(image != null) {
			image = null;
		}
		image = bmp.copy(bmp.getConfig(), true);
		Log.v("AndroidOCR","Set image -> " + image.getWidth() + "x" + image.getHeight());
		if(image.getWidth() > imageMaxSize || image.getHeight() > imageMaxSize) {
			image = OCRImage.resize(imageMaxSize, image);
			Log.v("AndroidOCR","Resize image -> " + image.getWidth() + "x" + image.getHeight());
		}
	}
	
	public void setNetParameter(int idxParam, double value) {
		
		switch(idxParam) {
		case 0:
			parameters.nPE_hidden = (int)value;break;
		case 1:
			parameters.nPE_output = (int)value;break;
		case 2:
			parameters.nTrainingData = (int)value;break;
		case 3:
			parameters.learningRate = value;break;
		case 4:
			parameters.errorTolerance = value;break;
		case 5:
			parameters.maxEpochs = (long)value;break;
		default:
			break;
		}
		
		parameters.saveSettings(parameters.nPE_hidden, 
				parameters.nPE_output, 
				parameters.nTrainingData, 
				parameters.errorTolerance, 
				parameters.learningRate, 
				parameters.maxEpochs);
	}
	
	private void preprocess() {
		image = PreprocessImage.binarize(image);
		Log.v("AndroidOCR","Binary image -> " + image.getWidth() + "x" + image.getHeight());
		image = SegmentationChar.doSegment(image);
		Log.v("AndroidOCR","Segmented image -> " + image.getWidth() + "x" + image.getHeight());
	}
	
	public void addTrainingSet(String label) {
		int pixel;
		String o = "";
		// tambahkan target output
		/*double[] target = getCharRepresentation(label);
		for(int i = 0;i < parameters.nPE_output;i++) {
			targets[DATASET_COUNTER][i] = target[i];
		}*/
		for(int i = 0;i < parameters.nPE_output;i++) {
			if(i == getCharIndex(label)) {
				targets[DATASET_COUNTER][i] = 1;
			} else {
				targets[DATASET_COUNTER][i] = 0;
			}
			o += targets[DATASET_COUNTER][i] + " ";
		}
		Log.v("AndroidOCR","Added output rep -> " + o);
		
		// praproses gambar
		preprocess();
		// segmen karakter di resize untuk normalisasi masukan
		// semua masukan di-resize ke 24x24 agar menghasilkan panjang matriks yg sama
		image = OCRImage.resize(24, 24, image);
		Log.v("AndroidOCR","Normilized image -> " + image.getWidth() + "x" + image.getHeight());
		int i = 0;
		o = "";
		//double[][] m = new double[image.getWidth()][image.getHeight()];
		for(int y = 0;y < image.getHeight();y++) {
			for(int x = 0;x < image.getWidth();x++) {
				pixel = Color.red(image.getPixel(x, y));
				//m[x][y] = pixel;
				if(pixel == 0) {
					pixel = 1;
				} else {
					pixel = 0;
				}
				trainingSets[DATASET_COUNTER][i] = pixel;
				o  += Integer.toString(pixel) + " ";
				i++;
			}
		}
		Log.v("AndroidOCR","Added samples " + label + "_" + DATASET_COUNTER + " -> " + o);
		//saveMatrix(trainingSets[DATASET_COUNTER]);
		Log.v("AndroidOCR","Training Data " + (DATASET_COUNTER + 1) + " of " + parameters.nTrainingData);
		if(DATASET_COUNTER < parameters.nTrainingData) {
			DATASET_COUNTER += 1;
		}
	}
	
	private int getCharIndex(String l) {
		boolean stop = false;
		int idx = 0;
		while(!stop && (idx < characters.length)) {
			if(l.equals(characters[idx])) {
				stop = true;
				break;
			}
			idx++;
		}
		
		return idx;
	}
	
	/*private double[] getCharRepresentation(String label) {
		int charIdx = getCharIndex(label);
		int size = 0;
		String binInt = String.format("%5s", Integer.toBinaryString(charIdx)).replace(' ', '0');
		Log.v("AndroidOCR", "Rep for " + label + " : " + binInt);
		size = binInt.length();
		double[] rep = new double[size];
		Log.v("AndroidOCR", "Rep Size: " + size);
		for(int i = 0;i < size;i++) {
			rep[i] = binInt.charAt(i);
		}
		return rep;
	}*/
	
	/**private void saveMatrix(double[] sample) {
		if(!trainingDir.exists()) {
			trainingDir.mkdirs();
		}
		if(trainingDir.canWrite()) {
			String csv = "";
			for(int i = 0;i < sample.length;i++) {
				csv += sample[i];
				if(i < sample.length) {
					csv += ",";
				}
			}
		}
	}*/
	
	public int checkDataExists(String label) {
		ArrayList<String> list = new ArrayList<String>();
		File[] files = trainingDir.listFiles();
		String[] tokens;
		String file;
		for(File filePath : files) {
			String path = filePath.getPath();
			list.add(path);
			tokens = path.split("/");
			file = tokens[tokens.length - 1];
			System.out.println(file);
			String[] f = file.split("\\.(?=[^\\.]+$)");
			if(f[f.length - 1].equals("csv")) {
				String[] a = f[0].split("_");
				if(a[0].equals(label)) {
					
				}
			}
		}
		
		return 0;
	}
	
	public void trainNetwork() {
		int[] nPEOnEachLayers = {(24*24), parameters.nPE_hidden, parameters.nPE_output};
		network = new BackpropagationNet(nPEOnEachLayers, 
				trainingSets, 
				targets, 
				parameters.learningRate, 
				parameters.errorTolerance, 
				parameters.maxEpochs);
		network.train();
		if(network.isTrained) {
			saveWeightsThreshold(network);
		}
	}
	
	/**private void returnTrainInfo() {
		
	}*/
	
	private void saveWeightsThreshold(BackpropagationNet net) {
		if(baseDir.canWrite()) {
			
		}
	}
	
	//private void loadWeightsThreshold() {}
	
	public String recognize() {
		double[] inputs;
		int pixel;
		int i = 0;
		
		// persiapan vector masukan
		//double[][] m = new double[image.getWidth()][image.getHeight()];
		// praproses gambar
		preprocess();
		// segmen karakter di resize untuk normalisasi masukan
		// semua masukan di-resize ke 24x24 agar menghasilkan panjang matriks yg sama
		image = OCRImage.resize(24, 24, image);
		inputs = new double[image.getWidth() * image.getHeight()];
		for(int y = 0;y < image.getHeight();y++) {
			for(int x = 0;x < image.getWidth();x++) {
				pixel = Color.red(image.getPixel(x, y));
				//m[x][y] = pixel;
				if(pixel == 0) {
					pixel = 1;
				} else {
					pixel = 0;
				}
				inputs[i] = pixel;
				trainingSets[0][i] = pixel;
				i++;
			}
		}
		
		int[] nPEOnEachLayers = {inputs.length, parameters.nPE_hidden, parameters.nPE_output};
		/*network = new BackpropagationNet(nPEOnEachLayers, 
				trainingSets, 
				targets, 
				parameters.learningRate, 
				parameters.errorTolerance, 
				parameters.maxEpochs);*/
		network = new BackpropagationNet(nPEOnEachLayers);
		int c = network.test(inputs);
		
		return characters[c];
	}
}
