package consoleVersion1;

import java.util.ArrayList;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Iterator;

/**
 * RobotArena class represents a Robot Arena where robots can move and interact with obstacles.
 * @author Zichen Liao
 */
public class RobotArena {
    private int xmax, ymax;
    private ArrayList<Robot> manyRobots;
    private ArrayList<Obstacle> obstacles; // List of obstacles in the arena
    private Random random;

    /**
     * Constructor with specified arena size.
     * 
     * @param xp the width of the arena
     * @param yp the height of the arena
     */
    public RobotArena(int xp, int yp) {
        xmax = xp;
        ymax = yp;
        manyRobots = new ArrayList<>();
        obstacles = new ArrayList<>();
        random = new Random();
    }

    /**
     * Method to get the arena's X dimension size.
     * 
     * @return the width of the arena
     */
    public int getXSize() {
        return xmax;
    }

    /**
     * Method to get the arena's Y dimension size.
     * 
     * @return the height of the arena
     */
    public int getYSize() {
        return ymax;
    }

    /**
     * Get the robot at the specified (x, y) position.
     * 
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return the robot at the specified position, or null if no robot is found
     */
    public Robot getRobotAt(double x, double y) {
        for (Robot r : manyRobots) {
            if (r.isHere(x, y)) {
                return r;
            }
        }
        return null;
    }

    /**
     * Add a new robot to a random valid position in the arena.
     * 
     * @param r the robot to add
     */
    public void addRobot(Robot r) {
        manyRobots.add(r);
    }

    /**
     * Remove all robots in the arena.
     */
    public void RemoveAllRobot() {
        // Create a list of robots to remove
        ArrayList<Robot> toRemove = new ArrayList<>();
        
        // Iterate through all robots
        for (Robot r : manyRobots) {
            r.tryToMove(this);
            // If the robot is marked for removal, add to the removal list
            if (r instanceof Prey && ((Prey)r).isMarkedForRemoval()) {
                toRemove.add(r);
            }
        }
        
        // Remove all marked robots
        manyRobots.removeAll(toRemove);
    }

    /**
     * Convert the arena to a string representation.
     * 
     * @return a string representation of the arena
     */
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (Robot r : manyRobots) {
            res.append(r.toString()).append("\n");
        }
        for (Obstacle o : obstacles) {
            res.append(o.toString()).append("\n");
        }
        return res.toString();
    }

    /**
     * Get all robots in the arena.
     * 
     * @return a list of robots in the arena
     */
    public ArrayList<Robot> getRobots() {
        return manyRobots; // Return a list of robots
    }

    /**
     * Get all obstacles in the arena.
     * 
     * @return a list of obstacles in the arena
     */
    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    /**
     * Clear all robots and obstacles from the arena.
     */
    public void clearArena() {
        manyRobots.clear();
        obstacles.clear();
    }

    /**
     * Clear all robots from the arena.
     */
    public void clearRobots() {
        manyRobots.clear();
    }

    /**
     * Add a specific obstacle to the arena.
     * 
     * @param o the obstacle to add
     */
    public void addObstacle(Obstacle o) {
        obstacles.add(o);
    }

    /**
     * Get all robots as an observable list.
     * 
     * @return an observable list of robots
     */
    public ObservableList<Robot> getObservableRobots() {
        return FXCollections.observableArrayList(manyRobots);
    }

    /**
     * Add random obstacles to the arena with specified dimensions (width and height).
     * 
     * @param count the number of obstacles to add
     * @param obstacleWidth the width of each obstacle
     * @param obstacleHeight the height of each obstacle
     */
    public void addRandomObstacles(int count, double obstacleWidth, double obstacleHeight) {
        for (int i = 0; i < count; i++) {
            double x, y;
            do {
                x = random.nextInt(xmax);
                y = random.nextInt(ymax);
            } while (getRobotAt(x, y) != null || isBorder(x, y));

            // Create a new obstacle and add to the list
            obstacles.add(new Obstacle(x, y, obstacleWidth, obstacleHeight));
        }
    }

    /**
     * Helper method to check if a given position is on the border of the arena.
     * 
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if the position is on the border, false otherwise
     */
    private boolean isBorder(double x, double y) {
        return x == 0 || x == xmax - 1 || y == 0 || y == ymax - 1;
    }

    /**
     * Move all robots in the arena.
     */
    public void moveAllRobot() {
        Iterator<Robot> iterator = manyRobots.iterator();
        while (iterator.hasNext()) {
            Robot r = iterator.next();
            r.tryToMove(this);
        }
    }

    /**
     * Update the state of all robots in the arena.
     */
    public void updateRobots() {
        ArrayList<Robot> toAdd = new ArrayList<>();
        ArrayList<Robot> toRemove = new ArrayList<>();

        // Iterate and update all robots
        for (Robot robot : manyRobots) {
            // Handle splitting logic for Prey
            if (robot instanceof Prey) {
                Prey prey = (Prey) robot;
                // Check if it should split
                if (prey.shouldSplit()) {
                    // Create a new Prey and add to the list
                    toAdd.add(new Prey(prey.getX(), prey.getY(), 
                             random.nextDouble() * 360));
                    prey.resetSurvivalCounter();
                }
                // Check if marked for removal
                if (prey.isMarkedForRemoval()) {
                    toRemove.add(prey);
                }
            }
            
            // Update robot position
            robot.tryToMove(this);
        }
        
        // Remove marked robots
        manyRobots.removeAll(toRemove);
        manyRobots.addAll(toAdd);
    }
}