import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import utils.Location;
import utils.LocationType;


public class Main {
	
	private static int maxX = 12;
	private static int maxY = 8;
	private static Location[][] map = new Location[maxX][maxY];
	
	public static void main(String[] args) {
		createMap();

		// Setup robot info and networking
		NXTInfo robotInfo = new NXTInfo (NXTCommFactory.BLUETOOTH, "Lil Yoda",
				"0016531B550D");
		
		//starts the PC sending/receiving thread
		NetworkComm robot = new NetworkComm(robotInfo, map, maxX, maxY);
		robot.run();
	
	}
	
	private static void createMap() {
	    for(int i=0;i<maxX;i++) {
	      for(int j=0;j<maxY;j++) {
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
