package job_selection;

import java.util.HashMap;

public class JobReadTest {
	String jfile = "csv/jobs.csv";
	String wrfile = "csv/items.csv";
	String lfile = "csv/locations.csv";
	HashMap<String, Item> itemMap = ItemReader.parseItems(wrfile, lfile);
	HashMap<String, Job> jobMap = JobReader.parseJobs(jfile, itemMap);
}
