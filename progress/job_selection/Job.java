package job_selection;

import java.util.HashMap;

public class Job {
	private int jobID;
	private HashMap<String, Integer> picks = new HashMap<String, Integer>();
	
	public Job(int id) {
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
}
