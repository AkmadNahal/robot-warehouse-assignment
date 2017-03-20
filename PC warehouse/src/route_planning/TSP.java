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

    System.out.println("Starting SA with temperature: " + startingTemperature + ", # of iterations: " + numberOfIterations + " and colling rate: " + coolingRate);
    double t = startingTemperature;
    double bestDistance = travel.getDistance();
    System.out.println("Initial distance of travel: " + bestDistance);
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
        if (i % 100 == 0) {
            System.out.println("Iteration #" + i);
        }
    }

    System.out.println();
    System.out.println("--- " + (bestDistance - (bestSolution.getTravel().size()-1)) + " ---");
    System.out.println();

    locations.clear();
    locations.addAll(bestSolution.getTravel());
  }
  
  public HashMap<String, ArrayList<Location>> smartAssignment(Location r1S, Location r2S, Location r3S, ArrayList<Location> route1, ArrayList<Location> route2, ArrayList<Location> route3) {
	  ArrayList<Location> sol1 = new ArrayList<Location>();
	  ArrayList<Location> sol2 = new ArrayList<Location>();
	  ArrayList<Location> sol3 = new ArrayList<Location>();
	  ArrayList<Location> temp1, temp2, temp3;
	  
	  // 1/2/3
	  temp1 = new ArrayList<>(); temp1.add(r1S); temp1.addAll(route1); simulateAnnealing(temp1);
	  temp2 = new ArrayList<>(); temp2.add(r2S); temp1.addAll(route2); simulateAnnealing(temp2);
	  temp3 = new ArrayList<>(); temp3.add(r3S); temp1.addAll(route3); simulateAnnealing(temp3);
	  
	  // To be continued
	  
	  HashMap<String, ArrayList<Location>> result = new HashMap<String, ArrayList<Location>>();
	  return result;
  }

}
