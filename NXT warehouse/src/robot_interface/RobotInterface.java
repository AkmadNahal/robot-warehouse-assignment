package robot_interface;

import lejos.nxt.Button;
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
				while(!movementManager.getIsRouteComplete()){
					int i = Button.waitForAnyPress(); 
					if(i == Button.ID_ENTER){
						if (pickedInLocation< movementManager.getNumberOfPicks()) {
							System.out.println("Amount is picked: " + pickedInLocation);
							System.out.println("Please pick " + (movementManager.getNumberOfPicks() - pickedInLocation) + " items.");
						}else if (pickedInLocation>movementManager.getNumberOfPicks()){
							System.out.println("Incorrect amount. Needed to pick in this location, " + movementManager.getNumberOfPicks() + ". Picked in this location: " + pickedInLocation);
						}else if (pickedInLocation == movementManager.getNumberOfPicks()){
							System.out.println("Right amount picked.");
							movementManager.setIsAtPickupLocation(false);
							movementManager.setIsRouteComplete(true);
						}
					}
					if (i == Button.ID_LEFT){
						if (pickedInLocation > 0) {
							pickedInLocation--;
						}
						System.out.println("Amount picked:" + pickedInLocation);
					}
					if (i == Button.ID_RIGHT){
						pickedInLocation++;
						System.out.println("Amount picked:" + pickedInLocation);
					}
				}
			}
		}
	}
}
