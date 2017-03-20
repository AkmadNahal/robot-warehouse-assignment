package route_planning;
import java.util.ArrayList;
import java.util.Collections;

import utils.Location;


public class Travel {

    private ArrayList<Location> travel = new ArrayList<>();
    private ArrayList<Location> previousTravel = new ArrayList<>();
    private RoutePlanner planner;

    public Travel(ArrayList<Location> locations, RoutePlanner _planner) {
      travel.addAll(locations);
      planner = _planner;
    }

    public void swapLocations() {
      int a,b;
      a = generateRandomIndex();
      b = generateRandomIndex();
      while(a == 0) {
        a = generateRandomIndex();
      }
      while(b == 0) {
        b = generateRandomIndex();
      }
      previousTravel = travel;
      Location x = travel.get(a);
      Location y = travel.get(b);
      travel.set(a, y);
      travel.set(b, x);
    }

    public void revertSwap() {
        travel = previousTravel;
    }

    private int generateRandomIndex() {
        return (int) (Math.random() * travel.size());
    }

    public Location getLocation(int index) {
        return travel.get(index);
    }

    public int getDistance() {

      int distance = 0;

      ArrayList<Location> l = new ArrayList<Location>(travel);
      for(int i=1;i<l.size();i++) {
        Location start = l.get(i-1);
        Location end = l.get(i);

        int a = planner.getAStarDistance(start, end);
        // int a = start.getDistanceFromLocation(end);
        distance += a;

        // distance += start.getDistanceFromLocation(end);
      }

      return distance;
    }
    
    public int getDistance(ArrayList<Location> l) {

        int distance = 0;

        for(int i=1;i<l.size();i++) {
          Location start = l.get(i-1);
          Location end = l.get(i);

          int a = planner.getAStarDistance(start, end);
          // int a = start.getDistanceFromLocation(end);
          distance += a;

          // distance += start.getDistanceFromLocation(end);
        }

        return distance;
      }

    public ArrayList<Location> getTravel() {
      return travel;
    }

    public void setTravel(ArrayList<Location> value) {
      travel = new ArrayList<Location>(value);
    }

}
