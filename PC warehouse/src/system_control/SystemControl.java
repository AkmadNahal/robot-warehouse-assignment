package system_control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.log4j.Logger;

import job_selection.Item;
import job_selection.ItemReader;
import job_selection.Job;
import job_selection.JobReader;
import job_selection.Round;
import job_selection.RoundCreator;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import route_planning.RoutePlanner;
import route_planning.TSP;
import rp.robotics.mapping.MapUtils;
import utils.Location;
import utils.LocationType;
import utils.SuperLocation;
import warehouse_interface.GridWalker;


public class SystemControl {

	private static final Logger logger = Logger.getLogger(SystemControl.class);
	
	public static void main(String[] args) {

		SuperLocation locationAccess1 = new SuperLocation(new Location(0, 0, LocationType.EMPTY)); //start location, ROBOT 1
		SuperLocation locationAccess2 = new SuperLocation(new Location(6, 0, LocationType.EMPTY)); //start location, ROBOT 2
		SuperLocation locationAccess3 = new SuperLocation(new Location(11, 0, LocationType.EMPTY)); //start location, ROBOT 3
		
		PCSessionManager sessionManager1 = new PCSessionManager(locationAccess1);
		ChangeNotifier notifier1 = new ChangeNotifier();
		
		PCSessionManager sessionManager2 = new PCSessionManager(locationAccess2);
		ChangeNotifier notifier2 = new ChangeNotifier();
		
		PCSessionManager sessionManager3 = new PCSessionManager(locationAccess3);
		ChangeNotifier notifier3 = new ChangeNotifier();
		
		ArrayList<Round> rounds = orderJobs(sessionManager1);
		
		GridWalkerManager gridWalkerManager = new GridWalkerManager(MapUtils.createRealWarehouse());
		gridWalkerManager.setup();
		GridWalker gridWalker1 = gridWalkerManager.startGridWalker(sessionManager1);
		GridWalker gridWalker2 = gridWalkerManager.startGridWalker(sessionManager2);
		GridWalker gridWalker3 = gridWalkerManager.startGridWalker(sessionManager3);
		gridWalkerManager.controllerAndView(gridWalker1, gridWalker2, gridWalker3, sessionManager1, sessionManager2, sessionManager3, rounds);
		Location[][] map = gridWalkerManager.createMap();
		
		logger.debug("Successfully set up map");
		
		sessionManager1.setRobotName("Lil' Bob");
		sessionManager2.setRobotName("Lil' Vader");
		sessionManager3.setRobotName("Lil' Yoda");


		// Setup robot info and networking
		NXTInfo robot1Info = new NXTInfo (NXTCommFactory.BLUETOOTH, sessionManager1.getRobotName(),
			"0016531AF650");
		NXTInfo robot2Info = new NXTInfo (NXTCommFactory.BLUETOOTH, sessionManager2.getRobotName(),
				"00165308E541");
		NXTInfo robot3Info = new NXTInfo (NXTCommFactory.BLUETOOTH, sessionManager3.getRobotName(),
				"0016531B550D");
		
		//starts the PC sending/receiving thread
		NetworkComm robot1 = new NetworkComm(robot1Info, sessionManager1, notifier1);
		(new Thread(robot1)).start();
		
		logger.debug("Successfully connected to Lil' Bob");
		
		NetworkComm robot2 = new NetworkComm(robot2Info, sessionManager2, notifier2);
		(new Thread(robot2)).start();
		
		logger.debug("Successfully connected to Lil' Vader");
		
		NetworkComm robot3 = new NetworkComm(robot3Info, sessionManager3, notifier3);
		(new Thread(robot3)).start();
		
		logger.debug("Successfully connected to Lil' Yoda");
		
		// Initialise route planner
		RoutePlanner planner = new RoutePlanner(map,gridWalkerManager.getMapSizeX(),gridWalkerManager.getMapSizeY());
				
		RouteManager routeManager = new RouteManager(rounds, sessionManager1, sessionManager2, sessionManager3, planner,
				notifier1, notifier2, notifier3, robot1, robot2, robot3, 
				new TSP(map, gridWalkerManager.getMapSizeX(), gridWalkerManager.getMapSizeY()), gridWalkerManager.getController());
		
		(new Thread (routeManager)).start();
		
		logger.debug("Successfully ordered jobs, and distributed them");
	
	}
	
	public static ArrayList<Round> orderJobs(PCSessionManager sessionManager){
		// Read job files
		String jfile = "src/job_selection/jobs.csv";
		String wrfile = "src/job_selection/items.csv";
		String lfile = "src/job_selection/locations.csv";
		HashMap<String, Item> itemMap = ItemReader.parseItems(wrfile, lfile);
		HashMap<String, Job> jobMap = JobReader.parseJobs(jfile, itemMap);

		// Sort jobs into highest reward based array
		ArrayList<Job> jobs = new ArrayList<Job>(jobMap.values());
		Collections.sort(jobs);
		sessionManager.setJobs(jobs);

		// Splits the jobs by weights - no job over 50
		return RoundCreator.createRounds(50f, itemMap, jobs);
	}


}
