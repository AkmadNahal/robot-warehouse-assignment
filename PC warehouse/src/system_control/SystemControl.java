package system_control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import job_selection.Item;
import job_selection.ItemReader;
import job_selection.Job;
import job_selection.JobReader;
import job_selection.Round;
import lejos.nxt.Sound;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import motion_control.JunctionDetection;
import motion_control.RouteFollower;
import route_planning.RoutePlanner;
import helper_classes.Config;
import utils.Direction;
import utils.Location;
import utils.LocationType;
import utils.SuperLocation;

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
	    
	    String jfile = "job_selection/jobs.csv";
		String wrfile = "job_selection/items.csv";
		String lfile = "job_selection/locations.csv";
		HashMap<String, Item> itemMap = ItemReader.parseItems(wrfile, lfile);
		HashMap<String, Job> jobMap = JobReader.parseJobs(jfile, itemMap);
		
		ArrayList<Job> jobs = new ArrayList<Job>(jobMap.values());
		Collections.sort(jobs);
		
		ArrayList<Round> rounds = new ArrayList<Round>();
		Round currentRound = new Round(50f);
		for (Job j : jobs) {
			HashMap<String, Integer> picks = j.getPicks();
			for (String i : picks.keySet()) {
				if (currentRound.addStop(itemMap.get(i), j.getPicks().get(i))) {
				} else {
					currentRound = new Round(50f);
				}
			}
			rounds.add(currentRound);
		}
		
		for (Round r : rounds) {
			Location nextGoal = itemMap.get(r).getLoc();
			ArrayList<Direction> solution = planner.getRoute(locationAccess.getCurrentLocation(), nextGoal);
			Behavior movement = new RouteFollower(config.getConfig(), config.getLeftSensorPort(), config.getRightSensorPort(), solution.size());
			Behavior junction = new JunctionDetection(config.getConfig(), config.getLeftSensorPort(), config.getRightSensorPort(), solution);
			Arbitrator arby = new Arbitrator(new Behavior[] {movement, junction}, true); //send whole list to robot
			arby.start();
				
			System.out.println("Arbitrator stopped - Route complete");
			Sound.beepSequence(); //robot-interface stuff here, to restart the loop!
		}
	}
	
	private static void createMap() {
	    for(int i=0;i<=maxMapSize;i++) {
	      for(int j=0;j<=maxMapSize;j++) {
	        map[i][j] = new Location(i,j,LocationType.EMPTY);
	      }
	    }
	  }
}
