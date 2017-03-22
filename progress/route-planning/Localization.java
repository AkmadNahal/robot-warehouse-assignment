import java.util.*;

public class Localization {

  private final int STEPS_FORWARD = 3;
  private final int STEPS_BACKWARD = 4;
  private final int STEPS_RIGHT = 3;
  private final int STEPS_LEFT = 4;

  private int moveTypeToExecute; // 0 - FORWARD, 1 - RIGHT, 2 - BACKWARD, 3 - LEFT
  private int moveCounter;

  private int NUMBER_OF_PARTICLES;

  private ArrayList<Particle> particles;
  private Location[][] map;
  private int maxX, maxY;
  private LocalisationMovement moveExecutor;

  // LocalizationMovement
  // executeRoute(Direction type) - ret boolean
  // readSurroundings() - ret DataOfJunction

  // if(front != 0 && right != 0 && back == 0 && left == 0) {
  // } else if(front != 0 && right == 0 && back != 0 && left == 0) {
  // } else if(front != 0 && right != 0 && back != 0 && left != 0) {
  // } else if(front == 0 && right != 0 && back != 0 && left == 0) {
  // } else if(front == 0 && right != 0 && back == 0 && left != 0) {
  // } else if(front != 0 && right != 0 && back == 0 && left != 0) {
  // } else if(front == 0 && right != 0 && back != 0 && left != 0) {
  // } else if(front != 0 && right == 0 && back != 0 && left != 0) {
  // } else if(front != 0 && right == 0 && back == 0 && left != 0) {
  // } else if(front == 0 && right == 0 && back != 0 && left != 0) {
  // } else if(front != 0 && right != 0 && back != 0 && left != 0) {
  // }

  public Localization(Location[][] _map, int _maxX, int _maxY, LocalisationMovement _moveExecutor) {
    map = _map;
    maxX = _maxX;
    maxY = _maxY;
    moveExecutor = _moveExecutor;
  }

  public Location localize() {
    createParticles();
    Location sol = null;

    while(true) {

      if(moveTypeToExecute == 0) {

        if(moveCounter < STEPS_FORWARD) {
          if(moveExecutor.executeMove(Direction.FORWARD)) {
            moveCounter++;

            DataOfJunction s = moveExecutor.readSurroundings();
            updateParticles(Direction.FORWARD, s.getyPlus(), s.getxPlus(), s.getyMinus(), s.getxMinus());
          }
        }

        if(moveCounter == STEPS_FORWARD) {
          moveTypeToExecute = 1;
        }

      } else if(moveTypeToExecute == 1) {

        if(moveCounter < STEPS_RIGHT) {
          if(moveExecutor.executeMove(Direction.RIGHT)) {
            moveCounter++;

            DataOfJunction s = moveExecutor.readSurroundings();
            updateParticles(Direction.RIGHT, s.getyPlus(), s.getxPlus(), s.getyMinus(), s.getxMinus());
          }
        }

        if(moveCounter == STEPS_RIGHT) {
          moveTypeToExecute = 2;
        }

      } else if(moveTypeToExecute == 2) {

        if(moveCounter < STEPS_BACKWARD) {
          if(moveExecutor.executeMove(Direction.BACKWARD)) {
            moveCounter++;

            DataOfJunction s = moveExecutor.readSurroundings();
            updateParticles(Direction.BACKWARD, s.getyPlus(), s.getxPlus(), s.getyMinus(), s.getxMinus());
          }
        }

        if(moveCounter == STEPS_BACKWARD) {
          moveTypeToExecute = 3;
        }

      } else if(moveTypeToExecute == 3) {

        if(moveCounter < STEPS_LEFT) {
          if(moveExecutor.executeMove(Direction.LEFT)) {
            moveCounter++;

            DataOfJunction s = moveExecutor.readSurroundings();
            updateParticles(Direction.LEFT, s.getyPlus(), s.getxPlus(), s.getyMinus(), s.getxMinus());
          }
        }

        if(moveCounter == STEPS_LEFT) {
          moveTypeToExecute = 0;
        }

      }

      if(isConverged()) {
        return particles.get(0).getLocation();
      }
    }

    return sol;
  }

  private void printParticles() {
    for(Particle p : particles) {
      System.out.print("(" + p.getLocation().getX() + " " + p.getLocation().getY() + " " + p.getWeight() + ") ");
    }
    System.out.println();
  }

  private void createParticles() {
    NUMBER_OF_PARTICLES = 0;
    particles = new ArrayList<Particle>();

    for(int i=0;i<maxX;i++) {
      for(int j=0;j<maxY;j++) {
        if(map[i][j].getType() == LocationType.EMPTY) {
          particles.add(new Particle(new Location(i,j,LocationType.EMPTY), 0.0f));
          NUMBER_OF_PARTICLES++;
        }
      }
    }

  }

  private boolean isConverged() {
    for(int i=1;i<particles.size();i++) {
      if(!particles.get(i).getLocation().equalsTo(particles.get(i-1).getLocation())) {
        return false;
      }
    }
    return true;
  }

  private void updateParticles(Direction move, int front, int right, int back, int left) {

    ArrayList<Particle> goodParticles = new ArrayList<Particle>();
    ArrayList<Particle> badParticles = new ArrayList<Particle>();

    if(move == Direction.FORWARD) {

      for(Particle p : particles) {

        int x = p.getLocation().getX();
        int y = p.getLocation().getY();
        int plusX = x+1;
        int plusY = y+1;
        int minusX = x-1;
        int minusY = y-1;

        boolean frontSquareEmpty = plusY < maxY && map[x][plusY].getType() == LocationType.EMPTY;

        if(front != 0 && right == 0 && back != 0 && left == 0) {
          boolean nextSquareNoFront = (plusY + 1) < maxY && map[x][plusY + 1].getType() == LocationType.EMPTY;
          boolean nextSquareHasRight = (plusX >= maxX || map[plusX][plusY].getType() == LocationType.BLOCK);
          boolean nextSquareNoBack = true;
          boolean nextSquareHasLeft = (minusX < 0 || map[minusX][plusY].getType() == LocationType.BLOCK);

          if(frontSquareEmpty && nextSquareNoFront && nextSquareHasRight && nextSquareNoBack && nextSquareHasLeft) {
            goodParticles.add(new Particle(new Location(x, plusY, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front != 0 && right != 0 && back != 0 && left != 0) {
          boolean nextSquareNoFront = (plusY + 1) < maxY && map[x][plusY + 1].getType() == LocationType.EMPTY;
          boolean nextSquareNoRight = (plusX < maxX && map[plusX][plusY].getType() == LocationType.EMPTY);
          boolean nextSquareNoBack = true;
          boolean nextSquareNoLeft = (minusX >= 0 && map[minusX][plusY].getType() == LocationType.EMPTY);

          if(frontSquareEmpty && nextSquareNoFront && nextSquareNoRight && nextSquareNoBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(x, plusY, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front == 0 && right != 0 && back != 0 && left == 0) {
          boolean nextSquareHasFront = (plusY + 1) >= maxY || map[plusX][plusY+1].getType() == LocationType.BLOCK;
          boolean nextSquareNoRight = (plusX < maxX && map[plusX][plusY].getType() == LocationType.EMPTY);
          boolean nextSquareNoBack = true;
          boolean nextSquareHasLeft = (minusX < 0 || map[minusX][plusY].getType() == LocationType.BLOCK);

          if(frontSquareEmpty && nextSquareHasFront && nextSquareNoRight && nextSquareNoBack && nextSquareHasLeft) {
            goodParticles.add(new Particle(new Location(x, plusY, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front == 0 && right != 0 && back != 0 && left != 0) {
          boolean nextSquareHasFront = (plusY + 1) >= maxY || map[plusX][plusY+1].getType() == LocationType.BLOCK;
          boolean nextSquareNoRight = (plusX < maxX && map[plusX][plusY].getType() == LocationType.EMPTY);
          boolean nextSquareNoBack = true;
          boolean nextSquareNoLeft = (minusX >= 0 && map[minusX][plusY].getType() == LocationType.EMPTY);

          if(frontSquareEmpty && nextSquareHasFront && nextSquareNoRight && nextSquareNoBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(x, plusY, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front != 0 && right == 0 && back != 0 && left != 0) {
          boolean nextSquareNoFront = (plusY + 1) < maxY && map[x][plusY + 1].getType() == LocationType.EMPTY;
          boolean nextSquareHasRight = (plusX >= maxX || map[plusX][plusY].getType() == LocationType.BLOCK);
          boolean nextSquareNoBack = true;
          boolean nextSquareNoLeft = (minusX >= 0 && map[minusX][plusY].getType() == LocationType.EMPTY);

          if(frontSquareEmpty && nextSquareNoFront && nextSquareHasRight && nextSquareNoBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(x, plusY, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front == 0 && right == 0 && back != 0 && left != 0) {
          boolean nextSquareHasFront = (plusY + 1) >= maxY || map[plusX][plusY+1].getType() == LocationType.BLOCK;
          boolean nextSquareHasRight = (plusX >= maxX || map[plusX][plusY].getType() == LocationType.BLOCK);
          boolean nextSquareNoBack = true;
          boolean nextSquareNoLeft = (minusX >= 0 && map[minusX][plusY].getType() == LocationType.EMPTY);

          if(frontSquareEmpty && nextSquareHasFront && nextSquareHasRight && nextSquareNoBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(x, plusY, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front != 0 && right != 0 && back != 0 && left != 0) {
          boolean nextSquareNoFront = (plusY + 1) < maxY && map[x][plusY + 1].getType() == LocationType.EMPTY;
          boolean nextSquareNoRight = (plusX < maxX && map[plusX][plusY].getType() == LocationType.EMPTY);
          boolean nextSquareNoBack = true;
          boolean nextSquareNoLeft = (minusX >= 0 && map[minusX][plusY].getType() == LocationType.EMPTY);

          if(frontSquareEmpty && nextSquareNoFront && nextSquareNoRight && nextSquareNoBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(x, plusY, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        }

      }

    } else if(move == Direction.BACKWARDS) {

      for(Particle p : particles) {

        int x = p.getLocation().getX();
        int y = p.getLocation().getY();
        int plusX = x+1;
        int plusY = y+1;
        int minusX = x-1;
        int minusY = y-1;

        boolean backSquareEmpty = minusY >= 0 && map[x][minusY].getType() == LocationType.EMPTY;

        if(front != 0 && right != 0 && back == 0 && left == 0) {
          boolean nextSquareNoFront = true;
          boolean nextSquareNoRight = (plusX < maxX && map[plusX][minusY].getType() == LocationType.EMPTY);
          boolean nextSquareHasBack = minusY < 0 || map[x][minusY-1].getType() == LocationType.BLOCK;
          boolean nextSquareHasLeft = (minusX < 0 || map[minusX][minusY].getType() == LocationType.BLOCK);

          if(backSquareEmpty && nextSquareNoFront && nextSquareNoRight && nextSquareHasBack && nextSquareHasLeft) {
            goodParticles.add(new Particle(new Location(x, minusY, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front != 0 && right == 0 && back != 0 && left == 0) {
          boolean nextSquareNoFront = true;
          boolean nextSquareHasRight = (plusX >= maxX || map[plusX][minusY].getType() == LocationType.BLOCK);
          boolean nextSquareNoBack = minusY >= 0 && map[x][minusY].getType() == LocationType.EMPTY;
          boolean nextSquareHasLeft = (minusX < 0 || map[minusX][minusY].getType() == LocationType.BLOCK);

          if(backSquareEmpty && nextSquareNoFront && nextSquareHasRight && nextSquareNoBack && nextSquareHasLeft) {
            goodParticles.add(new Particle(new Location(x, minusY, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front != 0 && right != 0 && back != 0 && left != 0) {
          boolean nextSquareNoFront = true;
          boolean nextSquareNoRight = (plusX < maxX && map[plusX][minusY].getType() == LocationType.EMPTY);
          boolean nextSquareNoBack = minusY >= 0 && map[x][minusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoLeft = (minusX >= 0 && map[minusX][minusY].getType() == LocationType.EMPTY);

          if(backSquareEmpty && nextSquareNoFront && nextSquareNoRight && nextSquareNoBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(x, minusY, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front != 0 && right != 0 && back == 0 && left != 0) {
          boolean nextSquareNoFront = true;
          boolean nextSquareNoRight = (plusX < maxX && map[plusX][minusY].getType() == LocationType.EMPTY);
          boolean nextSquareHasBack = minusY < 0 || map[x][minusY-1].getType() == LocationType.BLOCK;
          boolean nextSquareNoLeft = (minusX >= 0 && map[minusX][minusY].getType() == LocationType.EMPTY);

          if(backSquareEmpty && nextSquareNoFront && nextSquareNoRight && nextSquareHasBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(x, minusY, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front != 0 && right == 0 && back != 0 && left != 0) {
          boolean nextSquareNoFront = true;
          boolean nextSquareHasRight = (plusX >= maxX || map[plusX][minusY].getType() == LocationType.BLOCK);
          boolean nextSquareNoBack = minusY >= 0 && map[x][minusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoLeft = (minusX >= 0 && map[minusX][minusY].getType() == LocationType.EMPTY);

          if(backSquareEmpty && nextSquareNoFront && nextSquareHasRight && nextSquareNoBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(x, minusY, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front != 0 && right == 0 && back == 0 && left != 0) {
          boolean nextSquareNoFront = true;
          boolean nextSquareHasRight = (plusX >= maxX || map[plusX][minusY].getType() == LocationType.BLOCK);
          boolean nextSquareHasBack = minusY < 0 || map[x][minusY-1].getType() == LocationType.BLOCK;
          boolean nextSquareNoLeft = (minusX >= 0 && map[minusX][minusY].getType() == LocationType.EMPTY);

          if(backSquareEmpty && nextSquareNoFront && nextSquareHasRight && nextSquareHasBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(x, minusY, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front != 0 && right != 0 && back != 0 && left != 0) {
          boolean nextSquareNoFront = true;
          boolean nextSquareNoRight = (plusX < maxX && map[plusX][minusY].getType() == LocationType.EMPTY);
          boolean nextSquareNoBack = minusY >= 0 && map[x][minusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoLeft = (minusX >= 0 && map[minusX][minusY].getType() == LocationType.EMPTY);

          if(backSquareEmpty && nextSquareNoFront && nextSquareNoRight && nextSquareNoBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(x, minusY, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        }

      }

    } else if(move == Direction.RIGHT) {

      for(Particle p : particles) {

        int x = p.getLocation().getX();
        int y = p.getLocation().getY();
        int plusX = x+1;
        int plusY = y+1;
        int minusX = x-1;
        int minusY = y-1;

        boolean rightSquareEmpty = plusX < maxX && map[plusX][y].getType() == LocationType.EMPTY;

        if(front != 0 && right != 0 && back != 0 && left != 0) {
          boolean nextSquareNoFront = plusY < maxY && map[plusX][plusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoRight = ((plusX+1) < maxX && map[plusX+1][y].getType() == LocationType.EMPTY);
          boolean nextSquareNoBack = minusY >= 0 && map[plusX][minusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoLeft = true;

          if(rightSquareEmpty && nextSquareNoFront && nextSquareNoRight && nextSquareNoBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(plusX, y, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front == 0 && right != 0 && back == 0 && left != 0) {
          boolean nextSquareHasFront = plusY >= maxY || map[plusX][plusY].getType() == LocationType.BLOCK;
          boolean nextSquareNoRight = ((plusX+1) < maxX && map[plusX+1][y].getType() == LocationType.EMPTY);
          boolean nextSquareHasBack = minusY < 0 || map[plusX][minusY].getType() == LocationType.BLOCK;
          boolean nextSquareNoLeft = true;

          if(rightSquareEmpty && nextSquareHasFront && nextSquareNoRight && nextSquareHasBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(plusX, y, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front != 0 && right != 0 && back == 0 && left != 0) {
          boolean nextSquareNoFront = plusY < maxY && map[plusX][plusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoRight = ((plusX+1) < maxX && map[plusX+1][y].getType() == LocationType.EMPTY);
          boolean nextSquareHasBack = minusY < 0 || map[plusX][minusY].getType() == LocationType.BLOCK;
          boolean nextSquareNoLeft = true;

          if(rightSquareEmpty && nextSquareNoFront && nextSquareNoRight && nextSquareHasBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(plusX, y, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front == 0 && right != 0 && back != 0 && left != 0) {
          boolean nextSquareHasFront = plusY >= maxY || map[plusX][plusY].getType() == LocationType.BLOCK;
          boolean nextSquareNoRight = ((plusX+1) < maxX && map[plusX+1][y].getType() == LocationType.EMPTY);
          boolean nextSquareNoBack = minusY >= 0 && map[plusX][minusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoLeft = true;

          if(rightSquareEmpty && nextSquareHasFront && nextSquareNoRight && nextSquareNoBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(plusX, y, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front != 0 && right == 0 && back != 0 && left != 0) {
          boolean nextSquareNoFront = plusY < maxY && map[plusX][plusY].getType() == LocationType.EMPTY;
          boolean nextSquareHasRight = ((plusX+1) >= maxX || map[plusX+1][y].getType() == LocationType.BLOCK);
          boolean nextSquareNoBack = minusY >= 0 && map[plusX][minusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoLeft = true;

          if(rightSquareEmpty && nextSquareNoFront && nextSquareHasRight && nextSquareNoBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(plusX, y, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front != 0 && right == 0 && back == 0 && left != 0) {
          boolean nextSquareNoFront = plusY < maxY && map[plusX][plusY].getType() == LocationType.EMPTY;
          boolean nextSquareHasRight = ((plusX+1) >= maxX || map[plusX+1][y].getType() == LocationType.BLOCK);
          boolean nextSquareHasBack = minusY < 0 || map[plusX][minusY].getType() == LocationType.BLOCK;
          boolean nextSquareNoLeft = true;

          if(rightSquareEmpty && nextSquareNoFront && nextSquareHasRight && nextSquareHasBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(plusX, y, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front == 0 && right == 0 && back != 0 && left != 0) {
          boolean nextSquareHasFront = plusY >= maxY || map[plusX][plusY].getType() == LocationType.BLOCK;
          boolean nextSquareHasRight = ((plusX+1) >= maxX || map[plusX+1][y].getType() == LocationType.BLOCK);
          boolean nextSquareNoBack = minusY >= 0 && map[plusX][minusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoLeft = true;

          if(rightSquareEmpty && nextSquareHasFront && nextSquareHasRight && nextSquareNoBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(plusX, y, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front != 0 && right != 0 && back != 0 && left != 0) {
          boolean nextSquareNoFront = plusY < maxY && map[plusX][plusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoRight = ((plusX+1) < maxX && map[plusX+1][y].getType() == LocationType.EMPTY);
          boolean nextSquareNoBack = minusY >= 0 && map[plusX][minusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoLeft = true;

          if(rightSquareEmpty && nextSquareNoFront && nextSquareNoRight && nextSquareNoBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(plusX, y, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        }
      }

    } else if(move == Direction.LEFT) {

      for(Particle p : particles) {

        int x = p.getLocation().getX();
        int y = p.getLocation().getY();
        int plusX = x+1;
        int plusY = y+1;
        int minusX = x-1;
        int minusY = y-1;

        boolean leftSquareEmpty = minusX >= 0 && map[minusX][y].getType() == LocationType.EMPTY;

        if(front != 0 && right != 0 && back == 0 && left == 0) {
          boolean nextSquareNoFront = plusY < maxY && map[minusX][plusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoRight = true;
          boolean nextSquareHasBack = minusY < 0 || map[minusX][minusY].getType() == LocationType.BLOCK;
          boolean nextSquareHasLeft = ((minusX-1) < 0 || map[minusX-1][minusY].getType() == LocationType.BLOCK);

          if(leftSquareEmpty && nextSquareNoFront && nextSquareNoRight && nextSquareHasBack && nextSquareHasLeft) {
            goodParticles.add(new Particle(new Location(minusX, y, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front != 0 && right != 0 && back != 0 && left != 0) {
          boolean nextSquareNoFront = plusY < maxY && map[minusX][plusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoRight = true;
          boolean nextSquareNoBack = minusY >= 0 && map[minusX][minusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoLeft = ((minusX-1) >= 0 && map[minusX-1][minusY].getType() == LocationType.EMPTY);

          if(leftSquareEmpty && nextSquareNoFront && nextSquareNoRight && nextSquareNoBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(minusX, y, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front == 0 && right != 0 && back != 0 && left == 0) {
          boolean nextSquareHasFront = plusY >= maxY || map[minusX][plusY].getType() == LocationType.BLOCK;
          boolean nextSquareNoRight = true;
          boolean nextSquareNoBack = minusY >= 0 && map[minusX][minusY].getType() == LocationType.EMPTY;
          boolean nextSquareHasLeft = ((minusX-1) < 0 || map[minusX-1][minusY].getType() == LocationType.BLOCK);

          if(leftSquareEmpty && nextSquareHasFront && nextSquareNoRight && nextSquareNoBack && nextSquareHasLeft) {
            goodParticles.add(new Particle(new Location(minusX, y, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front == 0 && right != 0 && back == 0 && left != 0) {
          boolean nextSquareHasFront = plusY >= maxY || map[minusX][plusY].getType() == LocationType.BLOCK;
          boolean nextSquareNoRight = true;
          boolean nextSquareHasBack = minusY < 0 || map[minusX][minusY].getType() == LocationType.BLOCK;
          boolean nextSquareNoLeft = ((minusX-1) >= 0 && map[minusX-1][minusY].getType() == LocationType.EMPTY);

          if(leftSquareEmpty && nextSquareHasFront && nextSquareNoRight && nextSquareHasBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(minusX, y, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front != 0 && right != 0 && back == 0 && left != 0) {
          boolean nextSquareNoFront = plusY < maxY && map[minusX][plusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoRight = true;
          boolean nextSquareHasBack = minusY < 0 || map[minusX][minusY].getType() == LocationType.BLOCK;
          boolean nextSquareNoLeft = ((minusX-1) >= 0 && map[minusX-1][minusY].getType() == LocationType.EMPTY);

          if(leftSquareEmpty && nextSquareNoFront && nextSquareNoRight && nextSquareNoLeft && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(minusX, y, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front == 0 && right != 0 && back != 0 && left != 0) {
          boolean nextSquareHasFront = (plusY >= maxY || map[minusX][plusY].getType() == LocationType.BLOCK);
          boolean nextSquareNoRight = true;
          boolean nextSquareNoBack = minusY >= 0 && map[minusX][minusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoLeft = (((minusX-1) >= 0) && (map[minusX-1][minusY].getType() == LocationType.EMPTY));

          if(leftSquareEmpty && nextSquareHasFront && nextSquareNoRight && nextSquareNoBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(minusX, y, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        } else if(front != 0 && right != 0 && back != 0 && left != 0) {
          boolean nextSquareNoFront = plusY < maxY && map[minusX][plusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoRight = true;
          boolean nextSquareNoBack = minusY >= 0 && map[minusX][minusY].getType() == LocationType.EMPTY;
          boolean nextSquareNoLeft = ((minusX-1) >= 0 && map[minusX-1][minusY].getType() == LocationType.EMPTY);

          if(leftSquareEmpty && nextSquareNoFront && nextSquareNoRight && nextSquareNoBack && nextSquareNoLeft) {
            goodParticles.add(new Particle(new Location(minusX, y, LocationType.EMPTY), 0.0f));
          } else {
            badParticles.add(new Particle(new Location(x, y, LocationType.EMPTY), 0.0f));
          }
        }

      }

    }

    // Resample

    // Calculate weight for good particles
    for(int i=0;i<goodParticles.size();i++) {
      if(goodParticles.get(i).getWeight() == 0.0) {
        int counter = 1;
        for(int j=i+1;j<goodParticles.size();j++) {
          if(goodParticles.get(j).getLocation().equalsTo(goodParticles.get(i).getLocation())) {
            counter++;
          }
        }

        float newWeight = counter/goodParticles.size();
        for(int j=i;j<goodParticles.size();j++) {
          if(goodParticles.get(j).getLocation().equalsTo(goodParticles.get(i).getLocation())) {
            goodParticles.get(j).setWeight(newWeight);
          }
        }
      }
    }

    // Distribute bad particles
    Random generator = new Random();
    Location l, maxL;
    float minF, max;
    for(Particle p : badParticles) {
      l = null;
      maxL = null;
      minF = 2.0f;
      max = -1.0f;
      float number = generator.nextFloat() * 1.0f;

      for(Particle part : goodParticles) {
        if(number < part.getWeight() && part.getWeight() < minF) {
          minF = part.getWeight();
          l = part.getLocation();
        }

        if(part.getWeight() > max) {
          max = part.getWeight();
          maxL = part.getLocation();
        }
      }

      if(l != null) {
        goodParticles.add(new Particle(new Location(l.getX(), l.getY(), LocationType.EMPTY), number));
      } else {
        goodParticles.add(new Particle(new Location(maxL.getX(), maxL.getY(), LocationType.EMPTY), number));
      }
    }

    for(Particle p : goodParticles) {
      p.setWeight(0.0f);
    }

    particles.clear();
    particles.addAll(goodParticles);

    System.out.println("Final particles size " + particles.size());
  }


}
