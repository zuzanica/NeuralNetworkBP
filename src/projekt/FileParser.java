package projekt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

public class FileParser {
	public ArrayList<Double> testedInputs;
	
	public int fileInputCounter = 0;
	public ArrayList<ArrayList<Double>> inputsArr;
	public ArrayList<Double> targetArr;
			
	public ArrayList<Double> inputs1;
	public ArrayList<Double> inputs2;
	public ArrayList<Double> inputs3;
	public ArrayList<Double> inputs4;
	public double target1 = 0.0;
	public double target2 = 0.0;
	public double target3 = 1.0;
	public double target4 = 1.0;

	int epochs = 500;
	double eps = 0.001;
	double learningRate = 0.7;
	int inputCount = 2;
	int layerCount = 1;  		//pocet vrstiev bez vstupu a poslednej vrstvy
	int hNeutronsCount = 3;
	int oNeutronsCount = 1;
	String inFile = "conf/xor.csv";
	String outFile = "out.txt";

	public FileParser(){
		loadConf("conf/config.properties");
		inputsArr = new ArrayList<ArrayList<Double>>();
		targetArr = new ArrayList<Double>();
		parseCSV(inFile);
		printInitialState();
		
		/*inputs1 = new ArrayList<Double>();
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
		
		int i;
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
			inFile = prop.getProperty("InputFile");
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
	
	public void parseCSV(String file){
		String csvFile = file;
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

        	int i=0;
        	while((line = br.readLine()) != null) {
                // use comma as separator
    			System.out.println("Input LINE : " + i);
                String[] in = line.split(cvsSplitBy);
                ArrayList<Double> tmp = new ArrayList<Double>();
                tmp.add(Double.parseDouble(in[0]));
                tmp.add(Double.parseDouble(in[1]));
                targetArr.add(Double.parseDouble(in[2]));
                //System.out.println("input 1 = " + tmp.get(0) + " , input 2 " + tmp.get(1) + " , target : " + in[2]);
                inputsArr.add(tmp);
                i++;
            }
            
        } catch (IOException e) {
        	System.err.println("File " + inFile + " does not exist.");
        }
	}
	
	public void printInitialState(){
		System.out.println("Epoch count: " + epochs);
		System.out.println("Eps: " + eps);
		System.out.println("Learning rate: " + learningRate);
		System.out.println("Inputs count: " + inputCount);
		System.out.println("Hidden layer count: " + layerCount);
		System.out.println("Neutron count in hidden layer: " + hNeutronsCount);
		System.out.println("Output neuron count:" + oNeutronsCount);
		System.out.println("Data file: " + inFile);
		System.out.println("Output file: " + outFile);
		printInputs();
	}
	
	public void printInputs(){
		int i,j; 
		for(i=0; i<inputsArr.size(); i++){
			ArrayList<Double> tmp = inputsArr.get(i);
			System.out.print(i + ". "); 
			for(j=0 ; j < tmp.size(); j++){
				System.out.print("input " + j + " = " + tmp.get(j) + " "); 
			}
			System.out.println("Target : " + targetArr.get(i));
		}
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
