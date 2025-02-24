package consoleVersion1;

/**
 * Obstacle class represents a rectangular obstacle in a 2D space.
 * @author Zichen Liao
 */
public class Obstacle {
    private double x, y;    
    private double width, height;  

    /**
     * Constructor to initialize the obstacle with its position and size.
     *
     * @param x      the x-coordinate of the center
     * @param y      the y-coordinate of the center
     * @param width  the width of the obstacle
     * @param height the height of the obstacle
     */
    public Obstacle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the x-coordinate of the obstacle.
     *
     * @return the x-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the obstacle.
     *
     * @return the y-coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the width of the obstacle.
     *
     * @return the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Gets the height of the obstacle.
     *
     * @return the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Checks if the obstacle is colliding with a specified rectangle.
     *
     * @param checkX      the x-coordinate of the rectangle to check
     * @param checkY      the y-coordinate of the rectangle to check
     * @param checkWidth  the width of the rectangle to check
     * @param checkHeight the height of the rectangle to check
     * @return true if colliding, false otherwise
     */
    public boolean isColliding(double checkX, double checkY, double checkWidth, double checkHeight) {
        // Calculate horizontal and vertical distances
        double dx = Math.abs(checkX - x);
        double dy = Math.abs(checkY - y);

        // Check if the distances are less than the sum of half widths and heights
        if (dx < (width / 2 + checkWidth / 2) && dy < (height / 2 + checkHeight / 2)) {
            return true;  // Collision
        }
        return false;  // No collision
    }
}
