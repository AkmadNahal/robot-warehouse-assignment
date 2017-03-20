package system_control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import utils.Direction;

public class NetworkComm implements Runnable {

	private DataInputStream m_dis;
	private DataOutputStream m_dos;
	private final NXTInfo m_nxt;

	private PCSessionManager sessionManager;
	private ChangeNotifier notifier;
	
	private static final Logger logger = Logger.getLogger(NetworkComm.class);
	
	private final int RECEIVE_MOVE_FLAG = 99; //matches with RECEIVE_MOVE_FLAG in RobotControl.java
	private final int COMPLETE_ROUTE_FLAG = 50; //matches with COMPLETE_ROUTE_FLAG in RobotControl.java
	private final int SENDING_FLAG = 75; //matches with RECEIVING_FLAG in RobotControl.java
	private final int SENDING_PICK_NUM_FLAG = 100; //matches with SENDING_PICK_NUM_FLAG in RobotControl.java
	private final int AT_PICKUP_FLAG = 33; //matches with AT_PICKUP_FLAG in RobotControl.java

	public NetworkComm(NXTInfo _nxt, PCSessionManager sessionManager, ChangeNotifier _notifier) {
		m_nxt = _nxt;
		this.sessionManager = sessionManager;
		this.notifier = _notifier;
	}

	public boolean connect(NXTComm _comm) throws NXTCommException {
		if (_comm.open(m_nxt)) {

			m_dis = new DataInputStream(_comm.getInputStream());
			m_dos = new DataOutputStream(_comm.getOutputStream());
		}
		return isConnected();
	}

	public boolean isConnected() {
		return m_dos != null;
	}

	@Override
	public void run() {
		try {
			NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
			this.connect(nxtComm);
			while (true) {
				try{
					if (notifier.getAtPickup()){
						logger.debug("Sending pickup flag");
						int input = m_dis.readInt();
						logger.debug("Finished picking, changing notifier");
						notifier.setChanged(true);
						notifier.setAtPickup(false);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(sessionManager.getShouldSend()) {
					// Notify starting of route sending
					logger.debug("Started sending route");
					m_dos.writeInt(SENDING_FLAG);
					// Send the route
					for(Direction d : sessionManager.getRoute()) {
						m_dos.writeInt(d.getValue());
					}
					// Notify end of route sending
					m_dos.writeInt(SENDING_FLAG);
					m_dos.writeInt(SENDING_PICK_NUM_FLAG);
					m_dos.writeInt(sessionManager.getNumOfPicks());
					m_dos.writeInt(SENDING_PICK_NUM_FLAG);
					m_dos.flush();
					sessionManager.setShouldSend(false);
					logger.debug("Finished sending route and number of picks");

					int input;
					while((input = m_dis.readInt()) != COMPLETE_ROUTE_FLAG) {

						if(input == RECEIVE_MOVE_FLAG) {
							input = m_dis.readInt();
							Direction nextMove = Direction.fromInteger(input);
							sessionManager.getLocationAccess().updateCurrentLocation(nextMove);
						}

					}
					
					// Route completed
					logger.debug("Route execution completed");
					if(!notifier.getChanged()){
						notifier.setChanged(true);
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NXTCommException e) {
			logger.fatal("ERROR - Failed to connect to robot");
			logger.fatal(e);
		}
	}

	public void send(ArrayList<Direction> directions, int nrOfPicks) {
		sessionManager.setIsRouteComplete(false);
		sessionManager.setRoute(directions);
		sessionManager.setNumOfPicks(nrOfPicks);
		sessionManager.setShouldSend(true);
	}
	
	public void sendAtPickup(){
		try {
			logger.debug("Sending pickup flag");
			m_dos.writeInt(AT_PICKUP_FLAG);
			m_dos.flush();
			notifier.setAtPickup(true);
		}catch(IOException e){
			e.printStackTrace();
		}
	}


	public Runnable startConnection() {
		try {

			NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
			this.connect(nxtComm);

			return this;
		} catch (NXTCommException e) {
			e.printStackTrace();
		}
		return null;

	}
}
