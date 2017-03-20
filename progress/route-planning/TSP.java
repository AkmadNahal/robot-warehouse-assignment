import java.util.ArrayList;

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

  public ArrayList<Location> simulateAnnealing(double startingTemperature, int numberOfIterations, double coolingRate, ArrayList<Location> locations) {
    travel = new Travel(locations, planner);

    System.out.println("Starting SA with temperature: " + startingTemperature + ", # of iterations: " + numberOfIterations + " and colling rate: " + coolingRate);
    double t = startingTemperature;
    double bestDistance = travel.getDistance();
    System.out.println("Initial distance of travel: " + bestDistance);
    Travel bestSolution = travel;
    Travel currentSolution = new Travel(new ArrayList<Location>(travel.getTravel()), planner);

    for (int i = 0; i < numberOfIterations; i++) {

      System.out.print("[ ");
      for(int j=0;j<currentSolution.getTravel().size();j++) {
        System.out.print("(" + currentSolution.getTravel().get(j).getX() + "," + currentSolution.getTravel().get(j).getY() + "),");
      }
      System.out.print(" ] - " + currentSolution.getDistance());
      System.out.println();

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

    return bestSolution.getTravel();
  }

}
