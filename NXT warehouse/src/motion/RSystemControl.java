package motion;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;
import robot_interface.RobotInterface;
import utils.Config;

public class RSystemControl {

  public static void main(String[] args) {
	  
	System.out.println("Hover over a junction, and hit the middle button to calibrate");
	
	Button.waitForAnyPress();

    Config config = new Config();
    RobotLocationSessionManager locationManager = new RobotLocationSessionManager();
    RobotMovementSessionManager movementManager = new RobotMovementSessionManager();
    
	int calibratedValue = setCalibratedValue(config.getLeftSensorPort(), config.getRightSensorPort());
	
	System.out.println("Calibrated, press middle button to start");
	
	Button.waitForAnyPress();
    
    RouteExecutor routeExecutor = new RouteExecutor(config, movementManager, locationManager, calibratedValue);
    RobotControl robotControl = new RobotControl(movementManager, locationManager);
    RobotInterface robotInterface = new RobotInterface(movementManager);

    (new Thread(routeExecutor)).start();
    (new Thread(robotControl)).start();
    (new Thread(robotInterface)).start();

  }
  
	private static int setCalibratedValue(SensorPort _lh, SensorPort _rh) {

		LightSensor leftSensor = new LightSensor(_lh, true);
		LightSensor rightSensor = new LightSensor(_rh, true);

		Delay.msDelay(100);

		return (leftSensor.readValue() + rightSensor.readValue()) / 2;
	}

}
