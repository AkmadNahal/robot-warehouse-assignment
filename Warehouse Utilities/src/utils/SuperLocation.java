package utils;

import utils.Location;
import utils.LocationType;

public class SuperLocation {
	
	protected Location currentLocation;
	private Location priorLocation;
	private Location newLocation;
	private int newX;
	private int newY;
	
	public SuperLocation(Location currentLocation){
		this.currentLocation = currentLocation;
	}
	
	public void updateCurrentLocation(Direction move) {
		priorLocation = getCurrentLocation();
		switch(move){
			case FORWARD:
				newY = priorLocation.getY()+1;
				newLocation = new Location(priorLocation.getX(), newY, LocationType.EMPTY);
				setCurrentLocation(newLocation);
				break;
			case BACKWARDS:
				newY = priorLocation.getY() - 1;
				newLocation = new Location(priorLocation.getX(), newY, LocationType.EMPTY);
				setCurrentLocation(newLocation);
				break;
			case LEFT:
				newX = priorLocation.getX() - 1;
				newLocation = new Location(newX, priorLocation.getY(), LocationType.EMPTY);
				setCurrentLocation(newLocation);
				break;
			case RIGHT:
				newX = priorLocation.getX() + 1;
				newLocation = new Location(newX, priorLocation.getY(), LocationType.EMPTY);
				setCurrentLocation(newLocation);
				break;
		}
	}
	
	public Location getCurrentLocation(){
		return this.currentLocation;
	}
	
	public void setCurrentLocation(Location currentLocation){
		this.currentLocation = currentLocation;
	}
}
