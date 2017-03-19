package route_planning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import utils.Direction;
import utils.Location;
import utils.LocationType;

public class RoutePlanner {

  private Location[][] map;
  private int maxX, maxY;
  private final int WINDOW = 3;

  public RoutePlanner(Location[][] _map, int _maxX, int _maxY) {
    map = _map;
    maxX = _maxX;
    maxY = _maxY;
  }

  public HashMap<String, ArrayList<Location>> getMultiRobotRoute(Location robot1Start, Location robot1End, Location robot2Start, Location robot2End, Location robot3Start, Location robot3End) {
    ArrayList<Location> route1, route2, route3;
    HashMap<String, ArrayList<Location>> solution = new HashMap<String, ArrayList<Location>>();

    // Priority 1/2/3
    clearMap();
    route1 = chunkToWindow(getRoute(robot1Start, robot2End));
    route2 = chunkToWindow(getRoute(robot2Start, robot2End));
    route3 = chunkToWindow(getRoute(robot3Start, robot3End));
    clearMap();

    if(route1.size() == WINDOW && route2.size() == WINDOW && route3.size() == WINDOW) {
      // Success
      solution.put("robot1", route1);
      solution.put("robot2", route2);
      solution.put("robot3", route3);
      return solution;
    }

    // Priority 1/3/2
    clearMap();
    route1 = chunkToWindow(getRoute(robot1Start, robot2End));
    route3 = chunkToWindow(getRoute(robot3Start, robot3End));
    route2 = chunkToWindow(getRoute(robot2Start, robot2End));
    clearMap();

    if(route1.size() == WINDOW && route2.size() == WINDOW && route3.size() == WINDOW) {
      // Success
      solution.put("robot1", route1);
      solution.put("robot2", route2);
      solution.put("robot3", route3);
      return solution;
    }

    // Priority 2/1/3
    clearMap();
    route2 = chunkToWindow(getRoute(robot2Start, robot2End));
    route1 = chunkToWindow(getRoute(robot1Start, robot2End));
    route3 = chunkToWindow(getRoute(robot3Start, robot3End));
    clearMap();

    if(route1.size() == WINDOW && route2.size() == WINDOW && route3.size() == WINDOW) {
      // Success
      solution.put("robot1", route1);
      solution.put("robot2", route2);
      solution.put("robot3", route3);
      return solution;
    }

    // Priority 2/3/1
    clearMap();
    route2 = chunkToWindow(getRoute(robot2Start, robot2End));
    route3 = chunkToWindow(getRoute(robot3Start, robot3End));
    route1 = chunkToWindow(getRoute(robot1Start, robot2End));
    clearMap();

    if(route1.size() == WINDOW && route2.size() == WINDOW && route3.size() == WINDOW) {
      // Success
      solution.put("robot1", route1);
      solution.put("robot2", route2);
      solution.put("robot3", route3);
      return solution;
    }

    // Priority 3/1/2
    clearMap();
    route3 = chunkToWindow(getRoute(robot3Start, robot3End));
    route1 = chunkToWindow(getRoute(robot1Start, robot2End));
    route2 = chunkToWindow(getRoute(robot2Start, robot2End));
    clearMap();

    if(route1.size() == WINDOW && route2.size() == WINDOW && route3.size() == WINDOW) {
      // Success
      solution.put("robot1", route1);
      solution.put("robot2", route2);
      solution.put("robot3", route3);
      return solution;
    }

    // Priority 3/2/1
    clearMap();
    route3 = chunkToWindow(getRoute(robot3Start, robot3End));
    route2 = chunkToWindow(getRoute(robot2Start, robot2End));
    route1 = chunkToWindow(getRoute(robot1Start, robot2End));
    clearMap();

    if(route1.size() == WINDOW && route2.size() == WINDOW && route3.size() == WINDOW) {
      // Success
      solution.put("robot1", route1);
      solution.put("robot2", route2);
      solution.put("robot3", route3);
      return solution;
    }

    return null;

  }

  public ArrayList<Location> getRoute(Location startLocation, Location endLocation) {

    ArrayList<LocationNode> open_list = new ArrayList<LocationNode>();
    ArrayList<LocationNode> close_list = new ArrayList<LocationNode>();

    LocationNode startNode = new LocationNode(null, startLocation, 0, startLocation.getDistanceFromLocation(endLocation));
    open_list.add(startNode);

    LocationNode chosenNode = null;

    while(!open_list.isEmpty()) {

      chosenNode = open_list.get(0);

      // Check if solution
      if(chosenNode.getLocation().equalsTo(endLocation)) {
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

    if(chosenNode.getLocation().equalsTo(endLocation)) {

      while(chosenNode.getParent() != null) {
        solution.add(0, chosenNode.getLocation());
        chosenNode = chosenNode.getParent();
      }
    }

    solution.add(0, startLocation);

    return solution;
  }

  private ArrayList<LocationNode> getSuccessors(LocationNode node) {
    ArrayList<LocationNode> successors = new ArrayList<LocationNode>();

    int x = node.getLocation().getX();
    int y = node.getLocation().getY();

    if(x + 1 <= maxX && map[x+1][y].getType() == LocationType.EMPTY) {
      successors.add(new LocationNode(node, map[x+1][y], node.getGValue() + 1, 0));
    }
    if(x - 1 >= 0 && map[x-1][y].getType() == LocationType.EMPTY) {
      successors.add(new LocationNode(node, map[x-1][y], node.getGValue() + 1, 0));
    }
    if(y + 1 <= maxY && map[x][y+1].getType() == LocationType.EMPTY) {
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

  public ArrayList<Direction> coordinatesToDirections(ArrayList<Location> locations){

    ArrayList<Direction> directions = new ArrayList<Direction>();

    for(int i=1;i<locations.size(); i++) {
      int diffInX = locations.get(i).getX() - locations.get(i-1).getX();
      int diffInY = locations.get(i).getY() - locations.get(i-1).getY();

      if (diffInX < 0){
  			directions.add(Direction.LEFT);
  		}else if (diffInX > 0){
  			directions.add(Direction.RIGHT);
  		}
  		else if (diffInY < 0){
  			directions.add(Direction.BACKWARDS);
  		}else if (diffInY> 0){
  			directions.add(Direction.FORWARD);
  		}else{
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

  private ArrayList<Location> chunkToWindow(ArrayList<Location> route) {
    ArrayList<Location> solution = new ArrayList<Location>();

    for(int i=0;i<=WINDOW;i++) {
      solution.add(route.get(i));
    }
    return solution;
  }

}

