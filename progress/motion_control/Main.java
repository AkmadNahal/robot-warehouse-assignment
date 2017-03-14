import java.util.ArrayList;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.util.Delay;
import rp.config.RobotConfigs;

public class Main {
	
	public static void main (String[] args){
		Button.waitForAnyPress();
		
		int calibration = setCalibratedValue(SensorPort.S4, SensorPort.S1);
		System.out.println(calibration);
		
		Button.waitForAnyPress();
		
		ArrayList<ActionType> route = new ArrayList<ActionType>() {{
			add(ActionType.RIGHT);
			add(ActionType.RIGHT);
			add(ActionType.FORWARD);
			add(ActionType.LEFT);
		}};
		
		//RouteFollower Parameters:
		// robot_config, Left_Light_Sensor, Right_Light_Sensor, Distance_Sensor, route_length
		
		//JunctionDetection Parameters:
		// robot_config, Left_Light_Sensor, Right_Light_Sensor, route (list of moves)
		
		Behavior drive = new RouteFollower(RobotConfigs.CASTOR_BOT, SensorPort.S4, SensorPort.S1, SensorPort.S2, route, route.size());
		Behavior junction = new JunctionDetection(RobotConfigs.CASTOR_BOT, SensorPort.S4, SensorPort.S1, route, calibration);
		Arbitrator arby = new Arbitrator(new Behavior[] {drive, junction}, true);
		arby.start();
		
		System.out.println("Arbitrator stopped - route complete");
		Sound.beepSequence();
		
	}
	
	private static int setCalibratedValue(SensorPort _lh, SensorPort _rh){
		
		LightSensor leftSensor = new LightSensor(_lh, true);
		LightSensor rightSensor = new LightSensor(_rh, true);
		
		Delay.msDelay(100);
		
		return (leftSensor.readValue() + rightSensor.readValue())/2;
	}

}
