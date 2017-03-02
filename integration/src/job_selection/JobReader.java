package job_selection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.io.FileNotFoundException;

public class JobReader {

	public static HashMap<String, Job> parseJobs(String file) {
		BufferedReader reader;
		String splitBy = ",";
		HashMap<String, Job> jobs = new HashMap<String, Job>();

		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			int jobLimit = 100;
			int jobCount = 0;
			while ((line = reader.readLine()) != null && jobCount < jobLimit) {
				String[] job = line.split(splitBy);
				String jobID = job[0];
				Job j = new Job(jobID);
				int numPicks = (job.length - 1)/2;
				for (int i = 0; i < numPicks; i++) {
//					System.out.print(job[(2*i)+1] + ": ");
//					System.out.print(job[(2*i)+2]);
//					System.out.println();
					j.addPick(job[(2*i)+1], Integer.parseInt(job[(2*i)+2]));
				}
				jobs.put(jobID, j);
				jobCount += 1;
			}
			reader.close();
			return jobs;
		} catch (FileNotFoundException e) {
			System.out.println("File does not exist");
			return null;
		} catch (IOException e) {
			System.out.println("IO Failed");
			return null;
		} 
	}
}
