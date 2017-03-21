package nxt_localisation;

import java.io.PrintStream;
import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.addon.OpticalDistanceSensor;
import lejos.nxt.comm.RConsole;
import rp.config.WheeledRobotConfiguration;
import rp.util.HashMap;
import rp.util.Rate;


public class JunctionDetection extends AbstractBehaviour {

	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	private final OpticalDistanceSensor irSensor;

	boolean isOnJunction = true;
	private ArrayList<Direction> route;

	private RobotLocationSessionManager locationManager;
	
	private int calibratedValue;
	private int error = 8;
	
	private Rate r = new Rate(10);

	public JunctionDetection(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor, SensorPort _irSensor,
			ArrayList<Direction> route, RobotLocationSessionManager _locationManager, int calValue) {
		super(_config);

		lhSensor = new LightSensor(_lhSensor);
		rhSensor = new LightSensor(_rhSensor);
		
		irSensor = new OpticalDistanceSensor(_irSensor);

		this.route = new ArrayList<Direction>(route);
		this.locationManager = _locationManager;
		this.calibratedValue = calValue;
		
	}

	@Override
	public boolean takeControl() {
		
		int rightAvg = 0;
		int leftAvg = 0;
		
		for (int i = 0; i < 10; i++){
			int valueRight = rhSensor.readValue();
			int valueLeft = lhSensor.readValue();
			rightAvg += valueRight;
			leftAvg += valueLeft;
		}
		
		rightAvg = rightAvg/10;
		leftAvg = leftAvg/10;

		if ((rightAvg - calibratedValue < error) && (leftAvg - calibratedValue < error )) {
			isOnJunction = true;
		}

		return isOnJunction;
	}

	@Override
	public void action() {
		pilot.stop();
		locationManager.setCounter(locationManager.getCounter()+1);
		if (locationManager.getCounter() != 0) {
			
			int rightAvg = 0;
			int leftAvg = 0;
			
			for (int i = 0; i < 10; i++){
				int valueRight = rhSensor.readValue();
				int valueLeft = lhSensor.readValue();
				rightAvg += valueRight;
				leftAvg += valueLeft;
			}
			
			rightAvg = rightAvg/10;
			leftAvg = leftAvg/10;
			
			if((rightAvg - calibratedValue < error) && (leftAvg - calibratedValue < error )){
				System.out.println("TRUE");
				pilot.travel(0.05);
			}
			else{
				System.out.println("FALSE");
				locationManager.setCounter(locationManager.getCounter()-1);
				isOnJunction = false;
				return;
			}
		}
		pilot.stop();
		Sound.buzz();



		if (!(locationManager.getCounter() == route.size())) {
			Direction currentMove = route.get(locationManager.getCounter());

			if (currentMove == Direction.BACKWARDS) {
				pilot.rotate(180);
				pilot.stop();
			} else if (currentMove == Direction.LEFT) {
				pilot.rotate(-90);
				pilot.stop();
			} else if (currentMove == Direction.RIGHT) {
				pilot.rotate(90);
				pilot.stop();
			} else if (currentMove == Direction.STOP){
				pilot.stop();
				locationManager.setCounter(route.size());
			} else if (currentMove == Direction.SPIN){
				pilot.stop();
				HashMap<Integer, Integer> readings = new HashMap<Integer, Integer>();
				
				for(int i = 0; i < 4; i++){
					readings.put(i, irSensor.getDistance());
					pilot.rotate(90);
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
