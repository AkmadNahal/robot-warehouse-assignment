package job_selection;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.junit.*;

import helper_classes.Location;
import helper_classes.LocationType;

public class JobSelectionTest {
	String jfile = "csv/jobs.csv";
	String wrfile = "csv/items.csv";
	String lfile = "csv/locations.csv";
	HashMap<String, Item> itemMap = ItemReader.parseItems(wrfile, lfile);
	HashMap<String, Job> jobMap = JobReader.parseJobs(jfile, itemMap);
	
	@Test
	public void ItemTest() {
		assertEquals(itemMap.get("ad").getReward(), 8.33f, 0f);
		assertEquals(itemMap.get("ba").getReward(), 11.17f, 0f);
		assertEquals(itemMap.get("cd").getReward(), 3.78f, 0f);
		
		assertEquals(itemMap.get("ai").getWeight(), 1.90f, 0f);
		assertEquals(itemMap.get("bh").getWeight(), 5.25f, 0f);
		assertEquals(itemMap.get("cb").getWeight(), 1.13f, 0f);
		
		assertTrue(itemMap.get("aa").getLoc().equals(new Location(9,1, LocationType.EMPTY)));
		assertTrue(itemMap.get("be").getLoc().equals(new Location(2,1, LocationType.EMPTY)));
		assertTrue(itemMap.get("ch").getLoc().equals(new Location(3,2, LocationType.EMPTY)));
	}
	
	//Test that jobs are read form file properly. It does this by comparing expected items
	//with the string produced by Job.toString().
	@Test
	public void JobReadTest() {
		String[] job0 = {"cc:1","bf:1"};
		String[] job1 = {"af:1","ag:3","cg:2","ae:1"};
		String[] job11 = {"cc:1","cg:2","ag:2","bf:2"};
		String[] job69 = {"aj:2","bf:1","cc:1","cg:2"};
		
		for (String s : job0) {
			assertTrue(jobMap.get("10000").toString().contains(s));
		}
		for (String s : job1) {
			assertTrue(jobMap.get("10001").toString().contains(s));
		}
		for (String s : job11) {
			assertTrue(jobMap.get("10011").toString().contains(s));
		}
		for (String s : job69) {
			assertTrue(jobMap.get("10069").toString().contains(s));
		}
		assertTrue(jobMap.size() == 100);
	}
	
	//Test that the jobs are sorted based on reward by checking that each job reward is
	//greater than that of the next job in the list.
	@Test
	public void JobSortTest() {
		ArrayList<Job> jobs = new ArrayList<Job>(jobMap.values());
		Collections.sort(jobs);
		boolean descending = true;
		for (int i = 0; i < jobs.size() - 2; i++) {
			if (jobs.get(i+1).totalReward() > jobs.get(i).totalReward()) {
				descending = true;
			}
		}
		assertTrue(descending);
	}
	
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
