package motion;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;
import localization.Localization;
import localization.LocalisationMovement;
import utils.Location;
import utils.LocationType;
import robot_interface.RobotInterface;
import utils.Config;

public class RSystemControl {

	private static int maxMapSizeX = 12;
	private static int maxMapSizeY = 8;
	private static Location[][] map = new Location[maxMapSizeX][maxMapSizeY];

	public static void main(String[] args) {

		createMap();

		System.out.println("Hover over a junction, and hit the middle button to calibrate");

		Button.waitForAnyPress();
		LCD.clear();

		Config config = new Config();
		RobotLocationSessionManager locationManager = new RobotLocationSessionManager();
		RobotMovementSessionManager movementManager = new RobotMovementSessionManager();

		int calibratedValue = setCalibratedValue(config.getLeftSensorPort(), config.getRightSensorPort());

		System.out.println("Calibrated, press middle button to start");

		Button.waitForAnyPress();
		LCD.clear();

		// test local
		Localization localize = new Localization(calibratedValue);

		localize.localize();

		// end test

//		 RouteExecutor routeExecutor = new RouteExecutor(config,
//		 movementManager, locationManager, calibratedValue);
//		 RobotControl robotControl = new RobotControl(movementManager,
//		 locationManager);
//		 RobotInterface robotInterface = new RobotInterface(movementManager);
//		
//		 (new Thread(routeExecutor)).start();
//		 (new Thread(robotControl)).start();
//		 (new Thread(robotInterface)).start();

	}

	private static int setCalibratedValue(SensorPort _lh, SensorPort _rh) {

		LightSensor leftSensor = new LightSensor(_lh, true);
		LightSensor rightSensor = new LightSensor(_rh, true);

		int rightAvg = 0;
		int leftAvg = 0;

		Delay.msDelay(100);

		for (int i = 0; i < 10; i++) {
			int valueRight = rightSensor.readValue();
			int valueLeft = leftSensor.readValue();
			rightAvg += valueRight;
			leftAvg += valueLeft;
		}

		rightAvg = rightAvg / 10;
		leftAvg = leftAvg / 10;

		Delay.msDelay(100);

		return (leftAvg + rightAvg) / 2;
	}

	private static void createMap() {
		for (int i = 0; i < maxMapSizeX; i++) {
			for (int j = 0; j < maxMapSizeY; j++) {
				map[i][j] = new Location(i, j, LocationType.EMPTY);
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
