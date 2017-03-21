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

public class RouteManager implements Runnable {
	
	private PCSessionManager sessionManager1;
	private PCSessionManager sessionManager2;
	//private PCSessionManager sessionManager3;
	private ChangeNotifier notifier1;
	private ChangeNotifier notifier2;
	//private ChangeNotifier notifier3;
	private RoutePlanner planner;
	private ArrayList<Round> sortedJobs;
	private NetworkComm networkComm2;
	private NetworkComm networkComm1;
	private TSP tsp;
	
	private static final Logger logger = Logger.getLogger(RouteManager.class);
	
	public RouteManager(ArrayList<Round> sortedJobs, PCSessionManager sessionManager1, PCSessionManager sessionManager2,
			/*PCSessionManager sessionManager3,*/ RoutePlanner planner, ChangeNotifier _notifier1, ChangeNotifier _notifier2/*, ChangeNotifier _notifier3*/,
			NetworkComm networkComm1, NetworkComm networkComm2, TSP tsp){
		this.sortedJobs = sortedJobs;
		this.sessionManager1 = sessionManager1;
		this.sessionManager2 = sessionManager2;
		//this.sessionManager3 = sessionManager3;
		this.planner = planner;
		this.notifier1 = _notifier1;
		this.notifier2 = _notifier2;
		//this.notifier3 = _notifier3;
		this.networkComm1 = networkComm1;
		this.networkComm2 = networkComm2;
		this.tsp = tsp;
	}
	
	@Override
	public void run() {
		for (int n = 12/*49*/; n < sortedJobs.size(); n+=2) {
			Round robot1CurrentRound = sortedJobs.get(n);
			sessionManager1.setCurrentRound(robot1CurrentRound);
			Round robot2CurrentRound = sortedJobs.get(n+1);
			sessionManager1.setCurrentRound(robot2CurrentRound);
			
			sortedJobs.get(n).initialiseCounts();
			ArrayList<Integer> counts1 = sortedJobs.get(n).getCounts();
			sortedJobs.get(n+1).initialiseCounts();
			ArrayList<Integer> counts2 = sortedJobs.get(n+1).getCounts();
			//Round robot3CurrentRound = sortedJobs.get(n+2);
			
			ArrayList<Pick> robot1Picks = robot1CurrentRound.getRound();
			ArrayList<Pick> robot2Picks = robot2CurrentRound.getRound();
			//ArrayList<Pick> robot3Picks = robot3CurrentRound.getRound();
			
			for (int i = 0; i < robot1Picks.size(); i++){
				robot1Picks.get(i).setCount(counts1.get(i));
			}
			for (int i = 0; i < robot2Picks.size(); i++){
				robot2Picks.get(i).setCount(counts2.get(i));
			}
			
			
			ArrayList<Location> locationsInJob1 = robot1CurrentRound.getRoute();
			logger.debug(locationsInJob1.size() + ": Size r1");
			ArrayList<Location> locationsInJob2 = robot2CurrentRound.getRoute();
			logger.debug(locationsInJob2.size() + ": Size r2");
			
			locationsInJob1.add(0, sessionManager1.getLocationAccess().getCurrentLocation());
			locationsInJob2.add(0, sessionManager2.getLocationAccess().getCurrentLocation());

			// TSP
			tsp.simulateAnnealing(locationsInJob1);
			tsp.simulateAnnealing(locationsInJob2);
			//ArrayList<Location> robot3TSP = tsp.simulateAnnealing(TEMPERATURE, NUMBER_OF_ITERATIONS, COOLING_RATE, locationsInJob3);
			logger.debug("Finished TSP setup");
			
			// Fix collision issues in pickup
			Location.jamFixer(locationsInJob1, locationsInJob2);
			logger.debug("Fixed jams");
			
			// Reorder pick arrays according TSP location array
			robot1Picks = Round.reorderAccordingTSP(locationsInJob1, robot1Picks);
			robot2Picks = Round.reorderAccordingTSP(locationsInJob2, robot2Picks);
			logger.debug("Re-ordered picks, according to TSP");
			
			Item lastActualItem1 = robot1Picks.get(robot1Picks.size()-1).getItem();
			Item lastActualItem2 = robot2Picks.get(robot2Picks.size()-1).getItem();
			while (robot1Picks.size() != robot2Picks.size()){
				if (robot1Picks.size() < locationsInJob2.size()){
					Pick pickToAdd = new Pick(lastActualItem1, 0);
					robot1Picks.add(pickToAdd);
				}
				if (robot2Picks.size() < robot1Picks.size()){
					Pick pickToAdd = new Pick(lastActualItem2, 0);
					robot2Picks.add(pickToAdd);
				}
			}
			Item dropOff1 = new Item("", 0f, 0f, new Location(4,7, LocationType.EMPTY));
			robot1Picks.add(new Pick(dropOff1, -1));
			
			Item dropOff2 = new Item("", 0f, 0f, new Location(7,7, LocationType.EMPTY));
			robot2Picks.add(new Pick(dropOff2, -1));
			
			logger.debug("Normalises size of picks list");
			
			//ArrayList<Location> locationsInJob3 = robot3CurrentRound.getRoute();
			
			Location currentLocation1 = sessionManager1.getLocationAccess().getCurrentLocation();
			Location currentLocation2 = sessionManager2.getLocationAccess().getCurrentLocation();
			//Location currentLocation3 = sessionManager3.getLocationAccess().getCurrentLocation();
			
			int counter = 0;
			
			for (int i = 0; i < robot1Picks.size(); i++){
				System.out.print(robot1Picks.get(i).getCount());
			}
			System.out.println("");
			for (int i = 0; i < robot2Picks.size(); i++){
				System.out.print(robot2Picks.get(i).getCount());
			}
			System.out.println("");
			
			logger.debug(locationsInJob1.size());
			logger.debug(locationsInJob2.size());

			logger.debug("Started next job");
			
			while(counter < robot1Picks.size() && counter < robot2Picks.size()/* || counter < robot3Picks.size()*/) {
				logger.debug("Doing pick " + counter);
				Location target1 = robot1Picks.get(counter).getItem().getLoc();
				sessionManager1.setPickLocation(target1);
				Location target2 = robot2Picks.get(counter).getItem().getLoc();
				sessionManager2.setPickLocation(target2);
				
				logger.debug("Robot 1 Target: " + target1);
				logger.debug("Robot 2 Target: " + target2);

				//Location target3 = locationsInJob3.get(counter);
				
				int numOfPick1 = robot1Picks.get(counter).getCount();
				int numOfPick2 = robot2Picks.get(counter).getCount();
				
				System.out.println(numOfPick1);
				System.out.println(numOfPick2);
				
				while(!currentLocation1.equalsTo(target1) || !currentLocation2.equalsTo(target2) /*&& !currentLocation3.equalsTo(target3)*/){
					sessionManager1.setIsRouteComplete(false);
					sessionManager2.setIsRouteComplete(false);
					//sessionManager3.setIsRouteComplete(false);
					
					HashMap<String, ArrayList<Location>> route = planner.getMultiRobotRoute(currentLocation1, target1, currentLocation2, target2, new Location(11,7, LocationType.EMPTY), new Location(11,7, LocationType.EMPTY));
					ArrayList<Direction> route1 = planner.coordinatesToDirections(route.get("robot1"));
					ArrayList<Direction> route2 = planner.coordinatesToDirections(route.get("robot2"));
					ArrayList<Direction> route3 = planner.coordinatesToDirections(route.get("robot3"));
					
					sessionManager1.setRoute(route1);
					sessionManager2.setRoute(route2);
					//sessionManager3.setRoute(route3);
					
					sessionManager1.setNumOfPicks(numOfPick1);
					sessionManager2.setNumOfPicks(numOfPick2);
					//sessionManager3.setNumOfPicks(numOfPick3);
					
					sessionManager1.setShouldSend(true);
					sessionManager2.setShouldSend(true);
					//sessionManager3.setShouldSend(true);
										
					logger.debug(route1 + ": Route 1");
					logger.debug(route2 + ": Route 2");
					
					logger.debug("Waiting for route to complete");
					while(!sessionManager1.getIsRouteComplete() || !sessionManager2.getIsRouteComplete()/* && !sessionManager3.getIsRouteComplete()*/) {
						if(notifier1.getChanged()) {
							logger.debug("Notifier 1 changed");
							//sessionManager1.getLocationAccess().setCurrentLocation(route.get("robot1").get(route.get("robot1").size() - 1));
							currentLocation1 = sessionManager1.getLocationAccess().getCurrentLocation();
							logger.debug(currentLocation1 + ": Current Location - Robot 1");
							sessionManager1.setIsRouteComplete(true);
							notifier1.setChanged(false);
						}
						if(notifier2.getChanged()) {
							logger.debug("Notifier 2 changed");
							//sessionManager2.getLocationAccess().setCurrentLocation(route.get("robot2").get(route.get("robot2").size() - 1));
							currentLocation2 = sessionManager2.getLocationAccess().getCurrentLocation();
							logger.debug(currentLocation2 + ": Current Location - Robot 2");
							sessionManager2.setIsRouteComplete(true);
							notifier2.setChanged(false);
						}
						/*if(notifier3.getChanged()) {
							logger.debug("Notifier 3 changed");
							if (currentLocation3.equalsTo(target3)){
								sessionManager3.setIsRouteComplete(true);
							}
						}*/
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
				sessionManager1.setIsRouteComplete(false);
				
				notifier1.setChanged(false);
				notifier2.setChanged(false);
				
				networkComm1.sendAtPickup();
				networkComm2.sendAtPickup();
				
				//notifier1.setAtPickup(true);
				//notifier2.setAtPickup(true);
				//sessionManager3.setIsRouteComplete(true);
				
				while (!sessionManager1.getIsRouteComplete() || !sessionManager2.getIsRouteComplete()){
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
