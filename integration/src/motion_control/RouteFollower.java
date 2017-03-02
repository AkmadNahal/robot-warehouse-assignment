package motion_control;

import java.util.ArrayList;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import rp.config.WheeledRobotConfiguration;
import rp.util.Rate;
import helper_classes.Direction;

public class RouteFollower extends AbstractBehaviour {
	
	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	private int counter;
	
	private Direction Direction;
	
	//Stores the list of moves to carry out
	ArrayList<Direction> route;
	
	private boolean isRouteComplete = false;

	public RouteFollower(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor, ArrayList<Direction> route) {
		super(_config);
		this.counter = 0;

		lhSensor = new LightSensor(_lhSensor);
		rhSensor = new LightSensor(_rhSensor);
		
		this.route = new ArrayList<Direction>(route);
		
	}

	@Override
	public boolean takeControl() {
		return !isRouteComplete;
	}

	@Override
	public void action() {
		Rate r = new Rate(20);
		
		while(!isSuppressed){
			
			//Once movement list has been completed, robot stops
			if(counter < route.size()){
				
				//Will check what the previous move was, so it can re-orientate to always face the same orientation
				if(counter > 0){
					Direction previousMove = route.get(counter - 1);
					
					if(previousMove == Direction.BACKWARDS){
						pilot.rotate(180);
						pilot.stop();
					}
					else if(previousMove == Direction.LEFT){
						pilot.rotate(-90);
						pilot.stop();
					}
					else if(previousMove == Direction.RIGHT){
						pilot.rotate(90);
						pilot.stop();
					}
				}
			
				//Iterates through the arraylist, carrying out the movements in order.
				Direction currentMove = route.get(counter);
			
				if(currentMove == Direction.FORWARD){
					counter ++;
					pilot.forward();
				}
				else if(currentMove == Direction.BACKWARDS){
					counter++;
					pilot.rotate(180);
					pilot.stop();
					pilot.forward();
				}
				else if(currentMove == Direction.LEFT){
					counter++;
					pilot.rotate(90);
					pilot.stop();
					pilot.forward();
				}
				else if(currentMove == Direction.RIGHT){
					counter++;
					pilot.rotate(-90);
					pilot.stop();
					pilot.forward();
				}
			}
			else{
				isRouteComplete = true;
			}
			r.sleep();
		}
		isSuppressed = false;
		
	}

}
