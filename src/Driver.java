import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.NominalPrediction;
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

 
public class Driver {
 
    public static void main(String[] args) throws Exception{
         
         // Create an training set from file
         DataSource source = new DataSource("src\\Feature.arff");
         Instances data = source.getDataSet();
          
         // Set class index
         if (data.classIndex() == -1)
        	 data.setClassIndex(data.numAttributes() - 1);

     	 Random r = new Random();
         Instances[][] split = crossValidationSplit(data, 10, r);

         // Separate split into training and testing arrays
         Instances[] trainingSplits = split[0];
         Instances[] testingSplits  = split[1];

         // Choose a set of classifiers
        Classifier[] models = {     // new NaiveBayes(),
        		 					// new J48(),
                                    // new PART(),
                                    // new DecisionTable(),
                                    // new OneR(),
                                    // new DecisionStump(),
                                     new SMO()

        							};
		
         
         // Run for each classifier model
 
         
         for(int j = 0; j < models.length; j++) {

             // Collect every group of predictions for current model in a FastVector
             FastVector predictions = new FastVector();
             //Evaluation validation;
             //validation = simpleClassify(models[j], data, data);
             // For each training-testing split pair, train and test the classifier
             for(int i = 0; i < trainingSplits.length; i++) {
                 Evaluation validation = simpleClassify(models[j], trainingSplits[i], testingSplits[i]);
            	 predictions.appendElements(validation.predictions());

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
                 // Print current classifier's name and accuracy in a complicated, but nice-looking way.
                 System.out.println(models[j].getClass().getSimpleName() + ": " + String.format("%.2f%%", calculateAccuracy(predictions)) + "\n=====================");

             }
             // Calculate overall accuracy of current classifier on all splits
             double accuracy = calculateAccuracy(predictions);
             // Print current classifier's name and accuracy in a complicated, but nice-looking way.
             System.out.println(models[j].getClass().getSimpleName() + ": " + String.format("%.2f%%", accuracy) + "\n=====================");
             

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