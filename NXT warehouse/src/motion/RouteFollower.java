package motion;

import java.io.PrintStream;
import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import rp.config.WheeledRobotConfiguration;
import utils.Direction;

public class RouteFollower extends AbstractBehaviour {
	
	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	
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
	private UltrasonicSensor dsSensor;
	private ArrayList<Direction> route;
	private boolean abortRoute = false;

	public RouteFollower(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor, SensorPort _dsSensor, ArrayList<Direction> route, int numberOfMoves) {
		super(_config);
		
		this.lhSensor = new LightSensor(_lhSensor);
		this.rhSensor = new LightSensor(_rhSensor);
		this.dsSensor = new UltrasonicSensor(_dsSensor);
		
		this.route = route;
		this.routeLength = numberOfMoves;
		
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
				
				//Ensures the robot will not collide into walls/ other robots
				//If the safe distance is breached, route following will stop
				float distance = dsSensor.getRange();
				
				if(distance < 8){
					System.out.println(pilot.getMovementIncrement());
					float reverse = pilot.getMovementIncrement();
					pilot.stop();
					pilot.travel(-reverse);
					pilot.stop();
					
					Direction move = route.get(counter);
					
					if(move == Direction.BACKWARDS){
						pilot.rotate(180);
						pilot.stop();
					}
					else if(move == Direction.LEFT){
						pilot.rotate(-90);
						pilot.stop();
					}
					else if(move == Direction.RIGHT){
						pilot.rotate(90);
						pilot.stop();
					}
					
					Sound.buzz();
					System.out.println("abort");
					counter = routeLength;
					abortRoute  = true;
					break;
				}
				
				
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
			if(abortRoute == false){
				counter++;
				isSuppressed = false;
			}
		}
		else{
			isRouteComplete = true;
		}
	}
	
	protected static void redirectOutput(boolean _useBluetooth) {
		if (!RConsole.isOpen()) {
			if (_useBluetooth) {
				RConsole.openBluetooth(0);
			} else {
				RConsole.openUSB(0);
			}
		}
		PrintStream ps = RConsole.getPrintStream();
		System.setOut(ps);
		System.setErr(ps);
	}

}
