package job_selection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.io.FileNotFoundException;

public class JobReader {

	public static HashMap<Integer, Job> parseFile(String file) {
		BufferedReader reader;
		String splitBy = ",";
		HashMap<Integer, Job> jobs = new HashMap<Integer, Job>();

		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] job = line.split(splitBy);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File does not exist");
		} catch (IOException e) {
			System.out.println("IO Failed");
		} finally {
			return jobs;
		}
	}
}
