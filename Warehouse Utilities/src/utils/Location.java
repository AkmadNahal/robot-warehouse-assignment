package utils;

import java.lang.Math;

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

  public boolean equalsTo(Location location) {
    return (x == location.getX() && y == location.getY());
  }

}
