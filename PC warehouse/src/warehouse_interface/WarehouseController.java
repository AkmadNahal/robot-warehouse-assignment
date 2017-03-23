package warehouse_interface;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import job_selection.Round;
import rp.robotics.mapping.GridMap;
import rp.robotics.simulation.MapBasedSimulation;
import system_control.PCSessionManager;

public class WarehouseController {

	private GridMap mapModel;
	private WarehouseView view;
	private MapBasedSimulation sim;
	private WarehouseModel robotModel;
	private PCSessionManager sessionManager1;
	private PCSessionManager sessionManager2;
	private PCSessionManager sessionManager3;

	public WarehouseController(GridMap mapModel, MapBasedSimulation sim, GridWalker gridWalker1, GridWalker gridWalker2, GridWalker gridWalker3,
			PCSessionManager sessionManager1, PCSessionManager sessionManager2, PCSessionManager sessionManager3, ArrayList<Round> rounds) {
		this.mapModel = mapModel;
		this.sim = sim;
		robotModel = new WarehouseModel(gridWalker1, gridWalker2, gridWalker3, sessionManager1, sessionManager2, sessionManager3, rounds);
		this.sessionManager1 = sessionManager1;
		this.sessionManager2 = sessionManager2;	
		this.sessionManager3 = sessionManager3;		
	}

	public void registerView(WarehouseView view) {
		this.view = view;
		view.setCancelCallback(new WarehouseView.CancelButtonCallback() {
			@Override
			public void cancel(int i) {
				robotModel.cancel(i);
			}
		});
		view.setRefreshCallback(new WarehouseView.RefreshButtonCallback() {
			@Override
			public void refresh() {
				robotModel.refresh();

			}
		});
		view.setMap(mapModel, sim);
	}


	public void setTasks(PCSessionManager sessionManager) {
		view.setTasks(sessionManager);
	}
	
	public void setTotalReward(PCSessionManager sessionManager) {
		view.setReward(sessionManager);
	}
	
	public void setCompletedJobs(PCSessionManager sessionManager){
		view.setCompletedJobs(sessionManager);
	}
}
