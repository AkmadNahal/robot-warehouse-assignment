package motion_control;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
//import motion.Drive;
import rp.config.WheeledRobotConfiguration;
import rp.util.Rate;

public class RouteFollower extends AbstractBehaviour {
	
	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	//private Drive drive;

	
	private boolean isRouteComplete = false;
	private int counter = 0;
	private int routeLength;

	public RouteFollower(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor, int numberOfMoves) {
		super(_config);
		
		lhSensor = new LightSensor(_lhSensor);
		rhSensor = new LightSensor(_rhSensor);
		//drive = new Drive (_config, _lhSensor, _rhSensor);
		
		routeLength = numberOfMoves;
		
	}

	@Override
	public boolean takeControl() {
		return !isRouteComplete;
	}

	@Override
	public void action() {
		
		if(!(counter == (routeLength))){
			while(!isSuppressed){
				//drive.followPath();
			}
			counter++;
			isSuppressed = false;
		}
		else{
			isRouteComplete = true;
		}
	}

}
