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
import lejos.robotics.RangeFinder;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import motion_control.JunctionDetection;
import motion_control.RouteFollower;
import route_planning.RoutePlanner;
import rp.robotics.MobileRobotWrapper;
import rp.robotics.mapping.GridMap;
import rp.robotics.mapping.MapUtils;
import rp.robotics.navigation.GridPose;
import rp.robotics.navigation.Heading;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.simulation.MovableRobot;
import rp.robotics.simulation.SimulatedRobots;
import utils.Config;
import utils.Direction;
import utils.Location;
import utils.LocationType;
import utils.SuperLocation;
import warehouse_interface.GridWalker;
import warehouse_interface.WarehouseController;
import warehouse_interface.WarehouseView;

public class SystemControl {
	
	private static final int ROBOT_COUNT = 1;
	private static int maxMapSize = 10;
	private static Location[][] map = new Location[maxMapSize + 1][maxMapSize + 1];

	public static void main(String[] args) {
		
	    SuperLocation locationAccess = new SuperLocation(new Location(0, 0, LocationType.EMPTY)); //start location
		GridMap mapModel = MapUtils.createRealWarehouse();
		MapBasedSimulation sim = new MapBasedSimulation(mapModel);
		for (int i = 0; i < ROBOT_COUNT; i++) {
			GridPose gridStart = new GridPose(0, 0, Heading.PLUS_Y);
			MobileRobotWrapper<MovableRobot> wrapper = sim.addRobot(
					SimulatedRobots.makeConfiguration(false, true),
					mapModel.toPose(gridStart));
			RangeFinder ranger = sim.getRanger(wrapper);
			GridWalker controller = new GridWalker(wrapper.getRobot(),
					mapModel, gridStart, ranger, locationAccess);
			new Thread(controller).start();
		}
		
		WarehouseController control = new WarehouseController(mapModel, sim);
		WarehouseView view = new WarehouseView(ROBOT_COUNT);
		control.registerView(view);
		
		Config config = new Config();
		
		createMap();

	    map[1][1] = new Location(1,1,LocationType.BLOCK);
	    map[1][2] = new Location(1,2,LocationType.BLOCK);
	    map[2][1] = new Location(2,1,LocationType.BLOCK);
	    
	    RoutePlanner planner = new RoutePlanner(map,maxMapSize,maxMapSize);
	    
	    String jfile = "job_selection/jobs.csv";
		String wrfile = "job_selection/items.csv";
		String lfile = "job_selection/locations.csv";
		HashMap<String, Item> itemMap = ItemReader.parseItems(wrfile, lfile);
		HashMap<String, Job> jobMap = JobReader.parseJobs(jfile, itemMap);
		
		ArrayList<Job> jobs = new ArrayList<Job>(jobMap.values());
		Collections.sort(jobs);
		
		final float W_LIMIT = 50f;
		ArrayList<Round> rounds = new ArrayList<Round>();
		Round currentRound = new Round(W_LIMIT);
		
		for (Job j : jobs) {
			HashMap<String, Integer> picks = j.getPicks();
			for (String s : picks.keySet()) {
				if (!(currentRound.addStop(itemMap.get(s), j.getPicks().get(s)))) {
					rounds.add(currentRound);
					currentRound = new Round(W_LIMIT);
					currentRound.addStop(itemMap.get(s), j.getPicks().get(s));
				}
			}
		}
		rounds.add(currentRound);
		
		for (Round r : rounds) { //check this with Jerry!
			ArrayList<Location> locationsInJob = r.getRoute();
			for (int i = 0; i < locationsInJob.size(); i++){
				Location nextGoal = locationsInJob.get(i);
				ArrayList<Direction> solution = planner.getRoute(locationAccess.getCurrentLocation(), nextGoal);
				Behavior movement = new RouteFollower(config.getConfig(), config.getLeftSensorPort(), config.getRightSensorPort(), solution.size());
				Behavior junction = new JunctionDetection(config.getConfig(), config.getLeftSensorPort(), config.getRightSensorPort(), solution, locationAccess);
				Arbitrator arby = new Arbitrator(new Behavior[] {movement, junction}, true); //needs to send whole list to robot
				arby.start();
					
				System.out.println("Arbitrator stopped - Route complete");
				Sound.beepSequence(); //robot-interface stuff here, to restart the loop!
				
				
			}	
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
