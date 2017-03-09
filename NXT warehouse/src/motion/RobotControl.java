package motion;

import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class RobotControl {
	
	private static RouteExecutor routeExecutor = new RouteExecutor();
	private static RobotComm robotComm;
	
	public static void main(String[] args) {

		System.out.println("Waiting for Bluetooth connection...");
		BTConnection connection = Bluetooth.waitForConnection();
		System.out.println("Success!");
		
		robotComm = new RobotComm(connection);
		
		(new Thread(routeExecutor)).start();
		(new Thread(robotComm)).start();

	}
	

}
