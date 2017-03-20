package utils;

import java.util.ArrayList;
import java.util.Collections;

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

	public static void jamFixer(ArrayList<Location> route1,
			ArrayList<Location> route2/* ,ArrayList<Location> route3 */) {
		int minSize = route1.size() < route2.size() ? route1.size() : route2.size();
		//int minSize = route1.size() < route3.size() ? route1.size() : route3.size();
		//int minSize = route2.size() < route3.size() ? route2.size() : route3.size();
		boolean ok = false;
		
		while(!ok) {
			ok = true;
			for(int i=0;i<minSize;i++) {
				if(route1.get(i).equalsTo(route2.get(i))) {
					ok = false;
					int j = i + 1;
					while(route1.get(i).equalsTo(route2.get(j))) {
						j++;
					}
					
					if(j < route2.size()) {
						Collections.swap(route2, i, j);
					} else {
						j = i+1;
						while(route2.get(i).equalsTo(route1.get(j))) {
							j++;
						}
						
						if(j < route1.size()) {
							Collections.swap(route1, i, j);
						}
					}
				}
			}
			
		}
		
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
