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

	private boolean should_send;
	private ArrayList<Direction> sendable;

	public NetworkComm(NXTInfo _nxt) {
		m_nxt = _nxt;
		should_send = false;
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
		System.out.println("Run started");

		try {
			NXTInfo nxt = m_nxt;
			try{
				NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
				this.connect(nxtComm);
			}
			catch (Exception e){
				
			}
			while (true) {
				System.err.println("Running");
				if(getShouldSend()) {

					// Notify starting of route sending
					System.err.println("Started sending");
					m_dos.writeInt(99);

					// Send the route
					for(Direction d : sendable) {
						m_dos.writeInt(d.getValue());
					}

					// Notify end of route sending
					m_dos.writeInt(99);
					m_dos.flush();

					setShouldSend(false);
				}

				int answer = m_dis.readInt();
				System.out.println(m_nxt.name + " returned " + answer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void send(ArrayList<Direction> directions) {
		setShouldSend(true);
		sendable = directions;
	}


	public Runnable startConnection() {
		try {

			NXTInfo nxt = m_nxt;
			
			NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
			this.connect(nxtComm);

			return this;



		} catch (NXTCommException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	private synchronized void setShouldSend(boolean value){
		should_send = value;
	}
	
	private synchronized boolean getShouldSend(){
		return should_send;
	}
}
