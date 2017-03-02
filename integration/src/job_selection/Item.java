package job_selection;

import helper_classes.*;

public class Item {
	private String name;
	private float reward;
	private float weight;
	private Location location;
	
	public Item(String n, float r, float w, Location l) {
		this.name = n;
		this.reward = r;
		this.weight = w;
		this.location = l;
	}
	
	public float getReward() {
		return reward;
	}

	public float getWeight() {
		return weight;
	}
	
	public Location getLocation(){
		return this.location;
	}
}
