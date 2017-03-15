package utils;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import rp.config.RobotConfigs;
import rp.config.WheeledRobotConfiguration;

public class Config {
	
	private WheeledRobotConfiguration config;
	private final SensorPort lhSensorPort;
	private final SensorPort rhSensorPort;
	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	private SensorPort dsSensorPort;
	private UltrasonicSensor dsSensor;
	
	public Config(){
		this.config = RobotConfigs.CASTOR_BOT;
		this.lhSensorPort = SensorPort.S4;
		this.rhSensorPort = SensorPort.S1;
		this.dsSensorPort = SensorPort.S2;
		this.lhSensor = new LightSensor(this.lhSensorPort);
		this.rhSensor = new LightSensor(this.lhSensorPort);
		this.dsSensor = new UltrasonicSensor(this.dsSensorPort);
	}
	
	public WheeledRobotConfiguration getConfig(){
		return config;
	}
	
	public SensorPort getLeftSensorPort(){
		return this.lhSensorPort;
	}
	
	public SensorPort getRightSensorPort(){
		return this.rhSensorPort;
	}
	
	public SensorPort getDsSensorPort(){
		return this.dsSensorPort;
	}
	
	public LightSensor getLeftSensor(){
		return this.lhSensor;
	}
	
	public LightSensor getRightSensor(){
		return this.rhSensor;
	}
}
