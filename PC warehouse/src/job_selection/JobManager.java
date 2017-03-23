package job_selection;

import java.util.ArrayList;

import utils.Location;
import utils.LocationType;
import route_planning.RoutePlanner;
import route_planning.Travel;
import route_planning.TSP;

public class JobManager {
	
	private int maxMapSizeX = 12;
	private int maxMapSizeY = 8;
	private Location[][] map;
	private RoutePlanner planner;
	private TSP tsp;
	private Travel travel;
	
	public JobManager() {
		createMap();
		planner = new RoutePlanner(map, maxMapSizeX, maxMapSizeY);
		tsp = new TSP(map, maxMapSizeX, maxMapSizeY);
		travel = new Travel(new ArrayList<Location>(), planner);
	}
	
	public int getBestDistance(ArrayList<Location> locations) {
		tsp.simulateAnnealing(locations);
		return travel.getDistance(locations);
	}
	
	private void createMap() {
		map = new Location[maxMapSizeX][maxMapSizeY];
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
