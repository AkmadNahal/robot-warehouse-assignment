package job_selection;

import java.util.HashMap;

public class JobSelector {
	public static void main(String[] args) {
		String jfile = "progress/job_selection/jobs.csv";
		String wrfile = "progress/job_selection/items.csv";
		String lfile = "progress/job_selection/locations.csv";
		HashMap<String, Job> jobs = JobReader.parseJobs(jfile);
		HashMap<String, Item> items = ItemReader.parseItems(wrfile, lfile);
		
		
	}
}
