package project;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;


import matrix.Matrix;
import matrix.Features;
import matrix.SegColScan;
import matrix.ZonedDistanceFeatureExtractor;


public class BatchCode_Feature_Extraction{

	int n_features_methods = 5;
	int n_images_class = 277;
	int n_class = 10;
	String ext = ".png";
	String []feature_name = new String[n_features_methods];

	public BatchCode_Feature_Extraction() {		
		feature_name[0]="gradientVector";
		feature_name[1]="DistancesFeatureExtraction";
		feature_name[2]="zoned_vetor";
		feature_name[3]="CrossingFeatureExtraction";		
		feature_name[4]="ProjectionHistograms";	
	}

	public void run() throws Exception{		
		try{
			FileWriter []out = new FileWriter[n_features_methods];
			//String file = null;
			Matrix m;
			SegColScan s;
			Matrix segMatrix;
			Features []features = new Features[n_features_methods];
			ArrayList<String>[] labels = new ArrayList[n_features_methods];
			File writer;
			int segcount;
			
			for(int i=0;i<features.length;i++){
				writer = new File("src\\TestDataFeature"+i+".arff");
				out[i] = new FileWriter(writer);
				features[i] = new Features();
				labels[i] = new ArrayList<String>();
			}			

			int hnormalize = 30;//Height of the normalized matrices
			int wnormalize = 30;//Width of the normalized matrices	

			for(int k=0;k<n_features_methods;k++){

				System.out.println("Running features_method on test data: "+feature_name[k]);


				File folder = new File("src\\TestingData");
				if(folder.isDirectory()){
					for (File fileEntry : folder.listFiles()) {

						if(fileEntry.isFile()){

							Image image = Toolkit.getDefaultToolkit().createImage(fileEntry.getPath());
							m = new Matrix(image);
							Matrix M1 = m.clone();
							M1.invert(255);
							M1.smoothing();
							M1.toBinaryImage(M1.threshold());
							M1.thinning();
							M1.filling();
							
							String fileName= fileEntry.getName();
							String fileLabel = fileName.substring(fileName.lastIndexOf("_")+1, fileName.lastIndexOf("."));
							
							segcount = 0;
							//segment the image
							s = new SegColScan(M1.getArray());
							s.segmentImage();
							s.normalizeSegments();
							//s.callZonedExtractorOnEachSegment();
							
							for(int[][] arr : s.finalSegmentList){
								
								segMatrix = new Matrix(arr, arr.length, arr[0].length);
								segMatrix.normalize(hnormalize, wnormalize);
								
								switch(k){
									case 0: features[k].add(segMatrix.gradientVector());
									labels[k].add(fileLabel.charAt(segcount++) +"");
									break;
									case 1:	features[k].add(segMatrix.DistancesFeatureExtraction());
									labels[k].add(fileLabel.charAt(segcount++) +"");
									break;
									case 2: 
									ZonedDistanceFeatureExtractor z = new ZonedDistanceFeatureExtractor(segMatrix.getArray());
									z.start();
									float[] zoned_vetor = new float[z.features.length];	features[k].add(zoned_vetor);
									for(int c =0; c<z.features.length;c++)
										zoned_vetor[c] = z.features[c];
									labels[k].add(fileLabel.charAt(segcount++) +"");
									break;
									case 3: features[k].add(segMatrix.CrossingFeatureExtraction());	
									labels[k].add(fileLabel.charAt(segcount++) +"");
									break;
									case 4: features[k].add(segMatrix.ProjectionHistograms());	
									labels[k].add(fileLabel.charAt(segcount++) +"");
									break;
									default: 
										throw new Exception("Feature extraction method invalid");									
								}
							}
							
						}
					}

				}else{
					throw new Exception("Invalid folder path.");
				}

				
				features[k].normalization();
				out[k].write("@RELATION digits\n");
				for (int i=0; i < features[k].getColumns();i++)
					out[k].write("@Attribute "+ i + " numeric\n");

				out[k].write("@Attribute class {label0,label1,label2,label3,label4,label5,label6,label7,label8,label9}\n");
				out[k].write("@DATA\n");

				for (int i=0;i<features[k].getN_item();i++){
					for(int l =0;l<features[k].getColumns();l++)
					{

						out[k].write(features[k].getElement(i, l)+",");

					}
					out[k].write("label" + labels[k].get(i));
					out[k].write("\n");	
				}


				out[k].close();
			}
		}

		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static int[][] trimWhiteRows(int[][] seg){
		
		boolean blackflag = false;
		
	
			if(seg.length < 1 )
				return seg;
			if(seg[0].length < 1)
				return seg;
			
			int[][] eToESeg = new int[seg.length][seg[0].length];
			int newSegCurrentRow = 0;
			
			for(int i = 0; i < seg.length; i++ ){
				
				blackflag = false;
				for(int j = 0; j < seg[0].length; j++){
					
					if(seg[i][j] == 1){
						blackflag = true;
						break;
					}
				}
				
				if(blackflag){
					eToESeg[newSegCurrentRow] = seg[i];
					newSegCurrentRow++;
				}
			}
		
			// we need to make a new array with extra columns deleted
			int[][] realSegmentarray = new int[newSegCurrentRow][seg[0].length];
			for(int x = 0; x < newSegCurrentRow; x++){
				
				realSegmentarray[x] = eToESeg[x];
			}
			
			return realSegmentarray;
	}
	
	
	public static int[][] trimWhiteColumns(int[][] seg){

		boolean blackFlag = false;
		int noOfRows = seg.length;
		int noOfColumns = seg[0].length;
		ArrayList<Integer> emptyCols = new ArrayList<>();
		int[][] segment = null;
		
		// find a column with no foreground pixel.
		for(int i = 0; i<noOfColumns; i++){			
			blackFlag = false;
			for(int j = 0; j<noOfRows; j++ ){
				
				if(seg[j][i]==1){
						blackFlag = true;
						break;
				}
			}
			if(!blackFlag){
				emptyCols.add(i);
			}
		}
		
		// make and save the segment to the arrayList
		int noOfColmInsegment = noOfColumns - emptyCols.size();
		
		if(noOfColmInsegment > 1){
			segment = new int[noOfRows][noOfColmInsegment];
			
			for(int rownum = 0; rownum < noOfRows; rownum++){
				
				int currentCol = 0;
				for(int colnum = 0; colnum < noOfColumns; colnum++){
					
					if(!emptyCols.contains(colnum)){
						segment[rownum][currentCol++] = seg[rownum][colnum];
					}
				}
			}
		}
		return segment;
	}
	

}
