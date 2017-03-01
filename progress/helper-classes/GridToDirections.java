package helper_classes;

import java.util.ArrayList;

public class GridToDirections {
	
	private Direction direction;
	private ArrayList<Direction> pathMovement;
	
	public GridToDirections(){
		this.pathMovement = new ArrayList<Direction>();
	}
	
	public void coordinatesToDirections(int[] initialPosition, int[] newPosition){
		int diffInX = newPosition[0] - initialPosition[0];
		int diffInY = newPosition[1] - initialPosition[1];
		
		if (diffInX < 0){
			for (int i = diffInX; i < 0; i--){
				pathMovement.add(direction.LEFT);
			}
		}else if (diffInX > 0){
			for (int i = 0; i < diffInX; i++){
				pathMovement.add(direction.RIGHT);
			}
		}
		else if (diffInY < 0){
			for (int i = diffInY; i < 0; i--){
				pathMovement.add(direction.BACKWARDS);
			}
		}else if (diffInY> 0){
			for (int i = 0; i < diffInX; i++){
				pathMovement.add(direction.FORWARD);
			}
		}
	}
	
	public ArrayList<Direction> getPath(){
		return pathMovement;
	}
}
