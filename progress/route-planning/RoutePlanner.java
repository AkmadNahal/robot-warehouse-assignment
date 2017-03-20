import java.util.*;

public class RoutePlanner {

  private Location[][] map;
  private int maxX, maxY;
  private final int WINDOW = 5; // basically WINDOW-1 steps ahead

  public RoutePlanner(Location[][] _map, int _maxX, int _maxY) {
    map = _map;
    maxX = _maxX;
    maxY = _maxY;
  }

  public HashMap<String, ArrayList<Location>> getMultiRobotRoute(Location robot1Start, Location robot1End, Location robot2Start, Location robot2End, Location robot3Start, Location robot3End) {

    clearMapFull();

    ArrayList<Location> route1, route2, route3;
    map[robot1Start.getX()][robot1Start.getY()].setType(LocationType.ROBOT);
    map[robot2Start.getX()][robot2Start.getY()].setType(LocationType.ROBOT);
    map[robot3Start.getX()][robot3Start.getY()].setType(LocationType.ROBOT);

    // Priority 1/2/3
    route1 = chunkToWindow(getRoute(robot1Start, robot1End)); reservePlace(route1);
    route2 = chunkToWindow(getRoute(robot2Start, robot2End)); reservePlace(route2);
    route3 = chunkToWindow(getRoute(robot3Start, robot3End)); reservePlace(route3);

    if(route1.size() == WINDOW && route2.size() == WINDOW && route3.size() == WINDOW) {
      // Success
      System.out.println("Success 1");
      return createSolution(route1, route2, route3);
    }

    // Priority 1/3/2
    clearMap();
    route1 = chunkToWindow(getRoute(robot1Start, robot1End)); reservePlace(route1);
    route3 = chunkToWindow(getRoute(robot3Start, robot3End)); reservePlace(route3);
    route2 = chunkToWindow(getRoute(robot2Start, robot2End)); reservePlace(route2);

    if(route1.size() == WINDOW && route2.size() == WINDOW && route3.size() == WINDOW) {
      // Success
      System.out.println("Success 2");
      return createSolution(route1, route2, route3);
    }

    // Priority 2/1/3
    clearMap();
    route2 = chunkToWindow(getRoute(robot2Start, robot2End)); reservePlace(route2);
    route1 = chunkToWindow(getRoute(robot1Start, robot1End)); reservePlace(route1);
    route3 = chunkToWindow(getRoute(robot3Start, robot3End)); reservePlace(route3);

    if(route1.size() == WINDOW && route2.size() == WINDOW && route3.size() == WINDOW) {
      // Success
      System.out.println("Success 3");
      return createSolution(route1, route2, route3);
    }

    // Priority 2/3/1
    clearMap();
    route2 = chunkToWindow(getRoute(robot2Start, robot2End)); reservePlace(route2);
    route3 = chunkToWindow(getRoute(robot3Start, robot3End)); reservePlace(route3);
    route1 = chunkToWindow(getRoute(robot1Start, robot1End)); reservePlace(route1);

    if(route1.size() == WINDOW && route2.size() == WINDOW && route3.size() == WINDOW) {
      // Success
      System.out.println("Success 4");
      return createSolution(route1, route2, route3);
    }

    // Priority 3/1/2
    clearMap();
    route3 = chunkToWindow(getRoute(robot3Start, robot3End)); reservePlace(route3);
    route1 = chunkToWindow(getRoute(robot1Start, robot1End)); reservePlace(route1);
    route2 = chunkToWindow(getRoute(robot2Start, robot2End)); reservePlace(route2);

    if(route1.size() == WINDOW && route2.size() == WINDOW && route3.size() == WINDOW) {
      // Success
      System.out.println("Success 5");
      return createSolution(route1, route2, route3);
    }

    // Priority 3/2/1
    clearMap();
    route3 = chunkToWindow(getRoute(robot3Start, robot3End)); reservePlace(route3);
    route2 = chunkToWindow(getRoute(robot2Start, robot2End)); reservePlace(route2);
    route1 = chunkToWindow(getRoute(robot1Start, robot1End)); reservePlace(route1);

    if(route1.size() == WINDOW && route2.size() == WINDOW && route3.size() == WINDOW) {
      // Success
      System.out.println("Success 6");
      return createSolution(route1, route2, route3);
    }

    // According to the last priority, the empty routes will return just stop
    System.out.println("Success 7");
    if(route1.size() == 0) {
      for(int i=1;i<=WINDOW;i++) { route1.add(robot1Start); };
    } else {
      Location last = route1.get(route1.size() - 1);
      while(route1.size() < WINDOW) { route1.add(last); };
    }

    if(route2.size() == 0) {
      for(int i=1;i<=WINDOW;i++) { route2.add(robot2Start); };
    } else {
      Location last = route2.get(route2.size() - 1);
      while(route2.size() < WINDOW) { route2.add(last); };
    }

    if(route3.size() == 0) {
      for(int i=1;i<=WINDOW;i++) { route3.add(robot3Start); };
    } else {
      Location last = route3.get(route3.size() - 1);
      while(route3.size() < WINDOW) { route3.add(last); };
    }

    return createSolution(route1, route2, route3);
  }

  public int getAStarDistance(Location startLocation, Location endLocation) {
    return getRoute(startLocation, endLocation).size();
  }

  public ArrayList<Location> getRoute(Location startLocation, Location endLocation) {

    boolean solFound = false;
    ArrayList<LocationNode> open_list = new ArrayList<LocationNode>();
    ArrayList<LocationNode> close_list = new ArrayList<LocationNode>();

    LocationNode startNode = new LocationNode(null, startLocation, 0, startLocation.getDistanceFromLocation(endLocation));
    open_list.add(startNode);

    LocationNode chosenNode = null;

    while(!open_list.isEmpty()) {

      chosenNode = open_list.get(0);

      // Check if solution
      if(chosenNode.getLocation().equalsTo(endLocation)) {
        solFound = true;
        close_list.add(chosenNode);
        break;
      }

      // Get successors
      ArrayList<LocationNode> successors = getSuccessors(chosenNode);

      // Filter successors
      for(LocationNode node : successors) {

        // Set heuristic value
        node.setHValue(node.getLocation().getDistanceFromLocation(endLocation));

        if(containsNode(node, close_list)) {
          continue;
        } else if(containsNode(node, open_list)) {
          int index = getNodeIndex(node, open_list);
          LocationNode nd = open_list.get(index);

          if(node.getGValue() < nd.getGValue()) {
            open_list.remove(index);
            open_list.add(node);
          }

        } else {
          open_list.add(node);
        }
      }

      // Remove chosen node and add to close_list
      open_list.remove(0);
      close_list.add(chosenNode);

      // Sort open_list
      Collections.sort(open_list, new Comparator<LocationNode>() {
        public int compare(LocationNode n1, LocationNode n2) {
          if(n1.getFValue() < n2.getFValue()) {
            return -1;
          } else if(n1.getFValue() > n2.getFValue()) {
            return 1;
          } else {
            return 0;
          }
        }
      });

    }

    ArrayList<Location> solution = new ArrayList<Location>();

    if(solFound) {

      chosenNode = close_list.get(close_list.size() - 1);

      while(chosenNode.getParent() != null) {
        solution.add(0, chosenNode.getLocation());
        chosenNode = chosenNode.getParent();
      }
      solution.add(0, startLocation);
    }

    return solution;
  }

  private ArrayList<LocationNode> getSuccessors(LocationNode node) {
    ArrayList<LocationNode> successors = new ArrayList<LocationNode>();

    int x = node.getLocation().getX();
    int y = node.getLocation().getY();

    if(x + 1 < maxX && map[x+1][y].getType() == LocationType.EMPTY) {
      successors.add(new LocationNode(node, map[x+1][y], node.getGValue() + 1, 0));
    }
    if(x - 1 >= 0 && map[x-1][y].getType() == LocationType.EMPTY) {
      successors.add(new LocationNode(node, map[x-1][y], node.getGValue() + 1, 0));
    }
    if(y + 1 < maxY && map[x][y+1].getType() == LocationType.EMPTY) {
      successors.add(new LocationNode(node, map[x][y+1], node.getGValue() + 1, 0));
    }
    if(y - 1 >= 0 && map[x][y-1].getType() == LocationType.EMPTY) {
      successors.add(new LocationNode(node, map[x][y-1], node.getGValue() + 1, 0));
    }

    return successors;
  }

  private boolean containsNode(LocationNode node, ArrayList<LocationNode> list) {
    for(LocationNode element : list) {
      if(element.getLocation().equalsTo(node.getLocation())) {
        return true;
      }
    }
    return false;
  }

  private int getNodeIndex(LocationNode node, ArrayList<LocationNode> list) {
    int i = -1;
    for(LocationNode element : list) {
      i++;
      if(element.getLocation().equalsTo(node.getLocation())) {
        return i;
      }
    }
    return -1;
  }

  private ArrayList<Direction> coordinatesToDirections(ArrayList<Location> locations){

    ArrayList<Direction> directions = new ArrayList<Direction>();

    for(int i=1;i<locations.size(); i++) {
      int diffInX = locations.get(i).getX() - locations.get(i-1).getX();
      int diffInY = locations.get(i).getY() - locations.get(i-1).getY();

      if (diffInX < 0){
  			directions.add(Direction.LEFT);
  		} else if (diffInX > 0){
  			directions.add(Direction.RIGHT);
  		} else if (diffInY < 0){
  			directions.add(Direction.BACKWARDS);
  		} else if (diffInY> 0){
  			directions.add(Direction.FORWARD);
  		} else {
        directions.add(Direction.STOP);
      }
    }

    return directions;
	}

  private void clearMap() {
    for(int i=0;i<maxX;i++) {
      for(int j=0;j<maxY;j++) {
        if(map[i][j].getType() == LocationType.TEMP) {
          map[i][j].setType(LocationType.EMPTY);
        }
      }
    }
  }

  private void clearMapFull() {
    for(int i=0;i<maxX;i++) {
      for(int j=0;j<maxY;j++) {
        if(map[i][j].getType() == LocationType.TEMP || map[i][j].getType() == LocationType.ROBOT) {
          map[i][j].setType(LocationType.EMPTY);
        }
      }
    }
  }

  private ArrayList<Location> chunkToWindow(ArrayList<Location> route) {
    ArrayList<Location> solution = new ArrayList<Location>();
    if(route.size() != 0) {

      if(route.size() > WINDOW) {
        for(int i=0;i<WINDOW;i++) {
          solution.add(route.get(i));
        }
      } else {
        solution.addAll(route);

        while(solution.size() < WINDOW) {
          solution.add(route.get(route.size() - 1));
        }
      }
    }

    return solution;
  }

  private HashMap<String, ArrayList<Location>> createSolution(ArrayList<Location> route1, ArrayList<Location> route2, ArrayList<Location> route3) {
    HashMap<String, ArrayList<Location>> solution = new HashMap<String, ArrayList<Location>>();
    solution.put("robot1", chunkToWindow(route1));
    solution.put("robot2", chunkToWindow(route2));
    solution.put("robot3", chunkToWindow(route3));
    return solution;
  }

  private void reservePlace(ArrayList<Location> route) {
    for(Location l : route) {
      if(map[l.getX()][l.getY()].getType() == LocationType.EMPTY) {
        map[l.getX()][l.getY()].setType(LocationType.TEMP);
      }
    }
  }

}
