package localization;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;
import localization.Localization;
import motion.RobotControl;
import motion.RobotLocationSessionManager;
import motion.RobotMovementSessionManager;
import localization.LocalisationMovement;
import robot_interface.RobotInterface;
import utils.Config;

public class LocalizationMain {

	public static void main(String[] args) {

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

		 LocalizationComm robotControl = new LocalizationComm(movementManager, locationManager, calibratedValue);
		 robotControl.run();

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

}

