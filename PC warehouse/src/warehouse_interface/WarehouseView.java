package warehouse_interface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import job_selection.Round;
import rp.robotics.mapping.GridMap;
import rp.robotics.simulation.MapBasedSimulation;
import rp.robotics.visualisation.GridMapVisualisation;
import rp.robotics.visualisation.MapVisualisationComponent;
import system_control.PCSessionManager;

public class WarehouseView {
	public static interface CancelButtonCallback {
		void cancel(int i);
	}

	public static interface RefreshButtonCallback {
		void refresh();
	}

	private JMenuBar menuBar;
	private JFrame frame;
	private JTextArea jobsDisplayRobot1;
	private JTextArea jobsDisplayRobot2;
	private JTextArea jobsDisplayRobot3;
	private JPanel cancelationPanel;
	private JPanel jobsPanel;
	private JPanel vizPanel;
	private List<JButton> cancelRobotButtons = new ArrayList<>();
	private JButton refreshRobots;
	private RefreshButtonCallback refreshCallback;
	private CancelButtonCallback cancelCallback;

	public WarehouseView(int robotCount) {
		frame = new JFrame("Warehouse");
		frame.addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent arg0) {
				if (arg0.getNewState() == WindowEvent.WINDOW_CLOSING) {
					// TODO add callback, and controller needs to listen and
					// cancel
					// sim.
				}
			}
		});
		cancelationPanel = new JPanel();
		jobsPanel = new JPanel();
		vizPanel = new JPanel();
		for (int i = 1; i <= robotCount; i++) {
			cancelRobotButtons.add(createNewCancelRobotButton(i));
		}
		refreshRobots = new JButton("Refresh");
		jobsDisplayRobot1 = createDisplayField();
		jobsDisplayRobot2 = createDisplayField();
		jobsDisplayRobot3 = createDisplayField();
		jobsPanelVisualisation(jobsPanel);
		cancelationPanelVisualisation(cancelationPanel);
		frameVisualisation(frame);
	}

	private void jobsPanelVisualisation(JPanel jobsPanel) {
		jobsPanel.setLayout(new GridLayout(3, 1));
		jobsPanel.setPreferredSize(new Dimension(250, 100));
		jobsPanel.add(jobsDisplayRobot1);
		jobsPanel.add(jobsDisplayRobot2);
		jobsPanel.add(jobsDisplayRobot3);
	}

	private JButton createNewCancelRobotButton(final int i) {
		JButton button = new JButton(String.format("Cancel Robot Number %d", i));
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectionButtonPressed(i);
			}
		});
		return button;
	}

	private void cancelationPanelVisualisation(JPanel cancelationPanel) {
		cancelationPanel.setLayout(new GridLayout(4, 1));
		cancelationPanel.setPreferredSize(new Dimension(100, 100));

		refreshRobots.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshRobots();
			}
		});
		for (JButton b : cancelRobotButtons) {
			cancelationPanel.add(b);
		}
		cancelationPanel.add(refreshRobots);
	}

	protected void refreshRobots() {
		if (this.refreshCallback != null) {
			this.refreshCallback.refresh();
		}
	}

	private void selectionButtonPressed(int s) {
		if (this.cancelCallback != null) {
			this.cancelCallback.cancel(s);
		}
	}

	private void frameVisualisation(JFrame frame) {
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(700, 700));
		frame.setJMenuBar(menuBar);
		frame.pack();
		frame.setVisible(true);
		frame.add(cancelationPanel, BorderLayout.SOUTH);
		frame.add(jobsPanel, BorderLayout.EAST);
	}

	private JTextArea createDisplayField() {
		JTextArea field = new JTextArea();
		field.setPreferredSize(new Dimension(70, 70));
		field.setBackground(new Color(255, 255, 255));
		field.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		field.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		field.setOpaque(true);
		field.setEditable(false);
		field.setAlignmentX(5f);
		return field;
	}

	public void setCancelCallback(CancelButtonCallback cancelCallback) {
		this.cancelCallback = cancelCallback;
	}

	public void setRefreshCallback(RefreshButtonCallback refreshCallback) {
		this.refreshCallback = refreshCallback;
	}

	public void setMap(GridMap mapModel, MapBasedSimulation sim) {
		GridMapVisualisation viz = new GridMapVisualisation(mapModel, sim.getMap());
		MapVisualisationComponent.populateVisualisation(viz, sim);
		frame.add(viz);
	}
	
	public void setTasks(PCSessionManager sessionManager, String robotName) {
		Round currentRound = sessionManager.getCurrentRound();
		String jobID = currentRound.toString();
		String tasksList = "  " + robotName;
		tasksList += System.lineSeparator();
		tasksList += "Job #3e234234";
		tasksList += System.lineSeparator();
		tasksList += "Next goal:\n[ " + currentRound.getRoute().get(currentRound.getRoute().size()-1).getX() + " , "
				+ currentRound.getRoute().get(currentRound.getRoute().size()-1).getY() + " ]" ;
		if (robotName.equals("Lil' Bob")){
			jobsDisplayRobot1.setText(tasksList);
		}else if (robotName.equals("Lil' Vader")){
			jobsDisplayRobot2.setText(tasksList);
		}else if (robotName.equals("Lil' Yoda")){
			jobsDisplayRobot3.setText(tasksList);
		}
	}
}