package motion;

import java.io.PrintStream;
import java.util.ArrayList;

import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import utils.Config;
import utils.Direction;
import utils.Location;
import utils.LocationType;
import utils.SuperLocation;

public class RouteExecutor implements Runnable {
	
	private Config config = new Config();
	private SuperLocation locationAccess = new SuperLocation(new Location(0, 0, LocationType.EMPTY)); 
	private ArrayList<Direction> route;
	
	public RouteExecutor(ArrayList<Direction> _route) {
		route = _route;
	}

	@Override
	public void run() {
		redirectOutput(false);
		Behavior movement = new RouteFollower(config.getConfig(), config.getLeftSensorPort(), config.getRightSensorPort(), route.size());
		Behavior junction = new JunctionDetection(config.getConfig(), config.getLeftSensorPort(), config.getRightSensorPort(), route, locationAccess);
		Arbitrator arby = new Arbitrator(new Behavior[] {movement, junction}, true); //needs to send whole list to robot
		arby.start();
		System.out.println("Arbitrator stopped - Route complete");
		Sound.beepSequence(); //robot-interface stuff here, to restart the loop!
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
