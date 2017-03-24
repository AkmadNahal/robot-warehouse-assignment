package motion;

import java.io.PrintStream;
import java.util.ArrayList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.addon.OpticalDistanceSensor;
import lejos.nxt.comm.RConsole;
import lejos.robotics.RangeFinder;
import localization.DataOfJunction;
import rp.config.WheeledRobotConfiguration;
import rp.util.Rate;
import utils.Direction;
import utils.LocationType;
import utils.SuperLocation;

public class JunctionDetection extends AbstractBehaviour {

	private final LightSensor lhSensor;
	private final LightSensor rhSensor;
	private final RangeFinder irSensor;

	boolean isOnJunction = true;
	private ArrayList<Direction> route;

	private RobotLocationSessionManager locationManager;
	
	private int calibratedValue;
	private int error = 8;
	
	private Rate r = new Rate(10);

	public JunctionDetection(WheeledRobotConfiguration _config, SensorPort _lhSensor, SensorPort _rhSensor,
			ArrayList<Direction> route, RobotLocationSessionManager _locationManager, int calValue, SensorPort _irSensor) {
		super(_config);

		lhSensor = new LightSensor(_lhSensor);
		rhSensor = new LightSensor(_rhSensor);
		
		irSensor = new OpticalDistanceSensor(_irSensor);

		this.route = new ArrayList<Direction>(route);
		this.locationManager = _locationManager;
		this.calibratedValue = calValue;
		
	}
	
	public void setRoute(ArrayList<Direction> val) {
		route = val;
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
		System.out.println(locationManager.getCounter() + ": COUNTER");
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



		if (locationManager.getCounter() == route.size()) {
			Direction previousMove = route.get(locationManager.getCounter() - 1);

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
			Direction currentMove = route.get(locationManager.getCounter());
			Direction previousMove = null;

			// Send next move to the PC
			locationManager.setNextMove(currentMove);
			locationManager.setShouldSendNextMove(true);

			if (locationManager.getCounter() > 0) {
				previousMove = route.get(locationManager.getCounter() - 1);

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
					} else if (currentMove == Direction.STOP){
						pilot.stop();
						locationManager.setCounter(route.size());
					} else if (currentMove == Direction.SPIN){
						pilot.stop();
						DataOfJunction dataOfJunction = new DataOfJunction();
						System.out.println(configurateParameter(irSensor.getRange()));
						addToData(configurateParameter(irSensor.getRange()), 0, dataOfJunction);
						pilot.rotate(90);
						pilot.stop();
						
						System.out.println(configurateParameter(irSensor.getRange()));
						addToData(configurateParameter(irSensor.getRange()), 1, dataOfJunction);
						pilot.rotate(90);
						pilot.stop();
						
						System.out.println(configurateParameter(irSensor.getRange()));
						addToData(configurateParameter(irSensor.getRange()), 2, dataOfJunction);
						pilot.rotate(90);
						pilot.stop();
						
						System.out.println(configurateParameter(irSensor.getRange()));
						addToData(configurateParameter(irSensor.getRange()), 3, dataOfJunction);
						pilot.rotate(90);
						pilot.stop();
						
						locationManager.setCounter(route.size());
							
						locationManager.setReadings(dataOfJunction);
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
				} else if (currentMove == Direction.STOP){
					pilot.stop();
					locationManager.setCounter(route.size());
				} else if(currentMove == Direction.SPIN){
					pilot.stop();
					DataOfJunction dataOfJunction = new DataOfJunction();
						
					System.out.println(irSensor.getRange());
					addToData(configurateParameter(average(irSensor,5)), 0, dataOfJunction);
					pilot.rotate(90);
					pilot.stop();
					
					System.out.println(irSensor.getRange());
					addToData(configurateParameter(average(irSensor,5)), 1, dataOfJunction);
					pilot.rotate(90);
					pilot.stop();
					
					System.out.println(irSensor.getRange());
					addToData(configurateParameter(average(irSensor,5)), 2, dataOfJunction);
					pilot.rotate(90);
					pilot.stop();
					
					System.out.println(irSensor.getRange());
					addToData(configurateParameter(average(irSensor,5)), 3, dataOfJunction);
					pilot.rotate(90);
					pilot.stop();
					
					locationManager.setCounter(route.size());
					
					locationManager.setReadings(dataOfJunction);
				}
			}
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

	private int configurateParameter(float f) {
		if (f>19) {
			return 0;
		}
		return 1;
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
	
	private float average(RangeFinder sensor, int samples) {
		float total = 0.0f;
		for(int i = 0; i < samples; i++) {
			total += sensor.getRange();
		}
		return total/samples;
	}

}
