package system_control;

import java.util.ArrayList;

import job_selection.Round;
import route_planning.RoutePlanner;
import utils.Direction;
import utils.Location;
import utils.LocationType;

public class RouteManager implements Runnable {
	
	private ArrayList<Round> sortedJobs;
	private PCSessionManager sessionManager;
	private RoutePlanner planner;
	private ChangeNotifier notifier;
	
	public RouteManager(ArrayList<Round> sortedJobs, PCSessionManager sessionManager, RoutePlanner planner, ChangeNotifier _notifier){
		this.sortedJobs = sortedJobs;
		this.sessionManager = sessionManager;
		this.planner = planner;
		this.notifier = _notifier;
	}

	@Override
	public void run() {
		/*for (Round r : sortedJobs) {
			ArrayList<Location> locationsInJob = r.getRoute();
			for (int i = 0; i < locationsInJob.size(); i++){
				Location nextGoal = locationsInJob.get(i);
				ArrayList<Direction> solution = planner.getRoute(sessionManager.getLocationAccess().getCurrentLocation(), nextGoal);
				sessionManager.setNumOfPicks(4);
				sessionManager.setRoute(solution);
				sessionManager.setShouldSend(true);
				sessionManager.setIsRouteComplete(false);
				while (!sessionManager.getIsRouteComplete()){
					//do nothing!
				}
				//loops back
			}
		}*/
		ArrayList<Direction> testRoute = new ArrayList<Direction>();
		testRoute = planner.getRoute(sessionManager.getLocationAccess().getCurrentLocation(), new Location(1,0, LocationType.EMPTY));
		sessionManager.setNumOfPicks(4);
		sessionManager.setRoute(testRoute);
		sessionManager.setShouldSend(true);
		sessionManager.setIsRouteComplete(false);
		notifier.setChanged(false);
		sessionManager.setReadValue(10);
		System.out.println("Waiting for route to complete");
		
		synchronized(sessionManager.getIsRouteComplete()) {
			while(!sessionManager.getIsRouteComplete()) {
				System.out.println(sessionManager.getReadValue());
				if(sessionManager.getReadValue() == 50) {
					sessionManager.setIsRouteComplete(true);
				}
			}
		}
		
		System.out.println("Route 1 complete, about to go to next one");
		
		ArrayList<Direction> testRoute2 = new ArrayList<Direction>();
		testRoute2 = planner.getRoute(new Location(1, 0, LocationType.EMPTY), new Location(2, 0, LocationType.EMPTY));
		sessionManager.setNumOfPicks(4);
		sessionManager.setRoute(testRoute2);
		sessionManager.setShouldSend(true);
		sessionManager.setIsRouteComplete(false);
		while (!sessionManager.getIsRouteComplete()){
			//do nothing!
		}
	}

}
