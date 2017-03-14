import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;
import rp.config.WheeledRobotConfiguration;

public class JunctionDetection extends AbstractBehaviour {

	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	
	boolean isOnJunction = true;
	private ArrayList<ActionType> route;
	private int counter = 0;
	private final int error = 6;
	private int calibratedValue;

	public JunctionDetection(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor, ArrayList<ActionType> route, int calValue) {
		super(_config);
		
		lhSensor = new LightSensor(_lhSensor, true);
		rhSensor = new LightSensor(_rhSensor, true);
		
		this.route = new ArrayList<ActionType>(route);
		this.calibratedValue = calValue;
	}
	

	@Override
	public boolean takeControl() {
		
		int valueRight = rhSensor.readValue();
		int valueLeft = lhSensor.readValue();
		
		if((valueRight - calibratedValue < error) && (valueLeft - calibratedValue < error)){
			isOnJunction = true;
		}
		
		return isOnJunction;
	}

	@Override
	public void action() {
		if(counter != 0){
			pilot.travel(0.05);
		}
		pilot.stop();
		
			
		//Will check what the previous move was, so it can re-orientate to always face the same orientation
		if(!(counter == (route.size() + 1))){
			
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
		System.out.println(counter);
		isOnJunction = false;
	}

}
