package project;


import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.functions.VotedPerceptron;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;


import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import project.Feature_Extraction;
 
public class ClassificationBatchCode {
 
    public static void main(String[] args) throws Exception{
         
    	Feature_Extraction oneDigit_feature_Extraction = new Feature_Extraction(); 	
    	BatchCode_Feature_Extraction batch_feature_Extraction = new BatchCode_Feature_Extraction();
    	
    	
    	//oneDigit_feature_Extraction.run();
    	//batch_feature_Extraction.run();
    	
         // Create an training set from file
         DataSource trainingSource;
         Instances trainingData;
         
         DataSource testingSource;
         Instances testingData;
         
         Random r;
         long startTime;
         long endTime;
         
        for (int k=0;k<oneDigit_feature_Extraction.n_features_methods;k++){
        	 trainingSource = new DataSource("src\\Feature"+k+".arff");	        
        	 trainingData = trainingSource.getDataSet();
        
        	 testingSource = new DataSource("src\\TestDataFeature"+k+".arff");	        
        	 testingData = testingSource.getDataSet();
        
        	 
        	 System.out.println("===========================\nFeature Extration "+oneDigit_feature_Extraction.feature_name[k]+"\n---------------------------\n");
	          
	         // Set class index
	         if (trainingData.classIndex() == -1)
	        	 trainingData.setClassIndex(trainingData.numAttributes() - 1);

	         if (testingData.classIndex() == -1)
	        	 testingData.setClassIndex(testingData.numAttributes() - 1);
	         
	     	 r = new Random();
	         Instances[][] split = crossValidationSplit(trainingData, 10, r);
	
	         // Separate split into training and testing arrays
	         Instances[] trainingSplits = split[0];
	         Instances[] testingSplits  = split[1];
	         
	
	         // Choose a set of classifiers
	        Classifier[] models = {     new NaiveBayes(),
	        		 					new J48(),
	                                    new PART(),
	                                    //new DecisionTable(),
	                                    new OneR(),
	                                    new DecisionStump(),
	                                    new SMO()
	        							};
			
	         
	        	        
             // Run for each classifier model
	         for(int j = 0; j < models.length; j++) {
	
	             // Collect every group of predictions for current model in a FastVector
	             FastVector predictions = new FastVector();
	             
	             double maxAccuracy = 0;
	             int maxI = 0;
	             double accuracy = 0;

	             //Evaluation validation;
	             // For each training-testing split pair, train and test the classifier	       
	             startTime=System.currentTimeMillis();  
	             for(int i = 0; i < trainingSplits.length; i++) {
	            	 Evaluation validation = simpleClassify(models[j], trainingSplits[i], testingSplits[i]);
	            	 predictions=validation.predictions();	            	 
	            	 
	            	 accuracy = calculateAccuracy(predictions);
	            	 
	            	 if(accuracy>maxAccuracy){
	            		 maxAccuracy=accuracy;
	            		 maxI=i;
	            	 }
	            	 
	            	  // Uncomment to see the summary for each training-testing pair.
	                 //System.out.println(models[j].toString());
	            	 //System.out.println(validation.toSummaryString());
	                 // Get the confusion matrix
	                 /*double[][] cmMatrix = validation.confusionMatrix();
	                 for(int row_i=0; row_i<cmMatrix.length; row_i++){
	                     for(int col_i=0; col_i<cmMatrix.length; col_i++){
	                         System.out.print(cmMatrix[row_i][col_i]);
	                         System.out.print("|");
	                     }
	                     System.out.println();
	                 }
	                 */
	             }	             
	             endTime=System.currentTimeMillis();
	             System.out.println(models[j].getClass().getSimpleName()+":");
	             System.out.println("\tTime for building model: "+((endTime-startTime)/1000)+" seconds.");
	             
	             startTime=System.currentTimeMillis();             	             
            	 Evaluation testing = simpleClassify(models[j], trainingSplits[maxI],testingData);
	             endTime=System.currentTimeMillis();	
	             
            	 predictions = testing.predictions();
            	 accuracy = calculateAccuracy(predictions);
            	 
	             // Print current classifier's name and accuracy in a complicated, but nice-looking way.
	             System.out.println("\tTesting time: "+((endTime-startTime)/1000)+" seconds.");	
	             System.out.println("\tAccuracy: " + String.format("%.2f%%", accuracy));	
	             
	         }
	         
	         	
	         
        }       
    }
    public static Instances[][] crossValidationSplit(Instances data, int numberOfFolds, Random r){
    	Instances[][] split = new Instances[2][numberOfFolds];
    	for(int i = 0; i < numberOfFolds; i++){
    		split[0][i] = data.trainCV(numberOfFolds, i, r);
    		split[1][i] = data.testCV(numberOfFolds, i);
    	}
    	
    	return split;
    }
    public static Evaluation simpleClassify(Classifier model, Instances
    		trainingSet, Instances testingSet) throws Exception {
    		        Evaluation validation = new Evaluation(trainingSet);

    		        model.buildClassifier(trainingSet);
    		        validation.evaluateModel(model, testingSet);

    		        return validation;
    }
    public static double calculateAccuracy(FastVector predictions) {
        double correct = 0;

        for (int i = 0; i < predictions.size(); i++) {
            NominalPrediction np = (NominalPrediction)predictions.elementAt(i);
            if (np.predicted() == np.actual()) {
                correct++;
            }
        }

        return 100 * correct / predictions.size();
    }
}


