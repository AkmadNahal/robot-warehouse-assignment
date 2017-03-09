package system_control;

public class ChangeNotifier {

	private boolean changed = false;
	
	public void setChanged(boolean value) {
		changed = value;
	}
	
	public boolean getChanged() {
		return changed;
	}
	
}
