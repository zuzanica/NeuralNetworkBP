package projekt;

import java.util.ArrayList;

public class FileParser {
	public ArrayList<Double> testedInputs;
	
	public int fileInputCounter = 0;
	public ArrayList<Double> inputs1;
	public ArrayList<Double> inputs2;
	public ArrayList<Double> inputs3;
	public ArrayList<Double> inputs4;
	public double target1 = 0.0;
	public double target2 = 0.0;
	public double target3 = 1.0;
	public double target4 = 1.0;
	int layerCount = 1; 	//pocet vrstiev bez vstupu a poslednej vrstvy
	int inputCount = 2;
	int hNeutronsCount = 5;
	int oNeutronsCount = 1;
	
	public FileParser(){
		inputs1 = new ArrayList<Double>();
		inputs1.add(1.0);
		inputs1.add(1.0);
		inputs2 = new ArrayList<Double>();
		inputs2.add(0.0);
		inputs2.add(0.0);
		inputs3 = new ArrayList<Double>();
		inputs3.add(0.0);
		inputs3.add(1.0);
		inputs4 = new ArrayList<Double>();
		inputs4.add(1.0);
		inputs4.add(0.0);
		
		testedInputs = new ArrayList<Double>();
		testedInputs.add(1.0);
		testedInputs.add(1.0);
		
		/*int i;
		for(i=0; i < inputCount; i++){
			//weights[i] = new Double(1.0); //inicializ8cia pociatocnych vah
			//inputs.add(0 + (Math.random() * 1));
			if(fileInputCounter == 0){
				inputs.add(0.0);
			}else{
				inputs.add(1.0);
			}
		}
		fileInputCounter = (1 + fileInputCounter) % 2;*/
		//printInfo();
	}
	
	public void printInfo(){
		System.out.println("============================================= ");
		System.out.println("Inputs count: " + inputCount);
		int i,j;
		for( i = 0 ; i < inputCount; i++ ){
			System.out.print("input" +i+ "=" +  inputs1.get(i) + " ");
		}
		System.out.println();
		System.out.println("============================================= ");
	}
}
