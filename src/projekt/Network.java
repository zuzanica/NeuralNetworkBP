package projekt;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * @author xstude22
 * @see Network using backpropagation algorithm 
 */
public class Network {
	public double globalErr = 1.0;
	private int layerCount;		//hidden layer count
	private int hNeuronsCount;
	private int oNeuronsCount;
	private double target = 0.0;
	double momentum = 1;
	double learningRate = 0.7; 
	
	public ArrayList<Double> inputs = new ArrayList<Double>(); 		//data set input array
	public ArrayList<Double> actualInputs = new ArrayList<Double>(); //actually set input resp. output weights

	private Layer[] hiddenLayers;	
	private Layer outputLayer;
	
	/**
	 * 
	 * @param Object with initialization data 
	 */
	@SuppressWarnings("unchecked")
	public Network(FileParser data){
		
		this.layerCount = data.layerCount;
		this.hNeuronsCount = data.hNeutronsCount;
		this.oNeuronsCount = data.oNeutronsCount;
		this.inputs = (ArrayList<Double>) data.trainingDataset.get(0).clone();
		actualInputs.clear();
		actualInputs = (ArrayList<Double>) inputs.clone();
		initialize();
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
	
	/**
	 * Training function
	 * @param inputs: input data set
	 * @param target
	 */
	@SuppressWarnings("unchecked")
	public void netTraining(ArrayList<Double> inputs, double target){
		this.target= target;
		this.inputs.clear();
		this.inputs = (ArrayList<Double>) inputs.clone();
		actualInputs.clear();
		actualInputs = (ArrayList<Double>) inputs.clone();

		forwardPropagate();
		derivationComputing();
		backPropagation();
		//printOut();	
	}
	
	/**
	 * Testing function
	 * @param inputs: input data set
	 * @param exptectation: target
	 * @return
	 */
	@SuppressWarnings("unchecked")
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
		forwardPropagate();
		netGoal = compareNetOutput(exptectation);
		printOut();
		return netGoal;
	}
	
	/**
	 * Compare predicted output with target
	 * @param expectation: expected target
	 * @return
	 */
	public int compareNetOutput( double expectation ){
		int j, goal = 0;
		for(j =0 ; j < outputLayer.neuronCount; j++){
			if(( Math.abs(outputLayer.neuronArr[j].out - expectation + outputLayer.totalError) < 0.1) || 
			   ( Math.abs(outputLayer.neuronArr[j].out - expectation - outputLayer.totalError) < 0.1)){
				goal++;
			}		
		}
		return goal;
	}
	
	/**
	 * Predict output
	 */
	private void forwardPropagate(){
		int i,j,k;
		// hidden layers
		for( i = 0 ; i < layerCount; i++ ){
			Layer actualLayer = hiddenLayers[i];
			for(j =0 ; j < hiddenLayers[i].neuronCount; j++){
				hiddenLayers[i].neuronArr[j].net = 0.0;
				for(k =0 ; k < actualInputs.size(); k++){
					hiddenLayers[i].neuronArr[j].net += (actualLayer.neuronArr[j].weights[k] * actualInputs.get(k)) ;
				}
				hiddenLayers[i].neuronArr[j].out = activation(hiddenLayers[i].neuronArr[j].net);
			}
			refreshInputs(hiddenLayers[i].neuronArr);
		}
		
		//output layer
		for(j =0 ; j < outputLayer.neuronCount; j++){
			outputLayer.neuronArr[j].net = 0.0;
			for(k =0 ; k < actualInputs.size(); k++){
				outputLayer.neuronArr[j].net += (outputLayer.neuronArr[j].weights[k] * actualInputs.get(k)) ;					
			}
			outputLayer.neuronArr[j].out = activation(outputLayer.neuronArr[j].net);
		}
	}
	
	/**
	 * Count derivation in for all neurons in actual layer from neuron in previous layer.
	 */
	private void derivationComputing(){
		int i,j,k;
		outputLayer.totalError = 0.0;
		for(j =0 ; j < outputLayer.neuronCount; j++){
			outputLayer.totalError += countErrOut(outputLayer.neuronArr[j]);					 //total error computing
			globalErr += 0.5*outputLayer.totalError ;
			outputLayer.neuronArr[j].derivation = countNeuDerivation(outputLayer.neuronArr[j]);  //output layer derivation
		}
		
		//hidden layer derivations
		Layer actuaLayer = outputLayer, nextLayer = outputLayer;
		for( i = layerCount-1 ; i >= 0; i-- ){
			actuaLayer = hiddenLayers[i];
			if(i == layerCount-1){				//set correct next layer
				nextLayer = outputLayer; 
			}else{
				nextLayer = hiddenLayers[i+1]; 
			}
			
			for(j =0 ; j < actuaLayer.neuronCount; j++){
				hiddenLayers[i].neuronArr[j].derivation = 0.0;
				for(k =0 ; k < nextLayer.neuronCount; k++){
					hiddenLayers[i].neuronArr[j].derivation += nextLayer.neuronArr[k].weights[j] * nextLayer.neuronArr[k].derivation;  //sum weight * derivation of next layer
				}
				hiddenLayers[i].neuronArr[j].derivation *= actuaLayer.neuronArr[j].out * (1.0 - actuaLayer.neuronArr[j].out);
			}
		}	
	}
	
	/**
	 * Updates all weights in all layers.
	 */
	@SuppressWarnings("unchecked")
	private void backPropagation(){		
		int i,j,k ;
		//output layer
		refreshInputs(hiddenLayers[hiddenLayers.length-1].neuronArr); 		//set correct input of actual layer
		for(j =0 ; j < outputLayer.neuronCount; j++){						//all neutrons
			for(k =0 ; k < actualInputs.size(); k++){						//all inputs resp. outputs
				double deltaWeight = learningRate*( outputLayer.neuronArr[j].derivation * actualInputs.get(k)); 	//count delta weight
				outputLayer.neuronArr[j].weights[k] = momentum * outputLayer.neuronArr[j].weights[k] + deltaWeight; // update weight
			}
		}
		
		//hidden layer
		actualInputs.clear();
		actualInputs = (ArrayList<Double>) inputs.clone();
		for( i = 0 ; i < layerCount; i++ ){
			Layer actualLayer = hiddenLayers[i];
			for(j =0 ; j < actualLayer.neuronCount; j++){
				for(k =0 ; k < actualInputs.size(); k++){
					double deltaWeight = learningRate*( actualLayer.neuronArr[j].derivation * actualInputs.get(k)); 
					hiddenLayers[i].neuronArr[j].weights[k] = momentum * actualLayer.neuronArr[j].weights[k] + deltaWeight;			
				}
			}
			refreshInputs(hiddenLayers[i].neuronArr);			//set new inputs
		}
	}
	
	/**
	 * Set actual inputs from selected neurons output from next layer.
	 * @param neurons
	 */
	private void refreshInputs(Neuron[] neurons){
		actualInputs.clear();
		int i;
		for(i=0; i < neurons.length; i++){
			actualInputs.add(neurons[i].out);
		}
	}
	
	/**
	 * Count neuron out from activation function.
	 * @param totalNeuronNet
	 * @return output value in <0,1> range
	 */
	private double activation(double totalNeuronNet){
		double tmp = 1.0 / (1.0 + Math.exp(-totalNeuronNet));
		return tmp;
	}
	
	/**
	 * Count output error.
	 * @param neuron
	 * @return value of error in output layer
	 */
	private double countErrOut(Neuron neuron){
		double tmp = Math.pow((target - neuron.out), 2) /2 ;
		return tmp;
	}
	
	/**
	 * Count derivation in output layer.
	 * @param neuron
	 * @return double value of derivation
	 */
	private double countNeuDerivation(Neuron neuron){
		double rightSide = (target - neuron.out);
		double leftSide = (neuron.out * (1.0 - neuron.out));
		return rightSide * leftSide ;
	}
	
	/**
	 * Debugging print function .
	 */
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
		
	/**
	 * Output print function.
	 */
	public void printOut(){
		System.out.format("GLOBAL ERROR %.5f %n", globalErr);
		try {
			Main.output.write("GLOBAL ERROR " + globalErr + "\n");
		} catch (IOException e) {}
		int j,k;
		for(j =0 ; j < outputLayer.neuronCount; j++){
			System.out.format("INPUT: ");
			try {
				Main.output.write("INPUT: ");
			} catch (IOException e) {}
			
			for(k=0 ; k < inputs.size(); k++){
				System.out.format("%.1f  ",  inputs.get(k));
				try {
					Main.output.write(inputs.get(k) + " ");
				} catch (IOException e) {}
			}
			System.out.format("Expected OUT: %.1f NETWORK OUT: %.8f  ACTUAL OUT ERROR %.8f%n", target, outputLayer.neuronArr[j].out, outputLayer.totalError);
			try {
				Main.output.write("EXPECTED OUT: " + target + " NETWORK OUT: " + outputLayer.neuronArr[j].out + " ACTUAL OUT ERROR " + outputLayer.totalError+ "\n");
			} catch (IOException e) {}
		}
	}
	
	/**
	 * Show statistic of tested data set
	 * @param testedInputs 
	 * @param totalGoal: number of correctly predicted values 
	 * @param epoch count
	 */
	public void showStatistics(int testedInputs, int totalGoal, int epoch){
		double accuracy = ((double)totalGoal / (double)(testedInputs)) * 100;
		System.out.println("==========================================================================================================");
		System.out.format("Finished after: %d epochs.\n", epoch);
		System.out.format("Correct results: %d/%d \n", totalGoal,testedInputs);
		System.out.format("Accuracy: %.2f%%", accuracy);
		
		try {
			Main.output.write("========================================================================================================\n");
			Main.output.write("Finished after: " + epoch + " epochs.\n");
			Main.output.write("Correct Result: " + totalGoal + "/" + testedInputs + "\n");
			Main.output.write("Accuracy: " + accuracy + "%\n");
		} catch (IOException e) {}
	}
	
	
	
}
