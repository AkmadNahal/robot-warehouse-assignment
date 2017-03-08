import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class GridToDirectionsTest {
	
	
	//The first tests will test each individual component of the GridToDirections
	//method. i.e. can output each type of Direction
	
	//The second sets of test will see if the correct arraylist is outputted based
	//on a given sequence of locations.
	
	@Test
	public void testForward(){
		Location c1 = new Location(0, 0, LocationType.EMPTY);
		Location c2 = new Location(0, 1, LocationType.EMPTY);
		
		GridToDirections gridToDirection = new GridToDirections();
		
		assertEquals(Direction.FORWARD, gridToDirection.coordinatesToDirections(c1, c2));
	}
	
	@Test
	public void testBackward(){
		Location c1 = new Location(0, 1, LocationType.EMPTY);
		Location c2 = new Location(0, 0, LocationType.EMPTY);
		
		GridToDirections gridToDirection = new GridToDirections();
		
		assertEquals(Direction.BACKWARDS, gridToDirection.coordinatesToDirections(c1, c2));
	}
	
	@Test
	public void testLeft(){
		Location c1 = new Location(1, 0, LocationType.EMPTY);
		Location c2 = new Location(0, 0, LocationType.EMPTY);
		
		GridToDirections gridToDirection = new GridToDirections();
		
		assertEquals(Direction.LEFT, gridToDirection.coordinatesToDirections(c1, c2));
	}
	
	@Test
	public void testRight(){
		Location c1 = new Location(0, 0, LocationType.EMPTY);
		Location c2 = new Location(1, 0, LocationType.EMPTY);
		
		GridToDirections gridToDirection = new GridToDirections();
		
		assertEquals(Direction.RIGHT, gridToDirection.coordinatesToDirections(c1, c2));
	}
	
	//This test output should no occur within the system.
	//However, this is to test that a direction shouldnt be output when no change in location is seen.
	@Test
	public void testNull(){
		Location c1 = new Location(0, 0, LocationType.EMPTY);
		Location c2 = new Location(0, 0, LocationType.EMPTY);
		
		GridToDirections gridToDirection = new GridToDirections();
		
		assertEquals(null, gridToDirection.coordinatesToDirections(c1, c2));
	}
	
	@Test
	public void testDirectionsArrayList(){
		final Location c1 = new Location(0, 0, LocationType.EMPTY);
		final Location c2 = new Location(0, 1, LocationType.EMPTY);
		final Location c3 = new Location(1, 1, LocationType.EMPTY);
		final Location c4 = new Location(1, 0, LocationType.EMPTY);
		
		GridToDirections gridToDirection = new GridToDirections();
		
		//ArrayList of locations to be passed to the method
		ArrayList<Location> locations = new ArrayList<Location>() {{
			add(c1);
			add(c2);
			add(c3);
			add(c4);
		}};
		
		//ArrayList of type Direction that should be produced
		ArrayList<Direction> ideal = new ArrayList<Direction>() {{
			add(Direction.FORWARD);
			add(Direction.RIGHT);
			add(Direction.BACKWARDS);
		}};
		
		//ArrayList of what is actually produced
		ArrayList<Direction> output = new ArrayList<Direction>();
		
		for(int i = 0; i < locations.size()-1; i++){
			output.add(gridToDirection.coordinatesToDirections(locations.get(i), locations.get(i + 1)));
		}
		
		assertArrayEquals(ideal.toArray(), output.toArray());
	}
	
	@Test
	public void testDirectionsArrayList2(){
		final Location c1 = new Location(0, 0, LocationType.EMPTY);
		final Location c2 = new Location(0, 1, LocationType.EMPTY);
		final Location c3 = new Location(1, 1, LocationType.EMPTY);
		final Location c4 = new Location(1, 0, LocationType.EMPTY);
		final Location c5 = new Location(2, 0, LocationType.EMPTY);
		final Location c6 = new Location(2, 1, LocationType.EMPTY);
		final Location c7 = new Location(2, 2, LocationType.EMPTY);
		final Location c8 = new Location(1, 2, LocationType.EMPTY);
		
		GridToDirections gridToDirection = new GridToDirections();
		
		//ArrayList of locations to be passed to the method
		ArrayList<Location> locations = new ArrayList<Location>() {{
			add(c1);
			add(c2);
			add(c3);
			add(c4);
			add(c5);
			add(c6);
			add(c7);
			add(c8);
		}};
		
		//ArrayList of type Direction that should be produced
		ArrayList<Direction> ideal = new ArrayList<Direction>() {{
			add(Direction.FORWARD);
			add(Direction.RIGHT);
			add(Direction.BACKWARDS);
			add(Direction.RIGHT);
			add(Direction.FORWARD);
			add(Direction.FORWARD);
			add(Direction.LEFT);
		}};
		
		//ArrayList of what is actually produced
		ArrayList<Direction> output = new ArrayList<Direction>();
		
		for(int i = 0; i < locations.size()-1; i++){
			output.add(gridToDirection.coordinatesToDirections(locations.get(i), locations.get(i + 1)));
		}
		
		assertArrayEquals(ideal.toArray(), output.toArray());
	}

}
