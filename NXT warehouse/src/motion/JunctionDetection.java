package motion;

import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import rp.config.WheeledRobotConfiguration;
import utils.Direction;
import utils.LocationType;
import utils.SuperLocation;

public class JunctionDetection extends AbstractBehaviour {

	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	
	private Turn turn = new Turn(config);
	private CorrectPose correctPose = new CorrectPose(config);
	
	
	boolean isOnJunction = false;
	private ArrayList<Direction> route;
	private int counter = 0;
	
	
	private int threshold = 45;

	public JunctionDetection(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor, ArrayList<Direction> route) {
		super(_config);
		
		lhSensor = new LightSensor(_lhSensor);
		rhSensor = new LightSensor(_rhSensor);
		
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
				if (previousMove == Direction.BACKWARDS){
					pilot.rotate(180);
					pilot.stop();
				}else if(previousMove == Direction.LEFT){
					pilot.rotate(-90);
					pilot.stop();
				}else if(previousMove == Direction.RIGHT){
					pilot.rotate(90);
					pilot.stop();
				}
			}
			
			if(!(counter == route.size())){
				//Iterates through the arraylist, carrying out the movements in order.
				Direction currentMove = route.get(counter);
				counter++;
				if (currentMove == Direction.BACKWARDS){
					pilot.rotate(180);
					pilot.stop();
				}else if(currentMove == Direction.LEFT){
					pilot.rotate(90);
					pilot.stop();
				}else if(currentMove == Direction.RIGHT){
					pilot.rotate(-90);
					pilot.stop();
				}
			}
		}
		threshold = 35;
		isOnJunction = false;
	}

}
