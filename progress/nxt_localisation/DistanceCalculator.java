package nxt_localisation;

import static rp.robotics.mapping.MapUtils.createBox;

import java.util.ArrayList;

import lejos.geom.Line;
import lejos.geom.Rectangle;
import rp.robotics.mapping.GridMap;
import rp.robotics.mapping.LineMap;



public class DistanceCalculator {
	private static int maxMapSizeX = 12;
	private static int maxMapSizeY = 8;
	private static DataOfJunction[][] map;
	
	public DistanceCalculator() {
		map = new DataOfJunction[maxMapSizeX][maxMapSizeY];
	}
	
	public static void fillTheCoordinates(){
		for(int x=0;x<maxMapSizeX;x++) {
			for(int y=0;y<maxMapSizeY;y++) {
				if(map[x][y].getType().equals(LocationType.BLOCK)){
					map[x][y].setX(x);
					map[x][y].setY(y);
					map[x][y].setxPlus(-1);
					map[x][y].setyPlus(-1);
					map[x][y].setxMinus(-1);
					map[x][y].setyMinus(-1);
				} else {
					map[x][y].setX(x);
					map[x][y].setY(y);
					
					map[x][y].setxPlus(getXPlus(x, y));
					map[x][y].setxMinus(getXMinus(x, y));
					map[x][y].setyPlus(getYPlus(x, y));
					map[x][y].setyMinus(getYMinus(x, y));
					
					
					getYMinus(x, y);
				}
				System.out.println(map[x][y].getX() + " " + map[x][y].getY() + " " + map[x][y].getxPlus() + " " + map[x][y].getxMinus() + " " + map[x][y].getyPlus() + " " + map[x][y].getyMinus());
			}		
		}
	}
	private static int getYMinus(int x, int y) {
		if(y == 0){
			return 0;
		}
		if (map[x][y-1].getType().equals(LocationType.BLOCK)){
			return 0;
		}
		if (y >= 2 && map[x][y-2].getType().equals(LocationType.BLOCK)){
			return 1;
		}
		if (y>2){
			return 3;
		}
		return y;
	}
	private static int getYPlus(int x, int y) {
		if (y == maxMapSizeY-1) {
			return 0;
		}
		if (map[x][y].getType().equals(LocationType.BLOCK)){
			return 0;
		} 
		if (maxMapSizeY-y > 2){
			return 3;
		}
		return maxMapSizeY-x;
	}
	private static int getXMinus(int x, int y) {
		if (x == 0) {
			return 0;
		}
		if (map[x-1][y].getType().equals(LocationType.BLOCK)){
			return 0;
		} 
		if (x >= 2 && map[x-2][y].getType().equals(LocationType.BLOCK)){
			return 1;
		}
		if (x>2){
			return 3;
		} 
			
		return x;
	}
	private static int getXPlus(int x, int y) {
		if (x == maxMapSizeX-1) {
			return 0;
		}
		if (map[x+1][y].getType() == LocationType.BLOCK){
			return 0;
		}
		if (maxMapSizeX-2 == x) {
			return 1;
		}
		if (map[x+2][y].getType()==LocationType.BLOCK){
			return 1;
		}
		if (maxMapSizeX-x>2){
			return 3;
		} 
		return maxMapSizeX-x;
	}
	public static void createMap() {
		
	    for(int i=0;i<maxMapSizeX;i++) {
	      for(int j=0;j<maxMapSizeY;j++) {
	        map[i][j] = new DataOfJunction(i,j,LocationType.EMPTY);
	      }
	    }

	    map[1][1].setType(LocationType.BLOCK);
	    map[1][2].setType(LocationType.BLOCK);
	    map[1][3].setType(LocationType.BLOCK);
	    map[1][4].setType(LocationType.BLOCK);
	    map[1][5].setType(LocationType.BLOCK);

	    map[4][1].setType(LocationType.BLOCK);
	    map[4][2].setType(LocationType.BLOCK);
	    map[4][3].setType(LocationType.BLOCK);
	    map[4][4].setType(LocationType.BLOCK);
	    map[4][5].setType(LocationType.BLOCK);

	    map[7][1].setType(LocationType.BLOCK);
	    map[7][2].setType(LocationType.BLOCK);
	    map[7][3].setType(LocationType.BLOCK);
	    map[7][4].setType(LocationType.BLOCK);
	    map[7][5].setType(LocationType.BLOCK);

	    map[10][1].setType(LocationType.BLOCK);
	    map[10][2].setType(LocationType.BLOCK);
	    map[10][3].setType(LocationType.BLOCK);
	    map[10][4].setType(LocationType.BLOCK);
	    map[10][5].setType(LocationType.BLOCK);
	    
	  }

}

