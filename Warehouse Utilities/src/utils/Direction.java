package utils;

public enum Direction {
	FORWARD(0), BACKWARDS(180), LEFT(-90), RIGHT(90), STOP(-1), SPIN(360);

		private final int value;
	 	private Direction(int value) {
			this.value = value;
	 	}

	 	public int getValue() {
			 return value;
	 	}

		public static Direction fromInteger(int x) {
        switch(x) {
        case 0:
            return FORWARD;
        case 180:
            return BACKWARDS;
				case -90:
					return LEFT;
				case 90:
					return RIGHT;
				case -1:
					return STOP;
				case 360:
					return SPIN;
				default:
					return null;
        }
    }
}
