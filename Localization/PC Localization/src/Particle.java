

import utils.Location;

public class Particle {

  private Location location;
  private float weight;

  public Particle(Location _location, float weight) {
    location = _location;
  }

  public void setLocation(Location value) {
    location = value;
  }

  public Location getLocation() {
    return location;
  }

  public void setWeight(float value) {
    weight = value;
  }

  public float getWeight() {
    return weight;
  }

}
