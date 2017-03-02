package job_selection;

import java.util.HashMap;

public class Job {
	private String jobID;
	private HashMap<String, Integer> picks = new HashMap<String, Integer>();
	
	public Job(String id) {
		this.jobID = id;
	}
	
	public void addPick(String item, int count) {
		if (picks.containsKey(item)) {
			picks.replace(item, count);
		}
		else {
			picks.put(item, count);
		}
	}

	public float getReward() {
		
	}
}
