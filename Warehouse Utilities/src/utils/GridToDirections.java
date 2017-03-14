
package utils;

public class GridToDirections {
	
	private Direction direction;
	
	public Direction coordinatesToDirections(Location initialPosition, Location newPosition){
		int diffInX = newPosition.getX() - initialPosition.getX();
		int diffInY = newPosition.getY() - initialPosition.getY();
		
		if (diffInX < 0){
			direction = Direction.LEFT;
		}else if (diffInX > 0){
			direction = Direction.RIGHT;
		}
		else if (diffInY < 0){
			direction = Direction.BACKWARDS;
		}else if (diffInY> 0){
			direction = Direction.FORWARD;
		}
		return direction;
	}
}
