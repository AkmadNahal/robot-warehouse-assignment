package job_selection;

import java.util.ArrayList;
import java.util.HashMap;

public class RoundCreator {
	public static ArrayList<Round> createRounds(float W_LIMIT, HashMap<String, Item> itemMap, ArrayList<Job> jobs) {
	
			
			ArrayList<Round> rounds = new ArrayList<Round>();
			
			for (Job j : jobs) {
				Round currentRound = new Round(W_LIMIT, j.getID());
				for (String s : j.getPicks().keySet()) {
					if (!currentRound.addStop(itemMap.get(s), j.getPicks().get(s))) {
						rounds.add(currentRound);
						currentRound = new Round(W_LIMIT, j.getID());
						currentRound.addStop(itemMap.get(s), j.getPicks().get(s));
					}
				}
				rounds.add(currentRound);
			}
			
			return rounds;
		}
}
