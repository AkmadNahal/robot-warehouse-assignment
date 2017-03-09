package motion_m;

import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import motion.AbstractBehaviour;
//import motion.CorrectPose;
//import motion.Turn;
import rp.config.WheeledRobotConfiguration;
import utils.Direction;
import utils.LocationType;
import utils.SuperLocation;

public class JunctionDetection extends AbstractBehaviour {

	private final LightSensor lhSensor;
	private final LightSensor rhSensor;

	boolean isOnJunction = false;
	private ArrayList<Direction> route;
	private int counter = -1;

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

		counter++;

		if(counter == route.size()) {
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
		} else {
			threshold = 35;
			Direction currentMove = route.get(counter);
			Direction previousMove = null;

			if(counter > 0) {
				previousMove = route.get(counter - 1);

				if(currentMove != previousMove) {
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
			}

			if(previousMove != null) {
				if(previousMove != currentMove) {
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
			} else {
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
