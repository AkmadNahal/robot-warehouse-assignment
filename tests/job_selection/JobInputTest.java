package job_selection;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.junit.*;

import helper_classes.Location;
import helper_classes.LocationType;

public class JobInputTest {
	String jfile = "files/jobs.csv";
	String wrfile = "files/items.csv";
	String lfile = "files/locations.csv";
	HashMap<String, Item> itemMap = ItemReader.parseItems(wrfile, lfile);
	HashMap<String, Job> jobMap = JobReader.parseJobs(jfile, itemMap);
	
	//Tests that the number of items read are correct.
	@Test
	public void ItemTest() {
		assertTrue(itemMap.size() == 30);
	}
	
	//Tests that the number of jobs read are correct.
	//Also tests that jobs only contain items from the given set of items.
	@Test
	public void JobReadTest() {
		assertTrue(jobMap.size() == 100);
		
		for (Job j : jobMap.values()) {
			for (String s : j.getPicks().keySet()) {
				assertTrue(itemMap.keySet().contains(s));
			}
		}
	}
	
	//Test that the jobs are sorted based on value by checking that each job reward is
	//greater than that of the next job in the list.
	@Test
	public void JobSortTest() {
		ArrayList<Job> jobs = new ArrayList<Job>(jobMap.values());
		Collections.sort(jobs);
		boolean descending = false;
		for (int i = 0; i < jobs.size() - 2; i++) {
			if (jobs.get(i+1).getValue() > jobs.get(i).getValue()) {
				descending = true;
			}
		}
		assertTrue(descending);
	}
	
	//Tests that Jobs are divided into rounds correctly, such that rounds
	//are all less than 50 weight.
	@Test
	public void JobRoundTest() {
		ArrayList<Job> jobs = new ArrayList<Job>(jobMap.values());
		Collections.sort(jobs);
		ArrayList<Round> rounds = new ArrayList<Round>();
		rounds = RoundCreator.createRounds(50f, itemMap, jobs);
		
		int count = 0;
		for (Job j : jobMap.values()) {
			if (j.totalWeight() > 50) {
				count ++;
			}
		}
		assertTrue(rounds.size() == jobs.size() + count);
	}
}
