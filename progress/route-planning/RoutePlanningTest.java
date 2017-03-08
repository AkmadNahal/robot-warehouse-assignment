import static org.junit.Assert.*;

public class RoutePlanningTest {

  @Test
  public void testRoutePlanning {
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
    ArrayList<Direction> route = planner.getRoute(new Location(0,0,LocationType.EMPTY), new Location(4,4,LocationType.EMPTY));

    // This should be the array list
    ArrayList<Direction> solution = new ArrayList<Direction>();
    solution.add(Direction.RIGHT);
    solution.add(Direction.RIGHT);
    solution.add(Direction.FORWARD);
    solution.add(Direction.FORWARD);
    solution.add(Direction.LEFT);
    solution.add(Direction.FORWARD);
    solution.add(Direction.FORWARD);
    solution.add(Direction.RIGHT);
    solution.add(Direction.RIGHT);
    solution.add(Direction.RIGHT);

    assertEquals(solution, route);

  }

}
