package projekt;

import java.util.ArrayList;

/**
 * 
 * @author xstude22
 * @see Class represents layer with neurons
 */
public class Layer {
	public double totalError = 0.0;
	public int neuronCount;
	public Neuron[] neuronArr;
	
	public Layer( ArrayList<Double> inputs, int neuronCount){
		this.neuronCount = neuronCount;
		neuronArr = new Neuron[neuronCount];			//create Neuron array
		int i;
		for(i=0; i < neuronCount; i++){
			neuronArr[i] = new Neuron(inputs.size());	// create neuron, initialized with weight count
		}
	}
}
