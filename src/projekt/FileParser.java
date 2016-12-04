package projekt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;

/**
 * 
 * @author xstude22
 * @see Class parsing input files 
 */
public class FileParser {
	public ArrayList<Double> testedInputs;
	
	public int fileInputCounter = 0;
	public ArrayList<ArrayList<Double>> trainingDataset;
	public ArrayList<Double> targetArr;
	public ArrayList<ArrayList<Double>> testedDataset;
	public ArrayList<Double> testedTargetArr;

	//set default values
	int epochs = 500;
	double eps = 0.001;
	double learningRate = 0.7;
	int inputCount = 2;
	int layerCount = 1;  		//count of output layers
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
		if(trainingDataset.size() <= 0){
			String trainingFile = "conf/xor.csv";
			String testingFile = "conf/xor.csv";	
			parseCSV(trainingFile, trainingDataset, targetArr);
			parseCSV(testingFile, testedDataset, testedTargetArr);
		}
		
		normalizeDataset(targetArr);
		normalizeDataset(testedTargetArr);
		
		try {
            File file = new File(outFile);					//open output file
            Main.output = new BufferedWriter(new FileWriter(file));
        } catch ( IOException e ) {
        	System.err.println("Can no open output file.");
        }
		
		printInitialState();
		//saveCSV("conf/train_norm_dataset.csv",  trainingDataset, targetArr);
		//saveCSV("conf/test_norm_dataset.csv", testedDataset, testedTargetArr );
	}
	
	/**
	 * Load configuration file
	 * @param file
	 * @return
	 */
	public int loadConf(String file){
		Properties prop = new Properties();
		InputStream input = null;
		
		try {	
			input = new FileInputStream(file);
			prop.load(input); 

			// get the property value
			int tmp = conevertIntValue(prop.getProperty("InputsCount"));
			if(tmp >= 0 ){
				inputCount =  tmp;
			}
			tmp = conevertIntValue(prop.getProperty("HiddenLayers"));
			if(tmp >= 0 ){
				layerCount =  tmp;
			}
			tmp = conevertIntValue(prop.getProperty("HiddenNeurons"));
			if(tmp >= 0 ){
				hNeutronsCount =  tmp;
			}
			tmp = conevertIntValue(prop.getProperty("OutputNeutron"));
			if(tmp >= 0 ){
				oNeutronsCount =  tmp;
			}
			tmp = conevertIntValue(prop.getProperty("Epochs"));
			if(tmp >= 0 ){
				epochs =  tmp;
			}
			double dtmp = conevertDoubleValue(prop.getProperty("EPS"));
			if(dtmp >= 0.0 ){
				eps =  dtmp;
			}
			dtmp = conevertDoubleValue(prop.getProperty("LearningRate"));
			if(dtmp >= 0.0 ){
				learningRate =  dtmp;
			}
			String stmp = prop.getProperty("TrainingFile");
			if(stmp != null ){
				trainingFile =  stmp;
			}
			stmp = prop.getProperty("TestingFile");
			if(stmp != null ){
				testingFile =  stmp;
			}
			stmp = prop.getProperty("OutputFile");
			if(stmp != null ){
				outFile =  stmp;
			}
			
		} catch (IOException ex) {
			System.err.println("Configuration file does not exit. Network properties set to default.");
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
	
	public int conevertIntValue(String in){
		int tmp = -1 ;
		try{
			tmp = Integer.parseInt(in);
		}catch (Exception e) {
			System.err.println("Problem in " + in + " program set default property.");
			return tmp;
		}
		return tmp;
	}
	
	public double conevertDoubleValue(String in){
		double tmp = -1.0 ;
		try{
			tmp = Double.parseDouble(in);
		}catch (Exception e) {
			System.err.println("Problem in " + in + " program set default property.");
			return tmp;
		}
		return tmp;
	}
	
	/**
	 * Parse csv file into array
	 * @param file: input file
	 * @param dataset: created array from data set
	 * @param target: array of targets
	 * @return
	 */
	public ArrayList<ArrayList<Double>> parseCSV(String file,  ArrayList<ArrayList<Double>> dataset, ArrayList<Double> target ){
		String csvFile = file;
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

        	int j=0;
        	while((line = br.readLine()) != null) {
                String[] in = line.split(cvsSplitBy);
                ArrayList<Double> tmp = new ArrayList<Double>();
                for(j=0 ; j < in.length-1; j++){
                    tmp.add(Double.parseDouble(in[j])); 
    			}
                target.add(Double.parseDouble(in[j]));
                dataset.add(tmp);
            }
            
        } catch (IOException e) {
        	System.err.println("File " + trainingFile + " does not exist. Program sets to default property.");
        	dataset.clear();
        }
        
        return dataset;
	}
	
	/**
	 * Save normalized data set into csv
	 * @param csvfile
	 * @param dataset
	 * @param target
	 */
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
			for(j=0 ; j < tmp.size(); j++){
				builder.append(tmp.get(j)+",");
			}
			builder.append(target.get(i)+"\n");
		}
		pw.write(builder.toString());
		pw.close();

	}
	
	/**
	 *  	 	
	 * @param target: Array witch will be normalized
	 */
	public void normalizeDataset( ArrayList<Double> target){
		int i;
		double[] minmax = findMinMax(target);
		for(i=0; i < target.size(); i++){
			double val = (target.get(i) -  minmax[0]) / ( minmax[1] -  minmax[0]);
			target.set(i, val);
		}	
	}
	
	/**
	 * Find max and min in selected arrayList
	 * @param list: ArrayList
	 * @return min, max array
	 */
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
		try {
			Main.output.write("Epoch count: " + epochs + "\n");
			Main.output.write("Eps: " + eps + "\n");
			Main.output.write("Learning rate: " + learningRate + "\n");
			Main.output.write("Inputs count: " + inputCount + "\n");
			Main.output.write("Hidden layer count: " + layerCount + "\n");
			Main.output.write("Neutron count in hidden layer: " + hNeutronsCount + "\n");
			Main.output.write("Output neuron count:" + oNeutronsCount + "\n");
			Main.output.write("Training dataset file: " + trainingFile + "\n");
			Main.output.write("Testing dataset file: " + testingFile + "\n");
			Main.output.write("Output file: " + outFile + "\n");
		} catch (IOException e) {}
	}
	
	/**
	 * Debugging function print training data set
	 */
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
	
	/**
	 * Debugging function print testing data set
	 */
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
}
