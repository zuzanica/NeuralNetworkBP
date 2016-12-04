package projekt;

import java.util.Random;
/**
 * 
 * @author xstude22
 * @see Class represents one neuron
 */
public class Neuron {
	public double[] weights;
	public double net = 0.0; 	//neuron input
	public double out = 0.0;	//neuron output
	public double derivation = 0.0;
	
	public Neuron(int weightsCount){
		Random rand = new Random();
		weights = new double[weightsCount];
		int i;
		for(i=0; i < weightsCount; i++){
			weights[i] = -1 + (1 - (-1)) * rand.nextDouble(); //generate random weights
		}
	}
	
}
