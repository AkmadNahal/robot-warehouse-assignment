package motion;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import rp.config.WheeledRobotConfiguration;
import rp.util.Rate;

public class Drive extends AbstractBehaviour2 {
	
	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	
	private float minRange;
	private float maxRange;
	private float rangeDiff;
	
	private float minValue;
	private float maxValue;
	private float valDiff;
	
	private float P;
	
	private Rate r;
	
	public Drive(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor){
		super(_config);
		this.lhSensor = new LightSensor(_lhSensor);
		this.rhSensor = new LightSensor(_rhSensor);
	}
	
	public void initialise(){
		r = new Rate(20);
		
		minRange = 0;
		maxRange = 100;
		rangeDiff = maxRange - minRange;
		
		minValue = -200;
		maxValue = 200;
		valDiff = maxValue - minValue;
		
		P = 1f;
	}
	
	public void followPath(){
		float rHValue = rhSensor.getLightValue();
		float lHValue = lhSensor.getLightValue();
		
		float rHRatio = (rHValue - minRange)/rangeDiff;
		float lHRatio = (lHValue - minRange)/rangeDiff;
		
		float rHOutput = minValue + (valDiff * rHRatio);
		float lHOutput = minValue + (valDiff * lHRatio);
		
		float turnDiff = (rHOutput - lHOutput) + 8;
		
		float turnOut = P * turnDiff;
		
		pilot.steer(turnOut);
		r.sleep();
	}
}
