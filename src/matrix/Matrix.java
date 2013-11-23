package matrix;


import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.ImageIcon;

//Author : Yoann ROBIN
//IDStudent : 7118538


public class Matrix {

	private int[][] a;
	private int rows=0;
	private int columns=0;
	
	//return the number of rows in the matrix
	public int getRows(){
		return this.rows;
	}
	
	//return the number of columns in the matrix
	public int getColumns(){
		return this.columns;
	}
	
	//initialize a matrix from a 2D array and its number of rows and columns
	public Matrix(int[][] in, int rows, int columns){
		a=in;
		this.rows = rows;
		this.columns = columns;
	}
	
	
	//initialize a matrix from an image file
	public Matrix(Image image) throws Exception{
		/*Image image = Toolkit.getDefaultToolkit().createImage(file);*/ //Use that line to initalize an object Image
		BufferedImage i = toBufferedImage(image);
		columns = i.getWidth();
		rows = i.getHeight();
		int nbElements = rows*columns;
		int s = 0;
		a = new int[rows][columns];
		for (int j = 0; j < rows; j++) {
		    for (int k = 0; k < columns; k++) {
		    	int rgb = i.getRGB(k, j);
		    	int red =   (rgb >> 16) & 0xFF;
		    	int green = (rgb >>  8) & 0xFF;
		    	int blue =  (rgb      ) & 0xFF;
		        a[j][k] = (int) (0.2126*red + 0.7152*green + 0.0722*blue);
		        s += a[j][k];
		    }
		}
		this.toBinaryImage(s/nbElements);
	}
	
	
	private static BufferedImage toBufferedImage(Image image) {
	    if (image instanceof BufferedImage) {
	        // Return image unchanged if it is already a BufferedImage.
	        return (BufferedImage) image;
	    }

	    // Ensure image is loaded.
	    image = new ImageIcon(image).getImage();

	    BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null),  BufferedImage.TYPE_INT_ARGB);
	    Graphics g = bufferedImage.createGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.dispose();

	    return bufferedImage;
	}
	
	
	private void toBinaryImage(int threshold){
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if (a[i][j]>threshold){
					a[i][j]=1;
				}
				else{
					a[i][j]=0;
				}
			}
		}
	}
	
	
	//initialize a matrix from a file
	public Matrix(String file) throws Exception{
		Scanner input;
		input = new Scanner(new File(file));
		// pre-read in the number of rows/columns
		
		int n=0;
		input = new Scanner(new File(file));
		while(input.hasNextInt()){
			++n;
			input.nextInt();
		}
		input = new Scanner(new File(file));
		while(input.hasNextLine()){
			input.nextLine();
			++rows;
		}
		columns = n/rows;
		
		this.a = new int[rows][columns];

		input.close();

		// read in the data
		input = new Scanner(new File(file));
		for(int i = 0; i < rows; ++i)
		{
		    for(int j = 0; j < columns; ++j)
		    {
		        if(input.hasNextInt())
		        {
		            this.a[i][j] = input.nextInt();
		        }
		    }
		}
		input.close();
	}
	
	
	//return the moment mpq
	public double moment(int p, int q){
		double stack = 0;
		for(int i = 0; i < rows; ++i)
		{
		    for(int j = 0; j < columns; ++j)
		    {
		        stack = stack + (double) Math.pow(i, p) * (double) Math.pow(j, q) * a[i][j];
		    }
		}
		return stack;
	}
	
	//return the central moment upq
	public double centralMoment(int p, int q){
		double stack = 0;
		double m10 = this.moment(1, 0);
		double m01 = this.moment(0, 1);
		double m00 = this.moment(0, 0);
		
		double xc = m10/m00;
		double yc = m01/m00;
		for(int i = 0; i < rows; ++i)
		{
		    for(int j = 0; j < columns; ++j)
		    {
		        stack = stack + (double) Math.pow(i - xc, p) * (double) Math.pow(j - yc, q) * a[i][j];
		    }
		}
		return stack;
	}
	
	
	//return the slant angle of the matrix
	public double slantAngle(){
		return(Math.toDegrees(Math.atan(-this.centralMoment(1, 1)/this.centralMoment(0, 2))));
	}
	
	public Matrix invert(){
		
		int[][] Mfin = new int[rows][columns];
		for(int i = 0; i < rows; ++i)
		{
		    for(int j = 0; j < columns; ++j)
		    {
		    		Mfin[i][j] = 1-a[i][j] ;
		    }
		}

		Matrix Mresult = new Matrix(Mfin, rows, columns);
		return Mresult;
}
	
	
	//return the matrix with slant correction
	public Matrix correctSlant(){
			double m01 = this.moment(0, 1);
			double m00 = this.moment(0, 0);
			
			double yc = m01/m00;
			int[][] Mfin = new int[rows][columns];
			for(int i = 0; i < rows; ++i)
			{
			    for(int j = 0; j < columns; ++j)
			    {
			        Mfin[i][j] = 0;
			    }
			}

			for(int i = 0; i < rows; ++i)
			{
			    for(int j = 0; j < columns; ++j)
			    {
			    	int yp = i;
			    	int xp = (int) (j + (i - yc)*(-centralMoment(1, 1)/centralMoment(0, 2)));
			    	
			    	try{
			    		Mfin[yp][xp] = a[i][j];
			    	}
			    	catch (Exception e){}
			    }
			}
			Matrix Mresult = new Matrix(Mfin, rows, columns);
			return Mresult;
	}
	
	
	//return the height of the lowest pixel!=1
	private int getSouth(){
		int result = 0;
		for(int i = 0; i < rows; ++i)
		{
		    for(int j = 0; j < columns; ++j)
		    {
		    	if (this.a[i][j] == 0){
		    		result = i;
		    	}
		    }
		}
		return result;
	}
	
	//return the height of the highest pixel!=1
	private int getNorth(){
		int result = 0;
		for(int i = rows-1; i >= 0; --i)
		{
		    for(int j = columns-1; j >= 0; --j)
		    {
		    	if (this.a[i][j] == 0){
		    		result = i;
		    	}
		    }
		}
		return result;
	}
	
	//return the column of the first pixel!=1 from right
	private int getEast(){
		int result = 0;
		for(int j = 0; j < columns; ++j)
		{
			for(int i = 0; i < rows; ++i)
			{
		    	if (this.a[i][j] == 0){
		    		result = j;
		    	}
		    }
		}
		return result;
	}
	
	//return the column of the first pixel!=1 from left
	private int getWest(){
		int result = 0; 
		for(int j = columns-1; j >= 0; --j)
		{
			for(int i = rows-1; i >= 0; --i)
			{
		    	if (this.a[i][j] == 0){
		    		result = j;
		    	}
		    }
		}
		return result;
	}
	
	//return a normalized matrix of size hnew * wnew
	public Matrix normalize(int hnew, int wnew){
		int h1 = getNorth();
		int h2 = getSouth();
		int w1 = getWest();
		int w2 = getEast();
		int hold = h2 - h1;
		int wold = w2 - w1;
		
		double alpha = (double)wnew/(double)wold;
		double beta = (double)hnew/(double)hold;
		
		int[][] Mfin = new int[hnew][wnew];
		for(int i = 0; i < hnew; ++i)
		{
		    for(int j = 0; j < wnew; ++j)
		    {
		        Mfin[i][j] = 1;
		    }
		}
		
		
		for(int i = 0; i < hnew; ++i)
		{
		    for(int j = 0; j < wnew; ++j)
		    {
		    	try{
		    		Mfin[i][j] = a[(int)(i/beta)][(int)(j/alpha)];
		    	}
		    	catch (Exception e){}
		    }
		}
		Matrix Mresult = new Matrix(Mfin,hnew, wnew);
		return Mresult;
	}
	
	
	//return a matrix containing the contours of the original matrix
	public Matrix getContour(){
			
			int[][] Mfin = new int[rows][columns];
			for(int i = 0; i < rows; ++i)
			{
			    for(int j = 0; j < columns; ++j)
			    {
			    	if((i==0)|(j==0)|(i==rows-1)|(j==columns-1)){
			    		Mfin[i][j]=1-a[i][j];
			    	}
			    	else{
			    		Mfin[i][j] = 1;
			    	}
			    }
			}
			
			int previous=1;
			for(int i = 0; i < rows; ++i)
			{
			    for(int j = 0; j < columns; ++j)
			    {
			    	if (a[i][j]!=previous){
			    		if (a[i][j]==0){
			    			Mfin[i][j]=0;
			    		}
			    		else{
			    			Mfin[i][j-1]=0;
			    		}
			    	}
			    	previous = a[i][j];
			    }
			    previous = 1;
			}
			
			
			previous=1;
			for(int j = 0; j < columns; ++j)
			{	
				for(int i = 0; i < rows; ++i)
			    {
					if (a[i][j]!=previous){
			    		if (a[i][j]==0){
			    			Mfin[i][j]=0;
			    		}
			    		else{
			    			Mfin[i-1][j]=0;
			    		}
			    	}
					previous = a[i][j];	
			    }
				previous = 1;
			}
			
			
			Matrix Mresult = new Matrix(Mfin,rows, columns);
			return Mresult.invert();
		}
	
	
	//return a String to be displayed in the console
	//(you can copy-paste from console to excel to have a better view of the result
	public String toString(){
		String stack = "";
		for(int i = 0; i < rows; ++i)
		{
		    for(int j = 0; j < columns; ++j)
		    {
		    	if (j!=0){
		    			stack = stack + "\t" + this.a[i][j];
		    	}
		    	else{
		    		stack = stack + this.a[i][j];
		    	}
		    }
		    stack = stack + "\n";
		}
		return stack;
	}
	
		//return a matrix after applying smoothing (gaussian filter) on GREYSCALE images
	public Matrix smoothing(){
	
		int[][] Mfin = new int[rows][columns];
		int mask,mask_factor;
		int[] mask_weight= {1,	2,	1,
							2,	4,	2,
							1,	2,	1};
	
		mask_factor=(mask_weight[0]+mask_weight[1]+mask_weight[2]+mask_weight[3]+mask_weight[4]+mask_weight[5]+mask_weight[6]+mask_weight[7]+mask_weight[8]);
		
		for(int i = 1; i < rows-1; ++i)
		{
		    for(int j = 1; j < columns-1; ++j)
		    {
		    		mask = a[i-1][j-1]*mask_weight[0];
		    		mask += a[i-1][j]*mask_weight[1];
		    		mask += a[i-1][j+1]*mask_weight[2];
		    		mask += a[i][j-1]*mask_weight[3];
		    		mask += a[i][j]*mask_weight[4];
		    		mask += a[i][j+1]*mask_weight[5];
		    		mask += a[i+1][j-1]*mask_weight[6];
		    		mask += a[i+1][j]*mask_weight[7];
		    		mask += a[i+1][j+1]*mask_weight[8];
		    		
		    		Mfin[i][j] = mask=mask/mask_factor;
		    }
		}
		
		for(int j = 1; j < columns-1; ++j)
		{
			Mfin[0][j] = Mfin[1][j] ;
			Mfin[rows-1][j] = Mfin[rows-2][j];

		}
		
		for(int i = 0; i < rows; ++i)
		{
			Mfin[i][0] = Mfin[i][1] ;
			Mfin[i][columns-1] = Mfin[i][columns-2];

		}
		
		Matrix Mresult = new Matrix(Mfin, rows, columns);
	
		return Mresult;				
	}

	//return a matrix after applying filling on BINARY images
	public Matrix filling(){
		
		int[][] Mfin = new int[rows][columns];
		for(int i = 1; i < rows-1; ++i)
		{
		    for(int j = 1; j < columns-1; ++j)
		    {
		    		Mfin[i][j] = a[i][j]+a[i-1][j-1]*a[i+1][j+1]+a[i-1][j]*a[i+1][j]+a[i-1][j+1]*a[i+1][j-1]+a[i][j-1]*a[i][j+1];
		    		if(Mfin[i][j]>0)
		    			Mfin[i][j]=1;
		    }
		}

		for(int j = 1; j < columns-1; ++j)
		{
			Mfin[0][j] = Mfin[1][j] ;
			Mfin[rows-1][j] = Mfin[rows-2][j];

		}
		
		for(int i = 0; i < rows; ++i)
		{
			Mfin[i][0] = Mfin[i][1] ;
			Mfin[i][columns-1] = Mfin[i][columns-2];

		}		
		
		Matrix Mresult = new Matrix(Mfin, rows, columns);
		return Mresult;
	}

	//return a matrix after applying thinning on BINARY images	
	public Matrix thinning(){
		
		int[][] Mfin = new int[rows][columns];
		for(int i = 1; i < rows-1; ++i)
		{
		    for(int j = 1; j < columns-1; ++j)
		    {
		    		Mfin[i][j] = a[i][j]*(a[i-1][j-1]*a[i-1][j]*a[i-1][j-1]+a[i-1][j]*a[i-1][j+1]*a[i][j+1]+a[i][j+1]*a[i+1][j]*a[i+1][j+1]+a[i][j-1]*a[i+1][j-1]*a[i+1][j]);		
		    		if(Mfin[i][j]>0)
		    			Mfin[i][j]=1;
		    }
		}

		for(int j = 1; j < columns-1; ++j)
		{
			Mfin[0][j] = Mfin[1][j] ;
			Mfin[rows-1][j] = Mfin[rows-2][j];

		}
		
		for(int i = 0; i < rows; ++i)
		{
			Mfin[i][0] = Mfin[i][1] ;
			Mfin[i][columns-1] = Mfin[i][columns-2];

		}		
		
		Matrix Mresult = new Matrix(Mfin, rows, columns);
		return Mresult;
	}
	
	public void Skeletonize()
    {
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                if (i == 0 || i == (rows) - 1 || j == 0 || j == (columns) - 1 ||j==(columns)-2)
                {
                    a[i][j] = 1; //Ignoring Border Pixels .Assumption that border pixels doesn't have image 
                }
                else
                {
                    
                    int n_zero = 0;
                    {
                        if ( j != columns - 1)
                        {
                        	n_zero = GetNumberOfZeroTo1Transactions(i, j);
                        }

                    }
                    int n_black = 0;
                    {
                        if ( j != columns - 1)
                        {
                        	n_black = GetNumberOfSurroundingBlackPixels(i, j);
                        }
                    }

                    if (a[i][j] == 0 && 2 <= n_black && n_black <= 6 && n_zero == 1
                        && (a[i - 1][j] * a[i][j + 1] * a[i + 1][j] == 0)
                        && (a[i][j + 1] * a[i + 1][j] * a[i][j - 1] == 0))
                    {
                        a[i][j] = 1;
                    }

                }
            }
        }

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < columns; j++)
            {
                if (i == 0 || i == (rows) - 1 || j == 0 || j == (columns) - 1 || j == (columns) - 2)
                {
                    a[i][j] = 1; //Ignoring Border Pixels .Assumption that border pixels doesnt have image 
                }
                else
                {

                    int n_zero = 0;
                    {
                        if ( j != columns - 1)
                        {
                        	n_zero = GetNumberOfZeroTo1Transactions(i, j);
                        }

                    }
                    int n_black = 0;
                    {
                        if ( j != columns - 1)
                        {
                        	n_black = GetNumberOfSurroundingBlackPixels(i, j);
                        }
                    }

                    if (a[i][j] == 0 && 2 <= n_black && n_black <= 6 && n_zero == 1
                            && (a[i - 1][j] * a[i][j + 1] * a[i + 1][j] == 0)
                            && (a[i][j + 1] * a[i + 1][j] * a[i][j - 1] == 0))
                        {
                            a[i][j] = 1;
                        }

                }
            }
        }
 
    }

    private int GetNumberOfZeroTo1Transactions(int i, int j)
    {
        int countOFZeroToOneTransactions = 0;

        if (a[i - 1][j] == 0 && a[i - 1][j + 1] == 1)
        {
            countOFZeroToOneTransactions++;

        }
        if (a[i - 1][j + 1] == 0 && a[i][j + 1] == 1)
        {
            countOFZeroToOneTransactions++;
        }
        if (a[i][j + 1] == 0 && a[i + 1][j + 1] == 1)
        {
            countOFZeroToOneTransactions++;
        }
        if (a[i + 1][j + 1] == 0 && a[i + 1][j] == 1)
        {
            countOFZeroToOneTransactions++;
        }
        if (a[i + 1][j] == 0 && a[i + 1][j - 1] == 1)
        {
            countOFZeroToOneTransactions++;
        }
        if (a[i + 1][j - 1] == 0 && a[i][j - 1] == 1)
        {
            countOFZeroToOneTransactions++;
        }
        if (a[i][j - 1] == 0 && a[i - 1][j - 1] == 1)
        {
            countOFZeroToOneTransactions++;
        }
        if (a[i - 1][j - 1] == 0 && a[i - 1][j] == 1)
        {
            countOFZeroToOneTransactions++;
        }
        return countOFZeroToOneTransactions;
    }

    private int GetNumberOfSurroundingBlackPixels(int i, int j)
    {	
        return (a[i - 1][j] + a[i - 1][j + 1] + a[i][j + 1] + a[i + 1][j + 1] + a[i + 1][j] +
                a[i + 1][j - 1] + a[i][j - 1] + a[i - 1][j - 1]);
      
    }	
	
	//Calculate the GradientVector on BINARY images	
	public float[] gradientVector(){
		
		double gx=0;
		double gy=0;
		double g=0;
		double angle=0;
		int angle_index=0;
		float[][][] gradientVector;
		float []feature_vector;
		
		
		gradientVector = new float[5][5][16]; //the image is divided 25 parts (5 columns and 5 rows). Each part has 16 features.
		feature_vector = new float[5*5*16];
		
		for(int i = 1; i < rows-1; ++i)
		{
		    for(int j = 1; j < columns-1; ++j)
		    {
		    	gx=(a[i+1][j+1]+2*a[i][j+1]+a[i-1][j+1])-(a[i+1][j-1]+2*a[i][j-1]+a[i-1][j-1]);
		    	gy=(a[i-1][j-1]+2*a[i-1][j]+a[i-1][j+1])-(a[i+1][j-1]+2*a[i+1][j]+a[i+1][j+1]);
		    	angle=Math.atan2(gy, gx); //degrees from -pi to pi

		    	angle_index=(int) (angle/(2*Math.PI/16)); //int from -8 to 8
			 	angle_index+=8;//int from 0 to 16
			    	
			   	if(angle_index==16)
			   		angle_index=15;//int from 0 to 15
				    	
			   	gx=Math.pow(gx, 2);
			   	gy=Math.pow(gy, 2);
			   	g=Math.sqrt(gx+gy);
				    	

				gradientVector[(int)(i/(rows/5))][(int)(j/(columns/5))][angle_index]+=g;

		    }
		}	    	
				
				
/*				
				String stack = "";
				for(int i = 0; i < 5; ++i)
				{
				    for(int j = 0; j < 5; ++j)
				    {
				    	for(int k=0;k<16;k++)
				    	{
				    		stack = stack + " " + gradientVector[i][j][k];
				    	}
					    stack = stack + "\t";
				    }
				    stack = stack + "\n";
				}
				System.out.println(stack);
*/				
		for(int i = 0; i < 5; ++i)
		    for(int j = 0; j < 5; ++j)
		    	for(int k=0;k<16;k++)
		    		feature_vector[i*5*16+j*16+k] = gradientVector[i][j][k];
				    	

		return feature_vector;
				
	}
	
	public float[] ProjectionHistograms() {
		// Horizontal Histogram
		float[] featureVector = new float[rows+columns];
		
		int[] m_HorizontalFeatureVector = new int[rows];
		int[] m_VerticalFeatureVector = new int[columns];
		for (int i = 0; i < rows; i++) {
			int count = 0;
			for (int j = 0; j < columns; j++) {
				if (a[i][j] == 1) {
					count++;
				}

			}
			m_HorizontalFeatureVector[i] = count;
			 
		}
		// Vertical Histogram
		for (int j = 0; j < columns; j++) {
			int count = 0;
			for (int i = 0; i < rows; i++) {
				if (a[i][j] == 1) {
					count++;
				}

			}
			m_VerticalFeatureVector[j] = count;
		}
		
		for(int i = 0; i < m_HorizontalFeatureVector.length; i++){
			featureVector[i] = m_HorizontalFeatureVector[i];
		}

	    //Copy elements from second array into last part of new array
		for(int j = m_HorizontalFeatureVector.length;j < featureVector.length;j++){
			featureVector[j] = m_VerticalFeatureVector[j-m_HorizontalFeatureVector.length];
		}

		
		return featureVector;		
	}
	
	public float[] CrossingFeatureExtraction() {
		int[] m_HorizontalFeatureVector = new int[rows];
		int[] m_VerticalFeatureVector = new int[columns];
		float[] featureVector = new float[rows+columns];

		
		for (int i = 0; i < rows; i++) {
			int count = 0;
			for (int j = 0; j < columns; j++) {
				if (j != 0) {
					if (a[i][j - 1] == 0 && a[i][j] == 1) {
						count++;
					}
				}
			}
			m_HorizontalFeatureVector[i] = count;
		}

		for (int j = 0; j < columns; j++) {
			int count = 0;
			for (int i = 0; i < rows; i++) {
				if (i != 0) {
					if (a[i - 1][j] == 0 && a[i][j] == 1) {
						count++;
					}
				}

			}
			m_VerticalFeatureVector[j] = count;
		}
		
		for(int i = 0; i < m_HorizontalFeatureVector.length; i++){
			featureVector[i] = m_HorizontalFeatureVector[i];
		}

	    //Copy elements from second array into last part of new array
		for(int j = m_HorizontalFeatureVector.length;j < featureVector.length;j++){
			featureVector[j] = m_VerticalFeatureVector[j-m_HorizontalFeatureVector.length];
		}

		return featureVector;
	}
	
	public Vector<int[]> DistancesFeatureExtraction() {
		int[] leftList = new int[rows];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if (a[i][j] == 1) {
					leftList[i] = j;
					break;

				}
			}

		}

		int[] upList = new int[columns];

		for (int j = 0; j < columns; j++) {
			for (int i = 0; i < rows; i++) {
				if (a[i][j] == 1) {
					upList[j] = i;
					break;

				}
			}

		}

		int[] rightList = new int[rows];
		for (int i = 0; i < rows; i++) {
			for (int j = columns - 1; j >= 0; j--) {
				if (a[i][j] == 1) {
					rightList[i] = j;
					break;

				}
			}

		}

		int[] downList = new int[columns];
		for (int j = 0; j < columns; j++) {
			for (int i = rows - 1; i >= 0; i--) {
				if (a[i][j] == 1) {
					downList[j] = i;
					break;

				}
			}

		}
		Vector<int[]> objNewVector = new Vector<int[]>();
		objNewVector.add(leftList);
		objNewVector.add(upList);
		objNewVector.add(rightList);
		objNewVector.add(downList);
		return objNewVector;
	}

}
