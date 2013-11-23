package matrix;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class Features {


	private int n_item=0;
	private int columns=-1;	
	Vector<float[]> table = new Vector<float[]>();
		
	//return the number of rows in the table
	public int getColumns(){
		return this.columns;
	}
	
	public float getElement(int i,int j){
		return this.table.get(i)[j];
	}
	
	//return the number of columns in the table
	public int getN_item(){
		return this.n_item;
	}
	
	public Features(){
		
	}
	
	public void add(float[] in)throws Exception{
		if(columns==-1)
			columns=in.length;
		
		if (!(columns==in.length)){
			throw new Exception("Size Error");
		}else{
			this.table.add(in);
			this.n_item++;			
		}
	}
	
	//return a String to be displayed in the console
	//(you can copy-paste from console to excel to have a better view of the result
	public String toString(){
		String stack = "";
		for(int i = 0; i < n_item; ++i)
		{
		    for(int j = 0; j < columns; ++j)
		    {
		    	if (j!=0){
		    			stack = stack + "\t" + this.table.get(i)[j];
		    	}
		    	else{
		    		stack = stack + this.table.get(i)[j];
		    	}
		    }
		    stack = stack + "\n";
		}
		return stack;
	}

	public void normalization(){
		float norm=0;
		for (int j=0;j<columns;j++){

			for(int i=0;i<n_item;i++){
				norm+=Math.pow(this.table.get(i)[j],2);
			}
			norm=(float) Math.sqrt(norm);
			for(int i=0;i<n_item;i++){
				if(norm!=0)
					this.table.get(i)[j]=(this.table.get(i)[j])/norm;
			}
			norm = 0;
						
		}
	
	}

}