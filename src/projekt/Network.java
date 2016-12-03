package projekt;

import java.io.IOException;
import java.util.ArrayList;

public class Network {
	public double globalErr = 1.0;
	private int layerCount;
	private int hNeuronsCount;
	private int oNeuronsCount;
	private double target = 0.0;
	double momentum = 1;
	double learningRate = 0.7; 
	
	public ArrayList<Double> inputs = new ArrayList<Double>();	//aktualne vstupy siete, na zaciatku podla suboru, neskoro podla vrstvy v korej sa nachadzame, 
	public ArrayList<Double> actualInputs = new ArrayList<Double>(); 	/** TODO mozno bude trebat pridat este jedny tmp imputs*/

	private Layer[] hiddenLayers;
	private Layer outputLayer;
	
	public Network(FileParser data){
		
		this.layerCount = data.layerCount;
		this.hNeuronsCount = data.hNeutronsCount;
		this.oNeuronsCount = data.oNeutronsCount;
		this.inputs = (ArrayList<Double>) data.trainingDataset.get(0).clone();
		actualInputs.clear();
		actualInputs = (ArrayList<Double>) inputs.clone();
		initialize();
		//printInputs();
		//printInitialState();
		//printInfo();
	}
	
	public void initialize(){
		hiddenLayers = new Layer[layerCount];
		int i;
		for( i = 0 ; i < layerCount; i++ ){
			hiddenLayers[i] = new Layer(actualInputs, hNeuronsCount);
			refreshInputs(hiddenLayers[i].neuronArr);
		}	
		outputLayer = new Layer(actualInputs, oNeuronsCount);
	}
	
	
	public void netTraining(ArrayList<Double> inputs, double target){
		this.target= target;
		this.inputs.clear();
		this.inputs = (ArrayList<Double>) inputs.clone();
		actualInputs.clear();
		actualInputs = (ArrayList<Double>) inputs.clone();
		//printInputs();
		
		
		forwardPropagate();
		derivationComputing();
		errorBackPropagation();
		printOut();
		//printInfo();
				
	}
	
	public int test(ArrayList<Double> inputs, double exptectation){
		int netGoal = 0;
		System.out.println("=======================================================TESTING=========================================");	
		try {
			Main.output.write("=======================================================TESTING==========================================\n");
		} catch (IOException e) {}
		target = exptectation;
		this.inputs.clear();
		this.inputs = (ArrayList<Double>) inputs.clone();
		actualInputs.clear();
		actualInputs = (ArrayList<Double>) inputs.clone();
		//printInputs();
		forwardPropagate();
		netGoal = compareNetOutput(exptectation);
		printOut();
		return netGoal;
		//printInfo();
	}
	
	public int compareNetOutput( double expectation ){
		int j, goal = 0;
		for(j =0 ; j < outputLayer.neuronCount; j++){
			if(( Math.abs(outputLayer.neuronArr[j].out - target + outputLayer.totalError) < 0.1) || 
			   ( Math.abs(outputLayer.neuronArr[j].out - target - outputLayer.totalError) < 0.1)){
				goal++;
			}		
		}
		return goal;
	}
	
	public void showStatistics(int testedInputs, int totalGoal, int epoch){
		double accuracy = totalGoal/testedInputs * 100;
		System.out.println("==========================================================================================================");
		System.out.format("Finished after: %d epochs.\n", epoch);
		System.out.format("Correct results: %d/%d \n", totalGoal,testedInputs);
		System.out.format("Accuracy: %.2f%%\n", accuracy);
		
		try {
			Main.output.write("========================================================================================================\n");
			Main.output.write("Finished after: " + epoch + " epochs.\n");
			Main.output.write("Correct Result: " + totalGoal + "/" + testedInputs + "\n");
			Main.output.write("Accuracy: " + accuracy + "%\n");
		} catch (IOException e) {}
	}
	
	private void forwardPropagate(){
		int i,j,k;
		// hidden layers
		for( i = 0 ; i < layerCount; i++ ){
			Layer actualLayer = hiddenLayers[i];
			for(j =0 ; j < hiddenLayers[i].neuronCount; j++){
				hiddenLayers[i].neuronArr[j].net = 0.0;
				for(k =0 ; k < actualInputs.size(); k++){
					//System.out.println("inputs count : " + actualInputs.size());
					//System.out.println("input index: " + actualInputs.get(k));
					//System.out.print("weight" +actualLayer.neuronArr[j].weights[k] + " input" +  actualInputs.get(k) + " ");					
					//System.out.println("Neuron : " +j +  " net: " + actualLayer.neuronArr[j].net );
					//printInputs();
					hiddenLayers[i].neuronArr[j].net += (actualLayer.neuronArr[j].weights[k] * actualInputs.get(k)) ;
					
					//System.out.println();
				}
				hiddenLayers[i].neuronArr[j].out = countOutput(hiddenLayers[i].neuronArr[j].net);
				//System.out.println();
			}
			refreshInputs(hiddenLayers[i].neuronArr);
		}
		//System.out.println("***************************************************");
		//printInfo();
		//System.out.println("***************************************************");
		//output layer
		for(j =0 ; j < outputLayer.neuronCount; j++){
			outputLayer.neuronArr[j].net = 0.0;
			for(k =0 ; k < actualInputs.size(); k++){
				outputLayer.neuronArr[j].net += (outputLayer.neuronArr[j].weights[k] * actualInputs.get(k)) ;
				//System.out.println("weight" +k+ "=" +  outputLayer.neuronArr[j].weights[k] + " ACTUAL IN "+ actualInputs.get(k));					
			}
			outputLayer.neuronArr[j].out = countOutput(outputLayer.neuronArr[j].net);
			//System.out.println("CHYBA NET " + outputLayer.neuronArr[j].net);
			//System.out.println("CHYBA OUTPUT " + outputLayer.neuronArr[j].out);
		}
	}
	
	//count derivation in for all neurons in actual layer from neuron in previous layer
	private void derivationComputing(){
		int i,j,k;
		//total error computing
		outputLayer.totalError = 0.0;
		for(j =0 ; j < outputLayer.neuronCount; j++){
			outputLayer.totalError += countErrOut(outputLayer.neuronArr[j]);
			globalErr += 0.5*outputLayer.totalError ;
			//System.out.println(outputLayer.totalError);
			//output layer derivation
			//System.out.println("VYPOCET DERIVACIE POSLEDNEHO");
			outputLayer.neuronArr[j].derivation = countNeuDerivation(outputLayer.neuronArr[j]);
		}
		
		//hidden layer derivations
		
		Layer actuaLayer = outputLayer, nextLayer = outputLayer;
		for( i = layerCount-1 ; i >= 0; i-- ){
			actuaLayer = hiddenLayers[i];
			if(i == layerCount-1){
				nextLayer = outputLayer; 
			}else{
				nextLayer = hiddenLayers[i+1]; 
			}
			
			for(j =0 ; j < actuaLayer.neuronCount; j++){
				//System.out.println("VYPOCET DERIVACIE ");
				hiddenLayers[i].neuronArr[j].derivation = 0.0;
				for(k =0 ; k < nextLayer.neuronCount; k++){
					//System.out.println("Vystupujuca vaha " + nextLayer.neuronArr[k].weights[j]);
					//System.out.println("derivace predosleho  " + nextLayer.neuronArr[k].derivation);
					hiddenLayers[i].neuronArr[j].derivation += nextLayer.neuronArr[k].weights[j] * nextLayer.neuronArr[k].derivation; 
				}
				//System.out.println("V7stup aktualneho neuronu  " + actuaLayer.neuronArr[j].out);
				hiddenLayers[i].neuronArr[j].derivation *= actuaLayer.neuronArr[j].out * (1.0 - actuaLayer.neuronArr[j].out);
				//System.out.println("Cela nova derivacia neuronu  " + j + " je "+ hiddenLayers[i].neuronArr[j].derivation);
			}
		}	
	}
	
	private void errorBackPropagation(){		
		int i,j,k ;
		
		//System.out.println("UPRAVA VAH POSLEDNEJ VRSTVY");
		//output layer
		refreshInputs(hiddenLayers[hiddenLayers.length-1].neuronArr); //set correct input of actual layer
		for(j =0 ; j < outputLayer.neuronCount; j++){			//all neutrons
			for(k =0 ; k < actualInputs.size(); k++){			//all inputs resp. outputs
				//System.out.println("povodna vaha " + outputLayer.neuronArr[j].weights[k]);
				//System.out.println("derivace predosleho  " + outputLayer.neuronArr[j].derivation);
				//System.out.println("vstup " + actualInputs.get(k));
				outputLayer.neuronArr[j].weights[k] = momentum * outputLayer.neuronArr[j].weights[k] + 
						learningRate*( outputLayer.neuronArr[j].derivation * actualInputs.get(k));	// count new wigth	
				//System.out.println("nova vaha " + outputLayer.neuronArr[j].weights[k]);
				
			} // pri vypocte novej vahy je mozne pricitavat aj odcitavat zalezi na algoritme
			//System.out.println("cela vaha " + outputLayer.neuronArr[j].weights[0]);
			//System.out.println("cela vaha " + outputLayer.neuronArr[j].weights[1]);
		}
		
		//System.out.println("UPRAVA VAH SKRITYCH VRSTIEV");
		//hidden layer
		actualInputs.clear();
		actualInputs = (ArrayList<Double>) inputs.clone();
		//System.out.println("In 1 " + inputs.get(0) + " IN 2 " + inputs.get(1));
		for( i = 0 ; i < layerCount; i++ ){
			Layer actualLayer = hiddenLayers[i];
			for(j =0 ; j < actualLayer.neuronCount; j++){
				for(k =0 ; k < actualInputs.size(); k++){
					//System.out.println("povodna vaha " + actualLayer.neuronArr[j].weights[k]);
					//System.out.println("derivace predosleho  " + actualLayer.neuronArr[j].derivation);
					//System.out.println("vstup " + actualInputs.get(k));
					hiddenLayers[i].neuronArr[j].weights[k] = momentum * actualLayer.neuronArr[j].weights[k] +
							learningRate*( actualLayer.neuronArr[j].derivation * actualInputs.get(k));			
					//System.out.println("prepisana vaha " + hiddenLayers[i].neuronArr[j].weights[k]);
				}
			}
			refreshInputs(hiddenLayers[i].neuronArr);
		}
	}
	
	private void refreshInputs(Neuron[] neurons){
		actualInputs.clear();
		int i;
		for(i=0; i < neurons.length; i++){
			actualInputs.add(neurons[i].out);		// create neuron, initillized with weight count
		}
	}

	private double countOutput(double totalNeuronNet){
		double tmp = 1.0 / (1.0 + Math.exp(-totalNeuronNet));
		return tmp;
	}
	
	private double countErrOut(Neuron neuron){
		double tmp = Math.pow((target - neuron.out), 2) /2 ;
		return tmp;
	}
	
	private double countNeuDerivation(Neuron neuron){
		double rightSide = (target - neuron.out);
		double leftSide = (neuron.out * (1.0 - neuron.out));
		return rightSide * leftSide ;
	}
	
	public void printInfo(){
		int i,j,k;
		System.out.println("HIDDEN LAYER COUT: " + layerCount);
		for( i = 0 ; i < layerCount; i++ ){
			System.out.println("Layer: " + i);
			System.out.println("Neuorn count: " + hiddenLayers[i].neuronCount);
			for(j =0 ; j < hiddenLayers[i].neuronCount; j++){
				System.out.println("Neuron: " + j);
				System.out.println("NET: " + hiddenLayers[i].neuronArr[j].net);
				System.out.println("OUT: " + hiddenLayers[i].neuronArr[j].out);
				for(k =0 ; k < hiddenLayers[i].neuronArr[j].weights.length; k++){
					System.out.print("weight" +k+ "=" +  hiddenLayers[i].neuronArr[j].weights[k] + " ");					
				}
				System.out.println();
			}
			System.out.println();	
		}	
		System.out.println("........................................................................................ ");
		System.out.println("OUTPUT LAYER");
		System.out.println("TOTAL OUT ERROR: " + outputLayer.totalError);
		System.out.println("Neuorn count: " + outputLayer.neuronCount);
		for(j =0 ; j < outputLayer.neuronCount; j++){
			System.out.println("Neuron: " + j);
			//System.out.println("ERROROUT: " + outputLayer.neuronArr[j].errorOut);
			System.out.println("Derivation: " + outputLayer.neuronArr[j].derivation);
			System.out.println("NET: " + outputLayer.neuronArr[j].net);
			System.out.println("RESULT OUT: " + outputLayer.neuronArr[j].out);
			System.out.println("TARGET: " + target);
			for(k =0 ; k < outputLayer.neuronArr[j].weights.length; k++){
				System.out.print("weight" +k+ "=" +  outputLayer.neuronArr[j].weights[k] + " ");					
			}
			System.out.println();
		}
		
	}
	
	public void	printInputs(){
		System.out.println("==========================================================================================");
		System.out.println("Inputs count: " + inputs.size());
		int i,j,k;
		for( i = 0 ; i < actualInputs.size(); i++ ){
			System.out.print("actualInputs" +i+ "=" +  actualInputs.get(i) + " ");
		}
		System.out.println("TARGET: " + target);
		System.out.println();
		System.out.println("==========================================================================================  ");
	}
	
	public void printOut(){
		System.out.format("GLOBAL ERROR %.5f %n", globalErr);
		try {
			Main.output.write("GLOBAL ERROR " + globalErr + "\n");
		} catch (IOException e) {}
		int j;
		for(j =0 ; j < outputLayer.neuronCount; j++){
			System.out.format("INPUT : %.1f %.1f Expected OUT: %.1f NETWORK OUT: %.8f  ACTUAL OUT ERROR %.8f%n", inputs.get(0), inputs.get(1), target, outputLayer.neuronArr[j].out, outputLayer.totalError);
			try {
				Main.output.write("INPUT :" + inputs.get(0) + " " + inputs.get(1)+ " EXPECTED OUT: " + target + " NETWORK OUT: " + outputLayer.neuronArr[j].out + " ACTUAL OUT ERROR " + outputLayer.totalError+ "\n");
			} catch (IOException e) {			}

		}
	}
	
}
