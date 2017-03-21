package nxt_localisation;

public class DataOfJunction {
	private int x;
	private int y;
	private int xPlus;
	private int xMinus;
	private int yPlus;
	private int yMinus;
	private LocationType type;
	
	public DataOfJunction(int x, int y, LocationType type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public float getxPlus() {
		return xPlus;
	}

	public void setxPlus(int xPlus) {
		this.xPlus = xPlus;
	}

	public float getxMinus() {
		return xMinus;
	}

	public void setxMinus(int xMinus) {
		this.xMinus = xMinus;
	}

	public float getyPlus() {
		return yPlus;
	}

	public void setyPlus(int yPlus) {
		this.yPlus = yPlus;
	}

	public float getyMinus() {
		return yMinus;
	}

	public void setyMinus(int yMinus) {
		this.yMinus = yMinus;
	}
	public LocationType getType() {
	    return type;
	  }

	public void setType(LocationType value) {
	    type = value;
	 }
}
