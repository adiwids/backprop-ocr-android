package com.android.ocr;

public class NetworkLayer {
	
	public double[] inputs;
	private double netInput;
	
	public ProcessingElement[] PE;
	
	public NetworkLayer(int nLayers, int nInputs) {
		PE = new ProcessingElement[nLayers];
		inputs = new double[nInputs];
		
		for(int i = 0;i < nInputs;i++) {
			PE[i] = new ProcessingElement(nInputs);
		}
	}
}
