package route_planning;

import helper_classes.Location;

public class LocationNode {

  private LocationNode parent;
  private Location location;
  private int gValue,hValue;

  public LocationNode(LocationNode _parent, Location _location, int _gValue, int _hValue) {
    parent = _parent;
    location = _location;
    gValue = _gValue;
    hValue = _hValue;
  }

  // Getters & Setters

  public LocationNode getParent() {
    return parent;
  }

  public void setParent(LocationNode node) {
    parent = node;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location l) {
    location = l;
  }

  public int getGValue() {
    return gValue;
  }

  public void setGValue(int value) {
    gValue = value;
  }

  public int getHValue() {
    return hValue;
  }

  public void setHValue(int value) {
    hValue = value;
  }

  public int getFValue() {
    return gValue + hValue;
  }

}
