package motion;

import java.util.ArrayList;

import utils.Direction;

public class RobotMovementSessionManager {

	private ArrayList<Direction> route;
	private boolean shouldExecuteRoute;
	private int numOfPicks;
	private boolean isRouteComplete;
	private boolean atPickup;

	public RobotMovementSessionManager() {
		route = null;
		shouldExecuteRoute = false;
		numOfPicks = 0;
		isRouteComplete = false;
		atPickup = false;
	}

	public synchronized void setRoute(ArrayList<Direction> value) {
		route = value;
	}

	public synchronized ArrayList<Direction> getRoute() {
		return route;
	}

	public synchronized void setShouldExecuteRoute(boolean value) {
		shouldExecuteRoute = value;
	}

	public synchronized boolean getShouldExecuteRoute() {
		return shouldExecuteRoute;
	}

	public void setNumberOfPicks(int readInt) {
		this.numOfPicks = readInt;
	}

	public int getNumberOfPicks() {
		return this.numOfPicks;
	}
	
	public synchronized void setIsRouteComplete(boolean value) {
		isRouteComplete = value;
	}

	public synchronized boolean getIsRouteComplete() {
		return isRouteComplete;
	}

	public void setIsAtPickupLocation(boolean atPickup) {
		this.atPickup = atPickup;
	}
	
	public boolean getIsAtPickupLocation(){
		return this.atPickup;
	}

}
