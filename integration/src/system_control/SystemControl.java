package system_control;

import java.util.ArrayList;

import helper_classes.Config;
import helper_classes.Direction;
import helper_classes.Location;
import helper_classes.LocationType;
import helper_classes.SuperLocation;
import lejos.nxt.Sound;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import motion_control.JunctionDetection;
import motion_control.RouteFollower;
import route_planning.RoutePlanner;

public class SystemControl {
	
	private static int maxMapSize = 10;
	private static Location[][] map = new Location[maxMapSize + 1][maxMapSize + 1];

	public static void main(String[] args) {
		
		Config config = new Config();
		
		createMap();

	    map[1][1] = new Location(1,1,LocationType.BLOCK);
	    map[1][2] = new Location(1,2,LocationType.BLOCK);
	    map[2][1] = new Location(2,1,LocationType.BLOCK);
	    
	    RoutePlanner planner = new RoutePlanner(map,maxMapSize,maxMapSize);
	    
	    SuperLocation locationAccess = new SuperLocation(new Location(0, 0, LocationType.EMPTY)); //start location
	    
	    //looping would begin below, takes a job, and cycles through the picks
	    
	    Location nextGoal = new Location(2, 3, LocationType.EMPTY); //this will be derived from the chosen job

	    ArrayList<Direction> solution = planner.getRoute(locationAccess.getCurrentLocation(), nextGoal);
	   
	    Behavior movement = new RouteFollower(config.getConfig(), config.getLeftSensorPort(), config.getRightSensorPort(), solution);
		Behavior junction = new JunctionDetection(config.getConfig(), config.getLeftSensorPort(), config.getRightSensorPort());
		Arbitrator arby = new Arbitrator(new Behavior[] {movement, junction}, true);
		arby.start();
		
		System.out.println("Arbitrator stopped - Route complete");
		Sound.beepSequence();
	}
	
	private static void createMap() {
	    for(int i=0;i<=maxMapSize;i++) {
	      for(int j=0;j<=maxMapSize;j++) {
	        map[i][j] = new Location(i,j,LocationType.EMPTY);
	      }
	    }
	  }

}
