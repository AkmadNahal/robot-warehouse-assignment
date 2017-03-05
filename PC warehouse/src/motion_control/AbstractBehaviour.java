package motion_control;

import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.subsumption.Behavior;
import rp.config.WheeledRobotConfiguration;
import rp.systems.WheeledRobotSystem;

public abstract class AbstractBehaviour implements Behavior{
	
	protected DifferentialPilot pilot;
	protected boolean isSuppressed = false;
	protected WheeledRobotConfiguration config;
	
	public AbstractBehaviour(WheeledRobotConfiguration _config) {
		config = _config;
		pilot = new WheeledRobotSystem(_config).getPilot();
		pilot.setTravelSpeed(0.2);
		
	}
	
	@Override
	public void suppress() {
		isSuppressed = true;
	}

}
