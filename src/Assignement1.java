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

public class Assignement1 {

	public static void main(String[] args) throws Exception{
		
		/*//Original patterns
		Matrix M1 = new Matrix("..\\Pattern1.txt");//loading of the first pattern from its txt file
		M3 = M3.invert();
		int hnormalize = 30;//Height of the normalized matrices
		int wnormalize = 30;//Width of the normalized matrices
		Matrix M1n = M1.normalize(hnormalize, wnormalize);//normalization of M1
		Matrix M1ns = M1n.correctSlant();//slant correction of M1n
		Matrix M1nsc = M1ns.getContour();//contour of M1ns
		*/	
try{
					
			File writer = new File("src\\Feature.arff");
			FileWriter out = new FileWriter(writer);
			String file = null;
			Matrix m;
			Features f = new Features();

			
			int hnormalize = 30;//Height of the normalized matrices
			int wnormalize = 30;//Width of the normalized matrices
			
			for(int j=1;j<=100;j++)
			{
				for(int i =0;i<=9;i++)
				{
					if(j>=0 && j <=9)
					{
						file = "src\\Images\\"+i+"\\"+i+"-"+"0"+j+".jpg";
					}
					else
						file = "src\\Images\\"+i+"\\"+i+"-"+j+".jpg";
					
					Image image = Toolkit.getDefaultToolkit().createImage(file);
					m = new Matrix(image);
					m = m.invert();
					System.out.println(file);
					//Normalization
					
					Matrix M1n = m.normalize(hnormalize, wnormalize);//normalization of M1
					
					
					ZonedDistanceFeatureExtractor z = new ZonedDistanceFeatureExtractor(M1n.getArray());
					z.start();
					float[] blah = new float[z.features.length];
					
					for(int c =0;c<z.features.length;c++)
						blah[c] = z.features[c];
					
					//f.add(zchd(blah,M1n.CrossingFeatureExtraction(),M1n.ProjectionHistograms(),M1n.DistancesFeatureExtraction()));
					f.add(M1n.gradientVector());
				}
			}
			
			f.normalization();
			
			out.write("@RELATION digits\n");
			
			for (int i=0; i < f.getColumns();i++)
				out.write("@Attribute "+ i + " numeric\n");
			
			out.write("@Attribute class {label0,label1,label2,label3,label4,label5,label6,label7,label8,label9}\n");
			out.write("@DATA\n");
					
			for (int i=0;i<f.getN_item();i++){
				for(int k =0;k<f.getColumns();k++)
				{
					
					out.write(f.getElement(i, k)+",");
					
				}
				out.write("label"+((int)(i%10)));
				out.write("\n");	
			}
			
			
			out.close();
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
