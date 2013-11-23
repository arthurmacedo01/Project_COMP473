import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileWriter;


import matrix.Matrix;
import matrix.Features;

// Assignement 1 COMP473
//Author : Yoann ROBIN
//IDStudent : 7118538


//Main class

public class Assignement1 {

	public static void main(String[] args) throws Exception{
		
		/*//Original patterns
		Matrix M1 = new Matrix("..\\Pattern1.txt");//loading of the first pattern from its txt file
		Matrix M2 = new Matrix("..\\Pattern2.txt");//loading of the second pattern from its txt file
		Matrix M3 = new Matrix("..\\Pattern3.txt");//loading of the third pattern from its txt file
		M1 = M1.invert();
		M2 = M2.invert();
		M3 = M3.invert();
		
		System.out.println("Pattern 1 (original) :");//Display of M1
		System.out.println(M1);
		
		System.out.println("Pattern 2 (original) :");//Display of M2
		System.out.println(M2);

		System.out.println("Pattern 3 (original) :");//Display of M3
		System.out.println(M3);
		
		//Normalization
		int hnormalize = 30;//Height of the normalized matrices
		int wnormalize = 30;//Width of the normalized matrices
		Matrix M1n = M1.normalize(hnormalize, wnormalize);//normalization of M1
		Matrix M2n = M2.normalize(hnormalize, wnormalize);//normalization of M2
		Matrix M3n = M3.normalize(hnormalize, wnormalize);//normalization of M3
		
		System.out.println("Pattern 1 (normalized) :");//Display of M1 normalized
		System.out.println(M1n);
		
		System.out.println("Pattern 2 (normalized) :");//Display of M2 normalized
		System.out.println(M2n);
		
		System.out.println("Pattern 3 (normalized) :");//Display of M3 normalized
		System.out.println(M3n);
		
		
		//Slant Correction
		Matrix M1ns = M1n.correctSlant();//slant correction of M1n
		Matrix M2ns = M2n.correctSlant();//slant correction of M2n
		Matrix M3ns = M3n.correctSlant();//slant correction of M3n
		
		System.out.println("Pattern 1 (normalized and with slant correction) :");//Display of M1ns
		System.out.println(M1ns);
		
		System.out.println("Pattern 2 (normalized and with slant correction) :");//Display of M2ns
		System.out.println(M2ns);
		
		System.out.println("Pattern 3 (normalized and with slant correction) :");//Display of M3ns
		System.out.println(M3ns);
		
		
		//Contour
		Matrix M1nsc = M1ns.getContour();//contour of M1ns
		Matrix M2nsc = M2ns.getContour();//contour of M2ns
		Matrix M3nsc = M3ns.getContour();//contour of M3ns
		
		System.out.println("Pattern 1 (normalization, slant correction and contour) :");//Display of M1nsc
		System.out.println(M1nsc);
		
		System.out.println("Pattern 2 (normalization, slant correction and contour) :");//Display of M2nsc
		System.out.println(M2nsc);
		
		System.out.println("Pattern 3 (normalization, slant correction and contour) :");//Display of M3nsc
		System.out.println(M3nsc);*/
			
try{
					
			File writer = new File("src\\Feature.arff");
			FileWriter out = new FileWriter(writer);
			String file = null;
			Matrix m;
			Features f = new Features();

			
			int hnormalize = 30;//Height of the normalized matrices
			int wnormalize = 30;//Width of the normalized matrices
			for(int i =0;i<=9;i++)
			{
				for(int j=1;j<=100;j++)
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
					
					//System.out.println(M1n);
					
					f.add(M1n.CrossingFeatureExtraction());
					
				}
			}
			
			f.normalization();
			
			for (int i=0;i<f.getN_item();i++){
				for(int k =0;k<f.getColumns();k++)
				{
					
					out.write(f.getElement(i, k)+",");
					
				}
				out.write("label"+((int)i/100));
				out.write("\n");	
			}
			
			
			out.close();
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}

		
	
	}
	
}
