package job_selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class JobSelector {
	public static void main(String[] args) {
		String jfile = "csv/jobs.csv";
		String wrfile = "csv/items.csv";
		String lfile = "csv/locations.csv";
		HashMap<String, Item> itemMap = ItemReader.parseItems(wrfile, lfile);
		HashMap<String, Job> jobMap = JobReader.parseJobs(jfile, itemMap);

		ArrayList<Job> jobs = new ArrayList<Job>(jobMap.values());
		Collections.sort(jobs);
		// We now have a list of jobs, sorted based on highest total reward.

		final float W_LIMIT = 50f;
		ArrayList<Round> rounds = new ArrayList<Round>();
		Round currentRound = new Round(W_LIMIT);
		for (Job j : jobs) {	
			for (String s : j.getPicks().keySet()) {
				if (!currentRound.addStop(itemMap.get(s), j.getPicks().get(s))) {
					rounds.add(currentRound);
					currentRound = new Round(W_LIMIT);
					currentRound.addStop(itemMap.get(s), j.getPicks().get(s));
				}
			}
			rounds.add(currentRound);
			currentRound = new Round(W_LIMIT);
		}

		for (Round r : rounds) {
			System.out.print(r.getRoute() + " ");
			System.out.println(r.getCounts());
		}
	}
}
