package matrix;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class SegColScan {

	// contains the temporary segment list.
	ArrayList<int[][]> segmentList = new ArrayList<>();
	//contains the final segment list.
	public ArrayList<int[][]> finalSegmentList = new ArrayList<>();
	
	ArrayList<ZonedDistanceFeatureExtractor> zoneExtractorList = new ArrayList<>();
	
	int[][] image = null; // The image we are currently working on.
	int noOfRows;
	int noOfColumns;
	
	int maxSegRows = 0;
	int maxsegCols = 0;
	
	String tempFileName;
	
	public SegColScan(int[][] matrix){
	
		image = matrix;
	}
		
	public void start(String filePath, String fileName ) {
		
		try{
			
			tempFileName = fileName;
			
			Image img = Toolkit.getDefaultToolkit().createImage(filePath);
			Matrix m = new Matrix(img);
			//m = m.invert();
			
			//writeArrayToFile(m.getArray(), tempFileName + "_Unprocessed");
			
			//m = m.thinning();
		//	m = m.filling();
			//m = m.smoothing();
			//m = m.correctSlant();
			
			
			image = m.getArray();
			
			//writeArrayToFile(image, tempFileName + "_fullImage");
			
			segmentImage();
			normalizeSegments();
			callZonedExtractorOnEachSegment();
			//printSegments();	
			
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
	
	
	
	public void callZonedExtractorOnEachSegment(){
		
		for(int[][] arr : finalSegmentList){
			ZonedDistanceFeatureExtractor z = new ZonedDistanceFeatureExtractor(arr);
			z.start();
			zoneExtractorList.add(z);
		}
	}

	public void normalizeSegments(){
		
		segmentList = finalSegmentList;
		finalSegmentList = new ArrayList<>();
		
		//int count = 0;
		for(int[][] seg : segmentList){
			Matrix m = new Matrix(seg, seg.length, seg[0].length);
			
			//writeArrayToFile(seg, tempFileName + "_seg_" + (count) );
			m.normalize(30,  30);
			//writeArrayToFile(m.getArray(), tempFileName + "_normalizedSeg_" + (count++) );
			
			finalSegmentList.add(m.getArray());
		}
	}
	

	public void segmentImage(){
		
		if(this.image != null){
			
			noOfRows = image.length;
			noOfColumns = image[0].length;
		}
		
		segmentBasedOnAllWhiteColumn(0, 0);
		removeWhiteRowsFromAboveAndBelow();	
	}
	
	/* Unfortunately this function might not work if there is a noise fragment that acts like a bridge between two
	 * numbers. we got to do something about that, if those can be present in the image.
	 */
	public void segmentBasedOnAllWhiteColumn(int r, int c){

		boolean blackFlag = false;
		int i = c;
		
		// find a column with no foreground pixel.
		for(i = c; i<noOfColumns; i++){
			
			blackFlag = false;
			for(int j = r; j<noOfRows; j++ ){
				
				if(black(j,i)){
						blackFlag = true;
						break;
				}
			}
			if(!blackFlag){
				break;
			}
		}
		
		// make and save the segment to the arrayList
		int noOfColmInsegment = i - c+1;
		
		if(noOfColmInsegment > 1){
			int[][] segment = new int[noOfRows][noOfColmInsegment];
			
			for(int rownum = 0; rownum < noOfRows; rownum++){
				
				for(int colnum = 0; colnum < noOfColmInsegment; colnum++){
					
					if(r+rownum < noOfRows && c+colnum < noOfColumns){
						segment[rownum][colnum] = image[r + rownum][ c + colnum];
					}
				}
			}
			this.segmentList.add(segment);
		}
		
		//recurse this function if more columns are left.
		if(i < noOfColumns){
			segmentBasedOnAllWhiteColumn(0, i+1);
		}
	}
	
	// function removes any white rows from top and buttom. so all the segment after this process
	// contain image from edge to edge. this also filters out any segment that are just empty segments 
	// or if there are not black pixels on atleast 50% of the rows as it might be just noise.
	// just to keep it simple we remove any whole white row even if its in the middle.
	public void removeWhiteRowsFromAboveAndBelow(){
		
		boolean blackflag = false;
		
		for(int[][] seg : segmentList){
		
			if(seg.length < 1 )
				continue;
			if(seg[0].length < 1)
				continue;
			
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
			
			if(newSegCurrentRow > 5){
				
				// we need to make a new array with extra columns deleted
				int[][] realSegmentarray = new int[newSegCurrentRow][seg[0].length];
				for(int x = 0; x < newSegCurrentRow; x++){
					
					realSegmentarray[x] = eToESeg[x];
				}
				finalSegmentList.add(realSegmentarray);
				if(maxSegRows < realSegmentarray.length){
					maxSegRows = realSegmentarray.length;
				}
				if(maxsegCols < realSegmentarray[0].length){
					maxsegCols = realSegmentarray[0].length;
				}
			}
		}
	}
	
	public boolean black(int i, int j){
		return image[i][j] == 1;
	}
	
	public void printSegments(){
		
		try{
			int seg = 1;
			for(int[][] arr : finalSegmentList){
				
				writeArrayToFile(arr, "seg_" + seg++);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void writeArrayToFile(int[][] arr , String fileName){
		
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter("results\\" + fileName + ".txt"));
			
			for(int i = 0; i < arr.length; i++){
				for(int j=0; j< arr[0].length; j++){
					out.write(arr[i][j] + " ");
				}
				out.newLine();
			}
			out.flush();
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
