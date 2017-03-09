package motion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import lejos.nxt.comm.BTConnection;
import utils.Direction;

public class RobotComm implements Runnable {
	
	private boolean should_send_robot = true;
	private boolean is_route_income = false;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;
	private RouteExecutor routeExecutor = new RouteExecutor();
	
	ArrayList<Direction> route = new ArrayList<Direction>();
	
	public RobotComm(BTConnection connection){
		this.inputStream = connection.openDataInputStream();
		this.outputStream = connection.openDataOutputStream();
	}
	
	@Override
	public void run() {
		if (getShouldSendRobot()){
			try{
				int input = inputStream.readInt();
				if(input == 99 && !is_route_income) {
					// Route sending started
					is_route_income = true;
					route = new ArrayList<Direction>();
				} else if(input == 99 && is_route_income) {
					// Route sending ended, execute route
					routeExecutor.setRoute(route);
					routeExecutor.setShouldExecute(true);
					is_route_income = false;
					setShouldSendRobot(false);
				} else if(is_route_income) {
					route.add(Direction.fromInteger(input));
				}
			} catch (IOException e) {
				System.out.println("Exception: " + e.getClass());
			}
		}else if (!(routeExecutor.getIsExecuting())){
			try {
				outputStream.writeInt(50);
			} catch (IOException e) {
				System.out.println("Exception: " + e.getClass());
			} 
		}
	}
	
	private synchronized void setShouldSendRobot(boolean value){
		should_send_robot = value;
	}
	
	private synchronized boolean getShouldSendRobot(){
		return should_send_robot;
	}
	
}
