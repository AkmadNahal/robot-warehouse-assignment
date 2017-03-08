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
	private ArrayList<Direction> route;
	private boolean shouldExecute = false;
	private boolean isExecuting = false;
	private boolean hitJunct;
	
	@Override
	public void run() {
		while (true) {
			if (getShouldExecute()) {
				Behavior movement = new RouteFollower(config.getConfig(), config.getLeftSensorPort(),
						config.getRightSensorPort(), route.size());
				Behavior junction = new JunctionDetection(config.getConfig(), config.getLeftSensorPort(),
						config.getRightSensorPort(), route, this);
				Arbitrator arby = new Arbitrator(new Behavior[] { movement, junction }, true); 
				arby.start(); //START THE ARBITRATOR
				System.out.println("Arbitrator stopped - Route complete");
				Sound.beepSequence();
				setShouldExecute(false);
			}
		}

	}

	public void setRoute(ArrayList<Direction> route) {
		this.route = route;
	}

	public synchronized void setShouldExecute(boolean shouldExecute) {
		this.shouldExecute = shouldExecute;
	}

	public synchronized boolean getShouldExecute() {
		return this.shouldExecute;
	}

	public synchronized void setIsExecuting(boolean isExecuting) {
		this.isExecuting = isExecuting;
	}

	public synchronized boolean getIsExecuting() {
		return this.isExecuting;
	}
	
	public synchronized void setHitJunct(boolean hitJunct) {
		this.hitJunct = hitJunct;
	}

	public synchronized boolean getHitJunct() {
		return this.hitJunct;
	}

}
