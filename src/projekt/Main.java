package projekt;

import java.util.Random;

public class Main {

	public static double[] wights = {0.1, 0.5, -0.35, 0.24, -0.4, -0.15, -0.2, 0.37, 0.3, -0.92, -0.44, 0.21, -0.18, -0.33, 0.29 };
	public static int counter = 0; 
	public static Random rand = new Random();
	
	public Main(){
			
	}
	
	public static void main(String[] args) {
		int totalGoal = 0;
		FileParser data = new FileParser();
		Network bpNetwork = new Network(data);
		
		int j, i=0, inPosition = 0;
		while(i < data.epochs && bpNetwork.globalErr > data.eps){
			//inPosition = rand.nextInt((data.trainingDataset.size()));
			//inPosition = i%4;
			bpNetwork.globalErr = 0.0;
			for(j = 0 ; j < data.trainingDataset.size(); j++){
				//System.out.println(inPosition);
				System.out.println("EPOCH: " + i);
				bpNetwork.netTraining(data.trainingDataset.get(j), data.targetArr.get(j));			
			}
			i++;
		}

		for(j = 0 ; j < data.testedDataset.size(); j++){
			totalGoal += bpNetwork.test(data.trainingDataset.get(j), data.targetArr.get(j));				
		}
		
		bpNetwork.showStatistics(data.testedDataset.size(), totalGoal);
	}
	
	public void run(){
		
	}
}
