package warehouse_interface;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rp.robotics.mapping.GridMap;
import rp.robotics.navigation.GridPose;
import rp.robotics.simulation.MapBasedSimulation;

public class WarehouseController {

	private GridMap mapModel;
	private WarehouseView view;
	private MapBasedSimulation sim;
	private WarehouseModel model;

	public WarehouseController(GridMap mapModel, MapBasedSimulation sim) {
		this.mapModel = mapModel;
		this.sim = sim;
		model = new WarehouseModel();
	}

	public void registerView(WarehouseView view) {
		this.view = view;
		view.setCancelCallback(new WarehouseView.CancelButtonCalback() {
			@Override
			public void cancel(int i) {
				// TODO Auto-generated method stub

			}
		});
		view.setRefreshCallback(new WarehouseView.RefreshButtonCalback() {
			@Override
			public void refresh() {
				model.refresh();
			}
		});
		view.setMap(mapModel, sim);
		//setTasks();
	}

	/*private void setTasks(ArrayList<Object> a, Robot) {
		view.setTasks(Object, robot);
	}

	private List<String> readTasks() {
		List<String> tasks = new ArrayList<>();
		try (BufferedReader in = new BufferedReader(new FileReader("Info.txt"))) {
			String line;
			while ((line = in.readLine()) != null) {
				tasks.add(line);
			}
		} catch (IOException e1) {
		}
		return tasks;
	}*/
}
