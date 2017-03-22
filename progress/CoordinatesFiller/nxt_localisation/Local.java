package nxt_localisation;

import lejos.nxt.Button;
public class Local {
	
	public static void main(String[] args) {
		DistanceCalculator it = new DistanceCalculator();
		it.createMap();
		it.fillTheCoordinates();
		Button.waitForAnyPress();
	}
}
