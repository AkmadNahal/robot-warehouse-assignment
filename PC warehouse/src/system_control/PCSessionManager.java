package system_control;

import java.util.ArrayList;

import utils.Direction;
import utils.SuperLocation;

public class PCSessionManager {

	private boolean should_send;
	private ArrayList<Direction> route;
	private int numOfPicks;
	private SuperLocation locationAccess;
	private Boolean isRouteComplete;
	private int readValue;
	
	public PCSessionManager(SuperLocation locationAccess){
		this.should_send = false;
		this.route = null;
		this.numOfPicks = 0;
		this.locationAccess = locationAccess;
		
		this.isRouteComplete = false;
		readValue = 10;
	}
	
	public synchronized void setReadValue(int value) {
		readValue = value;
	}

	public synchronized int getReadValue() {
		return readValue;
	}

	public synchronized void setRoute(ArrayList<Direction> value) {
		route = value;
	}

	public synchronized ArrayList<Direction> getRoute() {
		return route;
	}

	public synchronized void setShouldSend(boolean value) {
		should_send = value;
	}

	public synchronized boolean getShouldSend() {
		return should_send;
	}
	
	public synchronized void setNumOfPicks(int numOfPicks) {
		this.numOfPicks = numOfPicks;
	}

	public synchronized int getNumOfPicks() {
		return this.numOfPicks;
	}	

	public synchronized SuperLocation getLocationAccess() {
		return this.locationAccess;
	}
	
	public synchronized void setIsRouteComplete(Boolean value) {
		isRouteComplete = value;
	}

	public synchronized Boolean getIsRouteComplete() {
		return isRouteComplete;
	}
	
}
