
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import utils.Direction;
import utils.Location;
import utils.LocationType;

public class NetworkComm {

	private final int YES = 10;
	private final int SURROUNDING = 60;
	private final int FORWARD = 20;
	private final int RIGHT = 30;
	private final int BACKWARD = 40;
	private final int LEFT = 50;

	private final int STEPS_FORWARD = 3;
	private final int STEPS_BACKWARD = 4;
	private final int STEPS_RIGHT = 3;
	private final int STEPS_LEFT = 4;

	private int moveTypeToExecute; // 0 - FORWARD, 1 - RIGHT, 2 - BACKWARD, 3 -
									// LEFT
	private int moveCounter;

	private int NUMBER_OF_PARTICLES;

	private ArrayList<Particle> particles;
	private Location[][] map;
	private int maxX, maxY;
	private int forward, back, right, left;

	private DataInputStream m_dis;
	private DataOutputStream m_dos;
	private final NXTInfo m_nxt;

	public NetworkComm(NXTInfo _nxt, Location[][] map, int maxX, int maxY) {
		m_nxt = _nxt;
		this.map = map;
		this.maxX = maxX;
		this.maxY = maxY;
		createParticles();
		moveCounter = 0;
		moveTypeToExecute = 0;
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

	public void run() {

		try {
			NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
			this.connect(nxtComm);

			while (!isConverged()) {

				if (moveTypeToExecute == 0) {

					if (moveCounter < STEPS_FORWARD) {
						System.out.println("Tries forward");
						m_dos.writeInt(FORWARD);
						m_dos.flush();
						int answer = m_dis.readInt();

						if (answer == YES) {
							moveCounter++;

							updateParticles(Direction.FORWARD);

							m_dos.writeInt(60);
							m_dos.flush();

							int forward = m_dis.readInt();
							int right = m_dis.readInt();
							int back = m_dis.readInt();
							int left = m_dis.readInt();

							System.out.println("Sensed data " + forward + " " + right + " " + back + " " + left);

							updateParticles(forward, right, back, left);
						} else {
							moveTypeToExecute = 1;
							moveCounter = 0;
						}
					}

				} else if (moveTypeToExecute == 1) {

					if (moveCounter < STEPS_RIGHT) {
						System.out.println("Tries right");
						m_dos.writeInt(RIGHT);
						m_dos.flush();
						int answer = m_dis.readInt();
						if (answer == 10) {
							moveCounter++;

							updateParticles(Direction.RIGHT);

							m_dos.writeInt(60);
							m_dos.flush();

							int forward = m_dis.readInt();
							int right = m_dis.readInt();
							int back = m_dis.readInt();
							int left = m_dis.readInt();

							updateParticles(forward, right, back, left);
						} else {
							moveTypeToExecute = 2;
							moveCounter = 0;
						}
					}

				} else if (moveTypeToExecute == 2) {

					if (moveCounter < STEPS_BACKWARD) {
						System.out.println("Tries back");
						m_dos.writeInt(BACKWARD);
						m_dos.flush();
						int answer = m_dis.readInt();
						if (answer == 10) {
							moveCounter++;

							updateParticles(Direction.BACKWARDS);

							m_dos.writeInt(60);
							m_dos.flush();

							int forward = m_dis.readInt();
							int right = m_dis.readInt();
							int back = m_dis.readInt();
							int left = m_dis.readInt();

							updateParticles(forward, right, back, left);
						} else {
							moveTypeToExecute = 3;
							moveCounter = 0;
						}
					}

				} else if (moveTypeToExecute == 3) {

					if (moveCounter < STEPS_LEFT) {
						System.out.println("Tries left");
						m_dos.writeInt(LEFT);
						m_dos.flush();
						int answer = m_dis.readInt();
						if (answer == 10) {
							moveCounter++;

							updateParticles(Direction.LEFT);

							m_dos.writeInt(60);
							m_dos.flush();

							int forward = m_dis.readInt();
							int right = m_dis.readInt();
							int back = m_dis.readInt();
							int left = m_dis.readInt();

							updateParticles(forward, right, back, left);
						} else {
							moveTypeToExecute = 0;
							moveCounter = 0;
						}
					}

				}

				System.out.println("Localized bitch: " + particles.get(0).getLocation().getX() + " - "
						+ particles.get(0).getLocation().getY());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NXTCommException e) {
		}
	}

	// Localization code

	private void createParticles() {
		NUMBER_OF_PARTICLES = 0;
		particles = new ArrayList<Particle>();

		for (int i = 0; i < maxX; i++) {
			for (int j = 0; j < maxY; j++) {
				if (map[i][j].getType() == LocationType.EMPTY) {
					particles.add(new Particle(new Location(i, j, LocationType.EMPTY), 0.0f));
					NUMBER_OF_PARTICLES++;
				}
			}
		}
	}

	private boolean isConverged() {
		for (int i = 1; i < particles.size(); i++) {
			if (!particles.get(i).getLocation().equalsTo(particles.get(i - 1).getLocation())) {
				return false;
			}
		}
		return true;
	}

	private void updateParticles(Direction move) {
		ArrayList<Particle> goodParticles = new ArrayList<Particle>();
		ArrayList<Particle> badParticles = new ArrayList<Particle>();

		if (move == Direction.FORWARD) {

			for (Particle p : particles) {

				if (isValidPosition(p.getLocation().getX(), p.getLocation().getY() + 1)) {
					goodParticles.add(new Particle(
							new Location(p.getLocation().getX(), p.getLocation().getY() + 1, LocationType.EMPTY),
							0.0f));
				} else {
					badParticles.add(new Particle(
							new Location(p.getLocation().getX(), p.getLocation().getY(), LocationType.EMPTY), 0.0f));
				}

			}

		} else if (move == Direction.BACKWARDS) {

			for (Particle p : particles) {

				if (isValidPosition(p.getLocation().getX(), p.getLocation().getY() - 1)) {
					goodParticles.add(new Particle(
							new Location(p.getLocation().getX(), p.getLocation().getY() - 1, LocationType.EMPTY),
							0.0f));
				} else {
					badParticles.add(new Particle(
							new Location(p.getLocation().getX(), p.getLocation().getY(), LocationType.EMPTY), 0.0f));
				}

			}

		} else if (move == Direction.RIGHT) {

			for (Particle p : particles) {

				if (isValidPosition(p.getLocation().getX() + 1, p.getLocation().getY())) {
					goodParticles.add(new Particle(
							new Location(p.getLocation().getX() + 1, p.getLocation().getY(), LocationType.EMPTY),
							0.0f));
				} else {
					badParticles.add(new Particle(
							new Location(p.getLocation().getX(), p.getLocation().getY(), LocationType.EMPTY), 0.0f));
				}
			}

		} else if (move == Direction.LEFT) {

			for (Particle p : particles) {

				if (isValidPosition(p.getLocation().getX() - 1, p.getLocation().getY())) {
					goodParticles.add(new Particle(
							new Location(p.getLocation().getX() - 1, p.getLocation().getY(), LocationType.EMPTY),
							0.0f));
				} else {
					badParticles.add(new Particle(
							new Location(p.getLocation().getX(), p.getLocation().getY(), LocationType.EMPTY), 0.0f));
				}
			}

		}

		// Resample

		// Calculate weight for good particles
		for (int i = 0; i < goodParticles.size(); i++) {
			if (goodParticles.get(i).getWeight() == 0.0) {
				int counter = 1;
				for (int j = i + 1; j < goodParticles.size(); j++) {
					if (goodParticles.get(j).getLocation().equalsTo(goodParticles.get(i).getLocation())) {
						counter++;
					}
				}

				float newWeight = (float) counter / (float) goodParticles.size();
				for (int j = i; j < goodParticles.size(); j++) {
					if (goodParticles.get(j).getLocation().equalsTo(goodParticles.get(i).getLocation())) {
						goodParticles.get(j).setWeight(newWeight);
					}
				}
			}
		}

		// Order good particles
		Collections.sort(goodParticles, new Comparator<Particle>() {

			@Override
			public int compare(Particle o1, Particle o2) {
				if (o1.getWeight() < o2.getWeight()) {
					return -1;
				} else if (o1.getWeight() > o2.getWeight()) {
					return 1;
				}
				return 0;
			}
		});

		// Distribute bad particles
		Random generator = new Random();
		//
		Location l;
		float difference = 100.0f;
		Particle closest;
		for (Particle p : badParticles) {

			float number = generator.nextFloat() * 1.0f;
			difference = 100.0f;
			closest = null;

			for (int i = 0; i < goodParticles.size(); i++) {
				if (goodParticles.get(i).getWeight() - number > 0.0f
						&& goodParticles.get(i).getWeight() - number < difference) {
					difference = p.getWeight() - number;
					closest = goodParticles.get(i);
				}
			}

			if (closest == null) {
				closest = goodParticles.get(goodParticles.size() - 1);
			}

			p.setLocation(new Location(closest.getLocation().getX(), closest.getLocation().getY(), LocationType.EMPTY));
		}

		goodParticles.addAll(badParticles);

		for (Particle p : goodParticles) {
			p.setWeight(0.0f);
		}

		particles.clear();
		particles.addAll(goodParticles);

		System.out.println(goodParticles.size() + " - " + badParticles.size());
		System.out.println(particles.size());

		particles.clear();
		particles.addAll(goodParticles);
	}

	private void updateParticles(int front, int right, int back, int left) {
		ArrayList<Particle> goodParticles = new ArrayList<Particle>();
		ArrayList<Particle> badParticles = new ArrayList<Particle>();

		for (Particle p : particles) {
			int x = p.getLocation().getX();
			int y = p.getLocation().getY();
			if (isValid(x, y + 1) != front && isValid(x, y - 1) != back && isValid(x - 1, y) != left
					&& isValid(x + 1, y) != right) {
				goodParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
			} else {
				badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
			}
		}

		if (goodParticles.size() > 0) {

			// Resample

			// Calculate weight for good particles
			for (int i = 0; i < goodParticles.size(); i++) {
				if (goodParticles.get(i).getWeight() == 0.0) {
					int counter = 1;
					for (int j = i + 1; j < goodParticles.size(); j++) {
						if (goodParticles.get(j).getLocation().equalsTo(goodParticles.get(i).getLocation())) {
							counter++;
						}
					}

					float newWeight = (float) counter / (float) goodParticles.size();
					for (int j = i; j < goodParticles.size(); j++) {
						if (goodParticles.get(j).getLocation().equalsTo(goodParticles.get(i).getLocation())) {
							goodParticles.get(j).setWeight(newWeight);
						}
					}
				}
			}

			// Order good particles
			Collections.sort(goodParticles, new Comparator<Particle>() {

				@Override
				public int compare(Particle o1, Particle o2) {
					if (o1.getWeight() < o2.getWeight()) {
						return -1;
					} else if (o1.getWeight() > o2.getWeight()) {
						return 1;
					}
					return 0;
				}
			});

			// Distribute bad particles
			Random generator = new Random();
			//
			Location l;
			float difference = 100.0f;
			Particle closest;
			for (Particle p : badParticles) {

				float number = generator.nextFloat() * 1.0f;
				difference = 100.0f;
				closest = null;

				for (int i = 0; i < goodParticles.size(); i++) {
					if (goodParticles.get(i).getWeight() - number > 0.0f
							&& goodParticles.get(i).getWeight() - number < difference) {
						difference = p.getWeight() - number;
						closest = goodParticles.get(i);
					}
				}

				if (closest == null) {
					closest = goodParticles.get(goodParticles.size() - 1);
				}

				p.setLocation(
						new Location(closest.getLocation().getX(), closest.getLocation().getY(), LocationType.EMPTY));
			}

			goodParticles.addAll(badParticles);

			for (Particle p : goodParticles) {
				p.setWeight(0.0f);
			}

			particles.clear();
			particles.addAll(goodParticles);

			System.out.println(goodParticles.size() + " - " + badParticles.size());
			System.out.println(particles.size());

			particles.clear();
			particles.addAll(goodParticles);
		}
	}

	private void print() {
		for (Particle p : particles) {
			System.out.print("(" + p.getLocation().getX() + " - " + p.getLocation().getY() + ") ");
		}
		System.out.println();
	}

	public int isValidBehind(int x, int y) {
		if (y - 1 >= 0) {
			if (map[x][y - 1].getType() == LocationType.BLOCK) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}

	public int isValidInFront(int x, int y) {
		if (y + 1 < maxY) {
			if (map[x][y + 1].getType() == LocationType.BLOCK) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}

	public int isValidLeft(int x, int y) {
		if (x - 1 >= 0) {
			if (map[x - 1][y].getType() == LocationType.BLOCK) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}

	public int isValidRight(int x, int y) {
		if (x + 1 < maxX) {
			if (map[x + 1][y].getType() == LocationType.BLOCK) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}

	public boolean isValidPosition(int x, int y) {
		if (x < 0 || y < 0 || x >= maxX || y >= maxY) {
			return false;
		} else if (map[x][y].getType() == LocationType.BLOCK) {
			return false;
		}
		return true;
	}

	public int isValid(int x, int y) {
		if (x < 0 || y < 0 || x >= maxX || y >= maxY) {
			return 0;
		} else if (map[x][y].getType() == LocationType.BLOCK) {
			return 0;
		}
		return 1;
	}
}