package com.android.ocr;

import android.util.Log;

/**
 * 
 * @author freakyelf
 * class NetworkLayer
 */

public class NetworkLayer {
	// input dari PE
	public double inputs[];
	// net-input
	private double netInput;
	// PE pada lapisan
	public ProcessingElement PE[];
	
	public NetworkLayer(int nPEs, int nInputs) {
		// inisialisasi banyaknya PE
		PE = new ProcessingElement[nPEs];
		inputs = new double[nInputs];
		for(int i = 0;i < PE.length;i++) {
			// inisialisasi receptor masukan PE berdasarkan jumlah input
			PE[i] = new ProcessingElement(nInputs);
		}
	}
	
	public void feedforward() {
		Log.v("AndroidOCR", "## Layer Feed Forwarding ##");
		for(int i = 0;i < PE.length;i++) {
			// assign threshold pada netinput
			netInput = PE[i].threshold;
			for(int j = 0;j < PE[i].weight.length;j++) {
				// untuk setiap PE hitung jumlah netinput dengan rumus Persamaan 2.8
				netInput = netInput + (inputs[j] * PE[i].weight[j]);
			}
			// hitung nilai aktivasi tiap PE
			PE[i].output = activation(netInput);
			//Log.v("AndroidOCR", "net-" + i + ": " + netInput + " --> " + PE[i].output);
		}
	}
	
	// fungsi aktivasi dengan Sigmoid
	private double activation(double netinput) {
		double activate;
		activate = 1 / (1 + Math.exp(-netinput));
		
		return activate;
	}
	
	// fungsi membaca output dari setiap PE
	public double[] getOutputs() {
		double outputs[];
		
		outputs = new double[PE.length];
		for(int i = 0;i < PE.length;i++) {
			outputs[i] = PE[i].output;
		}
		
		return outputs;
	}
	
	public ProcessingElement[] getPE() {
		return PE;
	}
}
