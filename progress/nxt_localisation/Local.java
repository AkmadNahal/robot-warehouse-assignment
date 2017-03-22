package nxt_localisation;

import lejos.nxt.Button;

public class Local {
	private final static int maxMapSizeX = 12;
	private final static int maxMapSizeY = 8;
	private static DataOfJunction[][] map = new DataOfJunction[maxMapSizeX][maxMapSizeY];
	
	public static void main(String[] args) {
		DistanceCalculator it = new DistanceCalculator(map ,maxMapSizeX, maxMapSizeY);
		it.createMap();
		it.fillTheCoordinates();
		Button.waitForAnyPress();
	}
}
