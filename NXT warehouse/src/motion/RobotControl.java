package motion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import lejos.nxt.Button;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

public class RobotControl {

	public static void main(String[] args) {

		System.out.println("Waiting for Bluetooth connection...");
		BTConnection connection = Bluetooth.waitForConnection();
		System.out.println("OK!");

		int rand = new Random().nextInt();

		System.out.println("Success!");

		DataInputStream inputStream = connection.openDataInputStream();
		DataOutputStream outputStream = connection.openDataOutputStream();

		boolean run = true;

		ArrayList<Direction> route = new ArrayList<Direction>();
		boolean is_route_income = false;

		while (run) {
			try {
				int input = inputStream.readInt();

				if(input == 9999 && !is_route_income) {
					// Route sending started
					is_route_income = true;
					route = new ArrayList<Direction>();
				} else if(input == 9999 && is_route_income) {
					// Route sending ended, send to robot to execute
					is_route_income = false;
				} else if(is_route_income) {
					route.add(Direction.fromInteger(input));
				}
				
			} catch (IOException e) {
				System.out.println("Exception: " + e.getClass());
				run = false;
			}
		}
	}

}
