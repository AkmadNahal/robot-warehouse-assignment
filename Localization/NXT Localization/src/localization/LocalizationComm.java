package localization;

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
import motion.RobotLocationSessionManager;
import motion.RobotMovementSessionManager;
import utils.Direction;

public class LocalizationComm {

	private final int YES = 10;
	private final int NO = 11;
	private final int SURROUNDING = 60;
	private final int FORWARD = 20;
	private final int RIGHT = 30;
	private final int BACKWARD = 40;
	private final int LEFT = 50;

	private RobotMovementSessionManager movementManager;
	private RobotLocationSessionManager locationManager;

	private ArrayList<Direction> route = null;
	private boolean m_run;
	private boolean is_route_income;

	private int calVal;
	private LocalisationMovement movement;

	private final int SEND_MOVE_FLAG = 99; // matches with RECEIVE_MOVE_FLAG in
											// NetworkComm.java
	private final int COMPLETE_ROUTE_FLAG = 50; // matches with
												// COMPLETE_ROUTE_FLAG in
												// NetworkComm.java
	private final int RECEIVING_FLAG = 75; // matches with SENDING_FLAG in
											// NetworkComm.java
	private final int AT_PICKUP_FLAG = 33; // matches with AT_PICKUP_FLAG in
											// NetworkComm.java

	public LocalizationComm(RobotMovementSessionManager _movementManager, RobotLocationSessionManager _locationManager,
			int x) {
		movementManager = _movementManager;
		locationManager = _locationManager;
		m_run = true;
		is_route_income = false;
		calVal = x;
		movement = new LocalisationMovement(calVal);
	}

	public void run() {

		LCD.clear();
		System.out.println("Waiting for Bluetooth connection...");
		BTConnection connection = Bluetooth.waitForConnection();
		LCD.clear();
		System.out.println("Success!\n\n");

		DataInputStream inputStream = connection.openDataInputStream();
		DataOutputStream outputStream = connection.openDataOutputStream();

		while (true) {
			try {
				int query = inputStream.readInt();

				if (query == FORWARD) {
					System.out.println("Got forward");
					if (movement.executeMove(Direction.FORWARD)) {
						outputStream.writeInt(YES);
						outputStream.flush();
					} else {
						outputStream.writeInt(NO);
						outputStream.flush();
					}
				} else if (query == RIGHT) {
					if (movement.executeMove(Direction.RIGHT)) {
						outputStream.writeInt(YES);
						outputStream.flush();
					} else {
						outputStream.writeInt(NO);
						outputStream.flush();
					}
				} else if (query == BACKWARD) {
					if (movement.executeMove(Direction.BACKWARDS)) {
						outputStream.writeInt(YES);
						outputStream.flush();
					} else {
						outputStream.writeInt(NO);
						outputStream.flush();
					}
				} else if (query == LEFT) {
					if (movement.executeMove(Direction.LEFT)) {
						outputStream.writeInt(YES);
						outputStream.flush();
					} else {
						outputStream.writeInt(NO);
						outputStream.flush();
					}
				} else if (query == SURROUNDING) {
					System.out.println("Want surrounding");
					DataOfJunction surr = movement.readSurroundings();

					outputStream.writeInt(surr.getyPlus());
					outputStream.writeInt(surr.getxPlus());
					outputStream.writeInt(surr.getyMinus());
					outputStream.writeInt(surr.getxMinus());
					outputStream.flush();
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
