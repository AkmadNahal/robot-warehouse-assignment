package system_control;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import job_selection.Item;
import job_selection.Pick;
import job_selection.Round;
import route_planning.RoutePlanner;
import route_planning.TSP;
import utils.Direction;
import utils.Location;
import utils.LocationType;
import warehouse_interface.WarehouseController;

public class RouteManager implements Runnable {
	
	private PCSessionManager sessionManager1;
	private PCSessionManager sessionManager2;
	private PCSessionManager sessionManager3;
	private ChangeNotifier notifier1;
	private ChangeNotifier notifier2;
	private ChangeNotifier notifier3;
	private RoutePlanner planner;
	private ArrayList<Round> sortedJobs;
	private NetworkComm networkComm1;
	private NetworkComm networkComm2;
	private NetworkComm networkComm3;
	private TSP tsp;
	private WarehouseController warehouseController;
	
	private static final Logger logger = Logger.getLogger(RouteManager.class);
	
	public RouteManager(ArrayList<Round> sortedJobs, PCSessionManager sessionManager1, PCSessionManager sessionManager2,
			PCSessionManager sessionManager3, RoutePlanner planner, ChangeNotifier _notifier1, ChangeNotifier _notifier2, ChangeNotifier _notifier3,
			NetworkComm networkComm1, NetworkComm networkComm2, NetworkComm networkComm3, TSP tsp, WarehouseController warehouseController){
		this.sortedJobs = sortedJobs;
		this.sessionManager1 = sessionManager1;
		this.sessionManager2 = sessionManager2;
		this.sessionManager3 = sessionManager3;
		this.planner = planner;
		this.notifier1 = _notifier1;
		this.notifier2 = _notifier2;
		this.notifier3 = _notifier3;
		this.networkComm1 = networkComm1;
		this.networkComm2 = networkComm2;
		this.networkComm3 = networkComm3;
		this.tsp = tsp;
		this.warehouseController = warehouseController;
	}
	
	@Override
	public void run() {
		for (int n = 0; n < sortedJobs.size(); n+=3) {
			Round robot1CurrentRound = sortedJobs.get(n);
			sessionManager1.setCurrentRound(robot1CurrentRound);
			Round robot2CurrentRound = sortedJobs.get(n+1);
			sessionManager2.setCurrentRound(robot2CurrentRound);
			Round robot3CurrentRound = sortedJobs.get(n+2);
			sessionManager3.setCurrentRound(robot3CurrentRound);
			
			sessionManager1.setCurrentWeight(0f);
			sessionManager2.setCurrentWeight(0f);
			sessionManager3.setCurrentWeight(0f);
			
			sortedJobs.get(n).initialiseCounts();
			ArrayList<Integer> counts1 = sortedJobs.get(n).getCounts();
			sortedJobs.get(n+1).initialiseCounts();
			ArrayList<Integer> counts2 = sortedJobs.get(n+1).getCounts();
			sortedJobs.get(n+2).initialiseCounts();
			ArrayList<Integer> counts3 = sortedJobs.get(n+2).getCounts();
			
			ArrayList<Pick> robot1Picks = robot1CurrentRound.getRound();
			ArrayList<Pick> robot2Picks = robot2CurrentRound.getRound();
			ArrayList<Pick> robot3Picks = robot3CurrentRound.getRound();
			
			for (int i = 0; i < robot1Picks.size(); i++){
				robot1Picks.get(i).setCount(counts1.get(i));
			}
			for (int i = 0; i < robot2Picks.size(); i++){
				robot2Picks.get(i).setCount(counts2.get(i));
			}
			for (int i = 0; i < robot3Picks.size(); i++){
				robot3Picks.get(i).setCount(counts3.get(i));
			}
			
			
			ArrayList<Location> locationsInJob1 = robot1CurrentRound.getRoute();
			logger.debug(locationsInJob1.size() + ": Size r1");
			logger.debug(robot1CurrentRound.getRoute());
			ArrayList<Location> locationsInJob2 = robot2CurrentRound.getRoute();
			logger.debug(locationsInJob2.size() + ": Size r2");
			logger.debug(robot2CurrentRound.getRoute());
			ArrayList<Location> locationsInJob3 = robot3CurrentRound.getRoute();
			logger.debug(locationsInJob3.size() + ": Size r3");
			logger.debug(robot3CurrentRound.getRoute());
			
			locationsInJob1.add(0, sessionManager1.getLocationAccess().getCurrentLocation());
			locationsInJob2.add(0, sessionManager2.getLocationAccess().getCurrentLocation());
			locationsInJob3.add(0, sessionManager3.getLocationAccess().getCurrentLocation());

			// TSP
			tsp.simulateAnnealing(locationsInJob1);
			tsp.simulateAnnealing(locationsInJob2);
			tsp.simulateAnnealing(locationsInJob3);
			logger.debug("Finished TSP setup");
			
			// Fix collision issues in pickup
			Location.jamFixer(locationsInJob1, locationsInJob2, locationsInJob3);
			logger.debug("Fixed jams");
			
			// Reorder pick arrays according TSP location array
			robot1Picks = Round.reorderAccordingTSP(locationsInJob1, robot1Picks);
			robot2Picks = Round.reorderAccordingTSP(locationsInJob2, robot2Picks);
			robot3Picks = Round.reorderAccordingTSP(locationsInJob3, robot3Picks);
			logger.debug("Re-ordered picks, according to TSP");
			
			Item lastActualItem1 = robot1Picks.get(robot1Picks.size()-1).getItem();
			Item lastActualItem2 = robot2Picks.get(robot2Picks.size()-1).getItem();
			Item lastActualItem3 = robot3Picks.get(robot3Picks.size()-1).getItem();
			ArrayList<Pick> highestNumOfPicks = new ArrayList<Pick>();
			
			if (robot1Picks.size() >= robot2Picks.size() && robot1Picks.size() >= robot2Picks.size()){
				highestNumOfPicks = robot1Picks;
			}else if (robot2Picks.size() >= robot1Picks.size() && robot2Picks.size() >= robot3Picks.size()){
				highestNumOfPicks = robot2Picks;
			}else if (robot3Picks.size() >= robot1Picks.size() && robot3Picks.size() >= robot2Picks.size()){
				highestNumOfPicks = robot3Picks;
			}
			
			while (robot1Picks.size() != highestNumOfPicks.size() || robot2Picks.size() != highestNumOfPicks .size()|| robot3Picks.size() != highestNumOfPicks.size()){
				if (robot1Picks.size() < highestNumOfPicks.size()){
					Pick pickToAdd = new Pick(lastActualItem1, 0);
					robot1Picks.add(pickToAdd);
				}
				if (robot2Picks.size() <  highestNumOfPicks.size()){
					Pick pickToAdd = new Pick(lastActualItem2, 0);
					robot2Picks.add(pickToAdd);
				}
				if (robot3Picks.size() <  highestNumOfPicks.size()){
					Pick pickToAdd = new Pick(lastActualItem3, 0);
					robot3Picks.add(pickToAdd);
				}
			}
			Item dropOff1 = new Item("", 0f, 0f, new Location(4,7, LocationType.EMPTY));
			robot1Picks.add(new Pick(dropOff1, -1));
			
			Item dropOff2 = new Item("", 0f, 0f, new Location(7,7, LocationType.EMPTY));
			robot2Picks.add(new Pick(dropOff2, -1));
			
			Item dropOff3 = new Item("", 0f, 0f, new Location(10,7, LocationType.EMPTY));
			robot2Picks.add(new Pick(dropOff3, -1));
			
			logger.debug("Normalises size of picks list");
			
			Location currentLocation1 = sessionManager1.getLocationAccess().getCurrentLocation();
			Location currentLocation2 = sessionManager2.getLocationAccess().getCurrentLocation();
			Location currentLocation3 = sessionManager3.getLocationAccess().getCurrentLocation();
			
			int counter = 0;
			
			for (int i = 0; i < robot1Picks.size(); i++){
				System.out.print(robot1Picks.get(i).getCount());
			}
			System.out.println("");
			for (int i = 0; i < robot2Picks.size(); i++){
				System.out.print(robot2Picks.get(i).getCount());
			}
			System.out.println("");
			for (int i = 0; i < robot3Picks.size(); i++){
				System.out.print(robot3Picks.get(i).getCount());
			}
			System.out.println("");
			
			logger.debug(locationsInJob1.size() + ": R1 round size");
			logger.debug(locationsInJob2.size() + ": R2 round size");
			logger.debug(locationsInJob3.size() + ": R3 round size");

			logger.debug("Started next job");
			
			while(counter < robot1Picks.size() && counter < robot2Picks.size() && counter < robot3Picks.size()) {
				logger.debug("Doing pick " + counter);
				Location target1 = robot1Picks.get(counter).getItem().getLoc();
				sessionManager1.setPickLocation(target1);
				Location target2 = robot2Picks.get(counter).getItem().getLoc();
				sessionManager2.setPickLocation(target2);
				Location target3 = robot3Picks.get(counter).getItem().getLoc();
				sessionManager3.setPickLocation(target3);
				
				logger.debug("Robot 1 Target: " + target1);
				logger.debug("Robot 2 Target: " + target2);
				logger.debug("Robot 3 Target: " + target3);

				int numOfPick1 = robot1Picks.get(counter).getCount();
				int numOfPick2 = robot2Picks.get(counter).getCount();
				int numOfPick3 = robot3Picks.get(counter).getCount();
				
				System.out.println(numOfPick1);
				System.out.println(numOfPick2);
				System.out.println(numOfPick3);
				
				while(!currentLocation1.equalsTo(target1) || !currentLocation2.equalsTo(target2) || !currentLocation3.equalsTo(target3)){
					sessionManager1.setIsRouteComplete(false);
					sessionManager2.setIsRouteComplete(false);
					sessionManager3.setIsRouteComplete(false);
					
					HashMap<String, ArrayList<Location>> route = planner.getMultiRobotRoute(currentLocation1, target1, currentLocation2, target2, currentLocation3, target3);
					ArrayList<Direction> route1 = planner.coordinatesToDirections(route.get("robot1"));
					ArrayList<Direction> route2 = planner.coordinatesToDirections(route.get("robot2"));
					ArrayList<Direction> route3 = planner.coordinatesToDirections(route.get("robot3"));
					
					sessionManager1.setRoute(route1);
					sessionManager2.setRoute(route2);
					sessionManager3.setRoute(route3);
					
					sessionManager1.setNumOfPicks(numOfPick1);
					sessionManager2.setNumOfPicks(numOfPick2);
					sessionManager3.setNumOfPicks(numOfPick3);
					
					warehouseController.setTasks(sessionManager1);
					warehouseController.setTasks(sessionManager2);
					warehouseController.setTasks(sessionManager3);
					
					sessionManager1.setShouldSend(true);
					sessionManager2.setShouldSend(true);
					sessionManager3.setShouldSend(true);
										
					logger.debug(route1 + ": Route 1");
					logger.debug(route2 + ": Route 2");
					logger.debug(route2 + ": Route 3");
					
					logger.debug("Waiting for route to complete");
					while(!sessionManager1.getIsRouteComplete() || !sessionManager2.getIsRouteComplete() || !sessionManager3.getIsRouteComplete()) {
						if(notifier1.getChanged()) {
							logger.debug("Notifier 1 changed");
							currentLocation1 = sessionManager1.getLocationAccess().getCurrentLocation();
							logger.debug(currentLocation1 + ": Current Location - Robot 1");
							sessionManager1.setIsRouteComplete(true);
							notifier1.setChanged(false);
						}
						if(notifier2.getChanged()) {
							logger.debug("Notifier 2 changed");
							currentLocation2 = sessionManager2.getLocationAccess().getCurrentLocation();
							logger.debug(currentLocation2 + ": Current Location - Robot 2");
							sessionManager2.setIsRouteComplete(true);
							notifier2.setChanged(false);
						}
						if(notifier3.getChanged()) {
							logger.debug("Notifier 3 changed");
							currentLocation3 = sessionManager3.getLocationAccess().getCurrentLocation();
							logger.debug(currentLocation3 + ": Current Location - Robot 3");
							sessionManager3.setIsRouteComplete(true);
							notifier3.setChanged(false);
						}
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
				
				logger.debug("At pickup point");
				
				counter++;
				
				sessionManager1.setIsRouteComplete(false);
				sessionManager2.setIsRouteComplete(false);
				sessionManager3.setIsRouteComplete(false);
				
				notifier1.setChanged(false);
				notifier2.setChanged(false);
				notifier3.setChanged(false);
				
				networkComm1.sendAtPickup();
				networkComm2.sendAtPickup();
				networkComm3.sendAtPickup();
				
				sessionManager1.setCurrentWeight(sessionManager1.getCurrentWeight() + numOfPick1 * robot1Picks.get(counter).getItem().getWeight());
				sessionManager2.setCurrentWeight(sessionManager2.getCurrentWeight() + numOfPick2 * robot2Picks.get(counter).getItem().getWeight());
				sessionManager3.setCurrentWeight(sessionManager3.getCurrentWeight() + numOfPick3 * robot3Picks.get(counter).getItem().getWeight());
				
				while (!sessionManager1.getIsRouteComplete() || !sessionManager2.getIsRouteComplete() || !sessionManager3.getIsRouteComplete()){
					if (notifier1.getChanged()){
						logger.debug("Finished picking for robot 1");
						sessionManager1.setIsRouteComplete(true);
						notifier1.setChanged(false);
					}
					if (notifier2.getChanged()){
						logger.debug("Finished picking for robot 2");
						sessionManager2.setIsRouteComplete(true);
						notifier2.setChanged(false);
					}
					if (notifier3.getChanged()){
						logger.debug("Finished picking for robot 3");
						sessionManager3.setIsRouteComplete(true);
						notifier3.setChanged(false);
					}
				}
				
				logger.debug("Finished executing route, onto next one!");
				
				//sessionManager1.getLocationAccess().setCurrentLocation(target1);
				//sessionManager2.getLocationAccess().setCurrentLocation(target2);
				//sessionManager3.getLocationAccess().setCurrentLocation(target3);
				
			}
				/*int numOfPicks = r.getCounts().get(i);
				sessionManager.setNumOfPicks(numOfPicks);
				sessionManager.setRoute(solution);
				sessionManager.setShouldSend(true);
				sessionManager.setIsRouteComplete(false);
				notifier.setChanged(false);
				logger.debug("Waiting for route to complete");
				while(!sessionManager.getIsRouteComplete()) {
					if(notifier.getChanged()) {
						logger.debug("Notifier changed");
						sessionManager.setIsRouteComplete(true);
					}
					
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				sessionManager.getLocationAccess().setCurrentLocation(nextGoal);
				logger.debug("Finished executing route");*/
				
		}
			logger.debug("Finished executing route");
	}
		
		/*ArrayList<Direction> testRoute = new ArrayList<Direction>();
		testRoute = planner.getRoute(sessionManager.getLocationAccess().getCurrentLocation(), new Location(1,0, LocationType.EMPTY));
		sessionManager.setNumOfPicks(4);
		sessionManager.setRoute(testRoute);
		sessionManager.setShouldSend(true);
		sessionManager.setIsRouteComplete(false);
		notifier.setChanged(false);
		System.out.println("Waiting for route to complete");
		//maybe replace sessionManager.getIsRouteComplete() with a local boolean??
		
		while(!sessionManager.getIsRouteComplete()) {
			if(notifier.getChanged()) {
				System.out.println("Notifier changed");
				sessionManager.setIsRouteComplete(true);
			}
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Route 1 complete, about to go to next one");
		
		ArrayList<Direction> testRoute2 = new ArrayList<Direction>();
		testRoute2 = planner.getRoute(new Location(1, 0, LocationType.EMPTY), new Location(0, 0, LocationType.EMPTY));
		sessionManager.setNumOfPicks(4);
		sessionManager.setRoute(testRoute2);
		sessionManager.setShouldSend(true);
		sessionManager.setIsRouteComplete(false);
		notifier.setChanged(false);
		System.out.println("Waiting for second route to complete");
		
		while(!sessionManager.getIsRouteComplete()) {
			if(notifier.getChanged()) {
				System.out.println("Notifier changed");
				sessionManager.setIsRouteComplete(true);
			}
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Route 2 complete, about to go to next one");
		
		ArrayList<Direction> testRoute3 = new ArrayList<Direction>();
		testRoute2 = planner.getRoute(new Location(0, 0, LocationType.EMPTY), new Location(1, 0, LocationType.EMPTY));
		sessionManager.setNumOfPicks(4);
		sessionManager.setRoute(testRoute3);
		sessionManager.setShouldSend(true);
		sessionManager.setIsRouteComplete(false);
		notifier.setChanged(false);
		System.out.println("Waiting for third route to complete");
		
		while(!sessionManager.getIsRouteComplete()) {
			if(notifier.getChanged()) {
				System.out.println("Notifier changed");
				sessionManager.setIsRouteComplete(true);
			}
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Route 3 complete, about to go to next one");
		
		/*ArrayList<Direction> testRoute4 = new ArrayList<Direction>();
		testRoute2 = planner.getRoute(new Location(1, 0, LocationType.EMPTY), new Location(0, 0, LocationType.EMPTY));
		sessionManager.setNumOfPicks(4);
		sessionManager.setRoute(testRoute4);
		sessionManager.setShouldSend(true);
		sessionManager.setIsRouteComplete(false);
		notifier.setChanged(false);
		System.out.println("Waiting for fourth route to complete");
		
		while(!sessionManager.getIsRouteComplete()) {
			if(notifier.getChanged()) {
				System.out.println("Notifier changed");
				sessionManager.setIsRouteComplete(true);
			}
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Route 3 complete, about to go to next one");*/
}
