package job_selection;

import java.util.HashMap;

public class JobSelector {
	public static void main(String[] args) {
		HashMap<Integer, Job> jobs = JobReader.parseJobs("progress/job_selection/jobs.csv");
		
	}
}
