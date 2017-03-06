package job_selection;

import java.util.*;
import helper_classes.*;
import utils.Location;

public class Round {
	private HashMap<Item, Integer> round = new HashMap<Item, Integer>();
	private final float MAX_WEIGHT;

	public Round(float mw) {
		MAX_WEIGHT = mw;
	}

	public boolean addStop(Item item, Integer count) {
		if ((item.getWeight() * count) + this.getWeight() < MAX_WEIGHT) {
			if (round.containsKey(item)) {
				round.put(item, round.get(item) + count);
			} else {
				round.put(item, count);
			}
			return true;
		} else {
			return false;
		}
	}

	public ArrayList<Location> getRoute() {
		ArrayList<Location> locations = new ArrayList<Location>();
		for (Item i : round.keySet()) {
			locations.add(i.getLoc());
		}
		return locations;
	}

	public float getWeight() {
		float total = 0;
		for (Item i : round.keySet()) {
			total += (i.getWeight() * round.get(i));
		}
		return total;
	}
}
