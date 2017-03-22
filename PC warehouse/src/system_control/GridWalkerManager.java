package system_control;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import job_selection.Round;
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
import warehouse_interface.GridWalker;
import warehouse_interface.WarehouseController;
import warehouse_interface.WarehouseView;

public class GridWalkerManager {
	
	private GridMap mapModel;
	private final int ROBOT_COUNT = 3;
	private int mapSizeX;
	private int mapSizeY;
	private MapBasedSimulation sim;
	private WarehouseController control;
	
	private static final Logger logger = Logger.getLogger(GridWalkerManager.class);
	
	public GridWalkerManager (GridMap mapModel){
		this.mapModel = mapModel;
	}
	
	public void setup(){
		mapModel = MapUtils.createRealWarehouse();
		mapSizeX = mapModel.getXSize();
		mapSizeY = mapModel.getYSize();
		
		setSim(new MapBasedSimulation(mapModel));
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
	
	public GridWalker startGridWalker(PCSessionManager sessionManager){
		GridPose gridStart = new GridPose(sessionManager.getLocationAccess().getCurrentLocation().getX(),
				sessionManager.getLocationAccess().getCurrentLocation().getY(), Heading.PLUS_Y);
		MobileRobotWrapper<MovableRobot> wrapper = getSim().addRobot(
				SimulatedRobots.makeConfiguration(false, true),
				mapModel.toPose(gridStart));
		RangeFinder ranger = getSim().getRanger(wrapper);
		GridWalker controller = new GridWalker(wrapper.getRobot(),
				mapModel, gridStart, ranger, sessionManager);
		new Thread(controller).start();
		return controller;
	}
	
	public void controllerAndView(GridWalker gridWalker1, GridWalker gridWalker2, GridWalker gridWalker3,
			PCSessionManager sessionManager1, PCSessionManager sessionManager2, PCSessionManager sessionManager3, 
			ArrayList<Round> rounds){
		control = new WarehouseController(mapModel, sim, gridWalker1, gridWalker2, gridWalker3,
				sessionManager1, sessionManager2, sessionManager3, rounds);
		WarehouseView view = new WarehouseView(ROBOT_COUNT);
		control.registerView(view);
	}
	
	public int getMapSizeX(){
		return this.mapSizeX;
	}
	
	public int getMapSizeY(){
		return this.mapSizeY;
	}
	
	public void setSim(MapBasedSimulation sim){
		this.sim = sim;
	}
	
	public MapBasedSimulation getSim(){
		return this.sim;
	}
	
	public WarehouseController getController(){
		return this.control;
	}

}
