package warehouse_interface;

import java.util.Scanner;

import lejos.robotics.RangeFinder;
import rp.robotics.mapping.GridMap;
import rp.robotics.navigation.GridPilot;
import rp.robotics.navigation.GridPose;
import rp.robotics.simulation.MovableRobot;
import rp.systems.StoppableRunnable;
import utils.Direction;
import utils.GridToDirections;
import utils.Location;
import utils.SuperLocation;

public class GridWalker implements StoppableRunnable {
	private final GridMap m_map;
	private final GridPilot m_pilot;
	private boolean m_running = true;
	private final RangeFinder m_ranger;
	private final MovableRobot m_robot;
	private SuperLocation locationAccess;
	private Location previousLocation;

	public GridWalker(MovableRobot _robot, GridMap _map, GridPose _start,
			RangeFinder _ranger, SuperLocation _locationAccess) {
		this.m_map = _map;
		this.m_pilot = new GridPilot(_robot.getPilot(), _map, _start);
		this.m_pilot.setTravelSpeed(0.6f);
		this.m_pilot.setTurnSpeed(60f);
		this.m_ranger = _ranger;
		this.m_robot = _robot;
		this.locationAccess = _locationAccess;
		this.previousLocation = locationAccess.getCurrentLocation();
	}

	private boolean enoughSpace() {
		return m_ranger.getRange() > m_map.getCellSize()
				+ m_robot.getRobotLength() / 2f;
	}

	private boolean moveAheadClear() {
		GridPose current = m_pilot.getGridPose();
		GridPose moved = current.clone();
		moved.moveUpdate();
		return m_map.isValidTransition(current.getPosition(),
				moved.getPosition())
				&& enoughSpace();
	}
	
	public Direction updateGUILocation(){
		GridToDirections converter = new GridToDirections();
		Location currentLocation = locationAccess.getCurrentLocation();
		if (!(previousLocation.equals(currentLocation))){
			return converter.coordinatesToDirections(previousLocation, currentLocation);
		}else{
			return Direction.STOP;
		}
	}

	@Override
	public void run() {

		while (m_running) {
			
			Direction nextMovement = updateGUILocation();
			
			if (nextMovement == Direction.LEFT) { //re-adjusts pose, just like real robot
				m_pilot.rotatePositive();
				m_pilot.moveForward();
				m_pilot.rotateNegative();
			} else if (nextMovement == Direction.RIGHT) {
				m_pilot.rotateNegative();
				m_pilot.moveForward();
				m_pilot.rotatePositive();
			}else if (moveAheadClear() && nextMovement == Direction.FORWARD) {
				m_pilot.moveForward();
			}else if (nextMovement == Direction.BACKWARDS){
				m_pilot.rotatePositive();
				m_pilot.rotatePositive();
				m_pilot.moveForward();
				m_pilot.rotateNegative();
				m_pilot.rotateNegative();
			}
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop() {
		m_running = false;
	}

}
