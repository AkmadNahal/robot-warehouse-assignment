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
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.RemoveByName;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;

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
		log.debug("Creating WEKA job set ARFF.");
		JobTraining.makeARFF(jfile, itemMap, "files/jobs.arff");
		log.debug("WEKA files succsessfully created.");
		
		try {
			log.debug("Reading ARFF file to training set.");
			DataSource tsource = new DataSource("files/training.arff");
			Instances tdata = tsource.getDataSet();
			tdata.setClass(tdata.attribute(33));
			log.debug("Successfully created training set.");
			
			DataSource jsource = new DataSource("files/jobs.arff");
			Instances jdata = jsource.getDataSet();
			
			
			AttributeSelection aSel = new AttributeSelection();
			aSel.setInputFormat(tdata);
			
			log.debug("Creating discretizer.");
			Discretize d = new Discretize();
			String[] options = {"-R", "30-31", "-precision", "4"};
			d.setOptions(options);
			d.setInputFormat(tdata);
			log.debug("Discretizer made.");
			
			log.debug("Applying Filters");
			Instances newData = Filter.useFilter(tdata, d);
			log.debug("Succesfully applied discretization.");
			newData = Filter.useFilter(newData, aSel);
			log.debug("Succesfully applied attribute selection.");
			
			NaiveBayes classifier = new NaiveBayes();
			classifier.buildClassifier(newData);
			
			ArrayList<String> rm = new ArrayList<String>();
			for (int i = 0; i < jdata.numAttributes(); i++) {
				boolean contains = false;
				for (int j = 0; j < tdata.numAttributes(); j++) {
					if (jdata.attribute(i).name().equals(tdata.attribute(j).name())) {
						System.out.println(jdata.attribute(i).name());
						contains = true;
					}
				}
				if (!contains) {
					rm.add(jdata.attribute(i).name());
				}
			}
			
			RemoveByName rmf = new RemoveByName();
			for (String s : rm) {
				String[] rmoptions = {"-E",s};
				rmf.setOptions(rmoptions);
				Filter.useFilter(jdata, rmf);
			}
			
			System.out.println(jdata.toString());
			
			ArffSaver s = new ArffSaver();
			s.setFile(new File("files/newTraining.arff"));
			s.setInstances(newData);
			s.writeBatch();
			
			log.debug("Finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
