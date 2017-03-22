package localization;

import java.util.ArrayList;

import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import utils.Config;
import utils.Direction;
import motion.RouteFollower;
import motion.JunctionDetection;
import motion.RobotLocationSessionManager;

public class LocalisationMovement {
	
	private RobotLocationSessionManager locManager;
	private Config config;
	private int calibratedValue;
	
	public LocalisationMovement(int calValue){
		this.locManager = new RobotLocationSessionManager();
		config = new Config();
		this.calibratedValue = calValue;
	}
	
	
	public boolean executeMove(Direction move){
		ArrayList<Direction> route = new ArrayList<Direction>();
		route.add(move);
		Behavior movement = new RouteFollower(config.getConfig(), config.getLeftSensorPort(),
				config.getRightSensorPort(), route, locManager, config.getDsSensorPort());
		Behavior junction = new JunctionDetection(config.getConfig(), config.getLeftSensorPort(),
				config.getRightSensorPort(), route, locManager, calibratedValue, config.getDsSensorPort());
		Arbitrator arby = new Arbitrator(new Behavior[] { movement, junction }, true);
		arby.start(); //START THE ARBITRATOR
		
		return locManager.getCorrectlyExecuted();
	}
	
	public DataOfJunction readSurroundings(){
		ArrayList<Direction> route = new ArrayList<Direction>();
		route.add(Direction.SPIN);
		Behavior movement = new RouteFollower(config.getConfig(), config.getLeftSensorPort(),
				config.getRightSensorPort(), route, locManager, config.getDsSensorPort());
		Behavior junction = new JunctionDetection(config.getConfig(), config.getLeftSensorPort(),
				config.getRightSensorPort(), route, locManager, calibratedValue, config.getDsSensorPort());
		Arbitrator arby = new Arbitrator(new Behavior[] { movement, junction }, true);
		arby.start(); //START THE ARBITRATOR
		
		
		return locManager.getReadings();
	}
}

