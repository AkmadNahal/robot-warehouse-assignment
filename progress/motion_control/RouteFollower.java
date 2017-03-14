import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;
import rp.config.WheeledRobotConfiguration;
import rp.util.Rate;

public class RouteFollower extends AbstractBehaviour {
	
	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	
	private final UltrasonicSensor distanceSensor;
	private ArrayList<ActionType> route;
	
	private boolean isRouteComplete = false;
	private boolean abortRoute = false;
	private int counter = 0;
	private int routeLength;

	public RouteFollower(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor, SensorPort _dsSensor, ArrayList<ActionType> route, int numberOfMoves) {
		super(_config);
		
		lhSensor = new LightSensor(_lhSensor, true);
		rhSensor = new LightSensor(_rhSensor, true);
		
		distanceSensor = new UltrasonicSensor(_dsSensor);
		
		this.route = route;
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
				
				//Ensures the robot will not collide into walls/ other robots
				//If the safe distance is breached, route following will stop
				float distance = distanceSensor.getRange();
				
				if(distance < 8){
					System.out.println(pilot.getMovementIncrement());
					float reverse = pilot.getMovementIncrement();
					pilot.stop();
					pilot.travel(-reverse);
					pilot.stop();
					
					ActionType move = route.get(counter);
					
					if(move == ActionType.BACKWARDS){
						pilot.rotate(180);
						pilot.stop();
					}
					else if(move == ActionType.LEFT){
						pilot.rotate(-90);
						pilot.stop();
					}
					else if(move == ActionType.RIGHT){
						pilot.rotate(90);
						pilot.stop();
					}
					
					Sound.buzz();
					System.out.println("abort");
					counter = routeLength;
					abortRoute = true;
					break;
				}
				
				//Line following calculations
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
			
			if(abortRoute == false){
				counter++;
				isSuppressed = false;
			}
		}
		else{
			isRouteComplete = true;
		}
	}

}
