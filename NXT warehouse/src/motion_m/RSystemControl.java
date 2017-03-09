package motion_m;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.RConsole;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import robot_interface.RobotInterface;
import utils.Direction;
import utils.Location;
import utils.LocationType;
import utils.SuperLocation;
import utils.Config;

public class RSystemControl {

  public static void main(String[] args) {

    Config config = new Config();
    RobotLocationSessionManager locationManager = new RobotLocationSessionManager();
    RobotMovementSessionManager movementManager = new RobotMovementSessionManager();
    RouteExecutor routeExecutor = new RouteExecutor(config, movementManager);
    RobotControl robotControl = new RobotControl(movementManager);
    RobotInterface robotInterface = new RobotInterface(movementManager);

    (new Thread(routeExecutor)).start();
    (new Thread(robotControl)).start();
    (new Thread(robotInterface)).start();

  }

}
