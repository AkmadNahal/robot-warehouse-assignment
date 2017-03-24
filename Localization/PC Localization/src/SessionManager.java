import utils.Direction;

public class SessionManager {
	
	private boolean shouldSend = false;
	private Direction move;
	
	private boolean received = false;
	private DataOfJunction surrounding;
	
	public void setMove(Direction value) {
		move = value;
	}
	
	public Direction getMove() {
		return move;
	}
	
	public void setShouldSend(boolean value) {
		shouldSend = value;
	}
	
	public boolean getShouldSend() {
		return shouldSend;
	}
	
	public void setReceived(boolean value) {
		received = value;
	}
	
	public boolean getReceived() {
		return received;
	}
	
	public DataOfJunction getSurrounding() {
		return surrounding;
	}
	
	public void setSurrounding(int fw, int r, int bck, int l) {
		DataOfJunction dt = new DataOfJunction();
		dt.setyPlus(fw);
		dt.setxPlus(r);
		dt.setyMinus(bck);
		dt.setxMinus(l);
		surrounding = dt;
	}
	
}
