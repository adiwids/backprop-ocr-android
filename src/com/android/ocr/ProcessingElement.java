package com.android.ocr;

import android.util.Log;

/**
 * 
 * @author freakyelf
 * class ProcessingElement a.k.a Neuron/Node/Unit
 */

public class ProcessingElement {
	// nilai aktivasi PE
	public double output;
	// bobot PE berdasarkan jumlah input
	public double weight[];
	// perubahan bobot untuk setiap input
	public double deltaWeight[];
	// nilai ambang/bias
	public double threshold;
	// peribahan nilai ambang
	public double deltaThreshold;
	// sinyal galat
	public double errorSignal;

	public ProcessingElement(int nPEs) {
		// inisialisasi bobot setiap PE
		weight = new double[nPEs];
		deltaWeight = new double[nPEs];
		// inisialisasi nilai bobot secara random
		initWeightsThreshold();
	}
	
	private void initWeightsThreshold() {
		for(int i = 0;i < weight.length;i++) {
			// nilai bobot diisi random -1 < w < 1
			weight[i] = -1 + 2 * Math.random();
			deltaWeight[i] = 0;
			//Log.v("AndroidOCR", "Init w PE-" + i + ": " + weight[i]);
		}
		// nilai ambang diisi random -1 < theta < 1
		threshold = -1 + 2 * Math.random();
		//Log.v("AndroidOCR", "Init threshold: " + threshold);
		deltaThreshold = 0;
	}
}
