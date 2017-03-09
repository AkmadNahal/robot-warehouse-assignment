package motion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.RConsole;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import utils.Direction;
import utils.Location;
import utils.LocationType;
import utils.SuperLocation;
import utils.Config;

public class RobotControl implements Runnable {

	private RobotMovementSessionManager movementManager;

	private ArrayList<Direction> route = null;
	private boolean shouldSendLocation;
	private boolean m_run;
	private boolean is_route_income;

	public RobotControl(RobotMovementSessionManager _movementManager) {
		movementManager = _movementManager;
		shouldSendLocation = false;
		m_run = true;
		is_route_income = false;
	}

	@Override
	public void run() {

		System.out.println("Waiting for Bluetooth connection...");
		BTConnection connection = Bluetooth.waitForConnection();
		System.out.println("Success!");

		DataInputStream inputStream = connection.openDataInputStream();
		DataOutputStream outputStream = connection.openDataOutputStream();
		
		boolean isExecutingRoute = false;

		while (m_run) {
			try {
				
				if (movementManager.getIsRouteComplete()){
					outputStream.writeInt(50);
					movementManager.setIsRouteComplete(false);
				}else{
					if (!isExecutingRoute){
						int input = inputStream.readInt();
	
						if(input == 99 && !is_route_income) {
							// Route sending started
							is_route_income = true;
							route = new ArrayList<Direction>();
						} else if(input == 99 && is_route_income) {
							//Route sending ended, execute route
							input = inputStream.readInt();
							movementManager.setNumberOfPicks(inputStream.readInt());
							input = inputStream.readInt();
							movementManager.setRoute(route);
							movementManager.setShouldExecuteRoute(true);
							is_route_income = false;
							isExecutingRoute = true;
						} else if(is_route_income) {
							route.add(Direction.fromInteger(input));
						}
					}
				}
			} catch (IOException e) {
				System.out.println("Exception: " + e.getClass());
				m_run = false;
			}
		}

	}

}
