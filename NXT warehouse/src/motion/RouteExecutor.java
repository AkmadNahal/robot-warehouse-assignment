package motion;

import java.io.PrintStream;

import lejos.nxt.Sound;
import lejos.nxt.comm.RConsole;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import utils.Config;

public class RouteExecutor implements Runnable {

	private Config config = new Config();
	private RobotMovementSessionManager movementManager;

	public RouteExecutor(Config _config, RobotMovementSessionManager _movementManager) {
		config = _config;
		movementManager = _movementManager;
	}

	@Override
	public void run() {
		while (true) {
			if(movementManager.getShouldExecuteRoute()) {
				Behavior movement = new RouteFollower(config.getConfig(), config.getLeftSensorPort(),
						config.getRightSensorPort(), movementManager.getRoute().size());
				Behavior junction = new JunctionDetection(config.getConfig(), config.getLeftSensorPort(),
						config.getRightSensorPort(), movementManager.getRoute());
				Arbitrator arby = new Arbitrator(new Behavior[] { movement, junction }, true);
				arby.start(); //START THE ARBITRATOR
				System.out.println("Arbitrator stopped - Route complete");
				Sound.beepSequence();
				movementManager.setRoute(null);
				movementManager.setShouldExecuteRoute(false);
				movementManager.setIsAtPickupLocation(true);
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
