package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
			ArrayList<Location> route2, ArrayList<Location> route3) {
		
		ArrayList<Location> shortestRoute = new ArrayList<Location>();
		ArrayList<Location> middleRoute = new ArrayList<Location>();
		ArrayList<Location> longestRoute = new ArrayList<Location>();
				
		if(route1.size() < route2.size()) {
			if(route1.size() < route3.size()) {
				shortestRoute = route1;
				middleRoute = route3;
				longestRoute = route2;
			} else {
				shortestRoute = route3;
				middleRoute = route1;
				longestRoute = route2;
			}
		} else {
			if(route2.size() < route3.size()) {
				shortestRoute = route2;
				if (route1.size() < route3.size()){
					middleRoute = route1;
					longestRoute = route3;
				}else{
					middleRoute = route3;
					longestRoute = route1;
				}
			} else {
				shortestRoute = route3;
				middleRoute = route2;
				longestRoute = route1;
			}
		}
		
		boolean ok = false;
		
		while(!ok) {
			ok = true;
			for(int i=0;i<shortestRoute.size();i++) {
				if(route1.get(i).equalsTo(route2.get(i))) {
					System.out.println("hello");
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
				
				if(route1.get(i).equalsTo(route3.get(i))) {
					System.out.println("hello2");
					ok = false;
					int j = i + 1;
					while(route1.get(i).equalsTo(route3.get(j))) {
						j++;
					}
					
					if(j < route3.size()) {
						Collections.swap(route3, i, j);
					} else {
						j = i+1;
						while(route3.get(i).equalsTo(route1.get(j))) {
							j++;
						}
						
						if(j < route1.size()) {
							Collections.swap(route1, i, j);
						}
					}
				}
				
				if(route3.get(i).equalsTo(route2.get(i))) {
					System.out.println("hello3");
					ok = false;
					int j = i + 1;
					while(route3.get(i).equalsTo(route2.get(j))) {
						j++;
					}
					
					if(j < route2.size()) {
						Collections.swap(route2, i, j);
					} else {
						j = i+1;
						while(route2.get(i).equalsTo(route3.get(j))) {
							j++;
						}
						
						if(j < route3.size()) {
							Collections.swap(route3, i, j);
						}
					}
				}
			}
			for(int i=0;i<middleRoute.size();i++) {
				if(middleRoute.get(i).equalsTo(longestRoute.get(i))) {
					System.out.println("hello");
					ok = false;
					int j = i + 1;
					while(middleRoute.get(i).equalsTo(longestRoute.get(j))) {
						j++;
					}
					
					if(j < longestRoute.size()) {
						Collections.swap(longestRoute, i, j);
					} else {
						j = i+1;
						while(longestRoute.get(i).equalsTo(middleRoute.get(j))) {
							j++;
						}
						
						if(j < middleRoute.size()) {
							Collections.swap(middleRoute, i, j);
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
