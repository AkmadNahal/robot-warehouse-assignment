package route_planning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import utils.Location;

public class TSP {

  private Location[][] map;
  private int mapSizeX, mapSizeY;
  private RoutePlanner planner;
  private Travel travel;

  private final double startingTemperature = 0.7;
  private final int numberOfIterations = 200;
  private final double coolingRate = 2.5;

  public TSP(Location[][] _map, int _mapSizeX, int _mapSizeY) {
    map = _map;
    mapSizeX = _mapSizeX;
    mapSizeY = _mapSizeY;
    planner = new RoutePlanner(map, mapSizeX, mapSizeY);
  }

  public void simulateAnnealing(ArrayList<Location> locations) {

	if (locations.size() <= 1){
		return;
	}

	travel = new Travel(locations, planner);

    System.out.println(locations.size());

    double t = startingTemperature;
    double bestDistance = travel.getDistance();
    Travel bestSolution = travel;
    Travel currentSolution = new Travel(new ArrayList<Location>(travel.getTravel()), planner);

    for (int i = 0; i < numberOfIterations; i++) {
        if (t > 0.1) {
            currentSolution.swapLocations();
            double currentDistance = currentSolution.getDistance();
            if (currentDistance < bestDistance) {
                bestDistance = currentDistance;
                bestSolution.setTravel(currentSolution.getTravel());
            } else if (Math.exp((bestDistance - currentDistance) / t) < Math.random()) {
                currentSolution.revertSwap();
            }
            t *= coolingRate;
        } else {
            continue;
        }
    }

    locations.clear();
    locations.addAll(bestSolution.getTravel());
  }

  public HashMap<String, ArrayList<Location>> smartAssignment(Location r1S, Location r2S, Location r3S, ArrayList<Location> route1, ArrayList<Location> route2, ArrayList<Location> route3) {
	  ArrayList<Location> sol1 = new ArrayList<Location>();
	  ArrayList<Location> sol2 = new ArrayList<Location>();
	  ArrayList<Location> sol3 = new ArrayList<Location>();
	  ArrayList<Location> temp1 = new ArrayList<Location>();
    ArrayList<Location> temp2 = new ArrayList<Location>();
    ArrayList<Location> temp3 = new ArrayList<Location>();

	  // 1/2/3
	  temp1.clear(); temp1.add(r1S); temp1.addAll(route1); simulateAnnealing(temp1);
	  temp2.clear(); temp2.add(r2S); temp2.addAll(route2); simulateAnnealing(temp2);
	  temp3.clear(); temp3.add(r3S); temp3.addAll(route3); simulateAnnealing(temp3);
    if(travel.getDistance(temp1) < travel.getDistance(sol1) && travel.getDistance(temp2) < travel.getDistance(sol2) && travel.getDistance(temp3) < travel.getDistance(sol3)) {
      sol1.clear(); sol1.addAll(temp1);
      sol2.clear(); sol1.addAll(temp2);
      sol3.clear(); sol1.addAll(temp3);
    }

    // 1/3/2
    temp1.clear(); temp1.add(r1S); temp1.addAll(route1); simulateAnnealing(temp1);
    temp2.clear(); temp2.add(r2S); temp2.addAll(route3); simulateAnnealing(temp2);
    temp3.clear(); temp3.add(r3S); temp3.addAll(route2); simulateAnnealing(temp3);
    if(travel.getDistance(temp1) < travel.getDistance(sol1) && travel.getDistance(temp2) < travel.getDistance(sol2) && travel.getDistance(temp3) < travel.getDistance(sol3)) {
      sol1.clear(); sol1.addAll(temp1);
      sol2.clear(); sol1.addAll(temp2);
      sol3.clear(); sol1.addAll(temp3);
    }

    // 2/1/3
	  temp1.clear(); temp1.add(r1S); temp1.addAll(route2); simulateAnnealing(temp1);
	  temp2.clear(); temp2.add(r2S); temp2.addAll(route1); simulateAnnealing(temp2);
	  temp3.clear(); temp3.add(r3S); temp3.addAll(route3); simulateAnnealing(temp3);
    if(travel.getDistance(temp1) < travel.getDistance(sol1) && travel.getDistance(temp2) < travel.getDistance(sol2) && travel.getDistance(temp3) < travel.getDistance(sol3)) {
      sol1.clear(); sol1.addAll(temp1);
      sol2.clear(); sol1.addAll(temp2);
      sol3.clear(); sol1.addAll(temp3);
    }

    // 2/3/1
	  temp1.clear(); temp1.add(r1S); temp1.addAll(route2); simulateAnnealing(temp1);
	  temp2.clear(); temp2.add(r2S); temp2.addAll(route3); simulateAnnealing(temp2);
	  temp3.clear(); temp3.add(r3S); temp3.addAll(route1); simulateAnnealing(temp3);
    if(travel.getDistance(temp1) < travel.getDistance(sol1) && travel.getDistance(temp2) < travel.getDistance(sol2) && travel.getDistance(temp3) < travel.getDistance(sol3)) {
      sol1.clear(); sol1.addAll(temp1);
      sol2.clear(); sol1.addAll(temp2);
      sol3.clear(); sol1.addAll(temp3);
    }

    // 3/1/2
	  temp1.clear(); temp1.add(r1S); temp1.addAll(route3); simulateAnnealing(temp1);
	  temp2.clear(); temp2.add(r2S); temp2.addAll(route1); simulateAnnealing(temp2);
	  temp3.clear(); temp3.add(r3S); temp3.addAll(route2); simulateAnnealing(temp3);
    if(travel.getDistance(temp1) < travel.getDistance(sol1) && travel.getDistance(temp2) < travel.getDistance(sol2) && travel.getDistance(temp3) < travel.getDistance(sol3)) {
      sol1.clear(); sol1.addAll(temp1);
      sol2.clear(); sol1.addAll(temp2);
      sol3.clear(); sol1.addAll(temp3);
    }

    // 3/2/1
	  temp1.clear(); temp1.add(r1S); temp1.addAll(route3); simulateAnnealing(temp1);
	  temp2.clear(); temp2.add(r2S); temp2.addAll(route2); simulateAnnealing(temp2);
	  temp3.clear(); temp3.add(r3S); temp3.addAll(route1); simulateAnnealing(temp3);
    if(travel.getDistance(temp1) < travel.getDistance(sol1) && travel.getDistance(temp2) < travel.getDistance(sol2) && travel.getDistance(temp3) < travel.getDistance(sol3)) {
      sol1.clear(); sol1.addAll(temp1);
      sol2.clear(); sol1.addAll(temp2);
      sol3.clear(); sol1.addAll(temp3);
    }

	  HashMap<String, ArrayList<Location>> result = new HashMap<String, ArrayList<Location>>();
    result.put("robot1", sol1);
    result.put("robot2", sol2);
    result.put("robot3", sol3);
	  return result;
  }

}
