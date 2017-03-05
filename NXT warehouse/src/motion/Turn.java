package motion;

import helper_classes.Direction;
import rp.config.WheeledRobotConfiguration;

public class Turn extends AbstractBehaviour {

	public Turn(WheeledRobotConfiguration _config) {
		super(_config);
	}

	public void move(Direction direction){
		if (direction == Direction.BACKWARDS){
			pilot.rotate(180);
			pilot.stop();
		}else if(direction == Direction.LEFT){
			pilot.rotate(90);
			pilot.stop();
		}else if(direction == Direction.RIGHT){
			pilot.rotate(-90);
			pilot.stop();
		}
	}

	
}
