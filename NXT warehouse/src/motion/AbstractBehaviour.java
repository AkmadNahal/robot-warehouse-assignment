package motion;

import lejos.robotics.navigation.DifferentialPilot;
import rp.config.WheeledRobotConfiguration;
import rp.systems.WheeledRobotSystem;

public abstract class AbstractBehaviour{
	
	protected DifferentialPilot pilot;
	protected boolean isSuppressed = false;
	protected WheeledRobotConfiguration config;
	
	public AbstractBehaviour(WheeledRobotConfiguration _config) {
		config = _config;
		pilot = new WheeledRobotSystem(_config).getPilot();
		pilot.setTravelSpeed(0.2);
		
	}
	
	public void suppress() {
		isSuppressed = true;
	}

}
