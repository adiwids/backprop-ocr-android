package com.android.ocr;

public class ProcessingElement {
	
	public double[] weights;
	public double threshold = 0;
	
	public double[] deltaWeight;
	public double deltaThreshold = 0;
	
	public double output;
	public double errorSignal;
	
	public ProcessingElement(int nInputs) {
		weights = new double[nInputs];
		deltaWeight = new double[nInputs];
	}
	
	public void randomizeWeights() {
		for(int i = 0;i < weights.length;i++) {
			weights[i] = -1 + (2 * Math.random());
			deltaWeight[i] = 0;
		}
	}
	
	public void randomizeThreshold() {
		threshold = -1 + (2 * Math.random());
		deltaThreshold = 0;
	}
}
