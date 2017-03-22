package nxt_localisation;

import java.io.PrintStream;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.OpticalDistanceSensor;
import lejos.nxt.comm.RConsole;
import rp.config.WheeledRobotConfiguration;
import rp.util.Rate;


public class JunctionDetection extends AbstractBehaviour {

	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	private final OpticalDistanceSensor irSensor;

	boolean isOnJunction = true;
	private Direction move;

	private LocalisationManager locationManager;
	
	private int calibratedValue;
	private int error = 8;
	
	private Rate r = new Rate(10);

	public JunctionDetection(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor, SensorPort _irSensor,
			Direction move, LocalisationManager _locationManager, int calValue) {
		super(_config);

		lhSensor = new LightSensor(_lhSensor);
		rhSensor = new LightSensor(_rhSensor);
		
		irSensor = new OpticalDistanceSensor(_irSensor);

		this.move = move;
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
			pilot.travel(0.05);
		}
		else{
			locationManager.setCounter(locationManager.getCounter()-1);
			isOnJunction = false;
			return;
		}
		pilot.stop();


		if (move == Direction.BACKWARDS) {
			pilot.rotate(180);
			pilot.stop();
		} else if (move == Direction.LEFT) {
			pilot.rotate(-90);
			pilot.stop();
		} else if (move == Direction.RIGHT) {
			pilot.rotate(90);
			pilot.stop();
		} else if (move == Direction.STOP){
			pilot.stop();
		} else if (move == Direction.SPIN){
			pilot.stop();
			DataOfJunction dataOfJunction = new DataOfJunction();
				
			for(int i = 0; i < 4; i++){
				addToData(configurateParameter(irSensor.getDistance()), i, dataOfJunction);
				pilot.rotate(90);
				pilot.stop();
			}
				
			locationManager.setReadings(dataOfJunction);
			
		}
		
		r.sleep(); //remove if buggy!
		isOnJunction = false;
	}

	private void addToData(int distance, int i, DataOfJunction dataOfJunction) {
		switch(i){
			case(0):
				dataOfJunction.setyPlus(distance);
				break;
			case(1):
				dataOfJunction.setxPlus(distance);
				break;
			case(2):
				dataOfJunction.setyMinus(distance);
				break;
			case(3):
				dataOfJunction.setxMinus(distance);
				break;
			}
				
	}

	private int configurateParameter(int distance) {
		if (distance<15) {
			return 0;
		}
		if (distance<40 && distance>25) {
			return 1;
		}
		if (distance<70 && distance>55) {
			return 2;
		}
		return 3;
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
