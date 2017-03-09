package system_control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import job_selection.Item;
import job_selection.ItemReader;
import job_selection.Job;
import job_selection.JobReader;
import job_selection.Round;
import job_selection.RoundCreator;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import lejos.robotics.RangeFinder;
import route_planning.RoutePlanner;
import rp.robotics.MobileRobotWrapper;
import rp.robotics.mapping.GridMap;
import rp.robotics.mapping.MapUtils;
import rp.robotics.navigation.GridPose;
import rp.robotics.navigation.Heading;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.simulation.MovableRobot;
import rp.robotics.simulation.SimulatedRobots;
import utils.Direction;
import utils.Location;
import utils.LocationType;
import utils.SuperLocation;
import warehouse_interface.GridWalker;
import warehouse_interface.WarehouseController;
import warehouse_interface.WarehouseView;

public class SystemControl {


	public static void main(String[] args) {

		SuperLocation locationAccess = new SuperLocation(new Location(0, 0, LocationType.EMPTY)); //start location
		
		GridWalkerManager gridWalkerManager = new GridWalkerManager(MapUtils.createRealWarehouse(), locationAccess);
		gridWalkerManager.setup();
		Location[][] map = gridWalkerManager.createMap();
		
		PCSessionManager sessionManager = new PCSessionManager(locationAccess);
		ChangeNotifier notifier = new ChangeNotifier();

		// Setup robot info and networking
		NXTInfo robot1Info = new NXTInfo (NXTCommFactory.BLUETOOTH, "Lil' Bob",
			"0016531AF650");
		
		//starts the PC sending/receiving thread
		NetworkComm robot1 = new NetworkComm(robot1Info, sessionManager, notifier);
		(new Thread(robot1)).start();

		// Initialise route planner
		RoutePlanner planner = new RoutePlanner(map,gridWalkerManager.getMapSizeX(),gridWalkerManager.getMapSizeY());
		
		ArrayList<Round> rounds = orderJobs();
		
		RouteManager routeManager = new RouteManager(rounds, sessionManager, planner, notifier);
		
		(new Thread (routeManager)).start();
	
	}
	
	public static ArrayList<Round> orderJobs(){
		// Read job files
		String jfile = "src/job_selection/jobs.csv";
		String wrfile = "src/job_selection/items.csv";
		String lfile = "src/job_selection/locations.csv";
		HashMap<String, Item> itemMap = ItemReader.parseItems(wrfile, lfile);
		HashMap<String, Job> jobMap = JobReader.parseJobs(jfile, itemMap);

		// Sort jobs into highest reward based array
		ArrayList<Job> jobs = new ArrayList<Job>(jobMap.values());
		Collections.sort(jobs);

		// Splits the jobs by weights - no job over 50
		return RoundCreator.createRounds(50f, itemMap, jobs);
	}


}
