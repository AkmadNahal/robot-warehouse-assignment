package warehouse_interface;

import org.apache.log4j.Logger;

import lejos.robotics.RangeFinder;
import rp.robotics.mapping.GridMap;
import rp.robotics.navigation.GridPilot;
import rp.robotics.navigation.GridPose;
import rp.robotics.navigation.Heading;
import rp.robotics.simulation.MovableRobot;
import rp.systems.StoppableRunnable;
import system_control.PCSessionManager;
import utils.Direction;
import utils.GridToDirections;
import utils.Location;

public class GridWalker implements StoppableRunnable {
	private final GridMap m_map;
	private final GridPilot m_pilot;
	private boolean m_running = true;
	private final RangeFinder m_ranger;
	private final MovableRobot m_robot;
	private PCSessionManager sessionManager;
	private Location previousLocation;
	private Direction previousMove;
	
	private static final Logger logger = Logger.getLogger(GridWalker.class);

	public GridWalker(MovableRobot _robot, GridMap _map, GridPose _start,
			RangeFinder _ranger, PCSessionManager sessionManager) {
		this.m_map = _map;
		this.m_pilot = new GridPilot(_robot.getPilot(), _map, _start);
		this.m_pilot.setTravelSpeed(1.5f);
		this.m_pilot.setTurnSpeed(300f);
		this.m_ranger = _ranger;
		this.m_robot = _robot;
		this.sessionManager = sessionManager;
		this.previousLocation = sessionManager.getLocationAccess().getCurrentLocation();
		this.previousMove = Direction.STOP;
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
		Location currentLocation = sessionManager.getLocationAccess().getCurrentLocation();
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
			previousLocation = sessionManager.getLocationAccess().getCurrentLocation();
			
			if(nextMovement == previousMove && nextMovement != Direction.STOP) {
				m_pilot.moveForward();
			} else {
				if (nextMovement == Direction.LEFT) { //re-adjusts pose, just like real robot
					m_pilot.rotatePositive();
					m_pilot.moveForward();
					m_pilot.rotateNegative();
					nextMovement = Direction.STOP;
				} else if (nextMovement == Direction.RIGHT) {
					m_pilot.rotateNegative();
					m_pilot.moveForward();
					m_pilot.rotatePositive();
					nextMovement = Direction.STOP;
				}else if (moveAheadClear() && nextMovement == Direction.FORWARD) {
					m_pilot.moveForward();
					nextMovement = Direction.STOP;
				}else if (nextMovement == Direction.BACKWARDS){
					m_pilot.rotatePositive();
					m_pilot.rotatePositive();
					m_pilot.moveForward();
					m_pilot.rotateNegative();
					m_pilot.rotateNegative();
					nextMovement = Direction.STOP;
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.fatal("ERROR - I/O exception with GUI grid movements");
				e.printStackTrace();
			}
		}
	}
	
	public void changePosition(){
		GridPose pose = new GridPose(sessionManager.getLocationAccess().getCurrentLocation().getX(), 
				sessionManager.getLocationAccess().getCurrentLocation().getY(),  Heading.PLUS_Y);
		m_pilot.setGridPose(pose);
	}

	@Override
	public void stop() {
		m_running = false;
	}

}
