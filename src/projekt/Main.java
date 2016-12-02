package projekt;

public class Main {

	public Main(){
	}
	
	public static void main(String[] args) {
		int epochs = 5000;
		FileParser data = new FileParser();
		Network bpNetwork = new Network(data);
		
		int i = 0;
		while(i < epochs){
			System.out.println("==================================================================================");
			System.out.println("======================ROUND: " + i+ "===================================================");
			
			System.out.println("***************************************************FIRST DATASET****************************************************");
			bpNetwork.netTraining(data.inputs4, data.target4);
		
			System.out.println("***************************************************SECOND DATASET****************************************************");
			
			bpNetwork.netTraining(data.inputs1, data.target1);
			
			System.out.println("***************************************************THIRD DATASET****************************************************");
			bpNetwork.netTraining(data.inputs3, data.target3);
			System.out.println("***************************************************FOURTH DATASET****************************************************");
			bpNetwork.netTraining(data.inputs2, data.target2);
			
			i++;
			System.out.println("=====================================================================================");
			
		}
		bpNetwork.test(data.testedInputs);
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
		bpNetwork.test(data.testedInputs);
	}

}
