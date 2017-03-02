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
		for (Job j : jobs) {
			System.out.print(j);
			System.out.print("\tREWARD:" + j.totalReward());
			System.out.print("\tWEIGHT:" + j.totalWeight());
			System.out.println();
		}
	}
}
