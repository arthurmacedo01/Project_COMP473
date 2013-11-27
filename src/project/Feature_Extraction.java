package project;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileWriter;


import matrix.Matrix;
import matrix.Features;
import matrix.ZonedDistanceFeatureExtractor;


// Assignement 1 COMP473
//Author : Yoann ROBIN
//IDStudent : 7118538


//Main class

public class Feature_Extraction{

	int n_features_methods = 5;
	int n_images_class = 277;
	int n_class = 10;
	String ext = ".png";
	String []feature_name = new String[n_features_methods];
	
	public Feature_Extraction() {		
		feature_name[0]="gradientVector";
		feature_name[1]="DistancesFeatureExtraction";
		feature_name[2]="zoned_vetor";
		feature_name[3]="CrossingFeatureExtraction";		
		feature_name[4]="ProjectionHistograms";	
	}
	
	public void run() throws Exception{		
		try{
			FileWriter []out = new FileWriter[n_features_methods];
			String file = null;
			Matrix m;
			Features []features = new Features[n_features_methods];
			File writer;
			for(int i=0;i<features.length;i++){
			     writer = new File("src\\Feature"+i+".arff");
				 out[i] = new FileWriter(writer);
				 features[i] = new Features();
			}			
			int hnormalize = 30;//Height of the normalized matrices
			int wnormalize = 30;//Width of the normalized matrices	
			
			for(int k=0;k<n_features_methods;k++){
				System.out.println("Running features_method: "+feature_name[k]);
				for(int j=0;j<n_images_class;j++)
				{
					for(int i =0;i<n_class;i++)
					{
						if(j>=0 && j <n_class)
						{
							file = "src\\Images\\"+i+"\\"+i+"-"+j+ext;
						}
						else
							file = "src\\Images\\"+i+"\\"+i+"-"+j+ext;
						//System.out.println(file);
						Image image = Toolkit.getDefaultToolkit().createImage(file);
						m = new Matrix(image);
						Matrix M1 = m.clone();
						M1.invert(255);
						M1.smoothing();
						M1.toBinaryImage(M1.threshold());
						M1.thinning();
						M1.filling();
						//M1.correctSlant();
						M1.normalize(hnormalize, wnormalize);
						//System.out.println(M1);
						ZonedDistanceFeatureExtractor z = new ZonedDistanceFeatureExtractor(M1.getArray());
						z.start();
						float[] zoned_vetor = new float[z.features.length];
						
						for(int c =0;c<z.features.length;c++)
							zoned_vetor[c] = z.features[c];
						
						//features.add(zchd(blah,M1.CrossingFeatureExtraction(),M1.ProjectionHistograms(),M1.DistancesFeatureExtraction()));
						switch(k){
							case 0: features[k].add(M1.gradientVector());
									break;
							case 1:	features[k].add(M1.DistancesFeatureExtraction());
									break;
							case 2: features[k].add(zoned_vetor);
									break;
							case 3: features[k].add(M1.CrossingFeatureExtraction());		
									break;
							case 4: features[k].add(M1.ProjectionHistograms());	
									break;
							default: 
									throw new Exception("Feature extraction method invalid");									
						}
					}
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
					out[k].write("label"+((int)(i%n_class)));
					out[k].write("\n");	
				}
				
				
				out[k].close();
			}
	}

			catch(Exception ex)
			{
				System.out.println(ex.getMessage());
			}
		
		
	
}
	public static float[] zchd(float[]z, float[]c, float[]h , float []d){
		
		float []output = new float[z.length+c.length+h.length+d.length];
		int i=0,j=0,k=0,l=0;
		
		for (i=0;i<z.length;i++){
			output[i] = z[i];
		}
		for (j=0;j<c.length;j++){
			output[i+j] = c[j];
		}
		for (k=0;k<h.length;k++){
			output[i+j+k] = h[k];		
		}
		for (l=0;l<d.length;l++){
			output[i+j+k+l] = d[l];		
		}
		
		return output;
	}
}
