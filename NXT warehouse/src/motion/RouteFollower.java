package motion;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
//import motion.Drive;
import rp.config.WheeledRobotConfiguration;
import rp.util.Rate;

public class RouteFollower extends AbstractBehaviour {
	
	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	private Drive drive;
	
	private float minRange;
	private float maxRange;
	private float rangeDiff;
	
	private float minValue;
	private float maxValue;
	private float valDiff;
	
	private float P;

	
	private boolean isRouteComplete = false;
	private int counter = 0;
	private int routeLength;

	public RouteFollower(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor, int numberOfMoves) {
		super(_config);
		
		lhSensor = new LightSensor(_lhSensor);
		rhSensor = new LightSensor(_rhSensor);
		drive = new Drive (_config, _lhSensor, _rhSensor);
		
		routeLength = numberOfMoves;
		
		minRange = 0;
		maxRange = 100;
		rangeDiff = maxRange - minRange;
		
		minValue = -200;
		maxValue = 200;
		valDiff = maxValue - minValue;
		
		P = 1f;
		
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
				float rHValue = rhSensor.getLightValue();
				float lHValue = lhSensor.getLightValue();
				
				float rHRatio = (rHValue - minRange)/rangeDiff;
				float lHRatio = (lHValue - minRange)/rangeDiff;
				
				float rHOutput = minValue + (valDiff * rHRatio);
				float lHOutput = minValue + (valDiff * lHRatio);
				
				float turnDiff = (rHOutput - lHOutput) + 8;
				
				float turnOut = P * turnDiff;
				
				pilot.steer(turnOut);
				
			}
			counter++;
			isSuppressed = false;
		}
		else{
			isRouteComplete = true;
		}
	}

}
