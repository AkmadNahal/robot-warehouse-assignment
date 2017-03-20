package job_selection;

import java.util.*;
import helper_classes.*;

public class Round {
	private ArrayList<Pick> round = new ArrayList<Pick>();
	private final float MAX_WEIGHT;

	public Round(float mw) {
		MAX_WEIGHT = mw;
	}

	public ArrayList<Pick> getRound() {
		return round;
	}

	public boolean addStop(Item item, Integer count) {
		if ((item.getWeight() * count) + this.getWeight() < MAX_WEIGHT) {
			boolean ok = false;
			for (int i = 0; i < round.size() && !ok; i++) {
				if (round.get(i).getItem().equals(item)) {
					round.get(i).setCount(round.get(i).getCount() + count);
					ok = true;
				}
			}
			round.add(new Pick(item, count));
			return true;
		} else {
			return false;
		}
	}

	public ArrayList<Location> getRoute() {
		ArrayList<Location> locations = new ArrayList<Location>();
		for (Pick p : round) {
			locations.add(p.getItem().getLoc());
		}
		return locations;
	}

	public ArrayList<Integer> getCounts() {
		ArrayList<Integer> counts = new ArrayList<Integer>();
		for (Pick p : round) {
			counts.add(p.getCount());
		}
		return counts;
	}

	public float getWeight() {
		float total = 0;
		for (Pick p : round) {
			total += (p.getItem().getWeight() * p.getCount());
		}
		return total;
	}
}
