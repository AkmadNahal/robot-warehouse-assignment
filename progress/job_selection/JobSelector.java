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

		ArrayList<Round> rounds = new ArrayList<Round>();
		rounds = RoundCreator.createRounds(50f, itemMap, jobs);

		for (Round r : rounds) {
			System.out.print(r.getRoute() + " ");
			System.out.println(r.getCounts());
		}
	}
}
