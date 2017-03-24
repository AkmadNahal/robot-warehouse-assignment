package motion;

import java.io.PrintStream;
import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.OpticalDistanceSensor;
import lejos.nxt.comm.RConsole;
import rp.config.WheeledRobotConfiguration;
import rp.util.Rate;
import utils.Direction;

public class RouteFollower extends AbstractBehaviour {

	private final LightSensor lhSensor;
	private final LightSensor rhSensor;

	private float minRange;
	private float maxRange;
	private float rangeDiff;

	private float minValue;
	private float maxValue;
	private float valDiff;

	private float P;

	private Rate r = new Rate(20);

	private boolean isRouteComplete = false;
	private OpticalDistanceSensor irSensor;
	private ArrayList<Direction> route;

	private RobotLocationSessionManager locationManager;

	public RouteFollower(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor,
			ArrayList<Direction> route, RobotLocationSessionManager locationManager) {
		super(_config);

		this.lhSensor = new LightSensor(_lhSensor);
		this.rhSensor = new LightSensor(_rhSensor);

		this.route = route;

		minRange = 0;
		maxRange = 100;
		rangeDiff = maxRange - minRange;

		minValue = -200;
		maxValue = 200;
		valDiff = maxValue - minValue;

		P = 1f;

		this.locationManager = locationManager;

	}

	public RouteFollower(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor,
			ArrayList<Direction> route, RobotLocationSessionManager locationManager, SensorPort _irSensor) {
		super(_config);

		this.lhSensor = new LightSensor(_lhSensor);
		this.rhSensor = new LightSensor(_rhSensor);
		this.irSensor = new OpticalDistanceSensor(_irSensor);

		this.route = route;

		minRange = 0;
		maxRange = 100;
		rangeDiff = maxRange - minRange;

		minValue = -200;
		maxValue = 200;
		valDiff = maxValue - minValue;

		P = 1f;

		this.locationManager = locationManager;

	}

	public void setRoute(ArrayList<Direction> val) {
		route = val;
	}

	@Override
	public boolean takeControl() {
		return !isRouteComplete;
	}

	@Override
	public void action() {

		if (!(locationManager.getCounter() == (route.size()))) {
			while (!isSuppressed) {

				// Ensures the robot will not collide into walls/ other robots
				// If the safe distance is breached, route following will stop

				if (irSensor.getRange() > 20f) {

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
				} else {

				}
			}
			if (locationManager.getCounter() == route.size()) {
				isRouteComplete = true;
				locationManager.setCorrectlyExecuted(true);
			} else {
				isSuppressed = false;
			}
		} else {
			isRouteComplete = true;
			locationManager.setCorrectlyExecuted(true);
		}
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
