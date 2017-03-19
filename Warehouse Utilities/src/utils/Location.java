package utils;

public class Location {

  private int x, y;
  private LocationType type;

  public Location(int _x, int _y, LocationType _type) {
    x = _x;
    y = _y;
    type = _type;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public LocationType getType() {
    return type;
  }

  public int getDistanceFromLocation(Location l) {
    return Math.abs(x - l.getX()) + Math.abs(y - l.getY());
  }
  
  public void setType(LocationType value) {
	    type = value;
	  }

  public boolean equalsTo(Location location) {
    return (x == location.getX() && y == location.getY());
  }
  
  @Override
  public String toString() {
	  return ("(" + x + "," + y + ")");
  }
  
  public boolean equals(Location l) {
	  if (l.x == this.x && l.y == this.y) {
		  return true;
	  } else {
		  return false;
	  }
  }

}
