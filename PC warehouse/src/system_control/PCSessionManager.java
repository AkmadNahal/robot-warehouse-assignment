package system_control;

import java.util.ArrayList;

import job_selection.Round;
import utils.Direction;
import utils.Location;
import utils.SuperLocation;

public class PCSessionManager {

	private boolean should_send;
	private ArrayList<Direction> route;
	private int numOfPicks;
	private SuperLocation locationAccess;
	private Boolean isRouteComplete;
	private Location pickLocation;
	private Round currentRound;
	private String robotName;
	private float currentWeight;
	private ArrayList<Round> cancelledRounds;
	
	public PCSessionManager(SuperLocation locationAccess){
		this.should_send = false;
		this.route = null;
		this.numOfPicks = 0;
		this.locationAccess = locationAccess;
		this.isRouteComplete = false;
		this.pickLocation = null;
		this.currentRound = null;
		this.robotName = "";
		this.currentWeight = 0f;
		this.cancelledRounds = null;
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
	
	public synchronized void setPickLocation(Location pickLocation) {
		this.pickLocation = pickLocation;
	}

	public synchronized Location getPickLocation() {
		return this.pickLocation;
	}
	
	public synchronized void setCurrentRound(Round currentRound) {
		this.currentRound = currentRound;
	}

	public synchronized Round getCurrentRound() {
		return this.currentRound;
	}
	
	public synchronized void setRobotName(String robotName) {
		this.robotName = robotName;
	}

	public synchronized String getRobotName() {
		return this.robotName;
	}
	
	public synchronized void setCurrentWeight(float currentWeight){
		this.currentWeight = currentWeight;
	}
	
	public synchronized float getCurrentWeight(){
		return this.currentWeight;
	}
	
	public synchronized void setCancelledRounds(ArrayList<Round> cancelledRounds){
		this.cancelledRounds = cancelledRounds;
	}
	
	public synchronized ArrayList<Round> getCancelledRounds(){
		return this.cancelledRounds;
	}
	
}
