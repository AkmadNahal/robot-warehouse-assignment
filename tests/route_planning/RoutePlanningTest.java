package warehouse_interface;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import route_planning.RoutePlanner;
import utils.Direction;
import utils.Location;
import utils.LocationType;

public class RoutePlanningTest {

  @Test
  public void testRoutePlanning() {
    int maxSize = 5;
    Location[][] map = new Location[maxSize][maxSize];

    // Create map
    for(int i=0;i<maxSize;i++) {
      for(int j=0;j<maxSize;j++) {
        map[i][j] = new Location(i,j,LocationType.EMPTY);
      }
    }

    // Setting some walls
    map[0][1] = new Location(0,1,LocationType.BLOCK);
    map[1][1] = new Location(1,1,LocationType.BLOCK);
    map[2][3] = new Location(2,3,LocationType.BLOCK);
    map[3][2] = new Location(3,2,LocationType.BLOCK);
    map[4][1] = new Location(4,1,LocationType.BLOCK);

    RoutePlanner planner = new RoutePlanner(map, maxSize, maxSize);
    ArrayList<Location> route = planner.getRoute(new Location(0,0,LocationType.EMPTY), new Location(2,4,LocationType.EMPTY));

    // This should be the array list
    ArrayList<Location> solution = new ArrayList<Location>();
    solution.add(new Location(0,0,LocationType.EMPTY));
    solution.add(new Location(1,0,LocationType.EMPTY));
    solution.add(new Location(2,0,LocationType.EMPTY));
    solution.add(new Location(2,1,LocationType.EMPTY));
    solution.add(new Location(2,2,LocationType.EMPTY));
    solution.add(new Location(1,2,LocationType.EMPTY));
    solution.add(new Location(1,3,LocationType.EMPTY));
    solution.add(new Location(1,4,LocationType.EMPTY));
    solution.add(new Location(2,4,LocationType.EMPTY));
    
    boolean ok = true;
    for(int i=0;i<route.size();i++) {
    	if(!route.get(i).equalsTo(solution.get(i))) {
    		ok = false;
    	}
    }
    
    assertTrue(ok);
  }

}
