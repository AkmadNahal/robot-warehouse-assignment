package motion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.RConsole;
import utils.Direction;

public class RobotControl implements Runnable {

	private RobotMovementSessionManager movementManager;
	private RobotLocationSessionManager locationManager;

	private ArrayList<Direction> route = null;
	private boolean shouldSendLocation;
	private boolean m_run;
	private boolean is_route_income;

	public RobotControl(RobotMovementSessionManager _movementManager, RobotLocationSessionManager _locationManager) {
		movementManager = _movementManager;
		locationManager = _locationManager;
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

				if(locationManager.getShouldSendNextMove()) {
					outputStream.writeInt(99);
					outputStream.writeInt(locationManager.getNextMove().getValue());
					locationManager.setShouldSendNextMove(false);
				}

				if (movementManager.getIsRouteComplete()){
					System.out.println("Sending 50 to pc");
					isExecutingRoute = false;
					outputStream.writeInt(50);
					outputStream.flush();
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
