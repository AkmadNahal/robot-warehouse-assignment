package job_selection;

import java.util.*;
import helper_classes.*;

public class Round {
	private HashMap<Item, Integer> round = new HashMap<Item, Integer>();
	
	public addStop(Item item, Integer count) {
		if(round.containsKey(item)) {
			round.put(item, round.get(item) + count);
		} else {
			round.put(item, count);
		}
	}
	
	public ArrayList<Location> route() {
		ArrayList<Location> locations = new ArrayList<Location>();
		for (Item i : round.keySet()) {
			locations.add(i.getLoc());
		}
		return locations;
	}
}
