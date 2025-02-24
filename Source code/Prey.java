package consoleVersion1;

/**
 * Prey class represents a robot that tries to survive in a robot arena.
 * @author Zichen Liao
 */
public class Prey extends Robot {
    private int survivalCounter = 0;
    private boolean markedForRemoval = false;
    private static final int SPLIT_THRESHOLD = 50;

    /**
     * Constructor to initialize the prey with its position and angle.
     *
     * @param bx    the x-coordinate of the prey
     * @param by    the y-coordinate of the prey
     * @param angle the initial angle of the prey
     */
    public Prey(double bx, double by, double angle) {
        super(bx, by, angle);
        this.speed = 0.4; 
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

        // Increment survival counter
        survivalCounter++;
    }

    /**
     * Checks if the prey should split based on survival counter.
     *
     * @return true if the prey should split, false otherwise
     */
    public boolean shouldSplit() {
        return survivalCounter >= SPLIT_THRESHOLD;
    }

    /**
     * Resets the survival counter.
     */
    public void resetSurvivalCounter() {
        survivalCounter = 0;
    }

    /**
     * Checks if the prey is marked for removal.
     *
     * @return true if marked for removal, false otherwise
     */
    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    /**
     * Marks the prey for removal.
     */
    public void markForRemoval() {
        markedForRemoval = true;
    }
}