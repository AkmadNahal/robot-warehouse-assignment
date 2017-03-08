package robot_interface;

public class RobotInterface {
	private static int limit;
	private static int picked;
	private static int locationPick;
	private static int pickedInLocation;
	
	public RobotInterface (int limit) {
		this.limit = limit;
		this.picked = 0;
		this.pickedInLocation = 0;
		this.locationPick = getLocationPick();
	}
	public static void pick(){ 		
		while(true){ 			
			int i = Button.waitForAnyPress(); 
			if(i == Button.ID_ENTER){
				if (pickedInLocation<=locationPick) {
					System.out.println("Amount is picked: " + pickedInLocation);
					System.out.println("Left capacity of the robot: " + (limit - pickedInLocation));
					System.out.println("Please pick " + (locationPick - pickedInLocation) + " items.");
				} else if (pickedInLocation>=locationPick){
					System.out.println("Incorrect amount. Needed to pick in this location, " + locationPick + ". Picked in this location: " + pickedInLocation);
				}
				System.out.println("Right amount picked.");
			}
			if(i == Button.ID_LEFT){
				if (picked > 0) {
					picked--;
					System.out.println("This robot limit is, " + limit + ", now picked, " + picked);
				}
				System.out.println("Amount picked:" + picked);
			}
			if(i == Button.ID_RIGHT){
				picked++;
				if (picked<=limit) {
					System.out.println("Amount picked:" + picked);
				} else {
					System.out.println("This robot limit is, " + limit + ", now picked, " + picked + ". Please remove items." );
				}
			}
			}
		}
	public int getLocationPick() {
		return locationPick;
	}
	public void setLocationPick(int locationPick) {
		this.locationPick = locationPick;
	}
	public void clearPickedinLocation() {
		pickedInLocation = 0;
	}
	public void clearPicked() {
		picked = 0;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	} 
}
