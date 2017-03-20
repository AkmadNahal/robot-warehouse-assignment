package route_planning;

import java.util.ArrayList;
import java.util.Collections;

import utils.Location;

public class TSP {

  private Location[][] map;
  private int mapSizeX, mapSizeY;
  private RoutePlanner planner;
  private Travel travel;

  public TSP(Location[][] _map, int _mapSizeX, int _mapSizeY) {
    map = _map;
    mapSizeX = _mapSizeX;
    mapSizeY = _mapSizeY;
    planner = new RoutePlanner(map, mapSizeX, mapSizeY);
  }

  public void simulateAnnealing(double startingTemperature, int numberOfIterations, double coolingRate, ArrayList<Location> locations) {
    
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

}
