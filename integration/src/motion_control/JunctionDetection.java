package motion_control;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import rp.config.WheeledRobotConfiguration;

public class JunctionDetection extends AbstractBehaviour {

	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	
	boolean isOnJunction = false;

	public JunctionDetection(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor) {
		super(_config);
		
		lhSensor = new LightSensor(_lhSensor);
		rhSensor = new LightSensor(_rhSensor);
	}

	@Override
	public boolean takeControl() {
		
		float lhValue = lhSensor.getLightValue();
		float rhValue = rhSensor.getLightValue();
		
		if(lhValue < 35 && rhValue < 35){
			isOnJunction = true;
		}
		return isOnJunction;
	}

	@Override
	public void action() {
		pilot.stop();
		
		isOnJunction = false;
	}

}
