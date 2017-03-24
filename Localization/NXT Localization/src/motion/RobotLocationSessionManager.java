package motion;

import localization.DataOfJunction;
import utils.Direction;

public class RobotLocationSessionManager {

	private boolean shouldSendNextMove;
	private Direction nextMove;
	private int counter;
	private DataOfJunction surroundReadings;
	private boolean isCorrectlyExecuted;

	public RobotLocationSessionManager() {
		shouldSendNextMove = false;
		nextMove = null;
		counter = -1;
		surroundReadings = new DataOfJunction();
		isCorrectlyExecuted = true;
	}

	public synchronized void setShouldSendNextMove(boolean value) {
		shouldSendNextMove = value;
	}

	public synchronized boolean getShouldSendNextMove() {
		return shouldSendNextMove;
	}

	public synchronized void setNextMove(Direction value) {
		nextMove = value;
	}

	public synchronized Direction getNextMove() {
		return nextMove;
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
