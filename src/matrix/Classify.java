package matrix;
import java.util.ArrayList;
import java.util.HashSet;

public class Classify {

	Double[][] trainigVectors;
	Double[][] testingVectors;
	int[] testingLabels;
	int[] trainingLabels;
	//Saves at each index the label we have classified for the vector at that index in the training vector array
	int[] finalClassifiedLabel;
	int[][] kNearestLabelsForEachElement;
	int[][] kNearestIdsForEachElement;
	
	///
	HashSet<Integer> skipThese = new HashSet<Integer>();
	
	/***** below values are instance variable specific to each vector we are classifying. *****/
	// The minimum K labels for each vector.
	int[] minKLabels;
	// Top k values, least distance value is on position 0 and more than that on 1 and so on ...
	double[] minKDistances;
	// id of the min k vectors we chose, for reporting purposes
	int[] minKIds;

	public void ClassifyTestingData(Double[][] trainingVectors, Double[][] testingVectors, int[] trainingLabels, int[] testingLabels) {

		this.trainigVectors = trainingVectors;
		this.testingVectors = testingVectors;
		this.trainingLabels = trainingLabels;
		this.testingLabels = testingLabels;
		finalClassifiedLabel = new int[200];
		kNearestLabelsForEachElement = new int[200][3];
		kNearestIdsForEachElement = new int[200][3];
		
		
		for (int i = 0; i < testingVectors.length; i++) {

			Double[] vector = testingVectors[i];
			classifyVector(vector, i, true);
			selectAndSaveFinalLabel(i);
			// save k nearest labels for each testing vector
			for(int j = 0;  j < minKLabels.length; j++ ){
				kNearestLabelsForEachElement[i][j] = minKLabels[j];
			}
			// save k nearest Ids for each testing vector for reporting
			for(int j = 0;  j < minKIds.length; j++ ){
				kNearestIdsForEachElement[i][j] = minKIds[j];
			}
		}

	}

	public void consolidate(){
		
		for (int i = 0; i < trainigVectors.length; i++) {

			Double[] vector = trainigVectors[i];
			classifyVector(vector, i, false);
			
			if(selectFinalLabel(i) != trainingLabels[i]){
				skipThese.add(i);	
			}
		}
	}
	
	public int classifyVector(Double[] vectorToClassify, int vectorIndex, boolean testingData) {

		int classificationResult = -1;
		double euDistance = 0;
		

		//for (int i = 0; i < 18; i++) {
			// clear the instances.
			minKLabels = new int[3];
			minKDistances = new double[3];
			minKIds = new int[3];
			// initialize each distance with the max value
			for(int z=0; z<minKDistances.length; z++){
				minKDistances[z] = Double.MAX_VALUE;
			}
			
			for (int j = 0; j < trainigVectors.length; j++) {
				if(testingData && skipThese.contains(j)){
					continue;
				}
				euDistance = getEuclidianDistance(vectorToClassify, trainigVectors[j]);
				insertDistanceIfLowerThanOthers(euDistance, j);
			}
		//}
		return classificationResult;
	}

	public void selectAndSaveFinalLabel( int vectorIndex){
		
		//we can have max 10 labels
		int[] labelCount = new int[10];
		
		// find the count for each label
		for(int i=0; i<minKLabels.length; i++){
			labelCount[minKLabels[i]]++;
		}
		
		HashSet<Integer> labelsWithHighestCount = new HashSet<Integer>();
		//select the label/s with the highest count. This is the worst way we can do it but still...
		for(int i=0; i <labelCount.length; i++){
			boolean flag = true;
			for(int j = 0; j<labelCount.length; j++){
				if(labelCount[i] < labelCount[j]){
					flag = false;
					break;
				}
			}
			if(flag){
				labelsWithHighestCount.add(i);
			}
		}
		
		// now if there is more than 1 label in the set , then we find which has the least euclidean distance and take.
		// If there is only one label it will put that as the final label. (No specific code for that.)
		for(int i=0; i<minKLabels.length; i++){
			if(labelsWithHighestCount.contains(minKLabels[i])){
				finalClassifiedLabel[vectorIndex] = minKLabels[i]; 
				break;
			}
		}
		
	}
	
	public double getEuclidianDistance(Double[] x, Double[] y) {

		double result = 0;
		double diff = 0;
		double square = 0;

		for (int i = 0; i < x.length; i++) {

			diff = x[i] - y[i];
			square = diff * diff;

			result = result + square;
		}

		result = Math.sqrt(result);

		return result;
	}

	/*
	 * insert the value in the top K closest values array that we keep.
	 */
	public void insertDistanceIfLowerThanOthers(double value, int vectorIndex) {

		for (int i = 0; i < minKDistances.length; i++) {
			if (value < minKDistances[i]) {
				insertInArrayAtPos(i, value, vectorIndex);
				break;
			}
		}
	}

	/*
	 * insert the value at the specified pos, shifting all values 1 down,
	 * discarding the last value.
	 * we also move the array containing the classified lowest values while we move the 
	 * distance value array
	 */
	public void insertInArrayAtPos(int pos, double value, int vectorIndex) {

		for (int i = minKDistances.length - 1; i > pos; i--) {
			minKDistances[i] = minKDistances[i - 1];
			minKLabels[i] = minKLabels[i-1];
			minKIds[i] = minKIds[i-1];
		}

		minKDistances[pos] = value;
		// we get the label associated with the vector index and save it.
		minKLabels[pos] = trainingLabels[vectorIndex];
		minKIds[pos] = vectorIndex;
		
	}
	
	public void printClassifiedData(){
	
		int unmatched = 0;
		for(int i=0; i<testingVectors.length; i++){
			//System.out.println(String.format("For vector %d classified value is %d and real is %d [IDs 3 nearest labels are %d, %d, %d ]"
			//		, i, finalClassifiedLabel[i], i /*testingLabels[i]*/, kNearestIdsForEachElement[i][0], kNearestIdsForEachElement[i][1], kNearestIdsForEachElement[i][2]));
		
			System.out.println(String.format("For vector %d classified value is %d and real is %d [IDs 3 nearest labels are %d, %d, %d ]"
					, i, finalClassifiedLabel[i], testingLabels[i] , kNearestLabelsForEachElement[i][0], kNearestLabelsForEachElement[i][1], kNearestLabelsForEachElement[i][2]));
		
			
			if(finalClassifiedLabel[i] != testingLabels[i]){
				unmatched++;
			}
		}
		
		System.out.println("Matched = " + (testingVectors.length - unmatched) +",  Unmatched = " + unmatched);
		System.out.println("Achieved recognition accuracy =  " + (((testingVectors.length-unmatched)*100)/testingVectors.length ) + " %");
		
	}
	
	///
	public int selectFinalLabel( int vectorIndex){
		
		//we can have max 10 labels
		int[] labelCount = new int[10];
		int result = -1;
		
		// find the count for each label
		for(int i=0; i<minKLabels.length; i++){
			labelCount[minKLabels[i]]++;
		}
		
		HashSet<Integer> labelsWithHighestCount = new HashSet<Integer>();
		//select the label/s with the highest count. This is the worst way we can do it but still...
		for(int i=0; i <labelCount.length; i++){
			boolean flag = true;
			for(int j = 0; j<labelCount.length; j++){
				if(labelCount[i] < labelCount[j]){
					flag = false;
					break;
				}
			}
			if(flag){
				labelsWithHighestCount.add(i);
			}
		}
		
		// now if there is more than 1 label in the set , then we find which has the least euclidean distance and take.
		// If there is only one label it will put that as the final label. (No specific code for that.)
		for(int i=0; i<minKLabels.length; i++){
			if(labelsWithHighestCount.contains(minKLabels[i])){
				result = minKLabels[i]; 
				break;
			}
		}
		return result;
	}
}
