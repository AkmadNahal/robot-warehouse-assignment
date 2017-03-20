package job_selection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;

public class JobReader {

	public static HashMap<String, Job> parseJobs(String file, HashMap<String, Item> il) {
		
		BufferedReader reader;
		String splitBy = ",";
		HashMap<String, Job> jobs = new HashMap<String, Job>();

		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] job = line.split(splitBy);
				String jobID = job[0];
				Job j = new Job(jobID, il, 0);
				int numPicks = (job.length - 1)/2;
				for (int i = 0; i < numPicks; i++) {
//					System.out.print(job[(2*i)+1] + ": ");
//					System.out.print(job[(2*i)+2]);
//					System.out.println();
					j.addPick(job[(2*i)+1], Integer.parseInt(job[(2*i)+2]));
				}
				jobs.put(jobID, j);
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
	
public static HashMap<String, Job> parseJobs(String jfile, String cfile,HashMap<String, Item> il) {
		
		BufferedReader jreader;
		BufferedReader creader;
		String splitBy = ",";
		HashMap<String, Job> jobs = new HashMap<String, Job>();

		try {
			jreader = new BufferedReader(new FileReader(jfile));
			creader = new BufferedReader(new FileReader(cfile));
			String jline;
			String cline;
			while ((jline = jreader.readLine()) != null) {
				cline = creader.readLine();
				String[] job = jline.split(splitBy);
				int cancelled = Integer.parseInt((cline.split(","))[1]);
				String jobID = job[0];
				Job j = new Job(jobID, il, cancelled);
				int numPicks = (job.length - 1)/2;
				for (int i = 0; i < numPicks; i++) {
//					System.out.print(job[(2*i)+1] + ": ");
//					System.out.print(job[(2*i)+2]);
//					System.out.println();
					j.addPick(job[(2*i)+1], Integer.parseInt(job[(2*i)+2]));
				}
				jobs.put(jobID, j);
			}
			jreader.close();
			creader.close();
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
