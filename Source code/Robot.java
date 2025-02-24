package consoleVersion1;

/**
 * Robot class represents a Robot with properties such as position, angle, and speed.
 * @author Zichen Liao
 * It provides methods to get and set the robot's position, retrieve its ID, and display its information.
 */
import java.util.Random;
import javafx.scene.control.Alert;

public abstract class Robot {
    protected double x, y;
    protected int robotid;
    protected static int robotCount = 0;
    protected double angle;
    protected double speed;
    protected double width, height;
    protected Random random;
    protected int stuckCounter = 0;

    /**
     * Constructs a Robot with specified position and angle.
     * 
     * @param bx    the x-coordinate of the robot
     * @param by    the y-coordinate of the robot
     * @param angle the angle of the robot
     */
    public Robot(double bx, double by, double angle) {
        x = bx;
        y = by;
        robotid = robotCount++;
        this.angle = angle;
        this.speed = 1;
        this.width = 1.0;
        this.height = 0.5;
        random = new Random();
    }

    /**
     * Returns the x-coordinate of the robot.
     * 
     * @return the x-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the robot.
     * 
     * @return the y-coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the position of the robot.
     * 
     * @param nx the new x-coordinate
     * @param ny the new y-coordinate
     */
    public void setXY(double nx, double ny) {
        x = nx;
        y = ny;
    }

    /**
     * Returns the ID of the robot.
     * 
     * @return the robot ID
     */
    public int getId() {
        return robotid;
    }

    @Override
    public String toString() {
        return "Robot " + robotid + " at " + Math.round(x) + ", " + Math.round(y);
    }

    /**
     * Returns the angle of the robot.
     * 
     * @return the angle
     */
    public double getAngle() {
        return angle;
    }

    /**
     * Checks if the robot is at the specified coordinates.
     * 
     * @param sx the x-coordinate to check
     * @param sy the y-coordinate to check
     * @return true if the robot is at the specified coordinates, false otherwise
     */
    public boolean isHere(double sx, double sy) {
        return x == sx && y == sy;
    }

    /**
     * Abstract method for moving the robot in the specified arena.
     * 
     * @param arena the arena in which the robot is trying to move
     */
    public abstract void tryToMove(RobotArena arena);

    /**
     * Displays information about the robot in an alert dialog.
     */
    public void showInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Robot Information");
        alert.setHeaderText("Robot " + robotid);
        alert.setContentText("Position: (" + Math.round(x) + ", " + Math.round(y) + ")\nAngle: " + angle);
        alert.showAndWait();
    }
}