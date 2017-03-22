package job_selection;

import java.util.HashMap;

public class Job implements Comparable<Job> {
	private String jobID;
	private HashMap<String, Integer> picks = new HashMap<String, Integer>();
	private HashMap<String, Item> itemList;
	private boolean cancelled;
	private boolean predictedCancel;
	private float value;

	public Job(String id, HashMap<String, Item> il, int cancelled) {
		this.jobID = id;
		this.itemList = il;
		if (cancelled == 1) {
			this.cancelled = true;
		} else {
			this.cancelled = false;
		}
	}

	public void setPrediction(boolean prediction) {
		this.predictedCancel = prediction;
	}

	public void addPick(String item, int count) {
		if (picks.containsKey(item)) {
			picks.replace(item, count);
		} else {
			picks.put(item, count);
		}
	}

	public HashMap<String, Integer> getPicks() {
		return picks;
	}

	public float totalReward() {
		float total = 0;
		for (String i : picks.keySet()) {
			total += itemList.get(i).getReward() * picks.get(i);
		}
		return total;
	}

	public float totalWeight() {
		float total = 0;
		for (String i : picks.keySet()) {
			total += itemList.get(i).getWeight() * picks.get(i);
		}
		return total;
	}

	public int totalItems() {
		int total = 0;
		for (Integer i : picks.values()) {
			total += i;
		}
		return total;
	}

	@Override
	public int compareTo(Job j) {
		if (this.getValue() > j.getValue()) {
			return -1;
		} else if (this.getValue() < j.getValue()) {
			return 1;
		} else {
			return 0;
		}
	}

	public float getValue() {
		return this.value;
	}

	public void calcValue() {
		float value = 0;
		value = this.totalReward();

		this.value = value;
	}

	@Override
	public String toString() {
		String output = jobID;
		for (String i : picks.keySet()) {
			output += " " + i + ":" + picks.get(i);
		}
		return output;
	}

	public String getID() {
		return jobID;
	}

	public void setCancelled(boolean c) {
		this.cancelled = c;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}
}
