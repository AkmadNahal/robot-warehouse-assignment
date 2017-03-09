package system_control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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
				if(sessionManager.getShouldSend()) {
					// Notify starting of route sending
					System.err.println("Started sending");
					m_dos.writeInt(99);
					// Send the route
					for(Direction d : sessionManager.getRoute()) {
						m_dos.writeInt(d.getValue());
					}
					// Notify end of route sending
					m_dos.writeInt(99);
					m_dos.writeInt(100);
					m_dos.writeInt(sessionManager.getNumOfPicks());
					m_dos.writeInt(100);
					m_dos.flush();
					sessionManager.setShouldSend(false);
					int input = m_dis.readInt();
					if (input == 50){
						System.out.println("Input equals 50");
						notifier.setChanged(true);
						sessionManager.setReadValue(50);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NXTCommException e) {
			e.printStackTrace();
		} 
	}

	public void send(ArrayList<Direction> directions, int nrOfPicks) {
		sessionManager.setIsRouteComplete(false);
		sessionManager.setRoute(directions);
		sessionManager.setNumOfPicks(nrOfPicks);
		sessionManager.setShouldSend(true);
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
