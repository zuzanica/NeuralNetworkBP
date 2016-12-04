package projekt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

public class FileParser {
	public ArrayList<Double> testedInputs;
	
	public int fileInputCounter = 0;
	public ArrayList<ArrayList<Double>> trainingDataset;
	public ArrayList<Double> targetArr;
	public ArrayList<ArrayList<Double>> testedDataset;
	public ArrayList<Double> testedTargetArr;

	int epochs = 500;
	double eps = 0.001;
	double learningRate = 0.7;
	int inputCount = 2;
	int layerCount = 1;  		//pocet vrstiev bez vstupu a poslednej vrstvy
	int hNeutronsCount = 3;
	int oNeutronsCount = 1;
	String trainingFile = "conf/xor.csv";
	String testingFile = "conf/xor.csv";
	String outFile = "out.txt";

	public FileParser(){
		loadConf("conf/config.properties");
		trainingDataset = new ArrayList<ArrayList<Double>>();
		targetArr = new ArrayList<Double>();
		testedDataset = new ArrayList<ArrayList<Double>>();
		testedTargetArr = new ArrayList<Double>();
		
		parseCSV(trainingFile, trainingDataset, targetArr);
		parseCSV(testingFile, testedDataset, testedTargetArr);
		//printInitialState();
		//printtested();
		normalizeDataset(targetArr);
		//printtested();
		normalizeDataset(testedTargetArr);
		//printtested();
		printInitialState();
		saveCSV("conf/train_norm_dataset.csv",  trainingDataset, targetArr);
		saveCSV("conf/test_norm_dataset.csv", testedDataset, testedTargetArr );
	}
	
	/**
	 * Load configuration file
	 * @param file
	 * @return
	 */
	public int loadConf(String file){
		Properties prop = new Properties();
		OutputStream output = null;
		InputStream input = null;
		
		try {	
			input = new FileInputStream(file);
			prop.load(input); 

			// get the property value
			inputCount = Integer.parseInt( prop.getProperty("InputsCount"));
			layerCount = Integer.parseInt( prop.getProperty("HiddenLayers"));
			hNeutronsCount = Integer.parseInt( prop.getProperty("HiddenNeurons"));
			oNeutronsCount = Integer.parseInt( prop.getProperty("OutputNeutron"));
			epochs = Integer.parseInt( prop.getProperty("Epochs"));
			eps = Double.parseDouble( prop.getProperty("EPS"));
			learningRate = Double.parseDouble( prop.getProperty("LearningRate"));
			trainingFile = prop.getProperty("TrainingFile");
			testingFile = prop.getProperty("TestingFile");
			outFile = prop.getProperty("OutputFile");
			
		} catch (IOException ex) {
			System.err.println("Configuration file does not exit. Network properties set to default");
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
		return 1;
	}
	
	public ArrayList<ArrayList<Double>> parseCSV(String file,  ArrayList<ArrayList<Double>> dataset, ArrayList<Double> target ){
		String csvFile = file;
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

        	int j, i=0;
        	while((line = br.readLine()) != null) {
                // use comma as separator
    			//System.out.println("Input LINE : " + i);
                String[] in = line.split(cvsSplitBy);
                ArrayList<Double> tmp = new ArrayList<Double>();
                for(j=0 ; j < in.length-1; j++){
                    tmp.add(Double.parseDouble(in[j]));
                	//System.out.print("input " + j + " = " + tmp.get(j) + " "); 
    			}

                target.add(Double.parseDouble(in[j]));
                //System.out.println("input 1 = " + tmp.get(0) + " , input 2 " + tmp.get(1) + " , target : " + in[2]);
                dataset.add(tmp);
                i++;
            }
            
        } catch (IOException e) {
        	System.err.println("File " + trainingFile + " does not exist.");
        }
        
        return dataset;
	}
	
	public void saveCSV(String csvfile,  ArrayList<ArrayList<Double>> dataset, ArrayList<Double> target ){
		PrintWriter pw = null;
		try {
		    pw = new PrintWriter(new File(csvfile));
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		}
		
		StringBuilder builder = new StringBuilder();		
		int i,j;
		for(i=0; i<dataset.size(); i++){
			ArrayList<Double> tmp = dataset.get(i);
			//System.out.print(i + ". "); 
			for(j=0 ; j < tmp.size(); j++){
				builder.append(tmp.get(j)+",");
			}
			builder.append(target.get(i)+"\n");
		}

		pw.write(builder.toString());
		pw.close();

	}
		 	
	public void normalizeDataset( ArrayList<Double> target){
		int i;
		double[] minmax = findMinMax(target);
		for(i=0; i < target.size(); i++){
			//System.out.println(target.get(i));
			//System.out.println(minmax[0]);
			double val = (target.get(i) -  minmax[0]) / ( minmax[1] -  minmax[0]);
			//System.out.println(val);
			target.set(i, val);
			//System.out.println(target.get(i));
		}	
	}
	
	public double[] findMinMax(ArrayList<Double> list){
		double minmax[] = {list.get(0) ,list.get(0) };
		int i;
		for(i=0; i < list.size(); i++){
			if(list.get(i) < minmax[0]){
				 minmax[0] = list.get(i);
			}
			if(list.get(i) >  minmax[1]){
				 minmax[1] = list.get(i);
			}	
		}
		return minmax;
	}

	public double findMax(ArrayList<Double> list){
		  return list.indexOf (Collections.max(list)); 
	}	
	
	public void printInitialState(){
		System.out.println("Epoch count: " + epochs);
		System.out.println("Eps: " + eps);
		System.out.println("Learning rate: " + learningRate);
		System.out.println("Inputs count: " + inputCount);
		System.out.println("Hidden layer count: " + layerCount);
		System.out.println("Neutron count in hidden layer: " + hNeutronsCount);
		System.out.println("Output neuron count:" + oNeutronsCount);
		System.out.println("Training dataset file: " + trainingFile);
		System.out.println("Testing dataset file: " + testingFile);
		System.out.println("Output file: " + outFile);
		//printInputs();
	}
	
	public void printInputs(){
		int i,j; 
		for(i=0; i<trainingDataset.size(); i++){
			ArrayList<Double> tmp = trainingDataset.get(i);
			System.out.print(i + ". "); 
			for(j=0 ; j < tmp.size(); j++){
				System.out.print("input " + j + " = " + tmp.get(j) + " "); 
			}
			System.out.println("Target : " + targetArr.get(i));
		}
	}
	
	public void printtested(){
		int i,j; 
		for(i=0; i<testedDataset.size(); i++){
			ArrayList<Double> tmp = testedDataset.get(i);
			System.out.print(i + ". "); 
			for(j=0 ; j < tmp.size(); j++){
				System.out.print("input " + j + " = " + tmp.get(j) + " "); 
			}
			System.out.println("Target : " + testedTargetArr.get(i));
		}
	}
	
	public void printInfo(){
		System.out.println("============================================= ");
		System.out.println("Inputs count: " + inputCount);
		int i,j;
		for( i = 0 ; i < inputCount; i++ ){
			System.out.print("input" +i+ "=" +  trainingDataset.get(i) + " ");
		}
		System.out.println();
		System.out.println("============================================= ");
	}
}
