package warehouse_interface;

public class WarehouseModel {

	private GridWalker gridWalker1;
	private GridWalker gridWalker2;
	private GridWalker gridWalker3;

	public WarehouseModel(GridWalker gridWalker1, GridWalker gridWalker2, GridWalker gridWalker3) {
		this.gridWalker1 = gridWalker1;
		this.gridWalker2 = gridWalker2;
		this.gridWalker3 = gridWalker3;
	}

	public void robot1() {
		System.out.println("Robot 1");	
	}

	public void robot2() {
		System.out.println("Robot 2");		
	}

	public void robot3() {
		System.out.println("Robot 3");		
	}

	public void refresh() {
		gridWalker1.changePosition();
		gridWalker2.changePosition();
		gridWalker3.changePosition();
	}
}
