import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import rp.config.WheeledRobotConfiguration;
import rp.util.Rate;

public class RouteFollower extends AbstractBehaviour {
	
	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	
	
	//Stores the list of moves to carry out
	ArrayList<ActionType> route;
	
	
	private boolean isRouteComplete = false;
	private int counter = 0;

	public RouteFollower(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor, ArrayList<ActionType> route) {
		super(_config);
		
		lhSensor = new LightSensor(_lhSensor);
		rhSensor = new LightSensor(_rhSensor);
		
		this.route = new ArrayList<ActionType>(route);
		
	}

	@Override
	public boolean takeControl() {
		// TODO Auto-generated method stub
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
			
				//Iterates through the arraylist, carrying out the movements in order.
				ActionType currentMove = route.get(counter);
			
				if(currentMove == ActionType.FORWARD){
					counter ++;
					pilot.forward();
				}
				else if(currentMove == ActionType.BACKWARDS){
					counter++;
					pilot.rotate(180);
					pilot.stop();
					pilot.forward();
				}
				else if(currentMove == ActionType.LEFT){
					counter++;
					pilot.rotate(90);
					pilot.stop();
					pilot.forward();
				}
				else if(currentMove == ActionType.RIGHT){
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
