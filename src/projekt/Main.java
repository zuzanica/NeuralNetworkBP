package projekt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;
import java.util.Random;


public class Main {

	public static double[] wights = {0.1, 0.5, -0.35, 0.24, -0.4, -0.15, -0.2, 0.37, 0.3, -0.92, -0.44, 0.21, -0.18, -0.33, 0.29 };
	public static int counter = 0; 
	public static Random rand = new Random();
	public static BufferedWriter output = null;
	
	public Main(){
			
	}
	
	public static void main(String[] args) {
		int totalGoal = 0;
		FileParser data = new FileParser();
		Network bpNetwork = new Network(data);
		
        try {
            File file = new File(data.outFile);
            output = new BufferedWriter(new FileWriter(file));
        } catch ( IOException e ) {
        	System.err.println("Can no open output file.");
        }
		
			int j, i=0, inPosition = 0;
			while(i < data.epochs && bpNetwork.globalErr > data.eps){
				//inPosition = rand.nextInt((data.trainingDataset.size()));
				//inPosition = i%4;
				bpNetwork.globalErr = 0.0;
				for(j = 0 ; j < data.trainingDataset.size(); j++){
					//System.out.println(inPosition);
					inPosition = rand.nextInt((data.trainingDataset.size()));
					//inPosition = j;
					/*System.out.println("EPOCH: " + i);
					try {
						Main.output.write("EPOCH: " + i + "\n");
					} catch (IOException e) {}*/
					bpNetwork.netTraining(data.trainingDataset.get(inPosition), data.targetArr.get(inPosition));			
				}
				i++;
			}
	
			for(j = 0 ; j < data.testedDataset.size(); j++){
				totalGoal += bpNetwork.test(data.testedDataset.get(j), data.testedTargetArr.get(j));				
			}
			
			bpNetwork.showStatistics(data.testedDataset.size(), totalGoal, i);
		
     
          if ( Main.output != null ) {
        	  try {
        		  Main.output.close();
        	  } catch (IOException e) {
				e.printStackTrace();
        	  }
          }
	}
	
	public void run(){
		
	}
}
