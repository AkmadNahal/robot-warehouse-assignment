package motion_control;

import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import rp.config.WheeledRobotConfiguration;
import rp.util.Rate;

public class RouteFollower extends AbstractBehaviour {
	
	private final LightSensor lhSensor;
	private final LightSensor rhSensor;

	
	private boolean isRouteComplete = false;
	private int counter = 0;
	private int routeLength;

	public RouteFollower(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor, int numberOfMoves) {
		super(_config);
		
		lhSensor = new LightSensor(_lhSensor);
		rhSensor = new LightSensor(_rhSensor);
		
		routeLength = numberOfMoves;
		
	}

	@Override
	public boolean takeControl() {
		// TODO Auto-generated method stub
		return !isRouteComplete;
	}

	@Override
	public void action() {
		Rate r = new Rate(20);
		
		float minRange = 0;
		float maxRange = 100;
		float rangeDiff = maxRange - minRange;
		
		float minValue = -200;
		float maxValue = 200;
		float valDiff = maxValue - minValue;
		
		float P = 1f;
		
		if(!(counter == (routeLength))){
			while(!isSuppressed){
				
				float rHValue = rhSensor.getLightValue();
				float lHValue = lhSensor.getLightValue();
				
				float rHRatio = (rHValue - minRange)/rangeDiff;
				float lHRatio = (lHValue - minRange)/rangeDiff;
				
				float rHOutput = minValue + (valDiff * rHRatio);
				float lHOutput = minValue + (valDiff * lHRatio);
				
				float turnDiff = (rHOutput - lHOutput) + 8;
				
				float turnOut = P * turnDiff;
				
				pilot.steer(turnOut);
				r.sleep();
			}
			counter++;
			isSuppressed = false;
		}
		else{
			isRouteComplete = true;
		}
	}

}
