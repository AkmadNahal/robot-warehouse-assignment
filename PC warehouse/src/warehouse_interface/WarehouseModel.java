package warehouse_interface;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import job_selection.Round;
import system_control.PCSessionManager;

public class WarehouseModel {

	private GridWalker gridWalker1;
	private GridWalker gridWalker2;
	private GridWalker gridWalker3;
	private PCSessionManager sessionManager1;
	private PCSessionManager sessionManager2;
	private PCSessionManager sessionManager3;
	private ArrayList<Round> sortedJobs;
	private ArrayList<Round> cancelledRounds = new ArrayList<Round>();
	private String cancelledJobID;
	
	private static final Logger logger = Logger.getLogger(WarehouseModel.class);

	public WarehouseModel(GridWalker gridWalker1, GridWalker gridWalker2, GridWalker gridWalker3, PCSessionManager sessionManager1,
			PCSessionManager sessionManager2, PCSessionManager sessionManager3, ArrayList<Round> sortedJobs) {
		this.gridWalker1 = gridWalker1;
		this.gridWalker2 = gridWalker2;
		this.gridWalker3 = gridWalker3;
		this.sessionManager1 = sessionManager1;
		this.sessionManager2 = sessionManager2;
		this.sessionManager3 = sessionManager3;
		this.sortedJobs = sortedJobs;
	}

	public void cancel(int i){
		if (i == 1){
			cancelledJobID = sessionManager1.getCurrentRound().getJob();
		}else if (i == 2){
			cancelledJobID = sessionManager2.getCurrentRound().getJob();
		}else if (i == 3){
			cancelledJobID = sessionManager3.getCurrentRound().getJob();
		}
		sessionManager1.setCancelledJobID(cancelledJobID);
		sessionManager2.setCancelledJobID(cancelledJobID);
		sessionManager3.setCancelledJobID(cancelledJobID);
		
		logger.debug("Cancelled Job ID: " + cancelledJobID);
	}

	public void refresh() {
		gridWalker1.changePosition();
		gridWalker2.changePosition();
		gridWalker3.changePosition();
	}
}
