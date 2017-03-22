package job_selection;

import java.util.*;
import utils.*;

public class Round {
	private ArrayList<Pick> round = new ArrayList<Pick>();
	private final float MAX_WEIGHT;
	private ArrayList<Integer> counts;
	private String jobID;

	public Round(float mw, String jobID) {
		MAX_WEIGHT = mw;
		this.jobID = jobID;
	}

	public ArrayList<Pick> getRound() {
		return round;
	}

	public boolean addStop(Item item, Integer count) {
		if ((item.getWeight() * count) + this.getWeight() < MAX_WEIGHT) {
			boolean ok = false;
			for (int i = 0; i < round.size() && !ok; i++) {
				if (round.get(i).getItem().equals(item)) {
					round.get(i).setCount(round.get(i).getCount() + count);
					ok = true;
				}
			}
			round.add(new Pick(item, count));
			return true;
		} else {
			return false;
		}
	}

	public ArrayList<Location> getRoute() {
		ArrayList<Location> locations = new ArrayList<Location>();
		for (Pick p : round) {
			locations.add(p.getItem().getLoc());
		}
		return locations;
	}

	public ArrayList<Integer> getCounts() {
		return this.counts;
	}

	public float getWeight() {
		float total = 0;
		for (Pick p : round) {
			total += (p.getItem().getWeight() * p.getCount());
		}
		return total;
	}
	
	public void initialiseCounts(){
		ArrayList<Integer> counts = new ArrayList<Integer>();
		for (Pick p : round) {
			counts.add(p.getCount());
		}
		this.counts = counts;
	}
	
	public void addToCounts(ArrayList<Integer> count, int num){
		this.counts.add(num);
	}
	
	public static ArrayList<Pick> reorderAccordingTSP(ArrayList<Location> tspArr, ArrayList<Pick> pickArr) {
		ArrayList<Pick> sol = new ArrayList<Pick>();
		
		for(Location l : tspArr) {
			boolean ok = false;
			
			for(int i=0;i<pickArr.size() && !ok;i++) {
				if(l.equalsTo(pickArr.get(i).getItem().getLoc())) {
					ok = true;
					sol.add(pickArr.get(i));
				}
			}
		}
		
		return sol;
	}
	
	public String getJob() {
		return jobID;
	}
}
