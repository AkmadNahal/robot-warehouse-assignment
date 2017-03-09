package motion_m;

import utils.Direction;

public class RobotLocationSessionManager {

  private boolean shouldSendNextMove;
  private Direction nextMove;

  public RobotLocationSessionManager() {
    shouldSendNextMove = false;
    nextMove = null;
  }

  public synchronized void setShouldSendNextMove(boolean value) {
    shouldSendNextMove = value;
  }

  public synchronized boolean getShouldSendNextMove() {
    return shouldSendNextMove;
  }

  public synchronized void setNextMove(Direction value) {
    nextMove = value;
  }

  public synchronized Direction getNextMove() {
    return nextMove;
  }

}
