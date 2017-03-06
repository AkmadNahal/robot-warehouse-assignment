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
		while (run) {
			try {
				int input = inputStream.readInt();
				if (input != 0) {
					Button.waitForAnyPress();
					outputStream.writeInt(rand);
					outputStream.flush();
				} else {
					run = false;
				}
			} catch (IOException e) {
				System.out.println("Exception: " + e.getClass());
				run = false;
			}
		}
	}

}

