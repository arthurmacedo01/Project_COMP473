package matrix;

import java.util.ArrayList;

public class ZonedDistanceFeatureExtractor {

	int[][] arr;
	int rows;
	int cols;
	
	int cenX; // image centroid x cordinate
	int cenY;  // image centroid x cordinate
	
	ArrayList<int[]> zoneCentroids = new ArrayList<>();
	
	ArrayList<int[][]> zones = new ArrayList<>();
	
	ArrayList<Float> featureList = new ArrayList<>();
	
	public Float[] features;
	
	public ZonedDistanceFeatureExtractor(int[][] a){
		this.arr = a;
		this.rows = a.length;
		this.cols = a[0].length;
	}
	
	public void start(){
		this.calculateImageCentroid();
		this.makeZones();
		this.calculateFeatures();
	}
	
	public void calculateImageCentroid(){
		
		int[] centroid = calculateCentroid(arr);
		cenX = centroid[0];
		cenY = centroid[1];
	}
	
	
	
	public int[] calculateCentroid(int[][] input){
		
		int[] centroidCord = new int[2];		
		int mx=0;
		int my=0;
		int mass = 0;
		for(int i=0; i<input.length; i++){
			for(int j =0; j<input[0].length; j++){
				if(input[i][j] == 1){
					mx += j;
					my += i;
					mass += 1;
				}
			}
		}
		centroidCord[0] = Math.round((float) mx/mass);
		centroidCord[1] = Math.round((float) my/mass);
		
		return centroidCord;
	}
	
	// converting into 9 zones
	public void makeZones(){
	
		int r = rows/3;
		int c = cols/3;
		
		for(int rowM = 0; rowM<3; rowM++){
			for(int colM=0; colM<3; colM++){
				makeZone(rowM*r, colM*c, r, c);
			}
		}

	}
	
	public void makeZone(int rStart, int cStart, int r, int c){
		
		int[][] zoneArr = new int[r][c];
		for(int i = 0; i < r; i++){
			for(int j = 0; j < c; j++){
				
				zoneArr[i][j] = arr[rStart+i][cStart+j]; 
				//System.out.println((rStart+i) + "  " + (cStart+j));
			}
		}
		
		zones.add(zoneArr);
		zoneCentroids.add(calculateCentroid(zoneArr));
	}
	
	public void calculateFeatures(){
		
		featureList = new ArrayList<>(); 
		
		int zoneCount = 0;
		
		for(int[][] zone : zones){
			
			int rStart = zoneCount * zone.length;
			int cStart = zoneCount * zone.length;;
			
			double dis = 0;
			double zonalDis = 0;
					
			for(int i = 0; i < zone.length; i++){
				for(int j = 0; j < zone[0].length; j++){
					
					if(zone[i][j] == 1){
						dis = dis + Math.sqrt(( Math.pow(((rStart+i)- cenX), 2) + Math.pow(((cStart+j)-cenY), 2)));
						zonalDis = zonalDis + Math.sqrt(( Math.pow((i - zoneCentroids.get(zoneCount)[0]), 2) + Math.pow(((j - zoneCentroids.get(zoneCount)[1])), 2)));
						
						
					} 
				}
			}
			
			//System.out.print( dis + " " + zonalDis + " ");
			featureList.add((float) dis);
			featureList.add((float) zonalDis);
			
		}
		//System.out.println();
		
		features =  featureList.toArray(new Float[featureList.size()]);
		
	}
}