import java.util.ArrayList;
import java.util.HashMap;

public class Main {

  private static int maxMapSizeX = 12;
  private static int maxMapSizeY = 8;
  private static Location[][] map = new Location[maxMapSizeX + 1][maxMapSizeY + 1];


  public static void main(String[] args) {
    createMap();

    RoutePlanner planner = new RoutePlanner(map,maxMapSizeX,maxMapSizeY);

    HashMap<String, ArrayList<Location>> sol = planner.getMultiRobotRoute(map[0][0], map[4][6], map[4][0], map[2][7]);

  }

  private static void createMap() {
    for(int i=0;i<=maxMapSizeX;i++) {
      for(int j=0;j<=maxMapSizeY;j++) {
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
