package route_planning;

import java.util.*;
import helper_classes.Direction;
import helper_classes.Location;
import helper_classes.LocationType;

public class RoutePlanner {

  private Location[][] map;
  private int maxX, maxY;
  private ArrayList<Direction> directions;

  public RoutePlanner(Location[][] _map, int _maxX, int _maxY) {
    map = _map;
    maxX = _maxX;
    maxY = _maxY;
  }
  
  public ArrayList<Direction> getRoute(){
	  return this.directions;
  }

  public ArrayList<Direction> getRoute(Location startLocation, Location endLocation) {

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

    return coordinatesToDirections(solution);
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

  private ArrayList<Direction> coordinatesToDirections(ArrayList<Location> locations){

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
  		}
    }
    this.directions = directions;
    return directions;
	}

}
