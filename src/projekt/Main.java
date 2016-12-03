package projekt;

import java.util.Random;

public class Main {

	public static double[] wights = {0.1, 0.5, -0.35, 0.24, -0.4, -0.15, -0.2, 0.37, 0.3, -0.92, -0.44, 0.21, -0.18, -0.33, 0.29 };
	public static int counter = 0; 
	public static Random rand = new Random();
	
	public Main(){
			
	}
	
	public static void main(String[] args) {
		
		FileParser data = new FileParser();
		Network bpNetwork = new Network(data);
		
		int j, i=0, inPosition = 0;
		while(i < data.epochs && bpNetwork.globalErr > data.eps){
			//inPosition = rand.nextInt((data.inputsArr.size()));
			//inPosition = i%4;
			bpNetwork.globalErr = 0.0;
			for(j = 0 ; j < data.inputsArr.size(); j++){
				//System.out.println(inPosition);
				System.out.println("EPOCH: " + i);
				bpNetwork.netTraining(data.inputsArr.get(j), data.targetArr.get(j));			
			}
			i++;
		}
		bpNetwork.test(data.inputsArr.get(0));
		bpNetwork.test(data.inputsArr.get(1));
		bpNetwork.test(data.inputsArr.get(2));
		bpNetwork.test(data.inputsArr.get(3));				
		
		
		/*while(i < epochs){
			bpNetwork.globalErr = 0.0;
			System.out.println("***************************************************FIRST DATASET****************************************************");
			System.out.println("EPOCH: " + i);
			
			//System.out.println("***************************************************FIRST DATASET****************************************************");
			bpNetwork.netTraining(data.inputs1, data.target1);
			bpNetwork.netTraining(data.inputs4, data.target4);
		
			//System.out.println("***************************************************SECOND DATASET****************************************************");
			
			
			//System.out.println("***************************************************THIRD DATASET****************************************************");
			bpNetwork.netTraining(data.inputs3, data.target3);
			//bpNetwork.netTraining(data.inputs4, data.target4);
			
			//System.out.println("***************************************************FOURTH DATASET****************************************************");
			bpNetwork.netTraining(data.inputs2, data.target2);
			
			i++;
			//System.out.println("=====================================================================================");
			
		}*/
		/*bpNetwork.test(data.testedInputs);
		data.testedInputs.clear();
		data.testedInputs.add(1.0);
		data.testedInputs.add(0.0);
		bpNetwork.test(data.testedInputs);
		data.testedInputs.clear();
		data.testedInputs.add(0.0);
		data.testedInputs.add(0.0);
		bpNetwork.test(data.testedInputs);
		data.testedInputs.clear();
		data.testedInputs.add(0.0);
		data.testedInputs.add(1.0);
		bpNetwork.test(data.testedInputs);*/
	}

}
