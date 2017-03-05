import java.util.ArrayList;

import lejos.nxt.Button;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import rp.config.RobotConfigs;

public class Main {
	
	public static void main (String[] args){
		Button.waitForAnyPress();
		
		ArrayList<ActionType> route = new ArrayList<ActionType>() {{
			add(ActionType.RIGHT);
			add(ActionType.FORWARD);
			add(ActionType.LEFT);
			add(ActionType.FORWARD);
			add(ActionType.RIGHT);
			add(ActionType.FORWARD);
			add(ActionType.LEFT);
			add(ActionType.FORWARD);
			add(ActionType.RIGHT);
			add(ActionType.FORWARD);
			add(ActionType.LEFT);
			add(ActionType.FORWARD);
		}};
		
		Behavior drive = new RouteFollower(RobotConfigs.CASTOR_BOT, SensorPort.S4, SensorPort.S1, route.size());
		Behavior junction = new JunctionDetection(RobotConfigs.CASTOR_BOT, SensorPort.S4, SensorPort.S1, route);
		Arbitrator arby = new Arbitrator(new Behavior[] {drive, junction}, true);
		arby.start();
		
		System.out.println("Arbitrator stopped - route complete");
		Sound.beepSequence();
		
	}

}
