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

public class RobotControl {
	
	private static Config config = new Config();
	private static RouteExecutor routeExecutor = new RouteExecutor();
	
	
	
	private static boolean shouldSendLocation = false;

	public static void main(String[] args) {
		(new Thread(routeExecutor)).start();
		ArrayList<Direction> route = new ArrayList<Direction>();

		System.out.println("Waiting for Bluetooth connection...");
		BTConnection connection = Bluetooth.waitForConnection();
		System.out.println("OK!");

		System.out.println("Success!");

		DataInputStream inputStream = connection.openDataInputStream();
		DataOutputStream outputStream = connection.openDataOutputStream();

		boolean run = true;
		boolean is_route_income = false;

		while (run) {
			try {
				if (routeExecutor.getHitJunct() && !is_route_income){
					System.out.println("NOOT NOOT");
					outputStream.writeInt(99);
					outputStream.flush();
					routeExecutor.setHitJunct(false);
				}else {
				
						int input = inputStream.readInt();
					
					System.out.println(routeExecutor.getHitJunct() + ": Hit junction in robot control");
					
					if(input == 99 && !is_route_income) {
						// Route sending started
						is_route_income = true;
						route = new ArrayList<Direction>();
					} else if(input == 99 && is_route_income) {
						// Route sending ended, execute route
						routeExecutor.setRoute(route);
						routeExecutor.setShouldExecute(true);
						if (!routeExecutor.getShouldExecute())
								
						is_route_income = false;
					} else if(is_route_income) {
						route.add(Direction.fromInteger(input));
					}
				}
				
			} catch (IOException e) {
				System.out.println("Exception: " + e.getClass());
				run = false;
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
