package system_control;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.log4j.Logger;

import job_selection.Item;
import job_selection.ItemReader;
import job_selection.Job;
import job_selection.JobReader;
import job_selection.JobTraining;
import job_selection.Round;
import job_selection.RoundCreator;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import route_planning.RoutePlanner;
import route_planning.TSP;
import rp.robotics.mapping.MapUtils;
import utils.Location;
import utils.LocationType;
import utils.SuperLocation;
import warehouse_interface.GridWalker;
import weka.attributeSelection.GainRatioAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.REPTree;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;



public class SystemControl {

	private static final Logger logger = Logger.getLogger(SystemControl.class);
	
	public static void main(String[] args) {

		SuperLocation locationAccess1 = new SuperLocation(new Location(0, 0, LocationType.EMPTY)); //start location, ROBOT 1
		SuperLocation locationAccess2 = new SuperLocation(new Location(6, 0, LocationType.EMPTY)); //start location, ROBOT 2
		SuperLocation locationAccess3 = new SuperLocation(new Location(11, 0, LocationType.EMPTY)); //start location, ROBOT 3
		
		PCSessionManager sessionManager1 = new PCSessionManager(locationAccess1);
		ChangeNotifier notifier1 = new ChangeNotifier();
		
		PCSessionManager sessionManager2 = new PCSessionManager(locationAccess2);
		ChangeNotifier notifier2 = new ChangeNotifier();
		
		PCSessionManager sessionManager3 = new PCSessionManager(locationAccess3);
		ChangeNotifier notifier3 = new ChangeNotifier();
		
		ArrayList<Round> rounds = orderJobs(sessionManager1);
		
		GridWalkerManager gridWalkerManager = new GridWalkerManager(MapUtils.createRealWarehouse());
		gridWalkerManager.setup();
		GridWalker gridWalker1 = gridWalkerManager.startGridWalker(sessionManager1);
		GridWalker gridWalker2 = gridWalkerManager.startGridWalker(sessionManager2);
		GridWalker gridWalker3 = gridWalkerManager.startGridWalker(sessionManager3);
		gridWalkerManager.controllerAndView(gridWalker1, gridWalker2, gridWalker3, sessionManager1, sessionManager2, sessionManager3, rounds);
		Location[][] map = gridWalkerManager.createMap();
		
		logger.debug("Successfully set up map");
		
		sessionManager1.setRobotName("Lil' Bob");
		sessionManager2.setRobotName("Lil' Vader");
		sessionManager3.setRobotName("Lil' Yoda");


		// Setup robot info and networking
		NXTInfo robot1Info = new NXTInfo (NXTCommFactory.BLUETOOTH, sessionManager1.getRobotName(),
			"0016531AF650");
		NXTInfo robot2Info = new NXTInfo (NXTCommFactory.BLUETOOTH, sessionManager2.getRobotName(),
				"00165308E541");
		NXTInfo robot3Info = new NXTInfo (NXTCommFactory.BLUETOOTH, sessionManager3.getRobotName(),
				"0016531B550D");
		
		//starts the PC sending/receiving thread
		NetworkComm robot1 = new NetworkComm(robot1Info, sessionManager1, notifier1);
		(new Thread(robot1)).start();
		
		logger.debug("Successfully connected to Lil' Bob");
		
		NetworkComm robot2 = new NetworkComm(robot2Info, sessionManager2, notifier2);
		(new Thread(robot2)).start();
		
		logger.debug("Successfully connected to Lil' Vader");
		
		NetworkComm robot3 = new NetworkComm(robot3Info, sessionManager3, notifier3);
		(new Thread(robot3)).start();
		
		logger.debug("Successfully connected to Lil' Yoda");
		
		// Initialise route planner
		RoutePlanner planner = new RoutePlanner(map,gridWalkerManager.getMapSizeX(),gridWalkerManager.getMapSizeY());
				
		RouteManager routeManager = new RouteManager(rounds, sessionManager1, sessionManager2, sessionManager3, planner,
				notifier1, notifier2, notifier3, robot1, robot2, robot3, 
				new TSP(map, gridWalkerManager.getMapSizeX(), gridWalkerManager.getMapSizeY()), gridWalkerManager.getController());
		
		(new Thread (routeManager)).start();	
	}
	
	public static ArrayList<Round> orderJobs(PCSessionManager sessionManager){
		// Read job files
		String jfile = "files/jobs.csv";
		String trainfile = "files/training_jobs.csv";
		
		String wrfile = "files/items.csv";
		String lfile = "files/locations.csv";
		
		String cfile = "files/cancellations.csv";
		
		logger.debug("Started reading files");
		HashMap<String, Item> itemMap = ItemReader.parseItems(wrfile, lfile);
		HashMap<String, Job> jobMap = JobReader.parseJobs(jfile, itemMap);
		logger.debug("Finished reading files");
		ArrayList<Job> jobs = new ArrayList<Job>(jobMap.values());
		logger.debug("Creating WEKA training set ARFF.");
		JobTraining.makeARFF(trainfile, cfile, itemMap, "files/training.arff");
		logger.debug("Creating WEKA job set ARFF.");
		JobTraining.makeARFF(jfile, itemMap, "files/jobs.arff");
		logger.debug("WEKA files succsessfully created.");
		
		try {
			logger.debug("Reading ARFF file to training set.");
			DataSource tsource = new DataSource("files/training.arff");
			Instances tdata = tsource.getDataSet();
			tdata.setClass(tdata.attribute("cancelled"));
			logger.debug("Successfully created training set.");
			
			logger.debug("Reading ARFF file to job set");
			DataSource jsource = new DataSource("files/jobs.arff");
			Instances jdata = jsource.getDataSet();
			
			logger.debug("Successfully created job set.");
			logger.debug("Setting up classifier");
			
			AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
			Bagging bc = new Bagging();
			bc.setClassifier(new REPTree());
			classifier.setClassifier(bc);
			classifier.setEvaluator(new GainRatioAttributeEval());
			classifier.setSearch(new Ranker());
			logger.debug("Classifier configured");
			logger.debug("Building classifier");
			classifier.buildClassifier(tdata);
			logger.debug("Classifier successfully built");
			
			Instances newData = jdata;
			
			newData.setClass(newData.attribute("cancelled"));
		
			logger.debug("Starting predictions.");
			for (int i = 0; i < newData.numInstances(); i++) {
				Instance j = newData.instance(i);
				double prediction = classifier.classifyInstance(j);
				if (prediction == 1.0){
					jobMap.get(Integer.toString(10000 + i)).setPrediction(true);
				}
				else {
					jobMap.get(Integer.toString(10000 + i)).setPrediction(false);
				}
			}
			
			logger.debug("Successfully finished predictions");
			
			logger.debug("Saving results...");
			ArffSaver sj = new ArffSaver();
			sj.setFile(new File("files/newJobs.arff"));
			sj.setInstances(jdata);
			sj.writeBatch();
			
			ArffSaver s = new ArffSaver();
			s.setFile(new File("files/newTraining.arff"));
			s.setInstances(tdata);
			s.writeBatch();
			logger.debug("Results saved");
		} catch (Exception e) {
			logger.fatal(e);
		}
		
		// Sort jobs into highest reward based array
		logger.debug("Sorting jobs");
		Collections.sort(jobs);
		sessionManager.setJobs(jobs);
		logger.debug("Jobs ordered");

		// Splits the jobs by weights - no job over 50
		return RoundCreator.createRounds(50f, itemMap, jobs);
	}


}
