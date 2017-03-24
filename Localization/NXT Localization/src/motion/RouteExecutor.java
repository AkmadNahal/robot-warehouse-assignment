package motion;

import java.io.PrintStream;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.util.Delay;
import utils.Config;

public class RouteExecutor implements Runnable {

	private Config config = new Config();
	private RobotMovementSessionManager movementManager;
	private RobotLocationSessionManager locationManager;
	private int calibratedValue;

	public RouteExecutor(Config _config, RobotMovementSessionManager _movementManager,
			RobotLocationSessionManager _locationManager, int calibratedValue) {
		config = _config;
		movementManager = _movementManager;
		locationManager = _locationManager;
		this.calibratedValue = calibratedValue;
	}

	@Override
	public void run() {
		while (true) {
			if(movementManager.getShouldExecuteRoute()) {
				locationManager.setCounter(-1);
				Behavior movement = new RouteFollower(config.getConfig(), config.getLeftSensorPort(),
						config.getRightSensorPort(), movementManager.getRoute(), locationManager);
				Behavior junction = new JunctionDetection(config.getConfig(), config.getLeftSensorPort(),
						config.getRightSensorPort(), movementManager.getRoute(), locationManager, calibratedValue, config.getDsSensorPort());
				Arbitrator arby = new Arbitrator(new Behavior[] { movement, junction }, true);
				arby.start(); //START THE ARBITRATOR
				Sound.beepSequence();
				locationManager.setCounter(-1);
				movementManager.setRoute(null);
				movementManager.setShouldExecuteRoute(false);
				movementManager.setIsRouteComplete(true);
				//movementManager.setIsAtPickupLocation(true);
			}
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
