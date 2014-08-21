import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class AlgorithmRunner{
	public static void main(String[] args){
		System.out.println("Select the algorithm:");
		System.out.println("1. QuickFind-Eager");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String algo = null;
		try{
			algo = br.readLine().trim();
		}catch(IOException e){
			System.out.println("Error in reading the input.");
			System.exit(1);
		}
		switch(algo){
		case "1":
			System.out.println("Print the number of objects:");
			BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
			String numOfObjects = null;
			try{
				numOfObjects = br1.readLine().trim();
			}catch(IOException e){
				System.out.println("Error in reading the input.");
				System.exit(1);
			}
			int numberOfObjects = Integer.parseInt(numOfObjects);
			//create the QuickFindEager Object with the input number of objects.
			QuickFindEager quickFindEager = new QuickFindEager(numberOfObjects);
			
			//iterate the following until the user exits.
			while(true){
				System.out.println("Please print the choice of operation (1-connected, 2-union, 3-exit)" 
					+ " and the ids of the two objects on which the operations should be performed,"
					+ " by seperating each input with a comma.");
				BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
				String input = null;
				try{
					input = br2.readLine().trim();
				}catch(IOException e){
					System.out.println("Error in reading the input.");
				}
				String[] inputs = input.split(",");
				//convert the inputs to integers.
				int[] inputsInt = new int[3];
				//if 3-exit
				if(inputs[0].equals("3")){
					System.out.println("Exiting...");
                                        System.exit(1);	
				}
				if(inputs.length == 3){
				//convert the inputs to integers
					for(int i=0; i<3; i++){
						inputsInt[i] = Integer.parseInt(inputs[i]);
					}	
					if(inputsInt[0] == 1){
						boolean connected = quickFindEager.connected(inputsInt[1], inputsInt[2]);
						if(connected){
							System.out.println("Yes.");
						}else{
							System.out.println("No.");
						}
					}else if(inputsInt[0] == 2){
						quickFindEager.union(inputsInt[1], inputsInt[2]);
					}
				}else{
					System.out.println("Error in the input. Please try again.");
					System.exit(1);				
				}
			}
		}		
	}
}
