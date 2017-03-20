package system_control;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import job_selection.Round;
import route_planning.RoutePlanner;
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
	
	private static final Logger logger = Logger.getLogger(RouteManager.class);
	
	public RouteManager(ArrayList<Round> sortedJobs, PCSessionManager sessionManager1, PCSessionManager sessionManager2,
			/*PCSessionManager sessionManager3,*/ RoutePlanner planner, ChangeNotifier _notifier1, ChangeNotifier _notifier2/*, ChangeNotifier _notifier3*/,
			NetworkComm networkComm1, NetworkComm networkComm2){
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
	}
	
	@Override
	public void run() {
		for (int n = 49; n < sortedJobs.size(); n+=2) {
			Round robot1CurrentRound = sortedJobs.get(n);
			Round robot2CurrentRound = sortedJobs.get(n+1);
			//Round robot3CurrentRound = sortedJobs.get(n+2);
			
			ArrayList<Location> locationsInJob1 = robot1CurrentRound.getRoute();
			logger.debug(locationsInJob1.size() + ": Size r1");
			ArrayList<Location> locationsInJob2 = robot2CurrentRound.getRoute();
			logger.debug(locationsInJob2.size() + ": Size r2");
			
			//ArrayList<Location> locationsInJob3 = robot3CurrentRound.getRoute();
			
			Location currentLocation1 = sessionManager1.getLocationAccess().getCurrentLocation();
			Location currentLocation2 = sessionManager2.getLocationAccess().getCurrentLocation();
			//Location currentLocation3 = sessionManager3.getLocationAccess().getCurrentLocation();
			
			int counter = 0;
			
			logger.debug("Started next job");
			
			while(counter < locationsInJob1.size() && counter < locationsInJob2.size()/* || counter < locationsInJob3.size()*/) {
				logger.debug("Doing pick " + counter);
				Location target1 = locationsInJob1.get(counter);
				Location target2 = locationsInJob2.get(counter);
				
				logger.debug("Robot 1 Target: " + target1);
				logger.debug("Robot 2 Target: " + target2);

				//Location target3 = locationsInJob3.get(counter);
				
				int numOfPick1 = sortedJobs.get(n).getCounts().get(counter);
				int numOfPick2 = sortedJobs.get(n).getCounts().get(counter);
				//int numOfPick3 = sortedJobs.get(n).getCounts().get(counter);
				
				while(!currentLocation1.equalsTo(target1) || !currentLocation2.equalsTo(target2) /*&& !currentLocation3.equalsTo(target3)*/){
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
					
					sessionManager1.setIsRouteComplete(false);
					sessionManager2.setIsRouteComplete(false);
					//sessionManager3.setIsRouteComplete(false);
					
					//notifier3.setChanged(false);
					
					
					logger.debug(route1 + ": Route 1");
					logger.debug(route2 + ": Route 2");
					
					logger.debug("Waiting for route to complete");
					while(!sessionManager1.getIsRouteComplete() || !sessionManager2.getIsRouteComplete()/* && !sessionManager3.getIsRouteComplete()*/) {
						if(notifier1.getChanged()) {
							logger.debug("Notifier 1 changed");
							sessionManager1.getLocationAccess().setCurrentLocation(route.get("robot1").get(route.get("robot1").size() - 1));
							currentLocation1 = sessionManager1.getLocationAccess().getCurrentLocation();
							logger.debug(currentLocation1 + ": Current Location - Robot 1");
							sessionManager1.setIsRouteComplete(true);
							notifier1.setChanged(false);
						}
						if(notifier2.getChanged()) {
							logger.debug("Notifier 2 changed");
							sessionManager2.getLocationAccess().setCurrentLocation(route.get("robot2").get(route.get("robot2").size() - 1));
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
				
				notifier1.setChanged(false);
				notifier2.setChanged(false);
				
				networkComm1.sendAtPickup();
				networkComm2.sendAtPickup();
				
				//notifier1.setAtPickup(true);
				//notifier2.setAtPickup(true);
				//sessionManager3.setIsRouteComplete(true);
				
				sessionManager1.getLocationAccess().setCurrentLocation(target1);
				sessionManager2.getLocationAccess().setCurrentLocation(target2);
				//sessionManager3.getLocationAccess().setCurrentLocation(target3);
				
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
