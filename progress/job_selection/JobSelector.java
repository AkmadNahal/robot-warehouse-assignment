package job_selection;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

public class JobSelector {
	public static void main(String[] args) {
		Logger log = Logger.getRootLogger();
		log.setAdditivity(false);
		BasicConfigurator.configure();
		
		log.debug("Started job selection...");
		
		String jfile = "files/jobs.csv";
		String trainfile = "files/training_jobs.csv";
		
		String wrfile = "files/items.csv";
		String lfile = "files/locations.csv";
		
		String cfile = "files/cancellations.csv";
		
		
		
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
		
		log.debug("Creating WEKA training set ARFF.");
		JobTraining.makeARFF(trainfile, cfile, itemMap, "files/training.arff");
		log.debug("Creating WEKA test set ARFF.");
		JobTraining.makeARFF(jfile, itemMap, "files/jobs.arff");
		log.debug("WEKA files succsessfully created.");
		
		try {
			log.debug("Reading ARFF file to training set.");
			DataSource tsource = new DataSource("files/training.arff");
			Instances tdata = tsource.getDataSet();
			log.debug("Successfully created training set.");
			
//			log.debug("Discretizing Reward and Weight attributes.");
//			Discretize d = new Discretize();
//			d.setInputFormat(tdata);
//			String[] options = {"-B", "20", "-R", "31-32"};
//			d.setOptions(options);
//			
//			Instances newData = Filter.useFilter(tdata, d);
			
			
			
			ArffSaver s = new ArffSaver();
			s.setFile(new File("files/newTraining.arff"));
			s.setInstances(tdata);
			s.writeBatch();
			
			
		} catch (Exception e) {
		}
	}
}
