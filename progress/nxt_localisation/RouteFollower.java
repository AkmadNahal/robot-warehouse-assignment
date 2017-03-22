package nxt_localisation;

import java.io.PrintStream;
import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.addon.OpticalDistanceSensor;
import lejos.nxt.comm.RConsole;
import rp.config.WheeledRobotConfiguration;
import rp.util.Rate;

public class RouteFollower extends AbstractBehaviour {

	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	private final OpticalDistanceSensor irSensor;

	private float minRange;
	private float maxRange;
	private float rangeDiff;

	private float minValue;
	private float maxValue;
	private float valDiff;

	private float P;

	private Rate r = new Rate(20);

	private boolean isRouteComplete = false;
	private Direction move;
	
	private RobotLocationSessionManager locationManager;

	public RouteFollower(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor,
			SensorPort _irSensor, Direction move, RobotLocationSessionManager locationManager) {
		super(_config);

		this.lhSensor = new LightSensor(_lhSensor);
		this.rhSensor = new LightSensor(_rhSensor);
		this.irSensor = new OpticalDistanceSensor(_irSensor);

		this.move = move;

		minRange = 0;
		maxRange = 100;
		rangeDiff = maxRange - minRange;

		minValue = -200;
		maxValue = 200;
		valDiff = maxValue - minValue;

		P = 1f;
		
		this.locationManager = locationManager;

	}

	@Override
	public boolean takeControl() {
		return !isRouteComplete;
	}

	@Override
	public void action() {

			while (!isSuppressed) {
				
				if(irSensor.getDistance() > 20){



					float rHValue = rhSensor.getLightValue();
					float lHValue = lhSensor.getLightValue();

					float rHRatio = (rHValue - minRange) / rangeDiff;
					float lHRatio = (lHValue - minRange) / rangeDiff;

					float rHOutput = minValue + (valDiff * rHRatio);
					float lHOutput = minValue + (valDiff * lHRatio);

					float turnDiff = (rHOutput - lHOutput) + 8;

					float turnOut = P * turnDiff;

					pilot.steer(turnOut);
					r.sleep();
					locationManager.setCorrectlyExecuted(true);

				}
				else{
					locationManager.setCorrectlyExecuted(false);
					isRouteComplete = true;
				}
			}
				isSuppressed = false;
				isRouteComplete = true;
	}

	protected static void redirectOutput(boolean _useBluetooth) {
		if (!RConsole.isOpen()) {
			if (_useBluetooth) {
				RConsole.openBluetooth(0);
			} else {
				RConsole.openUSB(0);
			}
		}
		PrintStream ps = RConsole.getPrintStream();
		System.setOut(ps);
		System.setErr(ps);
	}

}
