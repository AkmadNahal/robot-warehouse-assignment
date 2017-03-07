package system_control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

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

		try {
			while (true) {

				if(should_send) {

					// Notify starting of route sending
					m_dos.writeInt(9999);

					// Send the route
					for(Direction d : sendable) {
						m_dos.writeInt(d.getValue());
					}

					// Notify end of route sending
					m_dos.writeInt(9999);
					m_dos.flush();

					should_send = false;
				}

				int answer = m_dis.readInt();
				System.out.println(m_nxt.name + " returned " + answer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void send(ArrayList<Direction> directions) {
		should_send = true;
		sendable = directions;
	}


	public void startConnection() {
		try {

			NXTInfo[] nxts = { m_nxt };

			ArrayList<NetworkComm> connections = new ArrayList<>(
					nxts.length);

			for (NXTInfo nxt : nxts) {
				connections.add(new NetworkComm(nxt));
			}

			for (NetworkComm connection : connections) {
				NXTComm nxtComm = NXTCommFactory
						.createNXTComm(NXTCommFactory.BLUETOOTH);
				connection.connect(nxtComm);
			}

			ArrayList<Thread> threads = new ArrayList<>(nxts.length);

			for (NetworkComm connection : connections) {
				threads.add(new Thread(connection));
			}

			for (Thread thread : threads) {
				thread.start();
			}

			for (Thread thread : threads) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		} catch (NXTCommException e) {
			e.printStackTrace();
		}

	}
}
