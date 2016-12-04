package projekt;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;

/**
 * 
 * @author xstude22
 *
 */
public class Main {

	//public static double[] wights = {0.1, 0.5, -0.35, 0.24, -0.4, -0.15, -0.2, 0.37, 0.3, -0.92, -0.44, 0.21, -0.18, -0.33, 0.29 };
	//public static int counter = 0; 
	public static Random rand = new Random();
	public static BufferedWriter output = null;
	
	public Main(){
	}
	
	public static void main(String[] args) {
		int totalGoal = 0;
		FileParser data = new FileParser();
		Network bpNetwork = new Network(data);
				
        //training part
		int j, i=0, inPosition = 0;
		while(i < data.epochs && bpNetwork.globalErr > data.eps){
			bpNetwork.globalErr = 0.0;
			for(j = 0 ; j < data.trainingDataset.size(); j++){						//train for the length of data set    
				inPosition = rand.nextInt((data.trainingDataset.size()));			//get random index
				bpNetwork.netTraining(data.trainingDataset.get(inPosition), data.targetArr.get(inPosition));		//train	
			}
			i++;
		}
	
		for(j = 0 ; j < data.testedDataset.size(); j++){						//test all data set
			totalGoal += bpNetwork.test(data.testedDataset.get(j), data.testedTargetArr.get(j));			
		}
			
		bpNetwork.showStatistics(data.testedDataset.size(), totalGoal, i);
     
        if ( Main.output != null ) {
        	try {
        		Main.output.close();
        	} catch (IOException e) {
        		System.err.println("Can no open output file.");
        	}
        }
	}
	
}
