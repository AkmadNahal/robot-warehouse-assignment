import java.util.ArrayList;

public class Main {

  private static int maxMapSize = 10;
  private static Location[][] map = new Location[maxMapSize + 1][maxMapSize + 1];


  public static void main(String[] args) {
    createMap();

    map[1][1] = new Location(1,1,LocationType.BLOCK);
    map[1][2] = new Location(1,2,LocationType.BLOCK);
    map[2][1] = new Location(2,1,LocationType.BLOCK);

    RoutePlanner planner = new RoutePlanner(map,maxMapSize,maxMapSize);

    ArrayList<Location> solution = planner.getRoute(map[0][0], map[3][1]);

    for(Location location : solution) {
      System.out.println(location.getX() + " - " + location.getY());
    }
  }

  private static void createMap() {
    for(int i=0;i<=maxMapSize;i++) {
      for(int j=0;j<=maxMapSize;j++) {
        map[i][j] = new Location(i,j,LocationType.EMPTY);
      }
    }
  }

}
