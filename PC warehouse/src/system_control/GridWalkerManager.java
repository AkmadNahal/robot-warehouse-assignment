package system_control;

import lejos.robotics.RangeFinder;
import rp.robotics.MobileRobotWrapper;
import rp.robotics.mapping.GridMap;
import rp.robotics.mapping.MapUtils;
import rp.robotics.navigation.GridPose;
import rp.robotics.navigation.Heading;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.simulation.MovableRobot;
import rp.robotics.simulation.SimulatedRobots;
import utils.Location;
import utils.LocationType;
import utils.SuperLocation;
import warehouse_interface.GridWalker;
import warehouse_interface.WarehouseController;
import warehouse_interface.WarehouseView;

public class GridWalkerManager {
	
	private GridMap mapModel;
	private PCSessionManager sessionManager;
	private final int ROBOT_COUNT = 1;
	private int mapSizeX;
	private int mapSizeY;
	private int[] robotStartingXCoordinate = new int[]{0, 6, 11};
	
	
	public GridWalkerManager (GridMap mapModel, PCSessionManager sessionManager){
		this.mapModel = mapModel;
		this.sessionManager = sessionManager;
	}
	
	public void setup(){
		mapModel = MapUtils.createRealWarehouse();
		mapSizeX = mapModel.getXSize();
		mapSizeY = mapModel.getYSize();
		
		MapBasedSimulation sim = new MapBasedSimulation(mapModel);
		for (int i = 0; i < ROBOT_COUNT; i++) {
			GridPose gridStart = new GridPose(robotStartingXCoordinate[i], 0, Heading.PLUS_Y);
			MobileRobotWrapper<MovableRobot> wrapper = sim.addRobot(
					SimulatedRobots.makeConfiguration(false, true),
					mapModel.toPose(gridStart));
			RangeFinder ranger = sim.getRanger(wrapper);
			GridWalker controller = new GridWalker(wrapper.getRobot(),
					mapModel, gridStart, ranger, sessionManager);
			new Thread(controller).start();
		}

		WarehouseController control = new WarehouseController(mapModel, sim);
		WarehouseView view = new WarehouseView(ROBOT_COUNT);
		control.registerView(view);
	}
	
	public Location[][] createMap() {
		mapSizeX = mapModel.getXSize();
		mapSizeY = mapModel.getYSize();
		Location[][] map = new Location[mapSizeX+1][mapSizeY+1];
		for(int i=0;i < mapSizeX;i++) {
			for(int j=0;j < mapSizeY;j++) {
				if (mapModel.isValidGridPosition(i, j) && !mapModel.isObstructed(i, j)){
					map[i][j] = new Location(i, j, LocationType.EMPTY);
				} else if (mapModel.isValidGridPosition(i, j)){
					map[i][j] = new Location(i, j, LocationType.BLOCK);
				}
			}
	    }
		
		
		return map;
	}
	
	public int getMapSizeX(){
		return this.mapSizeX;
	}
	
	public int getMapSizeY(){
		return this.mapSizeY;
	}

}
