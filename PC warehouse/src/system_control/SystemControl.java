package system_control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import job_selection.Item;
import job_selection.ItemReader;
import job_selection.Job;
import job_selection.JobReader;
import job_selection.Round;
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
	private static int mapSizeX;
	private static int mapSizeY;
	private static Location[][] map;
	private static GridMap mapModel;

	public static void main(String[] args) {

		// Setup the GUI and initializing the location
		SuperLocation locationAccess = new SuperLocation(new Location(0, 0, LocationType.EMPTY)); //start location
		mapModel = MapUtils.createRealWarehouse();
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

		// Setup robot networking
		NXTInfo robot1Info = new NXTInfo (NXTCommFactory.BLUETOOTH, "Lil' Bob",
			"0016531AF650");
		NetworkComm robot1 = new NetworkComm(robot1Info);
		
		(new Thread(robot1)).start();


		// Setup map
		createMap();


		// Init route planner
		RoutePlanner planner = new RoutePlanner(map,mapSizeX,mapSizeY);
		

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

		// Route planning
		// TODO: revision
		ArrayList<Direction> testRoute = new ArrayList<Direction>();
		testRoute = planner.getRoute(locationAccess.getCurrentLocation(), new Location(4,6, LocationType.EMPTY));
		robot1.send(testRoute);
		
		/*for (Round r : rounds) {
			ArrayList<Location> locationsInJob = r.getRoute();
			for (int i = 0; i < locationsInJob.size(); i++){
				Location nextGoal = locationsInJob.get(i);
				ArrayList<Direction> solution = planner.getRoute(locationAccess.getCurrentLocation(), nextGoal);
				robot1.send(solution);
				break;
				
			}
		}*/

	}

	private static void createMap() {
		mapSizeX = mapModel.getXSize();
		mapSizeY = mapModel.getYSize();
		map = new Location[mapSizeX+1][mapSizeY+1];
		for(int i=0;i < mapSizeX;i++) {
			for(int j=0;j < mapSizeY;j++) {
				if (mapModel.isValidGridPosition(i, j) && !mapModel.isObstructed(i, j)){
					map[i][j] = new Location(i, j, LocationType.EMPTY);
				} else if (mapModel.isValidGridPosition(i, j)){
					map[i][j] = new Location(i, j, LocationType.BLOCK);
				}
			}
	    }
	}

}
