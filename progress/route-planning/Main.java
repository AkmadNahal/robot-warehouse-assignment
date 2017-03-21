import java.util.ArrayList;
import java.util.HashMap;

import route_planning.TSP;

public class Main {

  private static int maxMapSizeX = 12;
  private static int maxMapSizeY = 8;
  private static Location[][] map = new Location[maxMapSizeX][maxMapSizeY];
  private static int WINDOW = 5;


  public static void main(String[] args) {
    createMap();

    // RoutePlanner planner = new RoutePlanner(map,maxMapSizeX,maxMapSizeY);
    //
    // HashMap<String, ArrayList<Location>> solution = planner.getMultiRobotRoute(map[6][5], map[6][5], map[3][3], map[3][1], map[11][7], map[11][7]);
    //
    // ArrayList<Location> route1 = solution.get("robot1");
    // ArrayList<Location> route2 = solution.get("robot2");
    // ArrayList<Location> route3 = solution.get("robot3");
    //
    //
    // for(int i=0;i<WINDOW;i++) {
    //   System.out.println(route1.get(i).getX() + " - " + route1.get(i).getY() + "  |  " + route2.get(i).getX() + " - " + route2.get(i).getY() + "  |  " + route3.get(i).getX() + " - " + route3.get(i).getY());
    // }

    ArrayList<Location> arr1 = new ArrayList<Location>();
    arr1.add(new Location(0,0,LocationType.EMPTY));
    arr1.add(new Location(6,5,LocationType.EMPTY));
    arr1.add(new Location(3,1,LocationType.EMPTY));
    arr1.add(new Location(0,4,LocationType.EMPTY));
    arr1.add(new Location(3,7,LocationType.EMPTY));
    arr1.add(new Location(4,7,LocationType.EMPTY));

    TSP tsp = new TSP(map, maxMapSizeX, maxMapSizeY);

    ArrayList<Location> solution = tsp.simulateAnnealing(0.7, 200, 2.5, arr1);

    for(int i=0;i<solution.size();i++) {
      System.out.println(solution.get(i).getX() + " - " + solution.get(i).getY());
    }

  }

  private static void createMap() {
    for(int i=0;i<maxMapSizeX;i++) {
      for(int j=0;j<maxMapSizeY;j++) {
        map[i][j] = new Location(i,j,LocationType.EMPTY);
      }
    }

    map[1][1].setType(LocationType.BLOCK);
    map[1][2].setType(LocationType.BLOCK);
    map[1][3].setType(LocationType.BLOCK);
    map[1][4].setType(LocationType.BLOCK);
    map[1][5].setType(LocationType.BLOCK);

    map[4][1].setType(LocationType.BLOCK);
    map[4][2].setType(LocationType.BLOCK);
    map[4][3].setType(LocationType.BLOCK);
    map[4][4].setType(LocationType.BLOCK);
    map[4][5].setType(LocationType.BLOCK);

    map[7][1].setType(LocationType.BLOCK);
    map[7][2].setType(LocationType.BLOCK);
    map[7][3].setType(LocationType.BLOCK);
    map[7][4].setType(LocationType.BLOCK);
    map[7][5].setType(LocationType.BLOCK);

    map[10][1].setType(LocationType.BLOCK);
    map[10][2].setType(LocationType.BLOCK);
    map[10][3].setType(LocationType.BLOCK);
    map[10][4].setType(LocationType.BLOCK);
    map[10][5].setType(LocationType.BLOCK);
  }

}
