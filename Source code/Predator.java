package consoleVersion1;

/**
 * Predator class represents a robot that hunts prey in a robot arena.
 * @author Zichen Liao
 */
public class Predator extends Robot {
    private int preyDetectionCooldown = 0;

    /**
     * Constructor to initialize the predator with its position and angle.
     *
     * @param bx    the x-coordinate of the predator
     * @param by    the y-coordinate of the predator
     * @param angle the initial angle of the predator
     */
    public Predator(double bx, double by, double angle) {
        super(bx, by, angle);
        this.speed = 0.5; // Predator speed is halved
    }

    @Override
    public void tryToMove(RobotArena arena) {
        double newX = x + speed * Math.cos(Math.toRadians(angle));
        double newY = y + speed * Math.sin(Math.toRadians(angle));

        // Define a smaller boundary for collision detection
        double boundaryOffset = 0.2;

        // Check for collision with arena boundaries
        boolean hitBoundary = false;
        if (newX - width / 2 < boundaryOffset || newX + width / 2 >= arena.getXSize() - boundaryOffset) {
            angle = 180 - angle; // Reflect angle horizontally
            hitBoundary = true;
        }

        if (newY - height / 2 < boundaryOffset || newY + height / 2 >= arena.getYSize() - boundaryOffset) {
            angle = -angle; // Reflect angle vertically
            hitBoundary = true;
        }

        // Check for collision with obstacles
        for (Obstacle o : arena.getObstacles()) {
            if (o.isColliding(newX, newY, width, height)) {
                if (Math.abs(o.getX() - newX) > Math.abs(o.getY() - newY)) {
                    angle = 180 - angle; // Reflect angle horizontally
                } else {
                    angle = -angle; // Reflect angle vertically
                }
                hitBoundary = true;
                break;
            }
        }

        // Update position only if no collision
        if (!hitBoundary) {
            x = newX;
            y = newY;
            stuckCounter = 0;
        } else {
            stuckCounter++;
        }

        // If stuck for 3 consecutive steps, change direction randomly
        if (stuckCounter >= 3) {
            angle = random.nextDouble() * 360;
            stuckCounter = 0;
        }

        // Detect prey
        if (preyDetectionCooldown == 0) {
            for (Robot r : arena.getRobots()) {
                if (r instanceof Prey && isWithinDetectionRange(r)) {
                    angle = calculateAngleTowards(r);
                    ((Prey)r).markForRemoval(); // Mark prey for removal
                    break;
                }
            }
            preyDetectionCooldown = 2;
        } else {
            preyDetectionCooldown--;
        }
    }

    /**
     * Checks if the specified robot is within detection range.
     *
     * @param r the robot to check
     * @return true if within detection range, false otherwise
     */
    private boolean isWithinDetectionRange(Robot r) {
        double distance = Math.sqrt(Math.pow(r.getX() - x, 2) + Math.pow(r.getY() - y, 2));
        return distance < 4; // Assume detection range is 4 units
    }

    /**
     * Calculates the angle towards the specified robot.
     *
     * @param r the robot to calculate the angle towards
     * @return the angle in degrees
     */
    private double calculateAngleTowards(Robot r) {
        return Math.toDegrees(Math.atan2(r.getY() - y, r.getX() - x));
    }
}