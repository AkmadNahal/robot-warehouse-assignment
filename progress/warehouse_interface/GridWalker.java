package warehouseInterface;

import java.util.Scanner;

import lejos.robotics.RangeFinder;
import rp.robotics.mapping.GridMap;
import rp.robotics.navigation.GridPilot;
import rp.robotics.navigation.GridPose;
import rp.robotics.simulation.MovableRobot;
import rp.systems.StoppableRunnable;

public class GridWalker implements StoppableRunnable {
	private final GridMap m_map;
	private final GridPilot m_pilot;
	private boolean m_running = true;
	private final RangeFinder m_ranger;
	private final MovableRobot m_robot;
	private Scanner scanner;

	public GridWalker(MovableRobot _robot, GridMap _map, GridPose _start,
			RangeFinder _ranger) {
		m_map = _map;
		m_pilot = new GridPilot(_robot.getPilot(), _map, _start);
		m_ranger = _ranger;
		m_robot = _robot;
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

	@Override
	public void run() {
		scanner = new Scanner(System.in); // example, we can make it to move and
											// get parameters as you wish.

		while (m_running) {

			int choice = scanner.nextInt();
			if (choice == 1) {
				m_pilot.rotatePositive();
			} else if (choice == 2) {
				m_pilot.rotateNegative();
			}
			if (moveAheadClear()) {
				m_pilot.moveForward();
			}
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop() {
		m_running = false;
	}

}
