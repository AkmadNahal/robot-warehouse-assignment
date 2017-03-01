package helper_classes;

public class SuperLocation {
	
	protected int[] currentLocation;
	private int[] priorLocation;
	private int[] newLocation;
	private int newX;
	private int newY;
	
	public void updateCurrentLocation(Direction move) {
		priorLocation = getCurrentLocation();
		switch(move){
			case FORWARD:
				newY = priorLocation[1] + 1;
				newLocation = new int[]{priorLocation[0], newY};
				setCurrentLocation(newLocation);
				break;
			case BACKWARDS:
				newY = priorLocation[1] - 1;
				newLocation = new int[]{priorLocation[0], newY};
				setCurrentLocation(newLocation);
				break;
			case LEFT:
				newX = priorLocation[1] + 1;
				newLocation = new int[]{newX, priorLocation[1]};
				setCurrentLocation(newLocation);
				break;
			case RIGHT:
				newX = priorLocation[1] - 1;
				newLocation = new int[]{newX, priorLocation[1]};
				setCurrentLocation(newLocation);
				break;
		}
	}
	
	public int[] getCurrentLocation(){
		return this.currentLocation;
	}
	
	public void setCurrentLocation(int[] currentLocation){
		this.currentLocation = currentLocation;
	}
}
