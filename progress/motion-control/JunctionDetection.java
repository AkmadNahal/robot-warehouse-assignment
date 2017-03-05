import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import rp.config.WheeledRobotConfiguration;

public class JunctionDetection extends AbstractBehaviour {

	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	
	boolean isOnJunction = false;
	private ArrayList<ActionType> route;
	private int counter = 0;
	
	private int threshold = 45;

	public JunctionDetection(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor, ArrayList<ActionType> route) {
		super(_config);
		
		lhSensor = new LightSensor(_lhSensor);
		rhSensor = new LightSensor(_rhSensor);
		
		this.route = new ArrayList<ActionType>(route);
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
		if(threshold != 45){
			pilot.travel(0.05);
		}
		pilot.stop();
		
		System.out.println("threshold: " + threshold);
		
			
		//Will check what the previous move was, so it can re-orientate to always face the same orientation
		if(!(counter == (route.size() + 1))){
			threshold = 35;
			
			if(counter > 0){
				ActionType previousMove = route.get(counter - 1);
				
				if(previousMove == ActionType.BACKWARDS){
					pilot.rotate(180);
					pilot.stop();
				}
				else if(previousMove == ActionType.LEFT){
					pilot.rotate(-90);
					pilot.stop();
				}
				else if(previousMove == ActionType.RIGHT){
					pilot.rotate(90);
					pilot.stop();
				}
			}
			
			if(!(counter == route.size())){
				//Iterates through the arraylist, carrying out the movements in order.
				ActionType currentMove = route.get(counter);
		
				if(currentMove == ActionType.FORWARD){
					System.out.println("forward");
					counter ++;
				}
				else if(currentMove == ActionType.BACKWARDS){
					System.out.println("backwards");
					counter++;
					pilot.rotate(180);
					pilot.stop();
				}
				else if(currentMove == ActionType.LEFT){
					System.out.println("left");
					counter++;
					pilot.rotate(90);
					pilot.stop();
				}
				else if(currentMove == ActionType.RIGHT){
					System.out.println("right");
					counter++;
					pilot.rotate(-90);
					pilot.stop();
				}
			}
		}
		threshold = 35;
		System.out.println(counter);
		isOnJunction = false;
	}

}
