package job_selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class JobSelector {
	public static void main(String[] args) {
		String jfile = "progress/job_selection/jobs.csv";
		String wrfile = "progress/job_selection/items.csv";
		String lfile = "progress/job_selection/locations.csv";
		HashMap<String, Item> itemMap = ItemReader.parseItems(wrfile, lfile);
		HashMap<String, Job> jobMap = JobReader.parseJobs(jfile, itemMap);

		ArrayList<Job> jobs = new ArrayList<Job>(jobMap.values());
		Collections.sort(jobs);
		// We now have a list of jobs, sorted based on highest total reward.

		int OW = 0;
		for (Job j : jobs) {
			System.out.print(j);
			System.out.print("Reward: " + j.totalReward());
			System.out.println(" Weight: " + j.totalWeight());
			if (j.totalWeight() > 50) {
				OW += 1;
			}
		}
		System.out.println(OW + "\n");

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

		System.out.println(rounds.size());
		for (Round r : rounds) {
			System.out.print(r.getRoute());
			System.out.print(r.getCounts());
			System.out.println(r.getWeight());
		}
	}
}
