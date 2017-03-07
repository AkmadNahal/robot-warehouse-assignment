package motion_control;

import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
//import motion.CorrectPose;
//import motion.Turn;
import rp.config.WheeledRobotConfiguration;
import utils.Direction;
import utils.LocationType;
import utils.SuperLocation;
import warehouse_interface.GridWalker;

public class JunctionDetection extends AbstractBehaviour {

	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	
	private SuperLocation locationAccess;
	
	boolean isOnJunction = false;
	private ArrayList<Direction> route;
	private int counter = 0;
	
	private int threshold = 45;

	public JunctionDetection(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor, ArrayList<Direction> route,
			SuperLocation locationAccess) {
		super(_config);
		
		lhSensor = new LightSensor(_lhSensor);
		rhSensor = new LightSensor(_rhSensor);

		this.locationAccess = locationAccess;
		
		this.route = new ArrayList<Direction>(route);
	}

	@Override
	public boolean takeControl() {
		
		float lhValue = lhSensor.getLightValue();
		float rhValue = rhSensor.getLightValue();
		
		if(lhValue < threshold && rhValue < threshold){
			isOnJunction = true;
		}
		return isOnJunction;
	}

	@Override
	public void action() {
		if (threshold != 45){
			pilot.travel(0.05);
		}
		pilot.stop();
		
			
		//Will check what the previous move was, so it can re-orientate to always face the same orientation
		if(!(counter == (route.size() + 1))){
			
			threshold = 35;
			
			if(counter > 0){
				Direction previousMove = route.get(counter - 1);
				//correctPose.adjust(previousMove);
				locationAccess.updateCurrentLocation(previousMove);
			}
			
			if(!(counter == route.size())){
				//Iterates through the arraylist, carrying out the movements in order.
				Direction currentMove = route.get(counter);
				counter++;
				//turn.move(currentMove);
			}
		}
		threshold = 35;
		System.out.println(counter);
		isOnJunction = false;
	}

}
