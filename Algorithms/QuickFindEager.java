/**
*This is the eager algorithm (basic algorithm) for dynamic connectivity problem.
*Given N objects, you can perform operations: 'union' to connect two objects
*and 'connected' to check if two objects are connected.
*/

public class QuickFindEager{
	private int[] objects;	
	
	/*Runs in O(N)*/
	public QuickFindEager(int numberOfObjects){
		objects = new int[numberOfObjects]; 
		for(int i=0; i<numberOfObjects; i++){
			objects[i] = i;
		}
	}
	
	/*Runs in O(1)*/
	public boolean connected(int idP, int idQ){
		return (objects[idP] == objects[idQ]);
	}

	/*Runs in O(N^2)*/
	public void union(int idP, int idQ){
		int idp = objects[idP];
		int idq = objects[idQ];
		for(int i=0; i<objects.length; i++){
			if(objects[i] == idp){
				objects[i] = idq;
			}
		}
	}

}
