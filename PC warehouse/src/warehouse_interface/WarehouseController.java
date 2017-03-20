package warehouse_interface;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rp.robotics.mapping.GridMap;
import rp.robotics.simulation.MapBasedSimulation;

public class WarehouseController {

	private GridMap mapModel;
	private WarehouseView view;
	private MapBasedSimulation sim;
	private WarehouseModel robotModel;

	public WarehouseController(GridMap mapModel, MapBasedSimulation sim, GridWalker gridWalker1, GridWalker gridWalker2/*, GridWalker gridWalker3*/) {
		this.mapModel = mapModel;
		this.sim = sim;
		robotModel = new WarehouseModel(gridWalker1, gridWalker2/*, gridWalker3*/);
	}

	public void registerView(WarehouseView view) {
		this.view = view;
		view.setCancelCallback(new WarehouseView.CancelButtonCallback() {
			@Override
			public void cancel(int i) {
				// TODO Auto-generated method stub

			}
		});
		view.setRefreshCallback(new WarehouseView.RefreshButtonCallback() {
			@Override
			public void refresh() {
				robotModel.refresh();

			}
		});
		view.setMap(mapModel, sim);
		setTasks();
	}

	private void setTasks() {
		view.setTasks(readTasks());
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
	}
}
