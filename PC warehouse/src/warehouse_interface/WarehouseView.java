package warehouse_interface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

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
	private JTextArea completedJobsDisplay;
	private JTextArea totalRewardDisplay;
	private JPanel cancelationPanel;
	private JPanel jobsPanel;
	private JPanel completedJobsAndRewardPanel;
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
		completedJobsAndRewardPanel = new JPanel();
		jobsPanel = new JPanel();
		vizPanel = new JPanel();
		for (int i = 1; i <= robotCount; i++) {
			cancelRobotButtons.add(createNewCancelRobotButton(i));
		}
		refreshRobots = new JButton("Refresh");
		completedJobsDisplay = createOtherDisplayField();
		totalRewardDisplay = createAnotherDisplayField();
		jobsDisplayRobot1 = createDisplayField();
		jobsDisplayRobot2 = createDisplayField();
		jobsDisplayRobot3 = createDisplayField();
		jobsPanelVisualisation(jobsPanel);
		completedJobsVisualisationPanel(completedJobsAndRewardPanel);
		cancelationPanelVisualisation(cancelationPanel);
		frameVisualisation(frame);
	}

	private void completedJobsVisualisationPanel(JPanel completedJobsAndRewardPanel){
		completedJobsAndRewardPanel.setLayout(new GridLayout(1,1));
		completedJobsAndRewardPanel.setPreferredSize(new Dimension(200,200));
		completedJobsAndRewardPanel.add(completedJobsDisplay);
	}
	
	private void jobsPanelVisualisation(JPanel jobsPanel) {
		jobsPanel.setLayout(new GridLayout(4, 1));
		jobsPanel.setPreferredSize(new Dimension(120, 100));
		jobsPanel.add(jobsDisplayRobot1);
		jobsPanel.add(jobsDisplayRobot2);
		jobsPanel.add(jobsDisplayRobot3);
		jobsPanel.add(totalRewardDisplay);
	}

	private JButton createNewCancelRobotButton(final int i) {
		String robotName = "";
		if (i == 1){
			robotName = "'Lil' Bob'";
		}else if (i == 2){
			robotName = "'Lil' Vader'";
		}else if (i == 3){
			robotName = "'Lil' Yoda'";
		}
		JButton button = new JButton("Cancel " + robotName);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectionButtonPressed(i);
			}
		});
		return button;
	}

	private void cancelationPanelVisualisation(JPanel cancelationPanel) {
		cancelationPanel.setLayout(new GridLayout(2, 2));
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
		frame.setPreferredSize(new Dimension(750, 600));
		frame.setBackground(new Color(255, 255, 255));
		frame.setJMenuBar(menuBar);
		frame.pack();
		frame.setVisible(true);
		frame.add(cancelationPanel, BorderLayout.SOUTH);
		frame.add(jobsPanel, BorderLayout.EAST);
		frame.add(completedJobsAndRewardPanel, BorderLayout.WEST);
	}

	private JTextArea createOtherDisplayField(){
		JTextArea field = new JTextArea();
		field.setPreferredSize(new Dimension(70, 80));
		field.setBackground(new Color(255, 255, 255));
		field.setFont(new Font(Font.DIALOG, Font.PLAIN, 13));
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		field.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		field.setOpaque(true);
		field.setEditable(false);
		field.setAlignmentX(5f);
		return field;
	}
	
	private JTextArea createAnotherDisplayField(){
		JTextArea field = new JTextArea();
		field.setPreferredSize(new Dimension(70, 10));
		field.setBackground(new Color(255, 255, 255));
		field.setFont(new Font(Font.DIALOG, Font.PLAIN, 13));
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		field.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		field.setOpaque(true);
		field.setEditable(false);
		field.setAlignmentX(5f);
		return field;
	}
	
	private JTextArea createDisplayField() {
		JTextArea field = new JTextArea();
		field.setPreferredSize(new Dimension(35, 35));
		field.setBackground(new Color(255, 255, 255));
		field.setFont(new Font(Font.DIALOG, Font.PLAIN, 13));
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
	
	public void setReward(PCSessionManager sessionManager){
		float totalReward = sessionManager.getTotalReward();
		String tasksList = "Total Reward:";
		tasksList += System.lineSeparator();
		DecimalFormat format = new DecimalFormat("#.00");
		if (sessionManager.getTotalReward() == 0f){
			tasksList += "0.00";
		}else{
			tasksList += format.format(sessionManager.getTotalReward());
		}
		totalRewardDisplay.setText(tasksList);
	}
	
	public void setTasks(PCSessionManager sessionManager) {
		Round currentRound = sessionManager.getCurrentRound();
		String jobID = currentRound.getJob();
		String tasksList = sessionManager.getRobotName();
		tasksList += System.lineSeparator();
		tasksList += jobID;
		tasksList += System.lineSeparator();
		if (sessionManager.getNumOfPicks() == 0){
			tasksList += new String("Waiting...");
		}else if (sessionManager.getNumOfPicks() == -1){
			tasksList += new String("To drop-off");
		}else{
			tasksList += new String("Next Pick: ");
			tasksList += sessionManager.getPickLocation();
		}
		tasksList += System.lineSeparator();
		if (sessionManager.getNumOfPicks() == 0){
			tasksList += new String("Waiting...");
		}else{
			tasksList += new String("Num. of Picks: ");
			tasksList += sessionManager.getNumOfPicks();
		}
		tasksList += System.lineSeparator();
		tasksList += new String("Weight: ");
		float currentWeight = sessionManager.getCurrentWeight();
		DecimalFormat format = new DecimalFormat("#.00");
		if (sessionManager.getCurrentWeight() == 0f){
			tasksList += "0.00";
		}else{
			tasksList += format.format(sessionManager.getCurrentWeight());
		}
		if (sessionManager.getRobotName().equals("Lil' Bob")){
			jobsDisplayRobot1.setText(tasksList);
		}else if (sessionManager.getRobotName().equals("Lil' Vader")){
			jobsDisplayRobot2.setText(tasksList);
		}else if (sessionManager.getRobotName().equals("Lil' Yoda")){
			jobsDisplayRobot3.setText(tasksList);
		}
	}
}