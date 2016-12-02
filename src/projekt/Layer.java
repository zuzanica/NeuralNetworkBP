package projekt;

import java.util.ArrayList;
import java.util.Arrays;

public class Layer {
	//public double inputs[];
	public double totalError = 0.0;
	public int neuronCount;
	public Neuron[] neuronArr;
	
	public Layer( ArrayList<Double> inputs, int neuronCount){
		this.neuronCount = neuronCount;
		
		//inputs =  Arrays.copyOf(inputs, inputs.length);
		
		//create Neuron array
		neuronArr = new Neuron[neuronCount];
		int i;
		for(i=0; i < neuronCount; i++){
			neuronArr[i] = new Neuron(inputs.size());		// create neuron, initillized with weight count
		}

	}

}
