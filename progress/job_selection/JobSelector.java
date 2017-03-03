package job_selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import rp.util.Collections;

public class JobSelector {
	public static void main(String[] args) {
		String jfile = "progress/job_selection/jobs.csv";
		String wrfile = "progress/job_selection/items.csv";
		String lfile = "progress/job_selection/locations.csv";
		HashMap<String, Item> itemMap = ItemReader.parseItems(wrfile, lfile);
		HashMap<String, Job> jobMap = JobReader.parseJobs(jfile, itemMap);
		
		ArrayList<Job> jobs = new ArrayList<Job>(jobMap.values());
		Collections.sort(jobs);
		//We now have a list of jobs, sorted based on highest total reward.
		
		ArrayList<Round> rounds = new ArrayList<Round>();
		Round currentRound = new Round(50f);
		for (Job j : jobs) {
			for (String s : j.getPicks().keySet()) {
				if (currentRound.addStop(itemMap.get(s), j.getPicks().get(s))) {
				} else {
					currentRound = new Round(50f);
				}
			}
			rounds.add(currentRound);
		}
		
		for (Round r : rounds) {
			System.out.println(r.getRoute());
		}
	}
}
