import java.util.ArrayList;

import lejos.nxt.Button;
import lejos.nxt.SensorPort;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import rp.config.RobotConfigs;

public class Main {
	
	public static void main (String[] args){
		Button.waitForAnyPress();
		
		ArrayList<ActionType> route = new ArrayList<ActionType>() {{
			add(ActionType.FORWARD);
			add(ActionType.LEFT);
			add(ActionType.FORWARD);
			add(ActionType.RIGHT);
		}};
		
		Behavior drive = new RouteFollower(RobotConfigs.EXPRESS_BOT, SensorPort.S1, SensorPort.S4, route);
		Behavior junction = new JunctionDetection(RobotConfigs.EXPRESS_BOT, SensorPort.S1, SensorPort.S4);
		Arbitrator arby = new Arbitrator(new Behavior[] {drive, junction}, true);
		arby.start();
	}

}
