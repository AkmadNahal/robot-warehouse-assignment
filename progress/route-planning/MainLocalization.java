public class MainLocalization {

  private static int maxMapSizeX = 12;
  private static int maxMapSizeY = 8;
  private static Location[][] map = new Location[maxMapSizeX][maxMapSizeY];
  private static int WINDOW = 5;

  public static void main(String[][] args) {
    createMap();
    LocalizationMovement movement = new LocalizationMovement();
    Localization localize = new Localization(map, maxMapSizeX, maxMapSizeY, movement);

    Location sol = localize.localize();

    System.out.println("Found this bitch: " + sol.getX() + " - " + sol.getY());
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
