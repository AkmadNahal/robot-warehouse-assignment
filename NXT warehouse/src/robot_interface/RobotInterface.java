package robot_interface;

import java.io.PrintStream;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.comm.RConsole;
import motion.RobotMovementSessionManager;

public class RobotInterface implements Runnable{
	private int pickedInLocation;
	private RobotMovementSessionManager movementManager;
	
	public RobotInterface (RobotMovementSessionManager movementManager) {
		this.pickedInLocation = 0;
		this.movementManager = movementManager;
	}
	
	@Override
	public void run(){ 	
		while(true){
			if (movementManager.getIsAtPickupLocation()){
				if (movementManager.getNumberOfPicks()>0) {
					System.out.println("Robot arrived to pick up location. Please pick" + movementManager.getNumberOfPicks());
					while(!movementManager.getIsRouteComplete()){
						int i = Button.waitForAnyPress(); 
						if(i == Button.ID_ENTER){
							if (pickedInLocation< movementManager.getNumberOfPicks()) {
								LCD.clear();
								System.out.println("Incorrect amount");
								System.out.println("Picked: " + pickedInLocation);
								System.out.println("Please pick " + (movementManager.getNumberOfPicks() - pickedInLocation) + " more items.");
							}else if (pickedInLocation>movementManager.getNumberOfPicks()){
								LCD.clear();
								System.out.println("Incorrect amount");
								System.out.println("Picked: " + pickedInLocation);
								System.out.println("Please pick " + (pickedInLocation - movementManager.getNumberOfPicks()) + " less items.");
							}else if (pickedInLocation == movementManager.getNumberOfPicks()){
								LCD.clear();
								System.out.println("Right amount picked.");
								System.out.println("To the next pick!");
								pickedInLocation = 0;
								movementManager.setIsAtPickupLocation(false);
								movementManager.setIsRouteComplete(true);
							}
						}
						if (i == Button.ID_LEFT){
							if (pickedInLocation > 0) {
								pickedInLocation--;
							}
							LCD.clear();
							System.out.println("Picking:" + pickedInLocation);
						}
						if (i == Button.ID_RIGHT){
							pickedInLocation++;
							LCD.clear();
							System.out.println("Picking:" + pickedInLocation);
						}
					}
				} else if (movementManager.getNumberOfPicks() == -1) {
					System.out.println("Robot arrived to drop off location");
					System.out.println("Please press ENTER to unload your items");
					while(!movementManager.getIsRouteComplete()) {
						int i = Button.waitForAnyPress();
						if(i == Button.ID_ENTER){
							LCD.clear();
							System.out.println("Items are unloaded.");
							System.out.println("To the next pick!");
							pickedInLocation = 0;
							movementManager.setIsAtPickupLocation(false);
							movementManager.setIsRouteComplete(true);
						}
					}
				}
				else if (movementManager.getNumberOfPicks() == 0) {
					movementManager.setIsRouteComplete(true);
				}
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
