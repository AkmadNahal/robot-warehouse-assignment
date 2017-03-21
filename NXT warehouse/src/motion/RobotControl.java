package motion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.RConsole;
import lejos.util.Delay;
import utils.Direction;

public class RobotControl implements Runnable {

	private RobotMovementSessionManager movementManager;
	private RobotLocationSessionManager locationManager;

	private ArrayList<Direction> route = null;
	private boolean m_run;
	private boolean is_route_income;
	
	private final int SEND_MOVE_FLAG = 99; //matches with RECEIVE_MOVE_FLAG in NetworkComm.java
	private final int COMPLETE_ROUTE_FLAG = 50; //matches with COMPLETE_ROUTE_FLAG in NetworkComm.java
	private final int RECEIVING_FLAG = 75; //matches with SENDING_FLAG in NetworkComm.java
	private final int AT_PICKUP_FLAG = 33; //matches with AT_PICKUP_FLAG in NetworkComm.java

	public RobotControl(RobotMovementSessionManager _movementManager, RobotLocationSessionManager _locationManager) {
		movementManager = _movementManager;
		locationManager = _locationManager;
		m_run = true;
		is_route_income = false;
	}

	@Override
	public void run() {

		LCD.clear();
		System.out.println("Waiting for Bluetooth connection...");
		BTConnection connection = Bluetooth.waitForConnection();
		LCD.clear();
		System.out.println("Success!\n\n");

		DataInputStream inputStream = connection.openDataInputStream();
		DataOutputStream outputStream = connection.openDataOutputStream();

		boolean isExecutingRoute = false;

		while (m_run) {
			try {

				if(locationManager.getShouldSendNextMove()) {
					outputStream.writeInt(SEND_MOVE_FLAG);
					outputStream.writeInt(locationManager.getNextMove().getValue());
					outputStream.flush();
					locationManager.setShouldSendNextMove(false);
				}

				if (movementManager.getIsRouteComplete()){
					isExecutingRoute = false;
					outputStream.writeInt(COMPLETE_ROUTE_FLAG);
					outputStream.flush();
					movementManager.setIsRouteComplete(false);
				}else{
					if (!isExecutingRoute){
						int input = inputStream.readInt();

						if(input == RECEIVING_FLAG && !is_route_income) {
							// Route sending started
							is_route_income = true;
							route = new ArrayList<Direction>();
						} else if(input == RECEIVING_FLAG && is_route_income) {
							//Route sending ended, execute route
							input = inputStream.readInt(); //matches with SENDING_PICK_NUM_FLAG in RobotControl.java
							movementManager.setNumberOfPicks(inputStream.readInt());
							input = inputStream.readInt();
							movementManager.setRoute(route);
							movementManager.setShouldExecuteRoute(true);
							is_route_income = false;
							isExecutingRoute = true;
						} else if(is_route_income) {
							route.add(Direction.fromInteger(input));
						} else if (input == AT_PICKUP_FLAG){
							isExecutingRoute = true;
							movementManager.setIsAtPickupLocation(true);
						}
					}
				}
			} catch (IOException e) {
				System.out.println("Exception: " + e.getClass());
				m_run = false;
			}
		}

	}

	protected static void redirectOutput(boolean _useBluetooth) {
		if (!RConsole.isOpen()) {
			if (_useBluetooth) {
				RConsole.openBluetooth(0);
			} else {
				RConsole.openUSB(0);
			}
		}
		PrintStream ps = RConsole.getPrintStream();
		System.setOut(ps);
		System.setErr(ps);
	}

}
