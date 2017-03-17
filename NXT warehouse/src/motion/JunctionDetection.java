package motion;

import java.io.PrintStream;
import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import rp.config.WheeledRobotConfiguration;
import rp.util.Rate;
import utils.Direction;
import utils.LocationType;
import utils.SuperLocation;

public class JunctionDetection extends AbstractBehaviour {

	private final LightSensor lhSensor;
	private final LightSensor rhSensor;

	boolean isOnJunction = true;
	private ArrayList<Direction> route;
	private int counter = -1;

	private RobotLocationSessionManager locationManager;

	private int calibratedValue;
	private int error = 6;
	
	private Rate r = new Rate(20);

	public JunctionDetection(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor,
			ArrayList<Direction> route, RobotLocationSessionManager _locationManager, int calValue) {
		super(_config);

		lhSensor = new LightSensor(_lhSensor);
		rhSensor = new LightSensor(_rhSensor);

		this.route = new ArrayList<Direction>(route);
		this.locationManager = _locationManager;
		this.calibratedValue = calValue;
		
	}

	@Override
	public boolean takeControl() {

		int valueRight = rhSensor.readValue();
		int valueLeft = lhSensor.readValue();

		if ((valueRight - calibratedValue < error) && (valueLeft - calibratedValue < error )) {
			isOnJunction = true;
		}

		return isOnJunction;
	}

	@Override
	public void action() {
		counter++;
		if (counter != 0) {
			pilot.travel(0.05);
		}
		pilot.stop();
		Sound.buzz();



		if (counter == route.size()) {
			Direction previousMove = route.get(counter - 1);

			if (previousMove == Direction.BACKWARDS) {
				pilot.rotate(180);
				pilot.stop();
			} else if (previousMove == Direction.LEFT) {
				pilot.rotate(-90);
				pilot.stop();
			} else if (previousMove == Direction.RIGHT) {
				pilot.rotate(90);
				pilot.stop();
			}
		} else {
			Direction currentMove = route.get(counter);
			Direction previousMove = null;

			// Send next move to the PC
			locationManager.setNextMove(currentMove);
			locationManager.setShouldSendNextMove(true);

			if (counter > 0) {
				previousMove = route.get(counter - 1);

				if (currentMove != previousMove) {
					if (previousMove == Direction.BACKWARDS) {
						pilot.rotate(180);
						pilot.stop();
					} else if (previousMove == Direction.LEFT) {
						pilot.rotate(-90);
						pilot.stop();
					} else if (previousMove == Direction.RIGHT) {
						pilot.rotate(90);
						pilot.stop();
					}
				}
			}

			if (previousMove != null) {
				if (previousMove != currentMove) {
					if (currentMove == Direction.BACKWARDS) {
						pilot.rotate(180);
						pilot.stop();
					} else if (currentMove == Direction.LEFT) {
						pilot.rotate(90);
						pilot.stop();
					} else if (currentMove == Direction.RIGHT) {
						pilot.rotate(-90);
						pilot.stop();
					}
				}
			} else {
				if (currentMove == Direction.BACKWARDS) {
					pilot.rotate(180);
					pilot.stop();
				} else if (currentMove == Direction.LEFT) {
					pilot.rotate(90);
					pilot.stop();
				} else if (currentMove == Direction.RIGHT) {
					pilot.rotate(-90);
					pilot.stop();
				}
			}
		}
		
		r.sleep(); //remove if buggy!
		isOnJunction = false;
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
