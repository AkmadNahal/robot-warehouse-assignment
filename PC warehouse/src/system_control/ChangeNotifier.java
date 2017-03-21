package system_control;

public class ChangeNotifier {

	private boolean changed = false;
	private boolean atPickup = false;
	
	public synchronized void setChanged(boolean value) {
		this.changed = value;
	}
	
	public synchronized boolean getChanged() {
		return this.changed;
	}
	
	public synchronized void setAtPickup(boolean value){
		this.atPickup = value;
	}
	
	public synchronized boolean getAtPickup(){
		return this.atPickup;
	}
	
}
