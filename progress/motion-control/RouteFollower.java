import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import rp.config.WheeledRobotConfiguration;
import rp.util.Rate;

public class RouteFollower extends AbstractBehaviour {
	
	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	
	private ActionType ActionType;
	
	//Stores the list of moves to carry out
	ArrayList<ActionType> route;
	
	private boolean isRouteComplete = false;

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
		int counter = 0;
		
		while(!isSuppressed){
			
			//Once movement list has been completed, robot stops
			if(counter < route.size()){
			
				//Iterates through the arraylist, carrying out the movements in order.
				ActionType currentMove = route.get(counter);
			
				if(currentMove == ActionType.FORWARD){
					counter ++;
					pilot.forward();
				}
				else if(currentMove == ActionType.BACKWARD){
					counter++;
					pilot.rotate(180);
					pilot.forward();
				}
				else if(currentMove == ActionType.LEFT){
					counter++;
					pilot.rotate(90);
					pilot.stop();
				}
				else if(currentMove == ActionType.RIGHT){
					counter++;
					pilot.rotate(-90);
					pilot.stop();
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
