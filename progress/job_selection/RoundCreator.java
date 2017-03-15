package job_selection;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class RoundCreator {
	public static ArrayList<Round> createRounds(float W_LIMIT, HashMap<String, Item> itemMap, ArrayList<Job> jobs) {

		
		ArrayList<Round> rounds = new ArrayList<Round>();
		Round currentRound = new Round(W_LIMIT);
		for (Job j : jobs) {	
			for (String s : j.getPicks().keySet()) {
				if (!currentRound.addStop(itemMap.get(s), j.getPicks().get(s))) {
					rounds.add(currentRound);
					currentRound = new Round(W_LIMIT);
					currentRound.addStop(itemMap.get(s), j.getPicks().get(s));
				}
			}
			rounds.add(currentRound);
			currentRound = new Round(W_LIMIT);
		}
		
		return rounds;
	}
}
