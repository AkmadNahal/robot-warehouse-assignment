
package utils;

import java.util.ArrayList;

public class GridToDirections {
	
	private Direction direction;
	
	public Direction coordinatesToDirections(Location initialPosition, Location newPosition){
		int diffInX = newPosition.getX() - initialPosition.getX();
		int diffInY = newPosition.getY() - initialPosition.getY();
		
		if (diffInX < 0){
			for (int i = diffInX; i < 0; i--){
				direction = Direction.LEFT;
			}
		}else if (diffInX > 0){
			for (int i = 0; i < diffInX; i++){
				direction = Direction.RIGHT;
			}
		}
		else if (diffInY < 0){
			for (int i = diffInY; i < 0; i--){
				direction = Direction.BACKWARDS;
			}
		}else if (diffInY> 0){
			for (int i = 0; i < diffInX; i++){
				direction = Direction.FORWARD;
			}
		}
		return direction;
	}
}
