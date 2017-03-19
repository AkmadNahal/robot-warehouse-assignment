package warehouse_interface;

import lejos.robotics.RangeFinder;
import rp.robotics.MobileRobotWrapper;
import rp.robotics.mapping.GridMap;
import rp.robotics.mapping.MapUtils;
import rp.robotics.navigation.GridPose;
import rp.robotics.navigation.Heading;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.simulation.MovableRobot;
import rp.robotics.simulation.SimulatedRobots;

public class Main {

	private static final int ROBOT_COUNT = 3;

	public static void main(String[] args) {
		GridMap mapModel = MapUtils.createRealWarehouse();
		MapBasedSimulation sim = startSimulation(mapModel, ROBOT_COUNT);
		WarehouseController control = new WarehouseController(mapModel, sim);
		WarehouseView view = new WarehouseView(ROBOT_COUNT);
		control.registerView(view);
	}

	public static MapBasedSimulation startSimulation(GridMap mapModel,
			int robotCount) {
		MapBasedSimulation sim = new MapBasedSimulation(mapModel);
		for (int i = 0; i < robotCount; i++) {
			GridPose gridStart = new GridPose(0, 0, Heading.PLUS_Y);
			MobileRobotWrapper<MovableRobot> wrapper = sim.addRobot(
					SimulatedRobots.makeConfiguration(false, true),
					mapModel.toPose(gridStart));
			RangeFinder ranger = sim.getRanger(wrapper);
			GridWalker controller = new GridWalker(wrapper.getRobot(),
					mapModel, gridStart, ranger);
			new Thread(controller).start();
		}
		return sim;
	}
}
