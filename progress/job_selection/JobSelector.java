package job_selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class JobSelector {
	public static void main(String[] args) {
		Logger log = Logger.getRootLogger();
		log.setAdditivity(false);
		BasicConfigurator.configure();
		
		log.debug("Started job selection...");
		
		String jfile = "csv/jobs.csv";
		String wrfile = "csv/items.csv";
		String lfile = "csv/locations.csv";
		
		log.debug("Started reading item files...");
		HashMap<String, Item> itemMap = ItemReader.parseItems(wrfile, lfile);
		log.debug("Successfully read " + itemMap.size() + " items!");
		log.debug("Started reading job file...");
		HashMap<String, Job> jobMap = JobReader.parseJobs(jfile, itemMap);
		log.debug("Successfully read " + jobMap.size() + " jobs!");

		log.debug("Sorting jobs...");
		ArrayList<Job> jobs = new ArrayList<Job>(jobMap.values());
		Collections.sort(jobs);
		log.debug("Jobs sorted!");

		log.debug("Creating rounds from jobs...");
		ArrayList<Round> rounds = new ArrayList<Round>();
		rounds = RoundCreator.createRounds(50f, itemMap, jobs);
		log.debug("Successfully converted jobs into rounds.");
		log.debug(rounds.size() + " rounds created.");
		
		log.debug("Job selection finished.");
	}
}
