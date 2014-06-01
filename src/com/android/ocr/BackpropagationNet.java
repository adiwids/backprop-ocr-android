package com.android.ocr;

import java.text.DecimalFormat;

import android.util.Log;

public class BackpropagationNet {
	public double totalError;
	private double errorTolerance;
	private double learningRate;
	public int nLayers;
	private long maxEpochs;
	
	private int nTrainingSets;
	private int setNumber;
	
	public NetworkLayer layer[];
	// SALAH REPRESENTASI
	public double inputs[][];
	public double outputs[][];
	public double targetOutputs[][];
	// SALAH WOY
	
	public boolean isTrained = false;
	
	// nPEOnEachLayers adalah array jumlah PE untuk setiap lapisan
	// length dari array menunjukkan jumlah lapisan, sedangkan isi array adalah jumlah PE
	public BackpropagationNet(int nPEOnEachLayers[], 
			double inputPatterns[][], double outputPatterns[][],
			double lr,
			double error,
			long epoch) {
		// inisialisasi bpn parameter
		nTrainingSets = inputPatterns.length;
		nLayers = nPEOnEachLayers.length;
		errorTolerance = error;
		learningRate = lr;
		maxEpochs = epoch;
		
		layer = new NetworkLayer[nLayers];
		// asumsi layer[0] selalu lapisan masukan
		layer[0] = new NetworkLayer(nPEOnEachLayers[0], nPEOnEachLayers[0]);
		
		// inisialisasi lapisan yang lain
		for(int i = 1;i < nLayers;i++) {
			layer[i] = new NetworkLayer(nPEOnEachLayers[i], nPEOnEachLayers[i - 1]);
		}
		
		inputs = new double[nTrainingSets][layer[0].PE.length];
		// assign input
		for(int i = 0;i < nTrainingSets;i++) {
			for(int j = 0;j < layer[0].PE.length;j++) {
				inputs[i][j] = inputPatterns[i][j];
				//System.out.print(inputs[i][j]);
			}
		}
		// lapisan keluaran diasumsikan memiliki index jumlah lapisan - 1 (index array)
		outputs = new double[nTrainingSets][layer[nLayers - 1].PE.length];
		targetOutputs = new double[nTrainingSets][layer[nLayers - 1].PE.length];
		// assign target output
		for(int i = 0;i < nTrainingSets;i++) {
			for(int j = 0;j < layer[nLayers - 1].PE.length;j++) {
				targetOutputs[i][j] = outputPatterns[i][j];
				//System.out.print(targetOutputs[i][j] + " ");
			}
		}
	}
	
	public BackpropagationNet(int nPEOnEachLayers[]) {
		nLayers = nPEOnEachLayers.length;
		
		layer = new NetworkLayer[nLayers];
		// asumsi layer[0] selalu lapisan masukan
		layer[0] = new NetworkLayer(nPEOnEachLayers[0], nPEOnEachLayers[0]);
		
		// inisialisasi lapisan yang lain
		for(int i = 1;i < nLayers;i++) {
			layer[i] = new NetworkLayer(nPEOnEachLayers[i], nPEOnEachLayers[i - 1]);
		}
		
		// load saved weigths
	}
	
	public void feedforward() {
		for(int i = 0;i < layer[0].PE.length;i++) {
			// untuk lapisan masukan, input tidak mengalami proses apa2
			// jadi keluaran PE-nya = masukan
			layer[0].PE[i].output = layer[0].inputs[i];
		}
		// lapisan setelah lapisan masukan menerima input
		layer[1].inputs = layer[0].inputs;
		
		for(int i = 0;i < nLayers;i++) {
			// lakukan feed forward antar lapisan
			layer[i].feedforward();
			// kecuali lapisan keluaran (lapisan terakhir),
			// assign keluaran lapisan menjadi masukan bagi lapisan setelahnya
			if(i != nLayers - 1) {
				layer[i + 1].inputs = layer[i].getOutputs();
			}
		}
	}
	
	private void calculateTotalError() {
		totalError = 0;
		// untuk setiap pola yang diproses pada PE, hitung akumulasi error
		// menggunakan persamaan 2.13
		for(int i = 0;i < nTrainingSets;i++) {
			for(int j = 0;j < layer[nLayers - 1].PE.length;j++) {
				totalError = totalError + (0.5 * (Math.pow((targetOutputs[i][j] - outputs[i][j]), 2)));
			}
		}
	}
	
	public void train(){
		long k = 0, startTime = 0, endTime = 0;
		
		// kondisi berhenti proses pembelajaran adalah batasan epoch dan
		// total galat <= toleransi nilai batas
		do {
			startTime = System.nanoTime();
			// setiap pola dijadikan masukan bagi lapisan masukan
			for(setNumber = 0;setNumber < nTrainingSets;setNumber++) {
				for(int i = 0;i < layer[0].PE.length;i++) {
					layer[0].inputs[i] = inputs[setNumber][i];
				}
				// lalu feed forward hasilnya ke lapisan setelahnya
				feedforward();
				// assign pola target keluaran dari tiap2 PE lapisan keluaran
				for(int i = 0;i < layer[nLayers - 1].PE.length;i++) {
					//layer[nLayers - 1].PE[i].output = targetOutputs[setNumber][i];
					outputs[setNumber][i] = layer[nLayers - 1].PE[i].output;
				}
				// hitung sinyal galat keluaran & tersembunyi
				//calculateOutputsError();
				calculateHiddensError();
				// propagasi balik galat dan perbarui bobot & threshold
				backpropagateWeightsThresholds();
				endTime = System.nanoTime();
			}
			DecimalFormat df = new DecimalFormat("#0.00");
			Log.v("AndroidOCR", df.format((endTime - startTime) / 1000000d) + "ms | epoch: " + k + ", total error: " + totalError);
			k++;
			// hitung totalError
			calculateTotalError();
			//Log.v("AndroidOCR", "Total Error: " + totalError + ", Epoch: " + k);
		}
		while((totalError > errorTolerance) && (k < maxEpochs));
		isTrained = true;
	}
	
	// perhitungan sinyal galat PE lapisan keluaran
	// menggunakan rumus Persamaan 2.16
	/*private void calculateOutputsError() {
		for(int i = 0;i < layer[nLayers - 1].PE.length;i++) {
			layer[nLayers - 1].PE[i].errorSignal = (targetOutputs[setNumber][i] - layer[nLayers - 1].PE[i].output) *
					layer[nLayers - 1].PE[i].output *
					(1 - layer[nLayers - 1].PE[i].output);
		}
	}*/
	
	// perhitungan sinyal galat PE lapisan tersembunyi secara runut mundur
	// dengan menggunakan rumus Persamaan 2.19
	// sumError adalah jumlah sinyal error
	// errorSignal adalah fungsi turunan dari fungsi aktivasi dikali sumError (Persamaan 2.19)
	private void calculateHiddensError() {
		double sumError;
		
		// perhitungan sinyal galat PE lapisan keluaran
		// menggunakan rumus Persamaan 2.16
		//System.out.println("E on OutLayer : ");
		Log.v("AndroidOCR", "## Output Layer Errors ##");
		for(int i = 0;i < layer[nLayers - 1].PE.length;i++) {
			layer[nLayers - 1].PE[i].errorSignal = (targetOutputs[setNumber][i] - layer[nLayers - 1].PE[i].output) *
					layer[nLayers - 1].PE[i].output *
					(1 - layer[nLayers - 1].PE[i].output);
			//System.out.print(layer[nLayers - 1].PE[i].errorSignal + " ");
			Log.v("AndroidOCR", "errorSignal PE-" + i + ": " + targetOutputs[setNumber][i] + " vs " + layer[nLayers - 1].PE[i].output + 
					" --> " + layer[nLayers - 1].PE[i].errorSignal);
		}
		
		// perhitungan sinyal galat PE lapisan tersembunyi secara runut mundur
		// dengan menggunakan rumus Persamaan 2.19
		//System.out.println("E on HideLayer : ");
		Log.v("AndroidOCR", "## Hidden Layer Errors ##");
		for(int i = nLayers - 2;i > 0;i--) {
			Log.v("AndroidOCR", "Hidden layer index: " + i);
			for(int j = 0;j < layer[i].PE.length;j++) {
				sumError = 0;
				
				for(int k = 0;k < layer[i + 1].PE.length;k++) {
					sumError = sumError + 
							(layer[i + 1].PE[k].errorSignal * layer[i + 1].PE[k].weight[j]);
				}
				
				layer[i].PE[j].errorSignal = (layer[i].PE[j].output * (1 - layer[i].PE[j].output)) * sumError;
				//System.out.print(layer[i].PE[j].errorSignal + " ");
				Log.v("AndroidOCR", "sumError: " + sumError);
				Log.v("AndroidOCR", "errorSignal PE-" + j + ": " + layer[i].PE[j].errorSignal);
			}
		}
	}
	
	private void backpropagateWeightsThresholds() {
		//String logWeight = "";
		for(int i = nLayers - 1;i > 0;i--) {
			for(int j = 0;j < layer[i].PE.length;j++) {
				//logWeight = "";
				//System.out.println();
				// hitung perubahan bobot yg terjadi menggunakan rumus 2.20 utk setiap PE
				for(int k = 0;k < layer[i].inputs.length;k++) {
					layer[i].PE[j].deltaWeight[k] = learningRate *
							layer[i].PE[j].errorSignal * layer[i - 1].PE[k].output;
					// update bobot dengan rumus Persamaan 2.21
					layer[i].PE[j].weight[k] = layer[i].PE[j].weight[k] + layer[i].PE[j].deltaWeight[k];
					//logWeight += layer[i].PE[j].weight[k] + " ";
					//Log.v("AndroidOCR", "deltaWeight PE[" + j + "][" + i + "]: " + layer[i].PE[j].deltaWeight[k]);
					//Log.v("AndroidOCR", "newWeight PE[" + j + "][" + i + "]: " + layer[i].PE[j].weight[k]);
				}
				//System.out.println("Weight of PE-" + j + " Layer: " + i + " -> " + logWeight);
				
				// hitung perubahan threshold masing2 PE
				layer[i].PE[j].deltaThreshold = learningRate * layer[i].PE[j].errorSignal;
				// update threshold
				layer[i].PE[j].threshold = layer[i].PE[j].threshold + layer[i].PE[j].deltaThreshold;
				//Log.v("AndroidOCR", "deltaThreshold PE[" + j + "][" + i + "]: " + layer[i].PE[j].deltaThreshold);
				//Log.v("AndroidOCR", "newThreshold PE[" + j + "][" + i + "]: " + layer[i].PE[j].threshold);
			}
		}
	}
	
	// pengenalan mengembalikan index dari PE yang outputnya sesuai
	// index ini berdasarkan jumlah PE pada lapisan keluaran yang merepresentasikan
	//  jumlah karakter yang akan dikenali
	public int test(double[] input) {
		int c = 0;
		
		ProcessingElement[] outPE;
		//System.out.println("Input: ");
		for(int i = 0;i < layer[0].PE.length;i++) {
			layer[0].inputs[i] = input[i];
			//System.out.print(input[i] + " ");
		}
		feedforward();
		outPE = layer[layer.length - 1].getPE();
		/** SUSPECT !!!! */
		for(int i = 0;i < outPE.length;i++) {
			//System.out.println("Output " + i + ": " + outPE[i].output);
			/*if(outPE[c].output < outPE[i].output) {
				c = i;
			}*/
			if(outPE[i].output < outPE[c].output) {
				c = i;
			}
		}
		/** SUSPECT !!!! */
		
		return c;
	}
}
