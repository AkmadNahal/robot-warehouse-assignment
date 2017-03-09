package motion;

import robot_interface.RobotInterface;
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
