package projekt;

import java.util.Random;

public class Neuron {
	public double[] weights;
	public double net = 0.0;
	public double out = 0.0;
	public double derivation = 0.0;
	
	public Neuron(int weightsCount){
		Random rand = new Random();
		weights = new double[weightsCount];
		int i;
		for(i=0; i < weightsCount; i++){
			
			weights[i] = -1 + (1 - (-1)) * rand.nextDouble();
			System.out.println("VAHa:" + weights[i]);
			//weights[i] = -0.5 + (Math.random() * 0.5); //inicializ8cia pociatocnych vah
			//weights[i] = 1.0; //inicializ8cia pociatocnych vah
			//weights[i] = Main.wights[Main.counter];
			//Main.counter++;
		}
	}
	
}
