package motion;

import utils.Direction;
import rp.config.WheeledRobotConfiguration;

public class CorrectPose extends AbstractBehaviour {

	public CorrectPose(WheeledRobotConfiguration _config) {
		super(_config);
	}

	public void adjust(Direction direction){
		if (direction == Direction.BACKWARDS){
			pilot.rotate(180);
			pilot.stop();
		}else if(direction == Direction.LEFT){
			pilot.rotate(-90);
			pilot.stop();
		}else if(direction == Direction.RIGHT){
			pilot.rotate(90);
			pilot.stop();
		}
	}

	
}
