package nxt_localisation;

public class RobotLocationSessionManager {

	private boolean shouldSendNextMove;
	private Direction nextMove;
	private int counter;

	public RobotLocationSessionManager() {
		shouldSendNextMove = false;
		nextMove = null;
		counter = -1;
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

}
