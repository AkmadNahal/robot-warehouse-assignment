package nxt_localisation;

import rp.util.HashMap;

public class LocalisationManager {

	private int counter;
	private DataOfJunction surroundReadings;
	private boolean isCorrectlyExecuted;

	public LocalisationManager() {
		counter = -1;
		surroundReadings = new DataOfJunction();
		isCorrectlyExecuted = true;
	}

	public synchronized void setCounter(int counter) {
		this.counter = counter;
	}

	public synchronized int getCounter() {
		return this.counter;
	}
	
	public DataOfJunction getReadings(){
		return this.surroundReadings;
	}
	
	public void setReadings(DataOfJunction readings){
		this.surroundReadings = readings;
	}
	
	public boolean getCorrectlyExecuted(){
		return this.isCorrectlyExecuted;
	}
	
	public void setCorrectlyExecuted(boolean result){
		this.isCorrectlyExecuted = result;
	}

}
