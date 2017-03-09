package system_control;

public class ChangeNotifier {

	private boolean changed = false;
	
	public synchronized void setChanged(boolean value) {
		changed = value;
	}
	
	public synchronized boolean getChanged() {
		return changed;
	}
	
}
