package nxt_localisation;

import java.io.PrintStream;
import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import rp.config.WheeledRobotConfiguration;
import rp.util.Rate;

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
	private UltrasonicSensor dsSensor;
	private ArrayList<Direction> route;
	private boolean abortRoute = false;
	
	private RobotLocationSessionManager locationManager;

	public RouteFollower(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor,
			SensorPort _dsSensor, ArrayList<Direction> route, RobotLocationSessionManager locationManager) {
		super(_config);

		this.lhSensor = new LightSensor(_lhSensor);
		this.rhSensor = new LightSensor(_rhSensor);
		this.dsSensor = new UltrasonicSensor(_dsSensor);

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
				float distance = dsSensor.getRange();

				/*if (distance < 4) {
					System.out.println(pilot.getMovementIncrement());
					float reverse = pilot.getMovementIncrement();
					pilot.stop();
					pilot.travel(-reverse);
					pilot.stop();

					Direction move = route.get(counter);

					if (move == Direction.BACKWARDS) {
						pilot.rotate(180);
						pilot.stop();
					} else if (move == Direction.LEFT) {
						pilot.rotate(-90);
						pilot.stop();
					} else if (move == Direction.RIGHT) {
						pilot.rotate(90);
						pilot.stop();
					}

					Sound.buzz();
					System.out.println("abort");
					//counter = routeLength;
					abortRoute = true;
					break;
				}*/

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

			}
			if(locationManager.getCounter() == route.size()){
				isRouteComplete = true;
			}
			else{
				isSuppressed = false;
			}
		} else {
			isRouteComplete = true;
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
