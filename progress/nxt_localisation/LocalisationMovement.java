package nxt_localisation;

import java.util.ArrayList;

import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import rp.util.HashMap;

public class LocalisationMovement {
	
	private LocalisationManager locManager;
	
	public LocalisationMovement(){
		this.locManager = new LocalisationManager();
	}
	
	
	public boolean executeMove(Direction move){
		Behavior movement = new RouteFollower(config.getConfig(), config.getLeftSensorPort(),
				config.getRightSensorPort(), config.getDsSensorPort(), move, locManager);
		Behavior junction = new JunctionDetection(config.getConfig(), config.getLeftSensorPort(),
				config.getRightSensorPort(), move, locManager, calibratedValue);
		Arbitrator arby = new Arbitrator(new Behavior[] { movement, junction }, true);
		arby.start(); //START THE ARBITRATOR
		
		return locManager.getCorrectlyExecuted();
	}
	
	public DataOfJunction readSurroundings(){
		Behavior movement = new RouteFollower(config.getConfig(), config.getLeftSensorPort(),
				config.getRightSensorPort(), config.getDsSensorPort(), Direction.SPIN, locManager);
		Behavior junction = new JunctionDetection(config.getConfig(), config.getLeftSensorPort(),
				config.getRightSensorPort(), Direction.SPIN, locManager, calibratedValue);
		Arbitrator arby = new Arbitrator(new Behavior[] { movement, junction }, true);
		arby.start(); //START THE ARBITRATOR
		
		
		return locManager.getReadings();
	}
}

