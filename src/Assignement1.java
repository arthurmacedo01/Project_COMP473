import java.awt.Image;
import java.awt.Toolkit;

import matrix.Matrix;

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
		String file = "src\\Images\\5\\5-09.jpg";//if you have an error like width =-1 your file was not found
		Image image = Toolkit.getDefaultToolkit().createImage(file);
		Matrix m = new Matrix(image);
		m = m.invert();
		
		//Normalization
		int hnormalize = 30;//Height of the normalized matrices
		int wnormalize = 30;//Width of the normalized matrices
		Matrix M1n = m.normalize(hnormalize, wnormalize);//normalization of M1
		System.out.println(M1n);
		
		M1n = M1n.invert();
		M1n.Skeletonize();
		
		System.out.println(M1n);
	}
	
}
